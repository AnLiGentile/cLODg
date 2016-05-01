package it.istc.cnr.stlab.clodg.workflow;

import java.net.URL;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;

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
