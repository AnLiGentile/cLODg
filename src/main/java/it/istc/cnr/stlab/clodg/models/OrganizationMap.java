package it.istc.cnr.stlab.clodg.models;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class OrganizationMap implements EntityMap {

	private Map<String, Organization> organizations;
	private Map<URI, Organization> organizationsByURIs;

	public OrganizationMap() {
		organizations = new HashMap<String, Organization>();
		organizationsByURIs = new HashMap<URI, Organization>();
	}

	@Override
	public void addEntity(Entity entity) {
		if (entity instanceof Organization) {
			Organization organization = (Organization) entity;
			Organization organizationTmp = getEntityByURI(organization.getURI());
			if (organizationTmp == null) {
				organizationTmp = organization;
			} else {
				String label = null;

				if (organizationTmp.getLabel().length() < organization
						.getLabel().length())
					label = organization.getLabel();
				else
					label = organizationTmp.getLabel();

				organizationTmp = new Organization(label,
						organization.getURI(), organization.getAltLabels());
			}
			for (String label : organization.getAltLabels()) {
				organizations.put(label, organizationTmp);
			}
			organizationsByURIs.put(organizationTmp.getURI(), organizationTmp);
		}
	}

	@Override
	public Organization getEntity(String name) {
		return organizations.get(name);
	}

	@Override
	public Organization getEntityByURI(URI uri) {
		return organizationsByURIs.get(uri);
	}

}
