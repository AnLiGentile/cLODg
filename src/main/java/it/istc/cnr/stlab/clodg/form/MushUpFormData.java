package it.istc.cnr.stlab.clodg.form;

import it.istc.cnr.stlab.clodg.util.KnownPerson;
import it.istc.cnr.stlab.clodg.util.OfficialNameSpace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDFS;

public class MushUpFormData implements FormData {

	public OfficialNameSpace ns;

	public MushUpFormData(OfficialNameSpace ns2) {
	    super();
	    this.ns = ns2;
	}

	public Model toRDF(CSVReader csvReader, String wsName) {
		Model model = ModelFactory.createDefaultModel();
		Resource wsPaper = model
				.createResource("http://purl.org/spar/fabio/wsPaper/" + wsName);

		// TODO check if this namespace is elsewhere and modify it
		// String paperNs =
		// "http://data.semanticweb.org/conference/eswc/2014/paper/eswc-2014/";
		String paperNs = ns.mainTrackPaperNs + "mashUp/";
		String personNs = ns.personNs;
		String eswcOntology = ns.eswcOntology;

		Map<String, Person> knownPersons = KnownPerson.knownPersons;

		Property hashtagProperty = model.createProperty(eswcOntology
				+ "hashtag");
		Property titleProperty = model
				.createProperty("http://purl.org/dc/elements/1.1/title");
		try {

			String[] line = null;
			while ((line = csvReader.readNext()) != null) {
				String id = line[0].trim();
				String authorsString = line[1].trim().replace(" and ", ", ");
				String title = "";
				try {
					title = line[2].trim();
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println(" ERROR with " + wsName + " --> "
							+ line[1]);
				}
				// String category = line[3].trim();
				String hashtag = line[3].trim();

				if (authorsString.endsWith("."))
					authorsString = authorsString.substring(0,
							authorsString.length() - 1);

				String[] authors = authorsString.split(", ");

				Property authorlistProperty = model
						.createProperty("http://purl.org/ontology/bibo/authorList");

				Resource paper;
				paper = model.createResource(paperNs + id, wsPaper);

				paper.addLiteral(hashtagProperty, model.createTypedLiteral(
						hashtag, XSDDatatype.XSDstring));
				paper.addLiteral(titleProperty,
						model.createTypedLiteral(title, XSDDatatype.XSDstring));
				paper.addLiteral(RDFS.label,
						model.createTypedLiteral(id, XSDDatatype.XSDlong));

				Resource authorList = model.createResource(paperNs + id
						+ "/authorlist");

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
						firstName = names[0].trim();
						lastName = names[1].trim();
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

					Property p = model
							.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#_"
									+ i);
					i += 1;
					authorList.addProperty(p, person);

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

	@Override
	public Model toRDF(CSVReader csvReader) {
		return null;

	}

	public static void main(String[] args) {
		
		OfficialNameSpace ns = OfficialNameSpace.getInstance();
		
		MushUpFormData formData = new MushUpFormData(ns);

		File f = new File("in/allDataCleaned/semeval.txt");
		File wsOUTFolder = new File("./test");
		wsOUTFolder.mkdirs();

		// if(wsFolder.isDirectory()){
		// for (File f : wsFolder.listFiles()){
		if (f.getName().endsWith(".txt")) {
			String wsName = f.getName().substring(0,
					f.getName().lastIndexOf(".txt"));

			try {
				// CSVReader csvReader = new CSVReader(new FileReader(new
				// File("in/eswc2014_pd.csv")), ';');
				CSVReader csvReader = new CSVReader(new InputStreamReader(
						new FileInputStream(f), "UTF-8"), '\t');
				Model model = formData.toRDF(csvReader, wsName);
				csvReader.close();
				OutputStream out = new FileOutputStream(new File(wsOUTFolder
						+ File.separator + wsName + ".rdf"));
				model.write(out);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// }
		// }
		// else{
		// System.out.println("Indicate the workshop folder, with a CSV file for each workshop");
		// }
	}

}
