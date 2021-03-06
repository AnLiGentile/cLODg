package org.scholarlydata.builder;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDFS;

public class Organisation {

	private Resource resource;
	public Organisation(Resource resource) {
		this.resource = resource;
	}
	
	public Resource getResource() {
		return resource;
	}
	
	public String getURI(){
		return resource.getURI();
	}
	
	public String getName(){
		Statement stmt = resource.getProperty(FOAF.name);
		return ((Literal)stmt.getObject()).getLexicalForm();
	}
	
	public Resource asConfResource(){
		String localName = resource.getLocalName();
		return ModelFactory.createDefaultModel().createResource(ConferenceOntology.RESOURCE_NS + "organisation/" + localName);
	}
	
	public Resource asConfResource(Model model){
		String localName = resource.getLocalName();
		
		Resource organisation = model.createResource(ConferenceOntology.RESOURCE_NS + "organisation/" + localName);
		
		Model modelIn = resource.getModel();
		
		String sparql = 
				"CONSTRUCT {"
				+ "<" + organisation.getURI() + "> a <" + ConferenceOntology.Organisation.getURI() + "> . "
				+ "<" + organisation.getURI() + "> <" + RDFS.label + "> ?label . "
				+ "<" + organisation.getURI() + "> <" + ConferenceOntology.name + "> ?name . "
				+ "<" + organisation.getURI() + "> <" + OWL2.sameAs + "> <" + resource.getURI() + "> "
				+ "}"
				+ "WHERE{ "
				+ "<" + resource.getURI() + "> <" + RDFS.label + "> ?label . "
				+ "<" + resource.getURI() + "> <" + FOAF.name + "> ?name . "
				+ "OPTIONAL {<" + resource.getURI() + "> <" + FOAF.firstName + "> ?firstName}"
				+ "OPTIONAL {<" + resource.getURI() + "> <http://xmlns.com/foaf/0.1/lastName> ?lastName}"
				+ "OPTIONAL {<" + resource.getURI() + "> <" + FOAF.mbox_sha1sum + "> ?mbox_sha1sum}"
				+ "}";
		
		model.add(QueryExecutor.execConstruct(modelIn, sparql));
		
		return organisation;
		
	}
	
	public String getLocalName(){
		return this.resource.getLocalName();
	}
	
}
