package it.istc.cnr.stlab.clodg.app;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.util.FileManager;

public class PersonAligner {

	public Model align(List<Model> models) {

		String sparql = "CONSTRUCT {?person ?p ?o} WHERE{?person a <"
				+ FOAF.Person + "> . ?person ?p ?o}";
		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);

		Model out = ModelFactory.createDefaultModel();

		for (Model model : models) {
			QueryExecution queryExecution = QueryExecutionFactory.create(query,
					model);
			Model person = queryExecution.execConstruct(model);
			out.add(person);
		}

		sparql = "SELECT ?person WHERE{?person a <" + FOAF.Person + ">}";
		query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		QueryExecution queryExecution = QueryExecutionFactory
				.create(query, out);

		ResultSet resultSet = queryExecution.execSelect();
		for (int i = 1; resultSet.hasNext(); i++) {
			QuerySolution querySolution = resultSet.next();
			Resource person = querySolution.getResource("person");
			person.addLiteral(FOAF.openid, i);
		}
		return out;
	}

	public static void main(String[] args) {

		List<Model> models = new ArrayList<Model>();
		String[] fileNames = new String[] { "eswc_data_final.rdf",
				"eswc_data_final_no_research.rdf", "data_final_challenge.rdf",
				"data_final_mashup.rdf", "data_final_posterdemo.rdf" };
		for (String fileName : fileNames) {
			models.add(FileManager.get().loadModel("out/" + fileName));
		}

		Model model = new PersonAligner().align(models);
		model.write(System.out);
	}

}
