package org.scholarlydata.builder.arq;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.DC_11;
import org.scholarlydata.builder.ConferenceOntology;

public class EventTypeBinder extends FunctionBase1 {

	public static final String IRI = ConferenceOntology.NS + "eventTypeBind";
	
	@Override
	public NodeValue exec(NodeValue nodeValue) {
		if(nodeValue == null)
			return NodeValue.makeNode(ConferenceOntology.OrganisedEvent.asNode());
		else if(nodeValue.isIRI()){
			String localName = nodeValue.asNode().getLocalName();
			
			Model ontology = ConferenceOntology.getModel();
			
			Resource term = ModelFactory.createDefaultModel().createResource(ConferenceOntology.NS + localName);
			if(ontology.containsResource(term))
				return NodeValue.makeNode(term.asNode());
			else return NodeValue.makeNode(ConferenceOntology.OrganisedEvent.asNode());
			
		}
		else return NodeValue.makeNode(ConferenceOntology.OrganisedEvent.asNode());
	}

}
