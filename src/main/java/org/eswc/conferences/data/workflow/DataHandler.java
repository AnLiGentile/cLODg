package org.eswc.conferences.data.workflow;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class DataHandler {

	protected Model dataModel;

	public Model getDataModel() {
		return dataModel;
	}

	public void mergeData(Model mainConferenceModel) {
		StmtIterator stmtIterator = dataModel.listStatements();
		while (stmtIterator.hasNext()) {
			Statement stmt = stmtIterator.next();
			RDFNode object = stmt.getObject();
			if (object.isLiteral()) {
				if (!mainConferenceModel.contains(stmt.getSubject(),
						stmt.getPredicate(), (RDFNode) null)) {
					mainConferenceModel.add(stmt);
				}
			} else {
				if (!mainConferenceModel.contains(stmt))
					mainConferenceModel.add(stmt);
			}
		}
	}

}
