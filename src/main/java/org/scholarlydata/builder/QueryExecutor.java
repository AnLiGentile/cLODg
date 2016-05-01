package org.scholarlydata.builder;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;

public class QueryExecutor {

	
	private static QueryExecution createQueryExecution(Model model, String sparql){
		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		return QueryExecutionFactory.create(query, model);
	}

	public static ResultSet execSelect(Model model, String sparql){
		QueryExecution queryExecution = createQueryExecution(model, sparql);
		return queryExecution.execSelect();
	}
	
	public static Model execConstruct(Model model, String sparql){
		QueryExecution queryExecution = createQueryExecution(model, sparql);
		return queryExecution.execConstruct();
	}

}
