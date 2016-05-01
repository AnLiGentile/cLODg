package org.scholarlydata.builder;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.vocabulary.SKOS;

public class RoleKB {
	
	private final String datasetLocation = "role_mappings";
	
	private static RoleKB instance;
	
	private Dataset dataset;
	
	private RoleKB(){
		dataset = TDBFactory.createDataset(datasetLocation);
	}
	
	public static RoleKB getInstance(){
		if(instance == null) instance = new RoleKB();
		return instance;
	}
	
	public Set<Resource> getDogFoodRole(Resource roleInstance){
		Set<Resource> dogFoodRoles = new HashSet<Resource>();
		
		String sparql =   "SELECT DISTINCT ?role"
						+ "WHERE{ "
						+ "<" + roleInstance.getURI() + "> a ?role "
						+ "}";
		
		ResultSet resultSet = executesQuery(sparql);
		
		while(resultSet.hasNext()){
			QuerySolution querySolution = resultSet.next();
			dogFoodRoles.add(querySolution.getResource("role"));
		}
		
		return dogFoodRoles;
	}
	
	public Set<Resource> getConfRoleFromDFInstance(Resource roleInstance){
		Set<Resource> confRoles = new HashSet<Resource>();
		
		String sparql =   "SELECT DISTINCT ?confRole "
						+ "WHERE{ "
						+ "<" + roleInstance.getURI() + "> a ?role . "
						+ "?role <" + SKOS.closeMatch + "> ?confRole "
						+ "}";
		
		ResultSet resultSet = executesQuery(sparql);
		
		while(resultSet.hasNext()){
			QuerySolution querySolution = resultSet.next();
			confRoles.add(querySolution.getResource("confRole"));
		}
		
		return confRoles;
	}
	
	
	private ResultSet executesQuery(String sparql){
		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		QueryExecution queryExecution = QueryExecutionFactory.create(query, dataset);
		return queryExecution.execSelect();
		
	}

}
