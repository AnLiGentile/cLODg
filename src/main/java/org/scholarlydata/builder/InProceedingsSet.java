package org.scholarlydata.builder;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;

public class InProceedingsSet {

	
	private Model model;
	
	public InProceedingsSet(Model model) {
		this.model = model;
	}
	
	public Model getModel() {
		return model;
	}
	
	public Collection<InProceedings> list(){
		Collection<InProceedings> inProceedingsList = new ArrayList<InProceedings>();
	
		StmtIterator stmtIterator = model.listStatements(null, RDF.type, ModelFactory.createDefaultModel().createResource("http://swrc.ontoware.org/ontology#InProceedings"));
		while(stmtIterator.hasNext()){
			Statement stmt = stmtIterator.next();
			Resource inProceedingsResource = stmt.getSubject();
			InProceedings inProceedings = new InProceedings(inProceedingsResource);
			inProceedingsList.add(inProceedings);
		}
		return inProceedingsList;
	}
}
