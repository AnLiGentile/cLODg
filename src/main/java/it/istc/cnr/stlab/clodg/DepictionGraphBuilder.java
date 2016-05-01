package it.istc.cnr.stlab.clodg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.vocabulary.FOAF;

public class DepictionGraphBuilder implements RDFGraphBuilder {

	@Override
	public Model buildRDF(File directory) {
		Model modelout = ModelFactory.createDefaultModel();
		Model model = ModelFactory.createDefaultModel();

		try {
			model.read(new FileInputStream(directory), null, "RDF/XML");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!model.isEmpty()) {
			String sparql = "PREFIX foaf: <" + FOAF.NS + "> "
					+ "CONSTRUCT {?author foaf:depiction ?depiction} "
					+ "WHERE{?author foaf:depiction ?depiction}";

			Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
			QueryExecution queryExecution = QueryExecutionFactory.create(query,
					model);
			modelout = queryExecution.execConstruct();
		}
		return modelout;
	}

	protected Set<String> getDepictionList(Model model) {

		Set<String> depictions = new HashSet<String>();
		String sparql = "PREFIX foaf: <" + FOAF.NS + "> "
				+ "SELECT ?depiction "
				+ "WHERE{?author foaf:depiction ?depiction}";

		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				model);
		ResultSet resultSet = queryExecution.execSelect();

		while (resultSet.hasNext()) {
			depictions.add(resultSet.next().get("depiction").toString());
		}

		return depictions;
	}

	public static void main(String[] args) {
		DepictionGraphBuilder depictionGraphBuilder = new DepictionGraphBuilder();
		Model model = depictionGraphBuilder.buildRDF(new File(
				"out/eswc_data_final.rdf"));
		Set<String> depinctions = depictionGraphBuilder.getDepictionList(model);

		try {
			OutputStream outputStream = new FileOutputStream(new File(
					"out/depictions.rdf"));
			model.write(outputStream);
			outputStream.flush();
			outputStream.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
					"out/depictions.txt"));
			for (String depiction : depinctions) {
				bufferedWriter.write(depiction.trim());
				bufferedWriter.newLine();
			}

			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
