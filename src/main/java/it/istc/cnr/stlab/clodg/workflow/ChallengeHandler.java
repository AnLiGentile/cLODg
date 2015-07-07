package it.istc.cnr.stlab.clodg.workflow;

import java.net.URL;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

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
