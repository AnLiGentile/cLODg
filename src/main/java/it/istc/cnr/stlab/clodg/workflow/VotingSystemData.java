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

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.function.FunctionRegistry;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.DC_11;
import org.apache.jena.vocabulary.RDFS;

public class VotingSystemData {

	public static final String VOTE_NS = "http://eswc2015.semanticweb.org/voting/";
	public static final String CODE_NS = VOTE_NS + "code/";

	public VotingSystemData() {
		FunctionRegistry.get().put(
				"http://eswc2015.semanticweb.org/voting/trackextract",
				TrackFunction.class);
		FunctionRegistry.get().put(
            "http://eswc2015.semanticweb.org/voting/localname",
            LocalNameFunction.class);
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
						+ "?paper voting:paperOfTrack ?track . " 
						+ "?paper voting:id ?localname . "
						+ "?paper <" + DC_11.title.getURI() + "> ?title "
						+ "} "
						+ "WHERE{"
						+ "{?paper a <http://purl.org/spar/fabio/PosterPaper>} "
						+ "UNION "
						+ "{?paper a <http://purl.org/spar/fabio/DemoPaper>} "
						+ "BIND(voting:trackextract(?paper) AS ?track)"
						+ "BIND(voting:localname(?paper) AS ?localname)" 
						+ "?paper <http://purl.org/dc/terms/title> ?title "
						+ "}";
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