package it.istc.cnr.stlab.clodg.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.VEvent;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * @author annalisa
 * this class has been replaced by it.istc.cnr.stlab.clodg.workflow.GenerateAppData
 */
@Deprecated 
public class GenerateAppData {

	Map<String, String> hashtags;
	Map<String, String> widgets;

	public GenerateAppData() {
		this.hashtags = new HashMap<String, String>();
		this.widgets = new HashMap<String, String>();
	}

	public GenerateAppData(Model paperRdf) {
		super();
		this.paperRdf = paperRdf;
		this.paperTitles = this.getTitles(this.paperRdf);
	}

	public GenerateAppData(Model paperRdf, Model[] calendarRdf) {
		super();
		this.paperRdf = paperRdf;
		this.calendarRdf = calendarRdf;
		this.paperTitles = this.getTitles(this.paperRdf);
		this.allEvents = this.getSummaries(this.calendarRdf);
		this.paperEvents = this.generateEventPaperAlignment();
	}

	private Map<String, String> generateEventPaperAlignment() {
		Map<String, String> pe = new HashMap<String, String>();

		// System.out.println("number of events in model: "+this.getAllEvents().size());

		for (Entry<String, String> t : this.getAllEvents().entrySet()) {
			// System.out.println(t.getValue());
			// System.out.println(this.getIdForPaper(t.getValue()) +
			// " has event: "+t.getKey());
			String paperId = this.getIdForPaper(t.getValue());

			if (paperId != null) {
				pe.put(paperId, t.getKey());
			} else {
				System.err.println(t.getValue()
						+ " is not a paper presentation");
			}
		}
		return pe;

	}

	Map<String, String> paperTitles; // paper URI, paper title
	Map<String, String> allEvents; // eventID, summary
	Map<String, String> paperEvents; // paper URI, eventID

	public Map<String, String> getAllEvents() {
		return allEvents;
	}

	public void setAllEvents(Map<String, String> paperEvents) {
		this.allEvents = paperEvents;
	}

	Model paperRdf;
	Model[] calendarRdf;

	public Map<String, String> getPaperTitles() {
		return paperTitles;
	}

	public void setPaperTitles(Map<String, String> paperTitles) {
		this.paperTitles = paperTitles;
	}

	public String getIdForPaper(String paper) {
		paper = paper.toLowerCase();
		for (Entry<String, String> s : this.getPaperTitles().entrySet()) {
			String tit = s.getValue().toLowerCase();
			if (paper.contains(tit))
				return s.getKey();

		}
		return null;
	}

