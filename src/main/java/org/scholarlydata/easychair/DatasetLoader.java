package org.scholarlydata.easychair;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDFS;

import de.fuberlin.wiwiss.d2rq.jena.ModelD2RQ;

public class DatasetLoader {

	public void init(){
		ModelD2RQ model = new ModelD2RQ("scholarlydata/easychair/d2rq_mapping.ttl");
	}
	
	public static void main(String[] args) {
		Model model = FileManager.get().loadModel("test/eswc2016.rdf");
		String sparql = "select distinct ?label where {?role a <http://data.semanticweb.org/ns/swc/ontology#Chair>; <" + RDFS.label + "> ?label}";
		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = queryExecution.execSelect();
		
		ResultSetFormatter.out(System.out, resultSet);
	}
}
