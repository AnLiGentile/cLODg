package it.istc.cnr.stlab.clodg.workflow;

//import static it.istc.cnr.stlab.clodg.Namespaces.ARTICLE_NS;
//import static it.istc.cnr.stlab.clodg.Namespaces.IN_USE_ARTICLE_NS;
//import static it.istc.cnr.stlab.clodg.Namespaces.KEYNOTE_NS;
//import static it.istc.cnr.stlab.clodg.Namespaces.ORGANIZATION_NS;
//import static it.istc.cnr.stlab.clodg.Namespaces.PERSON_NS;
//import static it.istc.cnr.stlab.clodg.Namespaces.TOPLEVEL_NS;
import it.istc.cnr.stlab.clodg.Homonym;
import it.istc.cnr.stlab.clodg.models.Organization;
import it.istc.cnr.stlab.clodg.models.OrganizationMap;
import it.istc.cnr.stlab.clodg.models.Person;
import it.istc.cnr.stlab.clodg.models.PersonMap;
import it.istc.cnr.stlab.clodg.models.Urifier;
import it.istc.cnr.stlab.clodg.util.OfficialNameSpace;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.digest.DigestUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import au.com.bytecode.opencsv.CSVReader;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.DC_11;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * @author annalisa, andrea.nuzzolese
 *
 */
public class GenerateMainConferenceInitialGraph {

	public static ClassLoader classLoader;

	private OfficialNameSpace ns;

	private PersonMap personMap;
	private OrganizationMap organizationMap;
	private Map<String, Boolean> answersMap;
	private Map<String, Map<String, Homonym>> homonymsMap;

	private String xsltFolder;

	private String conferenceData, conferenceConfig;
	private Document conferenceDataDoc, conferenceConfigDoc;

	public GenerateMainConferenceInitialGraph() {
		getClassLoader();

		this.ns = OfficialNameSpace.getInstance();

		this.conferenceConfig = conferenceConfig;
		this.conferenceData = conferenceData;

		this.xsltFolder = this.ns.xsltFolder;
	}

