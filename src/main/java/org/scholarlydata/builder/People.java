package org.scholarlydata.builder;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDF;

public class People {

	private Model model;
	
	public People(Model model) {
		this.model = model;
	}
	
	public Collection<Person> list(){
		Collection<Person> personList = new ArrayList<Person>();
	
		StmtIterator stmtIterator = model.listStatements(null, RDF.type, FOAF.Person);
		while(stmtIterator.hasNext()){
			Statement stmt = stmtIterator.next();
			Resource personResource = stmt.getSubject();
			Person person = new Person(personResource);
			personList.add(person);
		}
		return personList;
	}
	
}
