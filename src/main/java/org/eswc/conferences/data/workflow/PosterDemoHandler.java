package org.eswc.conferences.data.workflow;

import java.net.URL;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;

public class PosterDemoHandler extends DataHandler {

	public PosterDemoHandler(Model posterDemoModel) {
		this.dataModel = posterDemoModel;

		String sparql = "PREFIX swrc: <http://swrc.ontoware.org/ontology#> "
				+ "PREFIX fabio: <http://purl.org/spar/fabio/> "
				+ "CONSTRUCT {?paper a swrc:InProceedings}"
				+ "WHERE{{?paper a fabio:DemoPaper} UNION {?paper a fabio:PosterPaper}}";
		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				posterDemoModel);
		Model inProceedingsModel = queryExecution.execConstruct();

		posterDemoModel.add(inProceedingsModel);
	}

	public static void main(String[] args) {
		/*
		 * We add the data about Poster and Demo.
		 */
		URL posterDemoModelURL = PosterDemoHandler.class.getClassLoader()
				.getResource("data/eswc2015_poster_demo.rdf");
		if (posterDemoModelURL != null) {
			String posterDemoModelPathName = posterDemoModelURL.getFile();
			Model posterDemoModel = FileManager.get().loadModel(
					posterDemoModelPathName);
			PosterDemoHandler posterDemoHandler = new PosterDemoHandler(
					posterDemoModel);
			posterDemoHandler.getDataModel().write(System.out);
		}
	}

}
