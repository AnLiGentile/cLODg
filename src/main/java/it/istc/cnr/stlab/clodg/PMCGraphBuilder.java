//package it.istc.cnr.stlab.clodg;
//
//import it.istc.cnr.stlab.clodg.util.OfficialNameSpace;
//import it.istc.cnr.stlab.clodg.util.SwdfIRI;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//
//
//import com.hp.hpl.jena.query.Query;
//import com.hp.hpl.jena.query.QueryExecution;
//import com.hp.hpl.jena.query.QueryExecutionFactory;
//import com.hp.hpl.jena.query.QueryFactory;
//import com.hp.hpl.jena.query.Syntax;
//import com.hp.hpl.jena.rdf.model.Model;
//import com.hp.hpl.jena.rdf.model.ModelFactory;
//import com.hp.hpl.jena.rdf.model.RDFNode;
//import com.hp.hpl.jena.sparql.function.FunctionRegistry;
//import com.hp.hpl.jena.sparql.vocabulary.FOAF;
//import com.hp.hpl.jena.util.FileManager;
//
//public class PMCGraphBuilder implements RDFGraphBuilder {
//
//	public PMCGraphBuilder(OfficialNameSpace ns) {
//		FunctionRegistry.get().put(
//				ns.basePaperEswcNs + "person", SwdfIRI.class);
//	}
//
//	@Override
//	public Model buildRDF(File directory) {
//		Model model = ModelFactory.createDefaultModel();
//
//		File[] files = directory.listFiles();
//		for (File file : files) {
//			Model m = ModelFactory.createDefaultModel();
//			try {
//				m.read(new FileInputStream(file), null, "RDF/XML");
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			if (!m.isEmpty()) {
//
//				String sparql = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
//						+ "PREFIX swc: <http://data.semanticweb.org/ns/swc/ontology#> "
//						+ "PREFIX eswc: <http://data.semanticweb.org/conference/eswc/2014/> "
//						+ "CONSTRUCT {"
//						+ "    ?personR a foaf:Person . "
//						+ "    ?personR foaf:firstName ?firstNameR . "
//						+ "    ?personR foaf:lastName ?lastNameR . "
//						+ "    ?personR foaf:name ?nameR . "
//						+ "    ?personR foaf:mbox ?mboxURI . "
//						+ "    ?personR foaf:depiction ?depiction . "
//						+ "    ?personR swc:holdsRole ?role . "
//						+ "    ?personR foaf:account ?account . "
//						+ "    ?account ?p ?o . "
//						+ "}"
//						+ "WHERE{ "
//						+ "    ?person a foaf:Person . "
//						+ "    ?person foaf:firstName ?firstName . "
//						+ "    ?person foaf:lastName ?lastName . "
//						+ "    ?person foaf:name ?name . "
//						+ "    ?person foaf:mbox ?mbox . "
//						+ "    ?person foaf:depiction ?depiction . "
//						+ "    ?person swc:holdsRole ?role . "
//						+ "    ?person foaf:account ?account . "
//						+ "    ?account ?p ?o . "
//						+ "    BIND(IRI(eswc:person(?firstName, ?lastName)) AS ?personR) . "
//						+ "    BIND(REPLACE(STR(?firstName), \"_\", \" \") AS ?firstNameR) . "
//						+ "    BIND(REPLACE(STR(?lastName), \"_\", \" \") AS ?lastNameR) . "
//						+ "    BIND (IRI(CONCAT(\"mailto:\", STR(?mbox))) AS ?mboxURI) . "
//						+ "}";
//				Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
//				QueryExecution queryExecution = QueryExecutionFactory.create(
//						query, m);
//				model.add(queryExecution.execConstruct());
//				/*
//				 * StmtIterator stmtIterator = m.listStatements(null, FOAF.mbox,
//				 * (RDFNode)null); List<Statement> statementsToRemove = new
//				 * ArrayList<Statement>(); List<Statement> statementsToAdd = new
//				 * ArrayList<Statement>(); while(stmtIterator.hasNext()){
//				 * Statement statement = stmtIterator.next();
//				 * statementsToRemove.add(statement); Resource subject =
//				 * statement.getSubject();
//				 * 
//				 * RDFNode object = statement.getObject();
//				 * statementsToAdd.add(new StatementImpl(subject, FOAF.mbox,
//				 * m.createResource("mailto:" + object.toString()))); }
//				 * 
//				 * m.remove(statementsToRemove); m.add(statementsToAdd);
//				 * m.write(System.out);
//				 * 
//				 * model.add(m);
//				 */
//			}
//		}
//
//		model.removeAll(null, FOAF.depiction, (RDFNode) null);
//		model.add(FileManager.get().loadModel("out/depictions_pmc_resized.rdf"));
//
//		return model;
//	}
//
//	public static void main(String[] args) {
//
//		Model model = FileManager.get()
//				.loadModel("out/eswc_data_final_pmc.rdf");
//		String sparql = "PREFIX foaf: <" + FOAF.NS + "> "
//				+ "CONSTRUCT {?person foaf:depiction ?depiction} "
//				+ "WHERE{?person foaf:depiction ?depiction}";
//		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
//		QueryExecution queryExecution = QueryExecutionFactory.create(query,
//				model);
//		try {
//			OutputStream outputStream = new FileOutputStream(new File(
//					"out/depictions_pmc.rdf"));
//			queryExecution.execConstruct().write(outputStream);
//			outputStream.flush();
//			outputStream.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//}
