package org.scholarlydata.builder;

import java.util.Set;

import org.apache.jena.rdf.model.Resource;

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
	
}
