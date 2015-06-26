package org.eswc.conferences.data;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;

public class AppIDCreator {

	public void create(Model model) {

		Property id = model
				.createProperty("http://www.ontologydesignpatterns.org/ont/eswc/ontology.owl#eswcId");

		String sparql = "SELECT ?person " + "WHERE{?person a <" + FOAF.Person
				+ ">}";
		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				model);

		ResultSet resultSet = queryExecution.execSelect();
		for (int i = 1; resultSet.hasNext(); i++) {
			QuerySolution solution = resultSet.next();
			Resource person = solution.getResource("person");
			person.addLiteral(id,
					model.createTypedLiteral(i, XSDDatatype.XSDpositiveInteger));
		}
	}

}
