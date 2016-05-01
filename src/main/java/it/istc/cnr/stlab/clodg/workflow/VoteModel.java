package it.istc.cnr.stlab.clodg.workflow;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.DC_11;

public class VoteModel {

	private Model adminModel;

	public VoteModel(Model adminModel) {
		this.adminModel = adminModel;
	}

	public Model getAdminModel() {
		return adminModel;
	}

	public Model getPublicModel() {
		String sparql = "PREFIX voting: <" + VotingSystemData.VOTE_NS + "> "
				+ "CONSTRUCT { " + "?x a voting:Vote . "
				+ "?x voting:sha1sum ?sha1sum . "
				+ "?paper a voting:EligibleForVotingPaper . "
				+ "?paper voting:paperOfTrack ?track . " 
				+ "?paper voting:id ?id . "
				+ "?paper <" + DC_11.title.getURI() + "> ?title " 
				+ "} " 
				+ "WHERE{ "
				+ "?x a voting:Vote . " + "?x voting:sha1sum ?sha1sum . "
				+ "?paper a voting:EligibleForVotingPaper . "
				+ "?paper voting:paperOfTrack ?track . "
				+ "?paper voting:id ?id . "
				+ "?paper <" + DC_11.title.getURI() + "> ?title "
				+ "}";
		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				adminModel);
		return queryExecution.execConstruct();
	}

	public static void main(String[] args) {
		String content = "This work has been supported by the Ministry of Education and Culture, CSC - IT Center for Science Ltd., National Research Data Initiative (TTA)25, and the Linked Data Finland project26 funded by the Finnish Funding Agency for Innovation (Tekes) of 20 partners.";
		int start = content.indexOf("Funding Agency");
		int end = start + "Funding Agency".length();
		System.out.println(start + end);
		System.out.println(content.codePointCount(0, start) + " - "
				+ content.codePointCount(0, end));
	}

}
