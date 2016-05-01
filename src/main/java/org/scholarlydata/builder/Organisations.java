package org.scholarlydata.builder;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDF;

public class Organisations {

	private Model model;
	public Organisations(Model model) {
		this.model = model;
	}
	
	public Collection<Organisation> list(){
		Collection<Organisation> organisationList = new ArrayList<Organisation>();
	
		StmtIterator stmtIterator = model.listStatements(null, RDF.type, FOAF.Organization);
		while(stmtIterator.hasNext()){
			Statement stmt = stmtIterator.next();
			Resource organisationResource = stmt.getSubject();
			Organisation organisation = new Organisation(organisationResource);
			organisationList.add(organisation);
		}
		return organisationList;
	}
}
