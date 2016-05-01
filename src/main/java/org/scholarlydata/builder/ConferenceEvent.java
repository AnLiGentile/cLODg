package org.scholarlydata.builder;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

public class ConferenceEvent {
	
	private Resource resource;
	public ConferenceEvent(Model model) {
		
		//ResIterator resIt = model.listResourcesWithProperty(RDF.type, SWC.ConferenceEvent);
		ResIterator resIt = model.listResourcesWithProperty(RDF.type, SWC.WorkshopEvent);
		if(resIt.hasNext()){
			resource = resIt.next();
		}
	}
	
	public String getAcronym(){
		return resource.getProperty(ModelFactory.createDefaultModel().createProperty(SWC.NS + "hasAcronym")).getObject().toString();
	}
	
	public Resource getResource() {
		return resource;
	}

}
