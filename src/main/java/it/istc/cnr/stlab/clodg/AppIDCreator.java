package it.istc.cnr.stlab.clodg;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;

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