	/**
	 * generates the map of paperId - paperTitle
	 * 
	 * @param model
	 * @return
	 */
	public Map<String, String> getTitles(Model model) {
		Map<String, String> titlesMap = new HashMap<String, String>();
		// TODO check why we have two different expressions for titles
		String sparql = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
				+ "PREFIX dc-terms: <http://purl.org/dc/terms/> "
				+ "SELECT * WHERE {" +

				"{" + "SELECT ?paper ?title " + "WHERE{"
				+ "    ?paper dc:title ?title" + "}" + "}" + "UNION" +

				"{" + "SELECT ?paper ?title " + "WHERE{"
				+ "    ?paper dc-terms:title ?title" + "}" + "}" + "}";

		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				model);
		ResultSet resultSet = queryExecution.execSelect();
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			Resource paper = querySolution.getResource("paper");
			Literal title = querySolution.getLiteral("title");
			String titleString = title.getLexicalForm().replaceAll(" +", " ");
			if (titleString.endsWith("."))
				titleString = titleString
						.substring(0, titleString.length() - 1);
			titlesMap.put(paper.getURI(), titleString);
		}

		return titlesMap;
	}

	/**
	 * generates the map of paperId - paperTitle
	 * 
	 * @param model
	 * @return
	 */
	public Map<String, String> getSummaries(Model[] models) {

		Map<String, String> titlesMap = new HashMap<String, String>();
		for (Model model : models) {
			String sparql = "PREFIX ical: <http://www.w3.org/2002/12/cal/icaltzd#> "
					+ "SELECT ?event ?summary "
					+ "WHERE{"
					+ "    ?event ical:summary ?summary" + "}";
			Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
			QueryExecution queryExecution = QueryExecutionFactory.create(query,
					model);
			ResultSet resultSet = queryExecution.execSelect();
			while (resultSet.hasNext()) {
				QuerySolution querySolution = resultSet.next();
				Resource paper = querySolution.getResource("event");
				Literal title = querySolution.getLiteral("summary");
				String titleString = title.getLexicalForm().replaceAll(" +",
						" ");
				titlesMap.put(paper.getURI(), titleString);
			}
		}
		return titlesMap;
	}

	/**
	 * this method takes as input a calendar file and a RDF model for each paper
	 * in the RDF model, it searches for an event with SUMMARY containing the
	 * paper title, if so, saves a single calendar event named as the paper URI
	 * 
	 * @param caledarFilePath
	 * @param model
	 * @param saveTo
	 *            path where to save single events
	 */
	public void generateSingleEvents(String caledarFilePath, String saveTo) {

		new File(saveTo).mkdirs();
		String OPEN = "BEGIN:VCALENDAR";

		String CLOSE = "END:VCALENDAR";

		FileInputStream fin;
		try {
			fin = new FileInputStream(caledarFilePath);

			CalendarBuilder builder = new CalendarBuilder();

			Calendar calendar = builder.build(fin);

			ComponentList c = calendar.getComponents();
			System.out.println("Calendar contains " + c.size() + " events");
			System.out.println(calendar.getProperties());

			Iterator it = c.iterator();

			while (it.hasNext()) {
				VEvent e = (VEvent) it.next();
				// System.out.println(e.getSummary());
				String summary = e.getSummary().getValue();
				summary = summary.replaceAll(" +", " ");

				String id = this.getIdForPaper(summary);
				if (id != null) {
					// System.out.println(summary);
					// System.out.println(id);

					// TODO PrinterOut
					String fileName = id.substring(id
							.indexOf("/eswc/2014/paper"));
					fileName = fileName.replaceAll(File.separator, "_");
					PrintWriter out = new PrintWriter(new FileWriter(saveTo
							+ File.separator + fileName + ".ics"));
					out.write(OPEN + "\n");

					out.write(calendar.getProperties().toString());
					out.write(e.toString());
					out.write(CLOSE);
					out.close();

				} else {
					System.out.println("*** no id for " + summary);

				}
			}

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * generates the map of paperUri - paperNumericID
	 * 
	 * @param model
	 * @return
	 */
	public static Map<String, Integer> generatePaperIds(Model model) {
		Map<String, Integer> titlesMap = new HashMap<String, Integer>();
		int id = 0;

		// TODO check why we have two different expressions for titles
		String sparql = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+

				"SELECT ?paper "
				+ "WHERE{"
				+ "    ?paper rdf:type <http://swrc.ontoware.org/ontology#InProceedings>"
				+ "}";

		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				model);
		ResultSet resultSet = queryExecution.execSelect();
		while (resultSet.hasNext()) {
			id = id + 1;
			String paperId = "1" + Integer.toString(id);

			QuerySolution querySolution = resultSet.next();
			Resource paper = querySolution.getResource("paper");

			titlesMap.put(paper.getURI(), Integer.parseInt(paperId));
		}

		return titlesMap;
	}

	/**
	 * generates the map of personUri - personNumericID
	 * 
	 * @param model
	 * @return
	 */
	public static Map<String, Integer> generatePpersonIds(Model model) {
		Map<String, Integer> titlesMap = new HashMap<String, Integer>();
		int id = 0;

		// TODO check why we have two different expressions for titles
		String sparql = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+

				"SELECT ?paper "
				+ "WHERE{"
				+ "    ?paper rdf:type <http://xmlns.com/foaf/0.1/Person>"
				+ "}";

		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				model);
		ResultSet resultSet = queryExecution.execSelect();
		while (resultSet.hasNext()) {
			id = id + 1;
			String personId = "2" + Integer.toString(id);

			QuerySolution querySolution = resultSet.next();
			Resource paper = querySolution.getResource("paper");

			titlesMap.put(paper.getURI(), Integer.parseInt(personId));
		}

		return titlesMap;
	}

	/**
	 * generates the map of eventUri - eventNumericID
	 * 
	 * @param model
	 * @return
	 */
	public static Map<String, Integer> generateEventIds(Model model) {
		Map<String, Integer> titlesMap = new HashMap<String, Integer>();
		int id = 0;

		// TODO check why we have two different expressions for titles
		String sparql = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+

				"SELECT ?paper "
				+ "WHERE{"
				+ "    ?paper rdf:type <http://www.w3.org/2002/12/cal/icaltzd#Vevent>"
				+ "}";

		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				model);
		ResultSet resultSet = queryExecution.execSelect();
		while (resultSet.hasNext()) {
			id = id + 1;
			String personId = "3" + Integer.toString(id);

			QuerySolution querySolution = resultSet.next();
			Resource paper = querySolution.getResource("paper");

			titlesMap.put(paper.getURI(), Integer.parseInt(personId));
		}

		return titlesMap;
	}

	/**
	 * generates the map of paperId - paperTitle
	 * 
	 * @param model
	 * @return
	 */
	public static Map<String, String> getLabels(Model model) {

		Map<String, String> titlesMap = new HashMap<String, String>();
		String sparql = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "SELECT ?uri ?l " + "WHERE{" + "    ?uri rdfs:label ?l" + "}";
		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				model);
		ResultSet resultSet = queryExecution.execSelect();
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			Resource paper = querySolution.getResource("uri");
			Literal title = querySolution.getLiteral("l");
			String titleString = title.getLexicalForm().replaceAll(" +", " ");
			titlesMap.put(paper.getURI(), titleString);
		}

		return titlesMap;
	}

	public void generate(Model model, Model idsModel) {

		String sparql = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX swrc: <http://swrc.ontoware.org/ontology#> "
				+ "PREFIX ical: <http://www.w3.org/2002/12/cal/icaltzd> "
				+ "PREFIX foaf: <" + FOAF.NS + "> " + "SELECT ?x " + "WHERE{"
				+ "    ?x rdfs:label ?label ." + "    {?x a foaf:Person}"
				+ "    UNION " + "    {?x a swrc:InProceedings}" + "    UNION "
				+ "    {?x a ical:Vevent}" + "}";

		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				model);
		ResultSet resultSet = queryExecution.execSelect();
		Set<Resource> resources = new HashSet<Resource>();
		while (resultSet.hasNext()) {
			resources.add(resultSet.next().getResource("x"));
		}
		for (Resource resource : resources) {
			model.removeAll(resource, RDFS.label, (RDFNode) null);
		}
		model.removeAll(
				null,
				model.createProperty("http://www.ontologydesignpatterns.org/ont/eswc/ontology.owl#hashtag"),
				(RDFNode) null);
		model.removeAll(
				null,
				model.createProperty("http://www.ontologydesignpatterns.org/ont/eswc/ontology.owl#widted"),
				(RDFNode) null);
		model.add(idsModel);
	}

	public void fetchIds(Model model) {
		String sparql = "PREFIX rdfs: <"
				+ RDFS.getURI()
				+ "> "
				+ "PREFIX eswc: <http://www.ontologydesignpatterns.org/ont/eswc/ontology.owl#> "
				+ "CONSTRUCT {" + "    ?x rdfs:label ?label . "
				+ "    ?x eswc:id ?id . " + "    ?x eswc:hashtag ?hashtag . "
				+ "    ?x eswc:widget ?widget " + "} " + "WHERE {"
				+ "    ?x rdfs:label ?label . " + "    ?x eswc:id ?id . "
				+ "    OPTIONAL{?x eswc:hashtag ?hashtag} .  "
				+ "    OPTIONAL{?x eswc:widget ?widget} " + "}";
		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				model);
		Model idsModel = queryExecution.execConstruct();

		idsModel.write(System.out);

		OutputStream out = null;
		try {
			out = new FileOutputStream("./out/appIDs.rdf");
			idsModel.write(out);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		new GenerateAppData().fetchIds(FileManager.get().loadModel(
				"out/papersForApp.rdf"));

	}

	public Map<String, String> getPaperEvents() {
		return paperEvents;
	}

	public void setPaperEvents(Map<String, String> paperEvents) {
		this.paperEvents = paperEvents;
	}

}
