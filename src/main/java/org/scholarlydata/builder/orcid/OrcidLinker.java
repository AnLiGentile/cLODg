package org.scholarlydata.builder.orcid;

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
import org.apache.jena.vocabulary.OWL2;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.scholarlydata.builder.QueryExecutor;

public class OrcidLinker {
	
	private final String sparql = "PREFIX person: <http://www.scholarlydata.org/person/> "
			+ "PREFIX conf: <http://www.scholarlydata.org/ontology/conference-ontology.owl#> "
			+ "SELECT DISTINCT ?person ?givenName ?familyName "
			+ "WHERE{ "
			+ "?person a conf:Person . "
			+ "?person conf:givenName ?givenName . "
			+ "?person conf:familyName ?familyName "
			+ "}"; 
	
	private final String virtuosoService = "http://www.scholarlydata.org/sparql";
	
	private final String orcidService = "http://pub.orcid.org/search/orcid-bio";
	private final String orcidAndClause = "%20AND%20";
	
	private final double threshold = 13.0;
	
	
	public Model link(){
		Model model = ModelFactory.createDefaultModel();
		
		ResultSet resultSet = QueryExecutor.execRemoteSelect(virtuosoService, sparql);
		
		while(resultSet.hasNext()){
			QuerySolution querySolution = resultSet.next();
			Resource person = querySolution.getResource("person");
			Literal givenNameLiteral = querySolution.getLiteral("givenName");
			Literal familyNameLiteral = querySolution.getLiteral("familyName");
			
			String givenName = givenNameLiteral.getLexicalForm();
			String familyName = familyNameLiteral.getLexicalForm();
			
			String pathRequest;
			try {
				pathRequest = "/?q=given-names:" + URLEncoder.encode(givenName, "UTF-8") + orcidAndClause + "family-name:" + URLEncoder.encode(familyName, "UTF-8") + "&rows=6";
				
				URLConnection connection = new URL(orcidService + pathRequest).openConnection();
				connection.setRequestProperty("Accept", "application/json");
				InputStream inputStream = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				
				String response = "";
				String line = null; 
				while((line = reader.readLine()) != null){
					response += line;
				}
				
				JSONObject json = new JSONObject(response);
				
				double maxConf = 0.0;
				String orcid = null;
				if(json != null){
					
					JSONObject results = json.getJSONObject("orcid-search-results");
					if(results != null){
						JSONArray items = results.getJSONArray("orcid-search-result");
						if(items != null){
							for(int i=0, j=items.length(); i<j; i++){
								JSONObject item = items.getJSONObject(i);
								double score = item.getJSONObject("relevancy-score").getDouble("value");
								
								if(score > threshold && score > maxConf){
									orcid = item.getJSONObject("orcid-profile").getJSONObject("orcid-identifier").getString("uri");
								}
							}
						}
					}
				}
				
				if(orcid != null) model.add(person, OWL2.sameAs, model.createResource(orcid));
				System.out.println(givenName + "  " + familyName + " - " + orcid);
				
				model.write(new FileOutputStream(new File("scholarlydata_orcids.rdf")));
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
		new OrcidLinker().link();
	}

}
