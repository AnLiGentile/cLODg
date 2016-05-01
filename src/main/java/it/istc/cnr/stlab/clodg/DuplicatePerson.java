package it.istc.cnr.stlab.clodg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.util.FileManager;

import au.com.bytecode.opencsv.CSVReader;
import it.istc.cnr.stlab.clodg.util.OfficialNameSpace;

public class DuplicatePerson {

	private OfficialNameSpace ns;

	public DuplicatePerson(OfficialNameSpace ns) {
super();
this.ns = ns;
}

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
						.getResource(ns.personNs + correct);
				Resource wrongPerson = model
						.getResource(ns.personNs + wrong);

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
		
	    OfficialNameSpace ns = OfficialNameSpace.getInstance();
		
		Model model = FileManager.get().loadModel("out/eswc_data_final.rdf");
		try {
			new DuplicatePerson(ns).remove(model, new CSVReader(new FileReader(
					new File("in/duplicates.csv")), ';'));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
