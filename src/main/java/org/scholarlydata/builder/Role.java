package org.scholarlydata.builder;

import java.util.Set;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.function.library.substring;

public class Role {

	private Resource resource;
	
	public Role(Resource resource) {
		this.resource = resource;
	}
	
	public Resource getResource() {
		return resource;
	}
	
	public Set<Resource> getTypes(){
		
		RoleKB roleKB = RoleKB.getInstance();
		
		//return (Resource) resource.getProperty(RDF.type).getObject();
		return roleKB.getConfRoleFromDFInstance(resource);
	}
	
	
	public static void main(String[] args) {
		System.out.println("In October 1850, already an accomplished mathematician, Maxwell left Scotland for the University of Cambridge. He initially attended Peterhouse, but before the end of his first term transferred to Trinity, where he believed it would be easier to obtain a fellowship.".substring(56,63));
	}
}
