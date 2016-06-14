package org.scholarlydata.builder.doi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.scholarlydata.builder.ConferenceOntology;
import org.scholarlydata.builder.QueryExecutor;

public class CrossRefBasedDOIFinder {

	private final String sparql = "PREFIX person: <http://www.scholarlydata.org/person/> "
			+ "PREFIX conf: <http://www.scholarlydata.org/ontology/conference-ontology.owl#> "
			+ "SELECT DISTINCT ?paper ?title ?person ?name "
			+ "WHERE{"
			+ "?paper a conf:InProceedings; "
			+ "conf:hasAuthorList ?authorList; "
			+ "conf:title ?title . "
			+ "?authorList conf:hasFirstItem ?item . "
			+ "?item conf:hasContent ?person . "
			+ "?person conf:familyName ?name "
			+ "}"; 
	
	private final String virtuosoService = "http://www.scholarlydata.org/sparql";
	
	private final String crossrefService = "http://api.crossref.org/works";
	
	private final double threshold = 3.0;
	
	public Model find(){
		Model model = ModelFactory.createDefaultModel();
		
		ResultSet resultSet = QueryExecutor.execRemoteSelect(virtuosoService, sparql);
		
		while(resultSet.hasNext()){
			QuerySolution querySolution = resultSet.next();
			Resource paper = querySolution.getResource("paper");
			Literal titleLiteral = querySolution.getLiteral("title");
			Resource person = querySolution.getResource("person");
			Literal nameLiteral = querySolution.getLiteral("name");
			
			String name = nameLiteral.getLexicalForm();
			String title = titleLiteral.getLexicalForm();
			
			String pathRequest;
			try {
				pathRequest = "?query=" + URLEncoder.encode(name, "UTF-8") + "%20" + URLEncoder.encode(title, "UTF-8");
				
				URLConnection connection = new URL(crossrefService + pathRequest).openConnection();
				InputStream inputStream = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				
				String response = "";
				String line = null; 
				while((line = reader.readLine()) != null){
					response += line;
				}
				
				JSONObject json = new JSONObject(response);
				
				double maxConf = 0.0;
				String doi = null;
				if(json != null){
					
					JSONObject message = json.getJSONObject("message");
					if(message != null){
						JSONArray items = message.getJSONArray("items");
						if(items != null){
							for(int i=0, j=items.length(); i<j; i++){
								JSONObject item = items.getJSONObject(i);
								String doiTmp = item.getString("DOI");
								double score = item.getDouble("score");
								String titleTemp = item.getJSONArray("title").getString(0);
								
								if(titleTemp.equalsIgnoreCase(title) && score > threshold && score > maxConf){
									doi = doiTmp;
									maxConf = score;
								}
							}
						}
					}
				}
				
				if(doi != null) model.add(paper, ConferenceOntology.doi, model.createLiteral(doi));
				System.out.println(title + " - " + doi);
				
				model.write(new FileOutputStream(new File("scholarlydata_dois.rdf")));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
		
		
		
		return model;
	}
	
	
	public static void main(String[] args) {
		new CrossRefBasedDOIFinder().find();
	}
	
}
