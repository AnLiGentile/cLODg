package it.istc.cnr.stlab.clodg.models;

import java.net.URI;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * 
 * @author andrea.nuzzolese
 *
 */
public class Person implements Entity {

	private String firstName;
	private String lastName;
	private Set<String> emails;
	private URI uri;

	public Person(String firstName, String lastName, Set<String> emails, URI uri) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.emails = emails;
		this.uri = uri;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getName() {
		return getLabel();
	}

	@Override
	public String getLabel() {
		StringBuilder sb = new StringBuilder();
		sb.append(firstName).append(" ").append(lastName);
		return sb.toString();
	}

	public Set<String> getEmails() {
		return emails;
	}

	public Set<String> getEmailHash() {
		return emails;
	}

	@Override
	public URI getURI() {
		return uri;
	}
}
