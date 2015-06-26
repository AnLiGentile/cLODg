package org.eswc.conferences.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;

public class GraphBuilderForSemanticWebDogFood {

	public static void main(String[] args) {
		String sparql = "PREFIX eswc: <http://www.ontologydesignpatterns.org/ont/eswc/ontology.owl#> "
				+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
				+ "CONSTRUCT { "
				+ "    ?person foaf:account ?account . "
				+ "    ?person foaf:mbox ?mbox . "
				+ "    ?account ?paccount ?oaccount . "
				+ "    ?paper eswc:widget ?widget "
				+ "}"
				+ "WHERE{"
				+ "    OPTIONAL{"
				+ "       ?paper eswc:widget ?widget "
				+ "    }"
				+ "    OPTIONAL{"
				+ "        ?person foaf:account ?account . "
				+ "        ?account ?paccount ?oaccount "
				+ "    } "
				+ "    OPTIONAL{ "
				+ "        ?person foaf:mbox ?mbox . "
				+ "    }" + "}";

		Model model = FileManager.get().loadModel(
				"out/completeWithTwitterWidget.rdf");

		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				model);
		Model out = queryExecution.execConstruct();

		model.remove(out);

		try {
			String path = "out/semantic_web_dog_food";
			File dir = new File(path);
			if (!dir.exists())
				dir.mkdirs();

			OutputStream outputStream = new FileOutputStream(path
					+ "/eswc-2014-complete.rdf");
			model.write(outputStream);

			outputStream.flush();
			outputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
