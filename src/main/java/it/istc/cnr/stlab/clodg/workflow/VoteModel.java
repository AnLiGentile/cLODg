package it.istc.cnr.stlab.clodg.workflow;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;

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
				+ "?paper voting:paperOfTrack ?track " + "} " + "WHERE{ "
				+ "?x a voting:Vote . " + "?x voting:sha1sum ?sha1sum . "
				+ "?paper a voting:EligibleForVotingPaper . "
				+ "?paper voting:paperOfTrack ?track " + "}";
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
