package it.istc.cnr.stlab.clodg.models;

import java.net.URI;
import java.util.Set;

public class Organization implements Entity {

	private String label;
	private URI uri;
	private Set<String> altLabels;

	public Organization(String label, URI uri, Set<String> altLabels) {
		this.label = label;
		this.uri = uri;
		this.altLabels = altLabels;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public URI getURI() {
		return uri;
	}

	public Set<String> getAltLabels() {
		return altLabels;
	}

}
