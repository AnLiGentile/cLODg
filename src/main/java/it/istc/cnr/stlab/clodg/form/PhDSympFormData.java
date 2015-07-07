package it.istc.cnr.stlab.clodg.form;

import it.istc.cnr.stlab.clodg.util.KnownPerson;
import it.istc.cnr.stlab.clodg.util.OfficialNameSpace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * @author annalisa
 *
 */
public class PhDSympFormData implements FormData {

	// TODO add partOf property in the Graph
	@Override
	public Model toRDF(CSVReader csvReader) {
		Model model = ModelFactory.createDefaultModel();

		model.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");

		model.setNsPrefix("cnr", "http://data.cnr.it/functions/");

		model.setNsPrefix("dcterms", "http://purl.org/dc/terms/creator");
		model.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
		model.setNsPrefix("odp",
				"http://www.ontologydesignpatterns.org/ont/eswc/ontology.owl#");
		model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		model.setNsPrefix("fn", "http://www.w3.org/2005/xpath-functions");
		model.setNsPrefix("bibo", "http://purl.org/ontology/bibo/");
		model.setNsPrefix("swc", "http://data.semanticweb.org/ns/swc/ontology#");
		model.setNsPrefix("frbr", "http://purl.org/vocab/frbr/core#");
		model.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
		model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		model.setNsPrefix("icaltzd", "http://www.w3.org/2002/12/cal/icaltzd#");
		model.setNsPrefix("swrc", "http://swrc.ontoware.org/ontology#");
		model.setNsPrefix("fabio", "http://purl.org/spar/fabio/");

		Resource phdPaper = model
				.createResource("http://purl.org/spar/fabio/PhDSymposiumPaper");

		String phdNs = OfficialNameSpace.phdPaperNs;

		String personNs = OfficialNameSpace.personNs;
		String eswcOntology = OfficialNameSpace.eswcOntology;

		Map<String, Person> knownPersons = KnownPerson.knownPersons;

		Property hashtagProperty = model.createProperty(eswcOntology
				+ "hashtag");
		Property titleProperty = model
				.createProperty("http://purl.org/dc/elements/1.1/title");
		Property abstractProperty = model
				.createProperty("http://swrc.ontoware.org/ontology#abstract");
		try {
			String[] line = null;
			while ((line = csvReader.readNext()) != null) {
				String id = line[0].trim();
				String authorsString = line[1].trim().replace(" and ", ", ");
				String title = line[2].trim();
				String category = line[3].trim();
				String hashtag = line[4].trim();
				String abs = "";
				try {
					abs = line[5].trim();
				} catch (Exception e) {
					System.err.println("abstract not present for paper "
							+ title);
				}

				String[] authors = authorsString.split(", ");

				Property authorlistProperty = model
						.createProperty("http://purl.org/ontology/bibo/authorList");
				Property foafMakerProperty = model
						.createProperty("http://xmlns.com/foaf/0.1/maker");
				Property foafMadeProperty = model
						.createProperty("http://xmlns.com/foaf/0.1/made");
				Property dcCreatorProperty = model
						.createProperty("http://purl.org/dc/elements/1.1/creator");
				Property swcHoldsRoleProperty = model
						.createProperty("http://data.semanticweb.org/ns/swc/ontology#holdsRole");

				Resource paperAuthorRole = model
						.createResource("http://data.semanticweb.org/conference/eswc/2015/author");

				Resource paper;
				Resource authorList;

				paper = model.createResource(phdNs + id, phdPaper);
				authorList = model.createResource(phdNs + id + "/authorlist");

				paper.addLiteral(hashtagProperty, model.createTypedLiteral(
						hashtag, XSDDatatype.XSDstring));
				paper.addLiteral(titleProperty,
						model.createTypedLiteral(title, XSDDatatype.XSDstring));
				if (!abs.equals(""))
					paper.addLiteral(abstractProperty, model
							.createTypedLiteral(abs, XSDDatatype.XSDstring));
				paper.addLiteral(RDFS.label,
						model.createTypedLiteral(id, XSDDatatype.XSDlong));

				paper.addProperty(authorlistProperty, authorList);

				Property lastNameProperty = model.createProperty(FOAF.NS
						+ "lastName");

				int i = 1;
				for (String author : authors) {
					Person knownAuthor = knownPersons.get(author);

					String firstName = null;
					String lastName = null;
					if (knownAuthor != null) {
						firstName = knownAuthor.getFirstname();
						lastName = knownAuthor.getLastname();
					} else {
						String[] names = author.split(" ");
						for (int j = 0; j < names.length; j++) {
							names[j] = names[j].trim();
							if (names[j].endsWith("."))
								names[j] = names[j].substring(0,
										names[j].length() - 1);
						}

						if (names.length == 2) {
							firstName = names[0].trim();
							lastName = names[1].trim();
						} else if (names.length == 3) {
							firstName = names[0].trim() + " " + names[1].trim();
							lastName = names[2].trim();
						} else if (names.length == 4) {
							firstName = names[0].trim() + " " + names[1].trim();
							lastName = names[2].trim() + " " + names[3].trim();
						} else if (names.length > 4) {
							firstName = names[0].trim() + " " + names[1].trim();
							for (int j = 0; j < names.length; j++) {
								lastName = lastName + " " + names[j].trim();
								lastName = lastName.trim();
							}
						}
					}
					String personId = StringUtils
							.stripAccents(firstName + "-" + lastName)
							.toLowerCase().replace(" ", "-");
					Resource person = model.createResource(personNs + personId,
							FOAF.Person);
					person.addLiteral(FOAF.name, model.createTypedLiteral(
							firstName + " " + lastName, XSDDatatype.XSDstring));
					person.addLiteral(FOAF.firstName, model.createTypedLiteral(
							firstName, XSDDatatype.XSDstring));
					person.addLiteral(lastNameProperty,
							model.createTypedLiteral(lastName,
									XSDDatatype.XSDstring));
					person.addProperty(foafMadeProperty, paper);
					person.addProperty(swcHoldsRoleProperty, paperAuthorRole);

					Property p = model
							.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#_"
									+ i);
					i += 1;
					authorList.addProperty(p, person);
					paper.addProperty(foafMakerProperty, person);
					paper.addProperty(dcCreatorProperty, person);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return model;

	}

	public static void main(String[] args) {
		PhDSympFormData formData = new PhDSympFormData();
		try {
			// CSVReader csvReader = new CSVReader(new FileReader(new
			// File("in/eswc2014_pd.csv")), ';');
			CSVReader csvReader = new CSVReader(new InputStreamReader(
					new FileInputStream(
							"src/main/resources/data/eswc2015_phd.csv"),
					Charset.forName("UTF-16")), '\t');

			Model model = formData.toRDF(csvReader);
			csvReader.close();
			OutputStream out = new FileOutputStream(new File(
					"src/main/resources/data/eswc2015_phd.rdf"));
			model.write(out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
