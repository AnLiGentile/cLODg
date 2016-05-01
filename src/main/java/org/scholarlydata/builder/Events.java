package org.scholarlydata.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDFS;

public class Events {

	private Model model;
	private Model swdfOnt;
	private ConferenceEvent conferenceEvent;
	private Map<Resource, Resource> swdf2confMapping;
	
	public static int anonEventCounter; 
	
	public Events(Model model) {
		this.model = model;
		this.swdfOnt = FileManager.get().loadModel("ontologies/swc_2009-05-09.rdf");
		this.conferenceEvent = new ConferenceEvent(model);
		this.swdf2confMapping = new HashMap<Resource, Resource>();
		
		anonEventCounter = 0;
	}
	
	public Collection<Event> list(){
		
		Collection<Event> events = new ArrayList<Event>();
		
		String sparql = "SELECT DISTINCT ?event "
				+ "WHERE { "
				+ "{?event <" + SWC.isSuperEventOf + "> ?super} "
				+ "UNION "
				+ "{?event <" + SWC.isSubEventOf + "> ?sub} "
				+ "UNION "
				+ "{?event a ?type . ?type <" + RDFS.subClassOf + "> <" + SWC.OrganisedEvent + ">} "
				//+ "?s <" + SWC.isSuperEventOf + "> ?super . "
				//+ "FILTER (!BOUND(?super)) "
				+ "}";
		
		Model unionModel = ModelFactory.createDefaultModel();
		unionModel.add(model);
		unionModel.add(swdfOnt);
		
		
		ResultSet resultSet = QueryExecutor.execSelect(unionModel, sparql);
		
		String confAcronym = conferenceEvent.getAcronym();
		confAcronym = confAcronym.toLowerCase().replaceAll(" ", "");
		
		while(resultSet.hasNext()){
			QuerySolution querySolution = resultSet.next();
			Resource eventRes = querySolution.getResource("event");
			
			Event event = new Event(eventRes, confAcronym);
			swdf2confMapping.put(eventRes, event.asConfResource());
			
			events.add(new Event(eventRes, confAcronym));
				
		}
		
		return events;
	}
	
}
