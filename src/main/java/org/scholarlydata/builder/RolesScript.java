package org.scholarlydata.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.expr.ExprException;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDFS;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class RolesScript {

	
	public static void main(String[] args) {
		try {
			CSVReader reader = new CSVReader(new FileReader(new File("ontologies/chair_labels.csv")), ';');
			CSVWriter writer = new CSVWriter(new FileWriter(new File("ontologies/chair_labels_enhanced.csv")), ';');
			String[] row = null;
			Model model = FileManager.get().loadModel("ontologies/swc_2009-05-09.rdf");
			while((row = reader.readNext()) != null){
				String role = row[0];
				String label = row[1];
				String event = row[2];
				
				System.out.println(role);
				if(!label.equalsIgnoreCase("chair")){
					String sparql = 
							"PREFIX rdfs: <" + RDFS.uri + "> "
							+ "PREFIX swc: <http://data.semanticweb.org/ns/swc/ontology#> "
							+ "SELECT ?role "
							+ "WHERE{ "
							+ "?role rdfs:subClassOf swc:Role . "
							+ "?role rdfs:label ?label . "
							+ "FILTER(REGEX(\"" + label + "\", STR(?label), \"i\")) "
							+ "FILTER(?role != swc:Chair)"
							+ "}";
					
					System.out.println(sparql);
					
					try{
						Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
						QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
						ResultSet resultSet = queryExecution.execSelect();
						
						if(resultSet.hasNext()){
							QuerySolution querySolution = resultSet.next();
							Resource roleRes = querySolution.getResource("role");
							writer.writeNext(new String[]{role,label,event,roleRes.getURI()});
						}
						else writer.writeNext(new String[]{role,label,event,""});
					}catch(ExprException e){
						writer.writeNext(new String[]{role,label,event,""});
					}
				}
				else writer.writeNext(new String[]{role,label,event,""});
			}
			
			reader.close();
			writer.close();
				
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		try {
			CSVReader reader = new CSVReader(new FileReader(new File("ontologies/roles.csv")));
			CSVWriter writer = new CSVWriter(new FileWriter(new File("ontologies/roles_cleaned.csv")));
			String[] row = null;
			while((row = reader.readNext()) != null){
				String role = row[0];
				int lashSlash = role.lastIndexOf("/");
				int lashHash = role.lastIndexOf("#");
				
				String localname = null;
				if(lashSlash > lashHash) localname = role.substring(lashSlash+1);
				else localname = role.substring(lashHash+1);
				
				writer.writeNext(new String[]{localname});
			}
			
			reader.close();
			writer.close();
				
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
}
