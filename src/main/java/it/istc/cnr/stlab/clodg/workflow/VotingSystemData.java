package it.istc.cnr.stlab.clodg.workflow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import org.apache.commons.codec.digest.DigestUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.function.FunctionRegistry;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDFS;

public class VotingSystemData {

	public static final String VOTE_NS = "http://eswc2015.semanticweb.org/voting/";
	public static final String CODE_NS = VOTE_NS + "code/";

	public VotingSystemData() {
		FunctionRegistry.get().put(
				"http://eswc2015.semanticweb.org/voting/trackextract",
				TrackFunction.class);
	}

	public VoteModel generateData(File votesCsv, Model conferenceData) {

		Model adminModel = ModelFactory.createDefaultModel();
		try {
			CSVReader csvReader = new CSVReader(new FileReader(votesCsv));
			String[] line = null;
			Property sha1sum = adminModel.createProperty(VOTE_NS + "sha1sum");
			Property voteClass = adminModel.createProperty(VOTE_NS + "Vote");
			while ((line = csvReader.readNext()) != null) {
				String code = line[0];
				if (code != null && !code.trim().isEmpty()) {
					code = code.trim();
					String sha1 = DigestUtils.sha1Hex(code.getBytes());
					Resource validCode = adminModel.createResource(CODE_NS
							+ sha1.toLowerCase(), voteClass);
					validCode.addProperty(RDFS.label, code);
					validCode.addProperty(sha1sum, sha1);
				}
			}
			csvReader.close();

			if (conferenceData != null && !conferenceData.isEmpty()) {
				String sparql = "PREFIX swrc: <http://swrc.ontoware.org/ontology#> "
						+ "PREFIX voting: <"
						+ VOTE_NS
						+ "> "
						+ "CONSTRUCT {"
						+ "?paper a voting:EligibleForVotingPaper . "
						+ "?paper voting:paperOfTrack ?track "
						+ "} "
						+ "WHERE{"
						+ "{?paper a <http://purl.org/spar/fabio/PosterPaper>} "
						+ "UNION "
						+ "{?paper a <http://purl.org/spar/fabio/DemoPaper>} "
						+ "BIND(voting:trackextract(?paper) AS ?track)" + "}";
				Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
				QueryExecution queryExecution = QueryExecutionFactory.create(
						query, conferenceData);
				Model paperModel = queryExecution.execConstruct();

				if (paperModel != null && !paperModel.isEmpty())
					adminModel.add(paperModel);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new VoteModel(adminModel);

	}

	public static void main(String[] args) {
		VotingSystemData votingSystemData = new VotingSystemData();
		URL fileURL = VotingSystemData.class.getClassLoader().getResource(
				"data/app_voting_codes_v2.txt");
		System.out.println(fileURL.getFile());
		File file = new File(fileURL.getFile());
		VoteModel voteModel = votingSystemData.generateData(file, FileManager
				.get().loadModel("rdf/form-data.rdf"));
		try {
			OutputStream adminModelOut = new FileOutputStream(
					"rdf/voting_admin.ttl");
			voteModel.getAdminModel().write(adminModelOut, "TURTLE");
			OutputStream publicModelOut = new FileOutputStream(
					"rdf/voting_public.ttl");
			voteModel.getPublicModel().write(publicModelOut, "TURTLE");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}