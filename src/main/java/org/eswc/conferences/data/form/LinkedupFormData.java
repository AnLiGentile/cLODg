package org.eswc.conferences.data.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eswc.conferences.data.util.KnownPerson;
import org.eswc.conferences.data.util.OfficialNameSpace;

import au.com.bytecode.opencsv.CSVReader;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class LinkedupFormData implements FormData {

	public Model toRDF(CSVReader csvReader, String wsName) {
		Model model = ModelFactory.createDefaultModel();
		Resource wsPaper = model
				.createResource("http://purl.org/spar/fabio/wsPaper/" + wsName);

		// TODO check if this namespace is elsewhere and modify it
		// String paperNs =
		// "http://data.semanticweb.org/conference/eswc/2014/paper/eswc-2014/";
		String paperNs = OfficialNameSpace.mainTrackPaperNs + "linkedUp/";
		String personNs = OfficialNameSpace.personNs;
		String eswcOntology = OfficialNameSpace.eswcOntology;

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
		LinkedupFormData formData = new LinkedupFormData();

		File f = new File("in/allDataCleaned/eswc_linkedUp.txt");
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
