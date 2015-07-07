package it.istc.cnr.stlab.clodg.models;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PersonMap implements EntityMap {

	private Map<String, Person> persons;
	private Map<URI, Person> personsByURIs;

	public PersonMap() {
		persons = new HashMap<String, Person>();
		personsByURIs = new HashMap<URI, Person>();
	}

	@Override
	public void addEntity(Entity entity) {
		if (entity instanceof Person) {
			Person person = (Person) entity;
			Person personTmp = getEntityByURI(person.getURI());
			if (personTmp == null) {
				personTmp = person;
			} else {
				String firstName = null;
				String lastName = null;
				Set<String> emails = personTmp.getEmails();
				emails.addAll(person.getEmails());

				if (personTmp.getFirstName().length() < person.getFirstName()
						.length())
					firstName = person.getFirstName();
				else
					firstName = personTmp.getFirstName();
				if (personTmp.getLastName().length() < person.getLastName()
						.length())
					lastName = person.getLastName();
				else
					lastName = personTmp.getLastName();

				personTmp = new Person(firstName, lastName, emails,
						person.getURI());
			}
			persons.put(personTmp.getName(), personTmp);
			personsByURIs.put(personTmp.getURI(), personTmp);
		}
	}

	@Override
	public Person getEntity(String name) {
		return persons.get(name);
	}

	@Override
	public Person getEntityByURI(URI uri) {
		return personsByURIs.get(uri);
	}
}