	public GenerateMainConferenceInitialGraph(String conferenceData,
			String conferenceConfig) {

		this();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;

		try {
			builder = factory.newDocumentBuilder();
			System.out.println(classLoader.getResourceAsStream(conferenceData));
			conferenceDataDoc = builder.parse(classLoader
					.getResourceAsStream(conferenceData));
			conferenceConfigDoc = builder.parse(classLoader
					.getResourceAsStream(conferenceConfig));
		} catch (ParserConfigurationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		homonymsMap = new HashMap<String, Map<String, Homonym>>();

		// TODO make this a configurable parameter
		CSVReader csvReader = new CSVReader(new InputStreamReader(
				classLoader.getResourceAsStream("data/ESWC2015-homonyms.csv")));

		String[] row = null;
		try {
			while ((row = csvReader.readNext()) != null) {
				String id = row[0];
				String email = row[1];
				String localId = row[2];

				Map<String, Homonym> homonyms = homonymsMap.get(id);
				if (homonyms == null) {
					homonyms = new HashMap<String, Homonym>();
					homonymsMap.put(id, homonyms);
				}
				homonyms.put(email, new Homonym(email, localId));

			}
			csvReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		answersMap = new HashMap<String, Boolean>();

		buildPersonsMap();
		buildOrganizationMap();
	}

	private PersonMap buildPersonsMap() {

		if (personMap == null) {
			personMap = new PersonMap();
			/*
			 * Read the RDF model containing all people names.
			 */
			Model namesModel = ModelFactory.createDefaultModel();
			namesModel.read(
					classLoader.getResourceAsStream("data/all-names.rdf"),
					"http://data.semanticweb.org/person/", "RDF/XML");

			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xPath = xPathFactory.newXPath();
			if (conferenceDataDoc != null) {

				try {
					/*
					 * Fetch persons from the authors list.
					 */
					XPathExpression xPathExpression = xPath
							.compile("/snapshot/submissions/submission/authors/author");
					NodeList nodes = (NodeList) xPathExpression.evaluate(
							conferenceDataDoc, XPathConstants.NODESET);
					addEntitysToMap(nodes, xPath, namesModel);

					/*
					 * Fetch persons from the programm committee.
					 */
					xPathExpression = xPath.compile("/snapshot/pc/pcMember");
					nodes = (NodeList) xPathExpression.evaluate(
							conferenceDataDoc, XPathConstants.NODESET);
					addEntitysToMap(nodes, xPath, namesModel);
				} catch (XPathExpressionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			if (conferenceConfigDoc != null) {

				try {
					/*
					 * Fetch persons from the chairs list.
					 */
					XPathExpression xPathExpression = xPath
							.compile("/conferenceData/chairs/chair/heldBy");
					NodeList nodes = (NodeList) xPathExpression.evaluate(
							conferenceConfigDoc, XPathConstants.NODESET);
					addEntitysToMap(nodes, xPath, namesModel);

				} catch (XPathExpressionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return personMap;
	}

	private void addEntitysToMap(NodeList personNodes, XPath xPath,
			Model namesModel) throws XPathExpressionException {
		for (int i = 0, j = personNodes.getLength(); i < j; i++) {
			Node node = personNodes.item(i);

			XPathExpression xPathExpression = xPath.compile("firstName");
			Node firstNameNode = (Node) xPathExpression.evaluate(node,
					XPathConstants.NODE);

			xPathExpression = xPath.compile("lastName");
			Node lastNameNode = (Node) xPathExpression.evaluate(node,
					XPathConstants.NODE);

			xPathExpression = xPath.compile("email");
			Node emailNode = (Node) xPathExpression.evaluate(node,
					XPathConstants.NODE);

			String firstName = firstNameNode.getTextContent();
			String lastName = lastNameNode.getTextContent();

			String email;
			if (emailNode != null)
				email = emailNode.getTextContent();
			else
				email = null;

			String name = firstName + " " + lastName;

			StmtIterator stmtIterator = namesModel.listStatements(null,
					FOAF.name, name);
			String personURI = "";
			while (stmtIterator.hasNext()) {
				Resource nameResource = stmtIterator.next().getSubject();
				String personURILocal = nameResource.getURI();

				if (personURILocal.length() > personURI.length())
					personURI = personURILocal;

				String sparql = "SELECT ?person ?name " + "WHERE{<"
						+ nameResource.getURI() + "> <" + FOAF.name
						+ "> ?name . " + "?person <" + FOAF.name + "> ?name . "
						+ "FILTER(?person != <" + nameResource.getURI() + ">)}";
				Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
				QueryExecution queryExecution = QueryExecutionFactory.create(
						query, namesModel);
				ResultSet resultSet = queryExecution.execSelect();

				while (resultSet.hasNext()) {
					QuerySolution querySolution = resultSet.next();
					Resource person = querySolution.getResource("person");
					personURILocal = person.getURI();

					if (personURILocal.length() > personURI.length()) {
						personURI = personURILocal;
					}
				}

			}

			if (personURI.isEmpty()) {
				personURI = Urifier.toURI(this.ns.personNs, name, email,
						homonymsMap);
			} else {
				Map<String, Homonym> homonyms = homonymsMap.get(personURI
						.replace(this.ns.personNs, ""));
				if (homonyms != null) {
					Homonym homonym = homonyms.get(email);
					if (homonym != null) {
						personURI = this.ns.personNs + homonym.getId();
					}
				}
			}
			// System.out.println(name + ": " + personURI);
			try {
				Set<String> emails = new HashSet<String>();
				emails.add(email);
				personMap.addEntity(new Person(firstName, lastName, emails,
						new URI(personURI)));
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/*
			 * xPathExpression = xPath.compile("email"); Node emailNode = (Node)
			 * xPathExpression.evaluate(node, XPathConstants.NODE);
			 * 
			 * System.out.println(firstNameNode.getTextContent() + " " +
			 * lastNameNode.getTextContent() + ": " +
			 * emailNode.getTextContent());
			 */
		}
	}

	private OrganizationMap buildOrganizationMap() {

		if (organizationMap == null) {
			organizationMap = new OrganizationMap();
			/*
			 * Read the RDF model containing all people names.
			 */
			Model namesModel = ModelFactory.createDefaultModel();
			// TODO all-names.rdf should be passed as input
			namesModel.read(
					classLoader.getResourceAsStream("data/all-names.rdf"),
					"http://data.semanticweb.org/person/", "RDF/XML");

			if (conferenceDataDoc != null) {
				XPathFactory xPathFactory = XPathFactory.newInstance();
				XPath xPath = xPathFactory.newXPath();
				try {
					XPathExpression xPathExpression = xPath
							.compile("/snapshot/organizations/organization");

					NodeList nodes = (NodeList) xPathExpression.evaluate(
							conferenceDataDoc, XPathConstants.NODESET);

					for (int i = 0, j = nodes.getLength(); i < j; i++) {
						Node node = nodes.item(i);
						xPathExpression = xPath.compile("name");
						Node labelNode = (Node) xPathExpression.evaluate(node,
								XPathConstants.NODE);

						String label = labelNode.getTextContent();

						addOrganizationToMap(answersMap, namesModel, label);
					}
				} catch (XPathExpressionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (conferenceConfigDoc != null) {
				XPathFactory xPathFactory = XPathFactory.newInstance();
				XPath xPath = xPathFactory.newXPath();
				try {
					XPathExpression xPathExpression = xPath
							.compile("/conferenceData/chairs/chair/heldBy/affiliation");

					NodeList nodes = (NodeList) xPathExpression.evaluate(
							conferenceConfigDoc, XPathConstants.NODESET);

					for (int i = 0, j = nodes.getLength(); i < j; i++) {
						Node node = nodes.item(i);
						String label = node.getTextContent();

						addOrganizationToMap(answersMap, namesModel, label);
					}
				} catch (XPathExpressionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return organizationMap;
	}

	private void addOrganizationToMap(Map<String, Boolean> answersMap,
			Model namesModel, String label) throws URISyntaxException {

		/*
		 * Check if the last part of the organization name contains a country
		 * name.
		 */
		String[] labelParts = label.split(",");
		String labelTmp = label;
		int j;
		boolean stop = false;

		for (j = labelParts.length - 1; j > 0 && !stop; j--) {
			String possibleCountryName = labelParts[j].trim().toLowerCase();

			// possibleCountryName =
			// possibleCountryName.replaceAll("([\\\\\\.\\[\\{\\(\\*\\+\\?\\^\\$\\|])",
			// "\\\\$1");
			possibleCountryName = possibleCountryName.replaceAll("\"", "\\\"");

			Boolean exists = answersMap.get(possibleCountryName);

			if (exists == null) {
				String sparql = "ask{?entity rdfs:label ?name . ?name bif:contains \"'^"
						+ possibleCountryName
						+ "$'\" . ?entity a dbpedia-owl:Place }";
				try {

					sparql = URLEncoder.encode(sparql, "UTF-8");

					URLConnection connection = new URL(
							"http://dbpedia.org/sparql?query=" + sparql)
							.openConnection();
					InputStream inputStream = connection.getInputStream();

					InputStreamReader isr = new InputStreamReader(inputStream);
					BufferedReader reader = new BufferedReader(isr);

					String response = "";
					String line = null;
					while ((line = reader.readLine()) != null) {
						response += line;
					}

					exists = Boolean.valueOf(response).booleanValue();

					answersMap.put(possibleCountryName, exists);
				} catch (UnsupportedEncodingException e) {
					exists = false;
				} catch (MalformedURLException e) {
					exists = false;
				} catch (IOException e) {
					exists = false;
				}

				if (!exists) {
					stop = true;
					j += 1;
				}
			}
		}

		StringBuilder sb = new StringBuilder();
		for (int z = 0; z <= j; z++) {
			if (z > 0)
				sb.append(",");
			sb.append(labelParts[z]);
		}
		label = sb.toString();

		Set<String> labels = new HashSet<String>();
		labels.add(label);
		labels.add(labelTmp);

		StmtIterator stmtIterator = namesModel.listStatements(null, FOAF.name,
				label);
		String organizationURI = "";
		while (stmtIterator.hasNext()) {
			Resource organizationResource = stmtIterator.next().getSubject();
			String organizationURILocal = organizationResource.getURI();

			if (organizationURILocal.length() > organizationURI.length())
				organizationURI = organizationURILocal;

			String sparql = "SELECT ?organization ?name " + "WHERE{<"
					+ organizationResource.getURI() + "> <" + FOAF.name
					+ "> ?name . " + "?person <" + FOAF.name + "> ?name . "
					+ "FILTER(?organization != <"
					+ organizationResource.getURI() + ">)}";
			Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
			QueryExecution queryExecution = QueryExecutionFactory.create(query,
					namesModel);
			ResultSet resultSet = queryExecution.execSelect();

			while (resultSet.hasNext()) {
				QuerySolution querySolution = resultSet.next();
				Resource organization = querySolution
						.getResource("organization");
				organizationURILocal = organization.getURI();
				Literal organizationNameLiteral = querySolution
						.getLiteral("name");
				String organizationName = organizationNameLiteral
						.getLexicalForm();

				if (organizationURILocal.length() > organizationURI.length())
					organizationURI = organizationURILocal;

				labels.add(organizationName);
			}

		}

		if (organizationURI.isEmpty()) {
			organizationURI = Urifier.toURI(ns.organizationNs, label, null,
					null);
		}

		organizationMap.addEntity(new Organization(label, new URI(
				organizationURI), labels));

	}

	private ClassLoader getClassLoader() {
		if (classLoader == null)
			classLoader = Thread.currentThread().getContextClassLoader();
		return classLoader;
	}

	public Model generateArticlesRDFModel() {

		Model model = ModelFactory.createDefaultModel();

		InputStream xsltStream = classLoader.getResourceAsStream(xsltFolder
				+ "/articles.xslt");
		TransformerFactory tFactory = TransformerFactory.newInstance();

		try {

			System.out.println("... " + xsltFolder);
			Transformer transformer = tFactory.newTransformer(new StreamSource(
					xsltStream));
			transformer.setParameter("conferenceLabel",
					OfficialNameSpace.conferenceLabel());
			transformer.setParameter("baseConference", ns.baseConference);
			transformer.setParameter("conferenceLongName",
					ns.conferenceLongName);
			transformer.setParameter("conferenceYear", ns.year);
			transformer.setParameter("conferenceMonth", ns.year);
			transformer.setParameter("location", ns.location);
			transformer.setParameter("dtstart", ns.dtstart);
			transformer.setParameter("dtend", ns.dtend);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			// transformer.transform(new DOMSource(conferenceDataDoc), new
			// StreamResult(new File(out)));
			transformer.transform(new DOMSource(conferenceDataDoc),
					new StreamResult(outputStream));

			InputStream inputStream = new ByteArrayInputStream(
					outputStream.toByteArray());
			outputStream.close();
			model.read(inputStream, null, "RDF/XML");
			inputStream.close();

			addAuthorsInfo(model);

			addKeynotes(xsltFolder + "/keynotes.xslt", model);
			addProgramCommitteeMembers(model);
			addChairs(xsltFolder + "/keynotes.xslt", model);

			/*
			 * OutputStream outputStream = new FileOutputStream(out);
			 * model.write(outputStream); outputStream.close();
			 */
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO this should be configurable
		/*
		 * We add the data about Poster and Demo.
		 */
		URL posterDemoModelURL = classLoader
				.getResource("data/eswc2015_poster_demo.rdf");
		if (posterDemoModelURL != null) {
			String posterDemoModelPathName = posterDemoModelURL.getFile();
			Model posterDemoModel = FileManager.get().loadModel(
					posterDemoModelPathName);
			PosterDemoHandler posterDemoHandler = new PosterDemoHandler(
					posterDemoModel);
			posterDemoHandler.mergeData(model);
		}

		// TODO this should be configurable

		/*
		 * We add the data about PhD symposium
		 */
		URL phDSympModelURL = classLoader.getResource("data/eswc2015_phd.rdf");
		if (phDSympModelURL != null) {
			String phDSympModelPathName = phDSympModelURL.getFile();
			Model phDSympModel = FileManager.get().loadModel(
					phDSympModelPathName);
			PhDSympHandler phDSympHandler = new PhDSympHandler(phDSympModel);
			phDSympHandler.mergeData(model);
		}

		// TODO this should be configurable

		/*
		 * We add the data about challenges
		 */
		URL challengeModelURL = classLoader
				.getResource("data/eswc2015_challenge.rdf");
		if (challengeModelURL != null) {
			String challengeModelPathName = challengeModelURL.getFile();
			Model challengeModel = FileManager.get().loadModel(
					challengeModelPathName);
			ChallengeHandler challHandler = new ChallengeHandler(challengeModel);
			challHandler.mergeData(model);
		}

		return model;
	}

	public Model convertToSemanticWebDogFood(Model model) {

		/*
		 * Remove all the statements having as porperty one of the following
		 * predicate: * foaf:depiction; * dbpedia-owl:thumbnail; * foaf-mbox.
		 */

		Property dbpediaThumbnail = model
				.createProperty("http://dbpedia.org/ontology/thumbnail");
		Property swrcTmpId = model
				.createProperty("http://swrc.ontoware.org/ontology#id");
		Resource inUsePaperType = model
				.createResource("http://purl.org/spar/fabio/InUsePaper");
		Resource researchPaperType = model
				.createResource("http://purl.org/spar/fabio/ResearchPaper");
		Resource demoPaperType = model
				.createResource("http://purl.org/spar/fabio/DemoPaper");
		Resource posterPaperType = model
				.createResource("http://purl.org/spar/fabio/PosterPaper");
		model.removeAll(null, FOAF.depiction, (RDFNode) null);
		model.removeAll(null, FOAF.mbox, (RDFNode) null);
		model.removeAll(null, dbpediaThumbnail, (RDFNode) null);
		model.removeAll(null, swrcTmpId, (RDFNode) null);
		model.removeAll(null, RDF.type, inUsePaperType);
		model.removeAll(null, RDF.type, researchPaperType);

		return model;
	}

	public void alignFormData(Model model) {
		URL formDataLocation = classLoader.getResource("data/form-data");
		if (formDataLocation != null) {
			String formDataLocationAsFilePath = formDataLocation.getFile();
			File formDataDir = new File(formDataLocationAsFilePath);

			FileFilter dirFilter = new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory() ? true : false;
				}
			};

			FileFilter rdfFilter = new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(".rdf");
				}
			};

			Property holdsRole = ModelFactory.createDefaultModel()
					.createProperty(OfficialNameSpace.HOLDS_ROLE_PROP);
			for (File subDir : formDataDir.listFiles(dirFilter)) {

				for (File rdf : subDir.listFiles(rdfFilter)) {

					System.out.println("RDF form model " + rdf.getName());
					InputStream inputStream;
					try {
						inputStream = new FileInputStream(rdf);
						Model formModel = ModelFactory.createDefaultModel();
						formModel.read(inputStream, null, "RDF/XML");

						ResIterator people = formModel
								.listResourcesWithProperty(RDF.type,
										FOAF.Person);

						while (people.hasNext()) {
							Resource person = people.next();

							// Add core information
							Statement statement = person.getProperty(RDF.type);
							model.add(statement);
							statement = person.getProperty(holdsRole);
							model.add(statement);

							// Update name and label
							statement = person.getProperty(FOAF.name);
							model.removeAll(person, FOAF.name, null);
							model.add(statement);
							model.removeAll(person, RDFS.label, null);
							model.add(person, RDFS.label, statement.getObject());

							// Update first name
							statement = person.getProperty(FOAF.firstName);
							model.removeAll(person, FOAF.firstName, null);
							model.add(statement);

							// Update last name
							Property foafLastName = model
									.createProperty(OfficialNameSpace.FOAF_LAST_NAME);
							statement = person.getProperty(foafLastName);
							model.removeAll(person, foafLastName, null);
							model.add(statement);

							// Update homepage
							statement = person.getProperty(FOAF.homepage);
							if (statement != null) {
								model.removeAll(person, FOAF.homepage, null);
								model.add(statement);
							}

							// Update mbox
							statement = person.getProperty(FOAF.mbox);
							Resource mbox = (Resource) statement.getObject();
							model.removeAll(person, FOAF.mbox, null);
							model.add(statement);
							// Update mbox sha1sum
							model.removeAll(person, FOAF.mbox_sha1sum, null);
							model.add(person, FOAF.mbox_sha1sum,
									DigestUtils.sha1Hex(mbox.getURI()));

							// Add depiction
							statement = person.getProperty(FOAF.depiction);
							if (statement != null) {
								Resource depiction = (Resource) statement
										.getObject();
								String depictionURI = depiction.getURI()
										.replace("/images/", "/imgresized/");
								if (depictionURI.endsWith(".tiff")
										|| depictionURI.endsWith(".TIFF")) {
									int index = depictionURI.lastIndexOf(".");
									depictionURI = depictionURI.substring(0,
											index) + ".jpg";
								} else if (!depictionURI.endsWith(".jpg")
										&& !depictionURI.endsWith(".JPG")
										&& !depictionURI.endsWith(".jpeg")
										&& !depictionURI.endsWith(".JPEG"))
									depictionURI += ".jpg";
								model.add(statement.getSubject(),
										statement.getPredicate(),
										model.createResource(depictionURI));
							}

							// Add Twitter account
							Property foafAccount = model
									.createProperty(OfficialNameSpace.FOAF_ACCOUNT);
							statement = person.getProperty(foafAccount);
							if (statement != null) {
								model.add(statement);
								Resource account = (Resource) statement
										.getObject();
								model.add(account.listProperties());
							}

							// Add paper image
							Resource inProceedings = model
									.createResource("http://swrc.ontoware.org/ontology#InProceedings");
							ResIterator papers = formModel
									.listResourcesWithProperty(RDF.type,
											inProceedings);
							if (papers.hasNext()) {
								Resource paper = papers.next();
								Property dbpediaThumbnail = model
										.createProperty("http://dbpedia.org/ontology/thumbnail");
								statement = paper.getProperty(dbpediaThumbnail);

								if (statement != null) {
									Resource thumbnail = (Resource) statement
											.getObject();
									String thumbnailURI = thumbnail
											.getURI()
											.replace("/images/", "/imgresized/");

									if (thumbnailURI.endsWith(".tiff")
											|| thumbnailURI.endsWith(".TIFF")) {
										int index = thumbnailURI
												.lastIndexOf(".");
										thumbnailURI = thumbnailURI.substring(
												0, index) + ".jpg";
									} else if (!thumbnailURI.endsWith(".jpg")
											&& !thumbnailURI.endsWith(".JPG")
											&& !thumbnailURI.endsWith(".jpeg")
											&& !thumbnailURI.endsWith(".JPEG"))
										thumbnailURI += ".jpg";
									model.add(statement.getSubject(),
											statement.getPredicate(),
											model.createResource(thumbnailURI));
								}
							}

						}

					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}
	}

	private void addAuthorsInfo(Model model) {

		if (conferenceDataDoc != null) {
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xPath = xPathFactory.newXPath();

			StmtIterator papersIt = model
					.listStatements(
							null,
							RDF.type,
							model.createResource("http://swrc.ontoware.org/ontology#InProceedings"));

			Resource authorTypeRole = model
					.createResource("http://www.ontologydesignpatterns.org/ont/eswc/ontology.owl#Author");
			Resource authorRole = model.createResource(ns.baseConference
					+ "author", authorTypeRole);
			authorRole.addProperty(RDFS.label, "Paper author");
			authorRole
					.addProperty(
							model.createProperty("http://data.semanticweb.org/ns/swc/ontology#isRoleAt"),
							model.createResource(ns.baseConference));

			while (papersIt.hasNext()) {
				Resource paper = papersIt.next().getSubject();

				String paperNs = null;
				String paperID = paper.getURI();
				if (paperID.startsWith(this.ns.mainTrackPaperNs)) {
					paperID = paperID.replace(this.ns.mainTrackPaperNs, "");
					paperNs = this.ns.mainTrackPaperNs;
				} else {
					paperID = paperID.replace(this.ns.inusePaperNs, "");
					paperNs = this.ns.inusePaperNs;
				}

				XPathExpression xPathExpression;
				try {
					xPathExpression = xPath
							.compile("/snapshot/submissions/submission/number[text()='"
									+ paperID + "']/../authors/author");
					NodeList nodes = (NodeList) xPathExpression.evaluate(
							conferenceDataDoc, XPathConstants.NODESET);

					Bag authorList = model.createBag(paperNs + paperID
							+ "/authorList");

					for (int i = 0, j = nodes.getLength(); i < j; i++) {
						Node node = nodes.item(i);
						xPathExpression = xPath.compile("firstName");
						Node firstNameNode = (Node) xPathExpression.evaluate(
								node, XPathConstants.NODE);

						xPathExpression = xPath.compile("lastName");
						Node lastNameNode = (Node) xPathExpression.evaluate(
								node, XPathConstants.NODE);

						xPathExpression = xPath.compile("email");
						Node emailNode = (Node) xPathExpression.evaluate(node,
								XPathConstants.NODE);

						xPathExpression = xPath.compile("webSite");
						Node webSiteNode = (Node) xPathExpression.evaluate(
								node, XPathConstants.NODE);

						xPathExpression = xPath.compile("../../number");
						Node submissionNumberNode = (Node) xPathExpression
								.evaluate(node, XPathConstants.NODE);

						xPathExpression = xPath.compile("organizationId");
						Node organizationIdNode = (Node) xPathExpression
								.evaluate(node, XPathConstants.NODE);

						Organization organization = null;

						if (organizationIdNode != null) {
							xPathExpression = xPath
									.compile("/snapshot/organizations/organization/id[text()='"
											+ organizationIdNode
													.getTextContent()
											+ "']/../name");
							Node organizationNode = (Node) xPathExpression
									.evaluate(conferenceDataDoc,
											XPathConstants.NODE);
							if (organizationNode != null) {
								String organizationName = organizationNode
										.getTextContent();
								if (organizationName != null
										&& !organizationName.isEmpty())
									organization = organizationMap
											.getEntity(organizationName);
							}
						}

						String articleID = paperNs
								+ submissionNumberNode.getTextContent();

						String firstName = firstNameNode.getTextContent();
						String lastName = lastNameNode.getTextContent();

						String email;
						if (emailNode != null)
							email = emailNode.getTextContent();
						else
							email = null;

						String webSite;
						if (webSiteNode != null)
							webSite = webSiteNode.getTextContent();
						else
							webSite = null;

						String name = firstName + " " + lastName;

						Person person;
						try {
							person = personMap.getEntityByURI(new URI(Urifier
									.toURI(this.ns.personNs, name, email,
											homonymsMap)));

						} catch (URISyntaxException e) {
							person = null;
							;
						}
						if (person == null) {
							person = personMap.getEntity(name);
						}

						Resource articleResource = model.getResource(articleID);
						Resource personResource = model.createResource(person
								.getURI().toString(), FOAF.Person);
						personResource.addProperty(FOAF.made, articleResource);
						personResource
								.addProperty(RDFS.label, person.getName());
						personResource.addProperty(FOAF.name, person.getName());
						personResource.addProperty(FOAF.firstName,
								person.getFirstName());

						if (webSite != null && !webSite.isEmpty())
							personResource.addProperty(FOAF.homepage,
									model.createResource(webSite));
						if (email != null && !email.isEmpty()) {
							email = email.toLowerCase();
							personResource.addProperty(FOAF.mbox,
									model.createResource("mailto:" + email));
							personResource.addProperty(FOAF.mbox_sha1sum,
									DigestUtils.sha1Hex("mailto:" + email));
						}
						personResource
								.addProperty(
										model.createProperty("http://xmlns.com/foaf/0.1/lastName"),
										person.getLastName());
						personResource
								.addProperty(
										model.createProperty("http://data.semanticweb.org/ns/swc/ontology#holdsRole"),
										authorRole);
						authorRole
								.addProperty(
										model.createProperty("http://data.semanticweb.org/ns/swc/ontology#heldBy"),
										personResource);

						if (organization != null) {

							Resource affiliationResource = model
									.createResource(organization.getURI()
											.toString(), FOAF.Organization);
							personResource
									.addProperty(
											model.createProperty("http://swrc.ontoware.org/ontology#affiliation"),
											affiliationResource);

							affiliationResource.addProperty(RDFS.label,
									organization.getLabel());
							affiliationResource.addProperty(FOAF.name,
									organization.getLabel());
							affiliationResource.addProperty(FOAF.member,
									personResource);

						}

						articleResource.addProperty(DC_11.creator,
								personResource);
						articleResource.addProperty(FOAF.maker, personResource);
						authorList.add(personResource);

					}
				} catch (XPathExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	private void addKeynotes(String xslt, Model model) {
		if (conferenceConfigDoc != null) {

			Resource presenter = model
					.createResource("http://data.semanticweb.org/ns/swc/ontology#Presenter");
			Resource keynoteSpeakerRole = model.createResource(
					ns.baseConference + "keynote-speaker", presenter);
			keynoteSpeakerRole.addProperty(RDFS.label, "Keynote speaker");
			keynoteSpeakerRole
					.addProperty(
							model.createProperty("http://data.semanticweb.org/ns/swc/ontology#isRoleAt"),
							model.createResource(ns.baseConference));

			TransformerFactory tFactory = TransformerFactory.newInstance();
			try {

				Transformer transformer = tFactory
						.newTransformer(new StreamSource(classLoader
								.getResourceAsStream(xslt)));

				StringWriter writer = new StringWriter();
				transformer.transform(new DOMSource(conferenceConfigDoc),
						new StreamResult(writer));
				String rdf = writer.toString();

				ByteArrayInputStream inputStream = new ByteArrayInputStream(
						rdf.getBytes());
				model.read(inputStream, "", "RDF/XML");

				XPathFactory xPathFactory = XPathFactory.newInstance();
				XPath xPath = xPathFactory.newXPath();
				XPathExpression xPathExpression = xPath
						.compile("/conferenceData/invitedTalks/invitedTalk/speaker");
				NodeList nodes = (NodeList) xPathExpression.evaluate(
						conferenceConfigDoc, XPathConstants.NODESET);

				for (int i = 0, j = nodes.getLength(); i < j; i++) {
					Node node = nodes.item(i);

					xPathExpression = xPath.compile("../uriID");
					Node keynoteIdNode = (Node) xPathExpression.evaluate(node,
							XPathConstants.NODE);
					String keynoteId = keynoteIdNode.getTextContent();

					xPathExpression = xPath.compile("firstName");
					Node firstNameNode = (Node) xPathExpression.evaluate(node,
							XPathConstants.NODE);
					String firstName = firstNameNode.getTextContent();

					xPathExpression = xPath.compile("lastName");
					Node lastNameNode = (Node) xPathExpression.evaluate(node,
							XPathConstants.NODE);
					String lastName = lastNameNode.getTextContent();

					xPathExpression = xPath.compile("email");
					Node emailNode = (Node) xPathExpression.evaluate(node,
							XPathConstants.NODE);

					String email;
					if (emailNode != null)
						email = emailNode.getTextContent();
					else
						email = null;

					String personUri = null;

					Person person;
					try {
						person = personMap.getEntityByURI(new URI(Urifier
								.toURI(this.ns.personNs, firstName + " "
										+ lastName, email, homonymsMap)));
					} catch (URISyntaxException e) {
						person = null;
					}
					if (person != null) {
						personUri = person.getURI().toString();
					} else {
						personUri = Urifier.toURI(this.ns.personNs, firstName
								+ " " + lastName, email, homonymsMap);
					}

					if (personUri != null) {

						Resource keynoteSpeaker = model.createResource(
								personUri, FOAF.Person);
						Resource keynote = model
								.createResource(this.ns.keynoteNs + keynoteId);

						keynoteSpeaker.addProperty(FOAF.made, keynote);
						keynote.addProperty(FOAF.maker, keynoteSpeaker);

						keynoteSpeaker.addProperty(FOAF.name, firstName + " "
								+ lastName);
						keynoteSpeaker.addProperty(RDFS.label, firstName + " "
								+ lastName);
						keynoteSpeaker.addProperty(FOAF.firstName, firstName);
						keynoteSpeaker
								.addProperty(
										model.createProperty("http://xmlns.com/foaf/0.1/lastName"),
										lastName);

						if (email != null && !email.isEmpty()) {
							email = email.toLowerCase();
							keynoteSpeaker.addProperty(FOAF.mbox,
									model.createResource("mailto:" + email));
							keynoteSpeaker.addProperty(FOAF.mbox_sha1sum,
									DigestUtils.sha1Hex("mailto:" + email));
						}

						keynoteSpeaker
								.addProperty(
										model.createProperty("http://data.semanticweb.org/ns/swc/ontology#holdsRole"),
										keynoteSpeakerRole);
						keynoteSpeakerRole
								.addProperty(
										model.createProperty("http://data.semanticweb.org/ns/swc/ontology#heldBy"),
										keynoteSpeaker);

					}
				}

			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		String sparql = "PREFIX swc: <http://data.semanticweb.org/ns/swc/ontology#> CONSTRUCT {?event swc:isSuperEventOf ?subevent} WHERE{?subevent swc:isSubEventOf ?event}";
		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				model);
		model.add(queryExecution.execConstruct());
	}

	private void addProgramCommitteeMembers(Model model) {
		if (conferenceDataDoc != null) {

			Resource programCommitteeMemberType = model
					.createResource("http://data.semanticweb.org/ns/swc/ontology#ProgrammeCommitteeMember");
			Resource programCommitteeMemberRole = model.createResource(
					ns.baseConference + "program-committee-member",
					programCommitteeMemberType);
			programCommitteeMemberRole.addProperty(RDFS.label,
					"Program committee member");
			programCommitteeMemberRole
					.addProperty(
							model.createProperty("http://data.semanticweb.org/ns/swc/ontology#isRoleAt"),
							model.createResource(ns.baseConference));

			TransformerFactory tFactory = TransformerFactory.newInstance();
			try {

				XPathFactory xPathFactory = XPathFactory.newInstance();
				XPath xPath = xPathFactory.newXPath();
				XPathExpression xPathExpression = xPath
						.compile("/snapshot/pc/pcMember");
				NodeList nodes = (NodeList) xPathExpression.evaluate(
						conferenceDataDoc, XPathConstants.NODESET);

				for (int i = 0, j = nodes.getLength(); i < j; i++) {
					Node node = nodes.item(i);

					xPathExpression = xPath.compile("firstName");
					Node firstNameNode = (Node) xPathExpression.evaluate(node,
							XPathConstants.NODE);
					String firstName = firstNameNode.getTextContent();

					xPathExpression = xPath.compile("lastName");
					Node lastNameNode = (Node) xPathExpression.evaluate(node,
							XPathConstants.NODE);
					String lastName = lastNameNode.getTextContent();

					xPathExpression = xPath.compile("email");
					Node emailNode = (Node) xPathExpression.evaluate(node,
							XPathConstants.NODE);

					String email;
					if (emailNode != null)
						email = emailNode.getTextContent();
					else
						email = null;

					String personUri = null;

					Person person;
					try {
						person = personMap.getEntityByURI(new URI(Urifier
								.toURI(this.ns.personNs, firstName + " "
										+ lastName, email, homonymsMap)));
					} catch (URISyntaxException e) {
						person = null;
					}
					if (person != null) {
						personUri = person.getURI().toString();
					} else {
						personUri = Urifier.toURI(this.ns.personNs, firstName
								+ " " + lastName, email, homonymsMap);
					}

					xPathExpression = xPath.compile("organizationId");
					Node organizationIdNode = (Node) xPathExpression.evaluate(
							node, XPathConstants.NODE);

					Organization organization = null;

					if (organizationIdNode != null) {
						xPathExpression = xPath
								.compile("/snapshot/organizations/organization/id[text()='"
										+ organizationIdNode.getTextContent()
										+ "']/../name");
						Node organizationNode = (Node) xPathExpression
								.evaluate(conferenceDataDoc,
										XPathConstants.NODE);
						if (organizationNode != null) {
							String organizationName = organizationNode
									.getTextContent();
							if (organizationName != null
									&& !organizationName.isEmpty())
								organization = organizationMap
										.getEntity(organizationName);
						}
					}

					if (personUri != null) {

						Resource programCommitteeMember = model.createResource(
								personUri, FOAF.Person);

						programCommitteeMember.addProperty(FOAF.name, firstName
								+ " " + lastName);
						programCommitteeMember.addProperty(RDFS.label,
								firstName + " " + lastName);
						programCommitteeMember.addProperty(FOAF.firstName,
								firstName);
						programCommitteeMember
								.addProperty(
										model.createProperty("http://xmlns.com/foaf/0.1/lastName"),
										lastName);

						if (email != null && !email.isEmpty()) {
							email = email.toLowerCase();
							programCommitteeMember.addProperty(FOAF.mbox,
									model.createResource("mailto:" + email));
							programCommitteeMember.addProperty(
									FOAF.mbox_sha1sum,
									DigestUtils.sha1Hex("mailto:" + email));
						}

						programCommitteeMember
								.addProperty(
										model.createProperty("http://data.semanticweb.org/ns/swc/ontology#holdsRole"),
										programCommitteeMemberRole);
						programCommitteeMemberRole
								.addProperty(
										model.createProperty("http://data.semanticweb.org/ns/swc/ontology#heldBy"),
										programCommitteeMember);

						if (organization != null) {

							Resource affiliationResource = model
									.createResource(organization.getURI()
											.toString(), FOAF.Organization);
							programCommitteeMember
									.addProperty(
											model.createProperty("http://swrc.ontoware.org/ontology#affiliation"),
											affiliationResource);

							affiliationResource.addProperty(RDFS.label,
									organization.getLabel());
							affiliationResource.addProperty(FOAF.name,
									organization.getLabel());
							affiliationResource.addProperty(FOAF.member,
									programCommitteeMember);

						}

					}
				}

			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void addChairs(String xslt, Model model) {
		if (conferenceConfigDoc != null) {

			TransformerFactory tFactory = TransformerFactory.newInstance();
			try {

				Transformer transformer = tFactory
						.newTransformer(new StreamSource(classLoader
								.getResourceAsStream(xslt)));

				StringWriter writer = new StringWriter();
				transformer.transform(new DOMSource(conferenceConfigDoc),
						new StreamResult(writer));
				String rdf = writer.toString();

				ByteArrayInputStream inputStream = new ByteArrayInputStream(
						rdf.getBytes());
				model.read(inputStream, "", "RDF/XML");

				XPathFactory xPathFactory = XPathFactory.newInstance();
				XPath xPath = xPathFactory.newXPath();
				XPathExpression xPathExpression = xPath
						.compile("/conferenceData/chairs/chair");
				NodeList nodes = (NodeList) xPathExpression.evaluate(
						conferenceConfigDoc, XPathConstants.NODESET);

				Resource chairType = model
						.createResource("http://data.semanticweb.org/ns/swc/ontology#Chair");
				Resource conference = model
						.createResource(this.ns.baseConference);
				Property hasRole = model
						.createProperty("http://data.semanticweb.org/ns/swc/ontology#hasRole");
				Property isRoleAt = model
						.createProperty("http://data.semanticweb.org/ns/swc/ontology#isRoleAt");

				Property holdsRole = model
						.createProperty("http://data.semanticweb.org/ns/swc/ontology#holdsRole");
				Property heldBy = model
						.createProperty("http://data.semanticweb.org/ns/swc/ontology#heldBy");
				Property affiliation = model
						.createProperty("http://swrc.ontoware.org/ontology#affiliation");

				Property lastName = model
						.createProperty("http://xmlns.com/foaf/0.1/lastName");

				for (int i = 0, j = nodes.getLength(); i < j; i++) {
					Node node = nodes.item(i);

					Node roleNode = node.getAttributes().getNamedItem("name");

					String role = roleNode.getTextContent();
					role = Urifier.toURI(this.ns.baseConference, role, null,
							null);

					Resource roleResource = model.createResource(role,
							chairType);
					roleResource.addProperty(RDFS.label,
							roleNode.getTextContent());

					conference.addProperty(hasRole, roleResource);
					roleResource.addProperty(isRoleAt, conference);

					xPathExpression = xPath.compile("heldBy");
					NodeList persons = (NodeList) xPathExpression.evaluate(
							node, XPathConstants.NODESET);
					for (int k = 0, z = persons.getLength(); k < z; k++) {
						Node personNode = persons.item(k);

						xPathExpression = xPath.compile("firstName");
						Node firstNameNode = (Node) xPathExpression.evaluate(
								personNode, XPathConstants.NODE);

						xPathExpression = xPath.compile("lastName");
						Node lastNameNode = (Node) xPathExpression.evaluate(
								personNode, XPathConstants.NODE);

						xPathExpression = xPath.compile("email");
						Node emailNode = (Node) xPathExpression.evaluate(
								personNode, XPathConstants.NODE);

						xPathExpression = xPath.compile("affiliation");
						Node organizationNode = (Node) xPathExpression
								.evaluate(personNode, XPathConstants.NODE);

						String name = firstNameNode.getTextContent() + " "
								+ lastNameNode.getTextContent();

						String email;
						if (emailNode != null)
							email = emailNode.getTextContent();
						else
							email = null;

						Person person;
						try {
							person = personMap.getEntityByURI(new URI(Urifier
									.toURI(this.ns.personNs, name, email,
											homonymsMap)));
						} catch (URISyntaxException e) {
							person = null;
						}
						if (person != null) {
							Resource personResource = model.createResource(
									person.getURI().toString(), FOAF.Person);
							personResource.addProperty(holdsRole, roleResource);
							roleResource.addProperty(heldBy, personResource);

							personResource.addProperty(RDFS.label,
									person.getName());
							personResource.addProperty(FOAF.name,
									person.getName());
							personResource.addProperty(FOAF.firstName,
									person.getFirstName());
							personResource.addProperty(lastName,
									person.getLastName());
							if (email != null && !email.isEmpty()) {
								email = email.toLowerCase();
								email = email.toLowerCase();
								personResource
										.addProperty(
												FOAF.mbox,
												model.createResource("mailto:"
														+ email));
								personResource.addProperty(FOAF.mbox_sha1sum,
										DigestUtils.sha1Hex("mailto:" + email));
							}

							/*
							 * Add affiliation
							 */
							if (organizationNode != null) {
								String organizationName = organizationNode
										.getTextContent();
								Organization organization = organizationMap
										.getEntity(organizationName);
								if (organization != null) {
									Resource organizationResource = model
											.createResource(organization
													.getURI().toString(),
													FOAF.Organization);
									personResource.addProperty(affiliation,
											organizationResource);

									organizationResource
											.addProperty(RDFS.label,
													organization.getLabel());
									organizationResource.addProperty(FOAF.name,
											organization.getLabel());
									organizationResource.addProperty(
											FOAF.member, personResource);
								}
							}
						}
					}
				}

			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// TODO implement XSLT transormation for easychair dump

	public static void main(String[] args) {

		FileManager
				.get()
				.loadModel(
						"/Users/andrea/Documents/software/conference-live-eswc2015/data/src/main/resources/data/form-data/pmc/harald-sack.rdf")
				.write(System.out);

	}

}
