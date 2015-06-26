package org.eswc.conferences.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.eswc.conferences.data.util.OfficialNameSpace;

import au.com.bytecode.opencsv.CSVReader;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.util.FileManager;

public class DuplicatePerson {

	public void list(Model model) {
		String sparql = "PREFIX foaf: <" + FOAF.NS + "> "
				+ "SELECT ?lastName ?person " + "WHERE{ "
				+ "    ?person a foaf:Person . "
				+ "    ?person foaf:lastName ?lastName . "
				+ "    ?person1 a foaf:Person . "
				+ "    ?person1 foaf:lastName ?lastName1 . "
				+ "    FILTER(?person != ?person1) . "
				+ "    FILTER(STR(?lastName) = STR(?lastName1)) . " + "}"
				+ "ORDER BY ?lastName";
		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				model);

		ResultSet resultSet = queryExecution.execSelect();
		ResultSetFormatter.out(System.out, resultSet);
	}

	public void remove(Model model, CSVReader csvReader) {
		String[] line = null;
		try {
			while ((line = csvReader.readNext()) != null) {
				String correct = line[0];
				String wrong = line[1];
				System.out.println(wrong);
				Resource correctPerson = model
						.getResource(OfficialNameSpace.personNs + correct);
				Resource wrongPerson = model
						.getResource(OfficialNameSpace.personNs + wrong);

				String sparql = "PREFIX foaf: <" + FOAF.NS + "> "
						+ "CONSTRUCT {" + "    <" + correctPerson
						+ "> ?outgoing ?val . " + "    ?obj ?ingoing <"
						+ correctPerson + "> . " + "} " + "WHERE{" + "    <"
						+ wrongPerson + "> ?outgoing ?val . "
						+ "    FILTER(isIRI(?val)) . " + "    ?obj ?ingoing <"
						+ wrongPerson + "> . " + "}";

				Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
				QueryExecution queryExecution = QueryExecutionFactory.create(
						query, model);

				Model m = queryExecution.execConstruct();

				model.add(m);

				model.removeAll(wrongPerson, null, (RDFNode) null);
				model.removeAll(null, null, wrongPerson);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Model model = FileManager.get().loadModel("out/eswc_data_final.rdf");
		try {
			new DuplicatePerson().remove(model, new CSVReader(new FileReader(
					new File("in/duplicates.csv")), ';'));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
