package it.istc.cnr.stlab.clodg.quality;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;

public class AnalyseNames {
	
	public Model loadGraph(String path){
		Model m = null;
		//TODO load model from static dump (use /cLODg/src/main/resources/data/all-names.rdf)
		return m;		
	}
	
	public HashMap<String, Set<String>> extractPersonNames (Model nameGraph){
		HashMap<String, Set<String>> names = new HashMap<String, Set<String>>();
		//TODO populate person names as follows:
		// - KEY: person URI
		// - VALUES: all name lexicalizations for the person (label, name, first name, last name ...)
		
		return names;
		
	}
	
	public HashMap<String, Set<String>> extractOrganizationNames (Model nameGraph){
		HashMap<String, Set<String>> organizations = new HashMap<String, Set<String>>();
		//TODO populate organizations names as follows:
		// - KEY: organization URI
		// - VALUES: all name lexicalizations for the organization (label ...)
		
		return organizations;
		
	}
	

	public static void main(String[] args) {

		AnalyseNames an = new AnalyseNames();
		Model m = an.loadGraph("./resources/data/all-names.rdf");
		HashMap<String, Set<String>> names = an.extractPersonNames(m);
		HashMap<String, Set<String>> orgs = an.extractOrganizationNames(m);
		
		for (Entry<String, Set<String>> s : names.entrySet()){
			System.out.println(s.getKey()+"\t"+s.getValue());
		}

		for (Entry<String, Set<String>> s : orgs.entrySet()){
			System.out.println(s.getKey()+"\t"+s.getValue());
		}
	}

}
