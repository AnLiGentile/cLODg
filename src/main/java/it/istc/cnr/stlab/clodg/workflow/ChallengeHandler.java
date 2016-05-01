package it.istc.cnr.stlab.clodg.workflow;

import java.net.URL;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;

/**
 * @author annalisa
 *
 */
public class ChallengeHandler extends DataHandler {

	public ChallengeHandler(Model phDSympModel) {
		this.dataModel = phDSympModel;

		// TODO adjust query for phDSymp
		String sparql = "PREFIX swrc: <http://swrc.ontoware.org/ontology#> "
				+ "PREFIX fabio: <http://purl.org/spar/fabio/> "
				+ "CONSTRUCT {?paper a swrc:InProceedings}"
				+ "WHERE{{?paper a fabio:ChallengePaper}}";
		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				phDSympModel);
		Model inProceedingsModel = queryExecution.execConstruct();

		phDSympModel.add(inProceedingsModel);
	}

	public static void main(String[] args) {
		/*
		 * We add the data about Poster and Demo.
		 */
		URL posterDemoModelURL = ChallengeHandler.class.getClassLoader()
				.getResource("data/eswc2015_challenge.rdf");
		if (posterDemoModelURL != null) {
			String posterDemoModelPathName = posterDemoModelURL.getFile();
			Model posterDemoModel = FileManager.get().loadModel(
					posterDemoModelPathName);
			ChallengeHandler posterDemoHandler = new ChallengeHandler(
					posterDemoModel);
			posterDemoHandler.getDataModel().write(System.out);
		}
	}

}
