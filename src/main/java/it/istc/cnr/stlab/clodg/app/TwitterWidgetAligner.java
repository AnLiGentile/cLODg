package it.istc.cnr.stlab.clodg.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;

public class TwitterWidgetAligner implements Aligner {

	// TODO data should be loaded from CSV on google drive, not from JSON
	@Override
	public Model align(JSONObject jsonObject, Model model) {
		try {
			JSONArray articles = jsonObject.getJSONArray("articles");

			Property property = model
					.createProperty("http://2014.eswc-conferences.org/ontology/twitterWidget");
			for (int i = 0, j = articles.length(); i < j; i++) {
				JSONObject article = articles.getJSONObject(i);

				String id = article.getString("id");
				String twitterWidget = article.getString("twitter_widget");

				// TODO must be done for phdSymp, poster and demo, and WS
				String sparql = "SELECT ?article WHERE{?article a <http://swrc.ontoware.org/ontology#InProceedings> . FILTER(STR(?article) = \"http://data.semanticweb.org/conference/eswc/2014/paper/research/"
						+ id
						+ "\" || STR(?article) = \"http://data.semanticweb.org/conference/eswc/2014/paper/in-use/"
						+ id + "\") }";

				Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
				QueryExecution queryExecution = QueryExecutionFactory.create(
						query, model);
				ResultSet resultSet = queryExecution.execSelect();

				if (resultSet.hasNext()) {
					QuerySolution querySolution = resultSet.next();
					Resource articleResource = querySolution
							.getResource("article");
					if (articleResource != null) {
						articleResource.addLiteral(property, twitterWidget);
					}
				}

				/*
				 * String uri =
				 * "http://data.semanticweb.org/conference/eswc/2014/paper/research/"
				 * + id;
				 * 
				 * Resource articleResource = model.getResource(uri);
				 * 
				 * System.out.println(uri);
				 * 
				 * if(articleResource != null){
				 * articleResource.addLiteral(property, twitterWidget); }
				 */
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return model;
	}

	public static void main(String[] args) {
		try {
			// TODO shouldn't this be articles.json??
			Reader reader = new FileReader(new File("in/contacts.json"));
			BufferedReader bufferedReader = new BufferedReader(reader);

			String line = null, content = "";

			while ((line = bufferedReader.readLine()) != null) {
				content += line;
			}

			bufferedReader.close();

			JSONObject jsonObject = new JSONObject(content);
			Model model = FileManager.get().loadModel("eswc_data_final.rdf");

			Aligner aligner = new TwitterWidgetAligner();
			aligner.align(jsonObject, model);
		} catch (FileNotFoundException e) {
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

}
