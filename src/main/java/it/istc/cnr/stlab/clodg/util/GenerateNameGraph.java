package it.istc.cnr.stlab.clodg.util;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author annalisa
 *
 */
public class GenerateNameGraph {

	// static String SERVICE = "http://dbpedia.org/sparql";
	static String SERVICE = "http://data.semanticweb.org/sparql";

	static String PREFIX = "PREFIX dbpprop: <http://dbpedia.org/property/> \n"
			+ "PREFIX dbpedia: <http://dbpedia.org/resource/> \n"
			+ "PREFIX dbpedia-owl: <http://dbpedia.org/> \n"
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n";

	// static String SERVICE = "http://data.linkedmdb.org/sparql";

	// static String SERVICE = "http://sparql.sindice.com/sparql";
	// static String SERVICE = "http://lod.openlinksw.com/sparql";

	public static String getSERVICE() {
		return SERVICE;
	}

	public static void setSERVICE(String sERVICE) {
		SERVICE = sERVICE;
	}

	public static String getPREFIX() {
		return PREFIX;
	}

	public static void setPREFIX(String pREFIX) {
		PREFIX = pREFIX;
	}

	/**
	 * 
	 * @param service
	 * @param sparqlQueryString
	 * @param solutionConcept
	 *            the target concept to extract from the query, tye name should
	 *            be the same as the name of the target variable in
	 *            sparqlQueryString
	 * @return the set of string from literal results
	 */
	public Set<String> queryService(String service, String sparqlQueryString,
			String solutionConcept) {
		Set<String> resulStrings = new HashSet<String>();

		Query query = QueryFactory.create(sparqlQueryString);

		QueryExecution qexec = QueryExecutionFactory.sparqlService(service,
				query);

		try {
			ResultSet results = qexec.execSelect();

			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();

				Literal l;
				try {
					if (soln.get(solutionConcept).isLiteral()) {
						l = soln.getLiteral(solutionConcept);
						resulStrings.add(l.getString());
					} else {
						RDFNode u = soln.get(solutionConcept);
						resulStrings.add(u.toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} finally {
			qexec.close();
		}
		return resulStrings;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GenerateNameGraph.generateGazeteer("./cacheCloudGazeteer"
				+ File.separator + "book");

	}

	public static void generateGazeteer(String resFolder) {
		String service = getSERVICE();
		generateGazeteer(resFolder, service);

	}

	/**
	 * @param args
	 */
	public static void generateGazeteer(String resFolder, String... service) {
		GenerateNameGraph queryDBPedia = new GenerateNameGraph();

		File book = new File(resFolder);
		if (!book.exists())
			book.mkdirs();

		String prefix = GenerateNameGraph.getPREFIX();

		String sparqlQueryString1;
		String sparqlQueryString2;

		sparqlQueryString1 = "CONSTRUCT { ?x <http://xmlns.com/foaf/0.1/name> ?name} WHERE { ?x <http://xmlns.com/foaf/0.1/name> ?name }";

		sparqlQueryString2 = prefix + '\n'
				+ "SELECT DISTINCT * WHERE { ?s ?p ?o } LIMIT 10";

		Query query = QueryFactory.create(sparqlQueryString1);

		QueryExecution qexec = QueryExecutionFactory.sparqlService(service[0],
				query);
		Model resultModel = qexec.execConstruct();
		qexec.close();

		try {
			OutputStream out = new FileOutputStream("./out/names.rdf");

			resultModel.write(out);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(resultModel);

		// gaz1 = queryDBPedia.queryService(
		// service, sparqlQueryString1, "name");

		Set<String> gaz1 = null;
		for (String s : service) {
			gaz1 = queryDBPedia.queryService(s, sparqlQueryString2, "p");
		}

		System.out.println("gazeteer size: " + gaz1.size());
		System.out.println(gaz1);

	}
}
