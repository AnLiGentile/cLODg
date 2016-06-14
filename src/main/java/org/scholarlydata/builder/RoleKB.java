package org.scholarlydata.builder;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;

public class RoleKB {
	
	private final String datasetLocation = "ontologies/rolesModel.rdf";
	
	private static RoleKB instance;
	
	private Model rolesModel;
	
	private RoleKB(){
		//dataset = TDBFactory.createDataset(datasetLocation);
		rolesModel = FileManager.get().loadModel(datasetLocation);
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
		
		System.out.println("Sparql " + sparql);
		
		ResultSet resultSet = executesQuery(sparql);
		
		while(resultSet.hasNext()){
			QuerySolution querySolution = resultSet.next();
			dogFoodRoles.add(querySolution.getResource("role"));
			
			
			System.out.println("ROle " + querySolution.getResource("role"));
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
	
	public void addRolesFromModel(Model model){
		String sparql = "PREFIX swc: <http://data.semanticweb.org/ns/swc/ontology#> "
				+ "SELECT ?role ?roletype "
				+ "WHERE{?role a swc:Chair . ?role a ?roletype . filter(?roletype != swc:Chair)}";
		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = queryExecution.execSelect();
		
		Model tmp = ModelFactory.createDefaultModel();
		while(resultSet.hasNext()){
			QuerySolution querySolution = resultSet.next();
			Resource role = querySolution.getResource("role");
			Resource roleType = querySolution.getResource("roletype");
			tmp.add(role, RDF.type, roleType);
		}
		
		
		
		//ResultSetFormatter.out(System.out, resultSet);
		
	}
	
	
	private ResultSet executesQuery(String sparql){
		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		QueryExecution queryExecution = QueryExecutionFactory.create(query, rolesModel);
		return queryExecution.execSelect();
		
	}

}
