package org.scholarlydata.builder;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.DC_11;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDFS;

public class InProceedings {

	private Resource resource;
	private ConferenceEvent conferenceEvent;
	
	public InProceedings(Resource resource) {
		this.resource = resource;
		conferenceEvent = new ConferenceEvent(resource.getModel());
	}
	
	public Resource getResource() {
		return resource;
	}
	
	public String getURI(){
		return resource.getURI();
	}
	
	private void addAuthorList(Resource confInProceedings, Model model){
		
		Resource authorList = model.createResource(confInProceedings.getURI() + "/authorList", ConferenceOntology.List);
		confInProceedings.addProperty(ConferenceOntology.hasAuthorList, authorList);
		
		String sparql = 
				"SELECT ?member ?pos "
				+ "WHERE { "
				+ "{<" + resource.getURI() + "> <http://www.cs.vu.nl/~mcaklein/onto/swrc_ext/2005/05#authorList> ?authorList} "
				+ "UNION "
				+ "{<" + resource.getURI() + "> <http://purl.org/ontology/bibo/authorList> ?authorList} "
				+ "?authorList ?p ?member . FILTER(REGEX(STR(?p), \"http://www.w3.org/1999/02/22-rdf-syntax-ns#_\")) . "
				+ "BIND(REPLACE(STR(?p), \"http://www.w3.org/1999/02/22-rdf-syntax-ns#_\", \"\") AS ?pos)" 
				+ "} "
				+ "ORDER BY ?pos";
		
		
		Model modelIn = resource.getModel();
		ResultSet resultSet = QueryExecutor.execSelect(modelIn, sparql);
		
		//ResultSetFormatter.out(System.out, resultSet);
		
		
		int itemCounter = 0;
		Resource previousAuthorListItem = null;
		Resource authorListItem = null;
		while(resultSet.hasNext()){
			if(authorListItem != null)
				previousAuthorListItem = authorListItem;
			
			QuerySolution querySolution = resultSet.next();
			Resource person = querySolution.getResource("member");
			Literal pos = querySolution.getLiteral("pos");
			authorListItem = model.createResource(confInProceedings.getURI() + "/authorList/item-" + pos.getLexicalForm(), ConferenceOntology.ListItem);
			authorListItem.addProperty(ConferenceOntology.hasContent, ModelFactory.createDefaultModel().createResource(person.getURI().replace("http://data.semanticweb.org/", "http://www.scholarlydata.org/")));	
			
			authorList.addProperty(ConferenceOntology.hasItem, authorListItem);
			
			if(itemCounter == 0){
				authorList.addProperty(ConferenceOntology.hasFirstItem, authorListItem);
			}
			else{
				authorListItem.addProperty(ConferenceOntology.previous, previousAuthorListItem);
				previousAuthorListItem.addProperty(ConferenceOntology.next, authorListItem);
			}
			
			itemCounter += 1;
			
		}
		
		if(authorListItem != null)
			authorList.addProperty(ConferenceOntology.hasLastItem, authorListItem);
		
	}
	
	
	/*
	 * Author list props: swrc-ext:authorList, bibo:authorList
	 */
	public Resource asConfResource(Model model){
		String inProcURI = resource.getURI();
		inProcURI = inProcURI.replace("http://data.semanticweb.org/conference/", "");
		int index = inProcURI.indexOf("/");
		index = inProcURI.indexOf("/", index+1);
		
		inProcURI = inProcURI.substring(index+1);
		if(inProcURI.startsWith("workshop/")) {
			inProcURI.replace("workshop/", "");
			index = inProcURI.indexOf("/", index+1);
			inProcURI = inProcURI.substring(index+1);
		}
		
		String confAcronym = conferenceEvent.getAcronym();
		confAcronym = confAcronym.toLowerCase().replace(" ", "");
		String localName = confAcronym + "/" + inProcURI;
		
		Resource inProceedings = model.createResource(ConferenceOntology.RESOURCE_NS + "inproceedings/" + localName);
		
		Model modelIn = resource.getModel();
		
		String sparql = 
				"CONSTRUCT {"
				+ "<" + inProceedings.getURI() + "> a <" + ConferenceOntology.InProceedings.getURI() + "> . "
				+ "<" + inProceedings.getURI() + "> <" + RDFS.label + "> ?label . "
				+ "<" + inProceedings.getURI() + "> <" + DC_11.creator + "> ?author . "
				+ "<" + inProceedings.getURI() + "> <" + ConferenceOntology.title + "> ?title . "
				+ "<" + inProceedings.getURI() + "> <" + ConferenceOntology.abstract_ + "> ?abstract . "
				+ "<" + inProceedings.getURI() + "> <" + ConferenceOntology.keyword + "> ?subject . "
				+ "<" + inProceedings.getURI() + "> <" + ConferenceOntology.hasTopic + "> ?topic . "
				+ "<" + inProceedings.getURI() + "> <" + ConferenceOntology.isPartOf + "> ?proceedings . "
				+ "<" + inProceedings.getURI() + "> <" + OWL2.sameAs + "> <" + resource.getURI() + "> . "
				+ "?proceedings <" + ConferenceOntology.hasPart + "> <" + inProceedings.getURI() + "> "
				+ "}"
				+ "WHERE{ "
				+ "<" + resource.getURI() + "> <" + RDFS.label + "> ?label . "
				+ "OPTIONAL{"
				+ "{<" + resource.getURI() + "> <" + DC_11.creator + "> ?creator} UNION {<" + resource.getURI() + "> <" + DCTerms.creator + "> ?creator} . "
				+ "BIND(IRI(REPLACE(STR(?creator), \"http://data.semanticweb.org/\", \"http://www.scholarlydata.org/\")) AS ?author)"
				+ "}"
				+ "OPTIONAL{{<" + resource.getURI() + "> <" + SWC.isPartOf + "> ?proc} UNION {<" + resource.getURI() + "> <" + SWC.NS + "partOf" + "> ?proc} BIND(IRI(REPLACE(STR(?proc), \"http://data.semanticweb.org/\", \"http://www.scholarlydata.org/\")) AS ?proceedings)} "
				+ "{<" + resource.getURI() + "> <" + DC_11.title + "> ?title} UNION {<" + resource.getURI() + "> <" + DCTerms.title + "> ?title} . "
				+ "OPTIONAL {<" + resource.getURI() + "> <http://swrc.ontoware.org/ontology#abstract> ?abstract } "
				+ "OPTIONAL {{<" + resource.getURI() + "> <" + FOAF.topic + "> ?topic} UNION {<" + resource.getURI() + "> <" + SWC.hasTopic + "> ?topic}}"
				+ "OPTIONAL {{<" + resource.getURI() + "> <http://swrc.ontoware.org/ontology#keywords> ?subject} UNION {<" + resource.getURI() + "> <" + DC_11.subject+ "> ?subject} UNION {<" + resource.getURI() + "> <" + DCTerms.subject+ "> ?subject}}"
				+ "}";
		
		model.add(QueryExecutor.execConstruct(modelIn, sparql));
		
		addAuthorList(inProceedings, model);
		
		return inProceedings;
		
	}
	
	
	
}
