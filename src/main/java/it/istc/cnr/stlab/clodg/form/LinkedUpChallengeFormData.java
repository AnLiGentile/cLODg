package it.istc.cnr.stlab.clodg.form;

import it.istc.cnr.stlab.clodg.util.KnownPerson;
import it.istc.cnr.stlab.clodg.util.OfficialNameSpace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
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

public class LinkedUpChallengeFormData implements FormData {

	public OfficialNameSpace ns;

	public LinkedUpChallengeFormData(OfficialNameSpace ns2) {
super();
this.ns = ns2;}

	@Override
	public Model toRDF(CSVReader csvReader) {
		Model model = ModelFactory.createDefaultModel();
		Resource inusePaper = model
				.createResource("http://purl.org/spar/fabio/LinkedUpPaper");

		String paperNs = ns.mainTrackPaperNs + "linkedUp/";
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
				String title = line[2].trim();
				String hashtag = line[3].trim();

				String[] authors = authorsString.split(", ");

				Property authorlistProperty = model
						.createProperty("http://purl.org/ontology/bibo/authorList");

				Resource paper;
				paper = model.createResource(paperNs + id, inusePaper);
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

	public static void main(String[] args) {
		
	    OfficialNameSpace ns = OfficialNameSpace.getInstance();
		LinkedUpChallengeFormData formData = new LinkedUpChallengeFormData(ns);
		try {
			CSVReader csvReader = new CSVReader(new FileReader(new File(
					"in/eswc_linkedUp.csv")), ';');
			Model model = formData.toRDF(csvReader);
			csvReader.close();
			OutputStream out = new FileOutputStream(new File(
					"out/eswc_linkedUp.rdf"));
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
