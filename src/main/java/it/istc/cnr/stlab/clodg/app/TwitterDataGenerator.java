//package it.istc.cnr.stlab.clodg.app;
//
//import it.istc.cnr.stlab.clodg.app.Aligner;
//import it.istc.cnr.stlab.clodg.util.OfficialNameSpace;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.io.PrintWriter;
//import java.io.UnsupportedEncodingException;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;
//import java.util.SortedMap;
//
//import net.fortuna.ical4j.data.CalendarBuilder;
//import net.fortuna.ical4j.data.ParserException;
//import net.fortuna.ical4j.model.Calendar;
//import net.fortuna.ical4j.model.ComponentList;
//import net.fortuna.ical4j.model.component.VEvent;
//
//import org.codehaus.jettison.json.JSONArray;
//import org.codehaus.jettison.json.JSONException;
//import org.codehaus.jettison.json.JSONObject;
//
//import au.com.bytecode.opencsv.CSVReader;
//
//import com.hp.hpl.jena.query.Query;
//import com.hp.hpl.jena.query.QueryExecution;
//import com.hp.hpl.jena.query.QueryExecutionFactory;
//import com.hp.hpl.jena.query.QueryFactory;
//import com.hp.hpl.jena.query.QuerySolution;
//import com.hp.hpl.jena.query.ResultSet;
//import com.hp.hpl.jena.query.Syntax;
//import com.hp.hpl.jena.rdf.model.Literal;
//import com.hp.hpl.jena.rdf.model.Model;
//import com.hp.hpl.jena.rdf.model.ModelFactory;
//import com.hp.hpl.jena.rdf.model.Property;
//import com.hp.hpl.jena.rdf.model.Resource;
//import com.hp.hpl.jena.util.FileManager;
//
//import edu.emory.mathcs.backport.java.util.TreeMap;
//
///**
// * @author annalisa
// *
// */
//public class TwitterDataGenerator implements Aligner {
//
//	Map<String, String> hashtags;
//	Map<String, String> widgets;
//
//	public TwitterDataGenerator() {
//		this.hashtags = new HashMap<String, String>();
//		this.widgets = new HashMap<String, String>();
//	}
//
//	public TwitterDataGenerator(Model paperRdf) {
//		super();
//		this.paperRdf = paperRdf;
//		this.paperTitles = this.getTitles(this.paperRdf);
//	}
//
//	public TwitterDataGenerator(Model paperRdf, Model[] calendarRdf) {
//		super();
//		this.paperRdf = paperRdf;
//		this.calendarRdf = calendarRdf;
//		this.paperTitles = this.getTitles(this.paperRdf);
//		this.allEvents = this.getSummaries(this.calendarRdf);
//		this.paperEvents = this.generateEventPaperAlignment();
//	}
//
//	private Map<String, String> generateEventPaperAlignment() {
//		Map<String, String> pe = new HashMap<String, String>();
//
//		// System.out.println("number of events in model: "+this.getAllEvents().size());
//
//		for (Entry<String, String> t : this.getAllEvents().entrySet()) {
//			// System.out.println(t.getValue());
//			// System.out.println(this.getIdForPaper(t.getValue()) +
//			// " has event: "+t.getKey());
//			String paperId = this.getIdForPaper(t.getValue());
//
//			if (paperId != null) {
//				pe.put(paperId, t.getKey());
//			} else {
//				System.err.println(t.getValue()
//						+ " is not a paper presentation");
//			}
//		}
//		return pe;
//
//	}
//
//	Map<String, String> paperTitles; // paper URI, paper title
//	Map<String, String> allEvents; // eventID, summary
//	Map<String, String> paperEvents; // paper URI, eventID
//
//	public Map<String, String> getAllEvents() {
//		return allEvents;
//	}
//
//	public void setAllEvents(Map<String, String> paperEvents) {
//		this.allEvents = paperEvents;
//	}
//
//	Model paperRdf;
//	Model[] calendarRdf;
//
//	public Map<String, String> getPaperTitles() {
//		return paperTitles;
//	}
//
//	public void setPaperTitles(Map<String, String> paperTitles) {
//		this.paperTitles = paperTitles;
//	}
//
//	public String getIdForPaper(String paper) {
//		paper = paper.toLowerCase();
//		for (Entry<String, String> s : this.getPaperTitles().entrySet()) {
//			String tit = s.getValue().toLowerCase();
//			if (paper.contains(tit))
//				return s.getKey();
//
//		}
//		return null;
//	}
//
//	/**
//	 * generates the map of paperId - paperTitle
//	 * 
//	 * @param model
//	 * @return
//	 */
//	public Map<String, String> getTitles(Model model) {
//		Map<String, String> titlesMap = new HashMap<String, String>();
//		// TODO check why we have two different expressions for titles
//		String sparql = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
//				+ "PREFIX dc-terms: <http://purl.org/dc/terms/> "
//				+ "SELECT * WHERE {" +
//
//				"{" + "SELECT ?paper ?title " + "WHERE{"
//				+ "    ?paper dc:title ?title" + "}" + "}" + "UNION" +
//
//				"{" + "SELECT ?paper ?title " + "WHERE{"
//				+ "    ?paper dc-terms:title ?title" + "}" + "}" + "}";
//
//		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
//		QueryExecution queryExecution = QueryExecutionFactory.create(query,
//				model);
//		ResultSet resultSet = queryExecution.execSelect();
//		while (resultSet.hasNext()) {
//			QuerySolution querySolution = resultSet.next();
//			Resource paper = querySolution.getResource("paper");
//			Literal title = querySolution.getLiteral("title");
//			String titleString = title.getLexicalForm().replaceAll(" +", " ");
//			if (titleString.endsWith("."))
//				titleString = titleString
//						.substring(0, titleString.length() - 1);
//			titlesMap.put(paper.getURI(), titleString);
//		}
//
//		return titlesMap;
//	}
//
//	/**
//	 * generates the map of paperId - paperTitle
//	 * 
//	 * @param model
//	 * @return
//	 */
//	public Map<String, String> getSummaries(Model[] models) {
//
//		Map<String, String> titlesMap = new HashMap<String, String>();
//		for (Model model : models) {
//			String sparql = "PREFIX ical: <http://www.w3.org/2002/12/cal/icaltzd#> "
//					+ "SELECT ?event ?summary "
//					+ "WHERE{"
//					+ "    ?event ical:summary ?summary" + "}";
//			Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
//			QueryExecution queryExecution = QueryExecutionFactory.create(query,
//					model);
//			ResultSet resultSet = queryExecution.execSelect();
//			while (resultSet.hasNext()) {
//				QuerySolution querySolution = resultSet.next();
//				Resource paper = querySolution.getResource("event");
//				Literal title = querySolution.getLiteral("summary");
//				String titleString = title.getLexicalForm().replaceAll(" +",
//						" ");
//				titlesMap.put(paper.getURI(), titleString);
//			}
//		}
//		return titlesMap;
//	}
//
//	/**
//	 * this method takes as input a calendar file and a RDF model for each paper
//	 * in the RDF model, it searches for an event with SUMMARY containing the
//	 * paper title, if so, saves a single calendar event named as the paper URI
//	 * 
//	 * @param caledarFilePath
//	 * @param model
//	 * @param saveTo
//	 *            path where to save single events
//	 */
//	public void generateSingleEvents(String caledarFilePath, String saveTo) {
//
//		new File(saveTo).mkdirs();
//		String OPEN = "BEGIN:VCALENDAR";
//
//		String CLOSE = "END:VCALENDAR";
//
//		FileInputStream fin;
//		try {
//			fin = new FileInputStream(caledarFilePath);
//
//			CalendarBuilder builder = new CalendarBuilder();
//
//			Calendar calendar = builder.build(fin);
//
//			ComponentList c = calendar.getComponents();
//			System.out.println("Calendar contains " + c.size() + " events");
//			System.out.println(calendar.getProperties());
//
//			Iterator it = c.iterator();
//
//			while (it.hasNext()) {
//				VEvent e = (VEvent) it.next();
//				// System.out.println(e.getSummary());
//				String summary = e.getSummary().getValue();
//				summary = summary.replaceAll(" +", " ");
//
//				String id = this.getIdForPaper(summary);
//				if (id != null) {
//					// System.out.println(summary);
//					// System.out.println(id);
//
//					// TODO PrinterOut
//					String fileName = id.substring(id
//							.indexOf("/eswc/2014/paper"));
//					fileName = fileName.replaceAll(File.separator, "_");
//					PrintWriter out = new PrintWriter(new FileWriter(saveTo
//							+ File.separator + fileName + ".ics"));
//					out.write(OPEN + "\n");
//
//					out.write(calendar.getProperties().toString());
//					out.write(e.toString());
//					out.write(CLOSE);
//					out.close();
//
//				} else {
//					System.out.println("*** no id for " + summary);
//
//				}
//			}
//
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ParserException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//
//	}
//
//	// TODO change this to extract calendar ids
//	@Override
//	public Model align(JSONObject jsonObject, Model model) {
//		try {
//			JSONArray articles = jsonObject.getJSONArray("articles");
//
//			Property property = model
//					.createProperty("http://2014.eswc-conferences.org/ontology/calendarWidget");
//			for (int i = 0, j = articles.length(); i < j; i++) {
//				JSONObject article = articles.getJSONObject(i);
//
//				String id = article.getString("id");
//				String twitterWidget = article.getString("calendar_widget");
//
//				// TODO must be done for phdSymp, poster and demo, and WS
//
//				String sparql = "SELECT ?article WHERE{?article a <http://swrc.ontoware.org/ontology#InProceedings> . FILTER(STR(?article) = \"http://data.semanticweb.org/conference/eswc/2014/paper/research/"
//						+ id
//						+ "\" || STR(?article) = \"http://data.semanticweb.org/conference/eswc/2014/paper/in-use/"
//						+ id + "\") }";
//
//				Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
//				QueryExecution queryExecution = QueryExecutionFactory.create(
//						query, model);
//				ResultSet resultSet = queryExecution.execSelect();
//
//				if (resultSet.hasNext()) {
//					QuerySolution querySolution = resultSet.next();
//					Resource articleResource = querySolution
//							.getResource("article");
//					if (articleResource != null) {
//						articleResource.addLiteral(property, twitterWidget);
//					}
//				}
//
//				/*
//				 * String uri =
//				 * "http://data.semanticweb.org/conference/eswc/2014/paper/research/"
//				 * + id;
//				 * 
//				 * Resource articleResource = model.getResource(uri);
//				 * 
//				 * System.out.println(uri);
//				 * 
//				 * if(articleResource != null){
//				 * articleResource.addLiteral(property, twitterWidget); }
//				 */
//			}
//
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return model;
//	}
//
//	public void addCalendar(Model model) {
//
//		Model calendarmodel[] = new Model[1];
//
//		calendarmodel[0] = FileManager.get().loadModel(
//				"out/mainConfCalendar.rdf");
//
//		model.add(calendarmodel[0]);
//		// calendarmodel[0] =
//		// FileManager.get().loadModel("out/workshopsCalendar.rdf");
//
//		// TODO for each paper in the final rdf
//		// serach the rdf calendar for a related evant
//		// use the function getTitles(Model model) to id-titles
//		// and generate properties
//
//		TwitterDataGenerator aligner = new TwitterDataGenerator(model,
//				calendarmodel);
//
//		SortedMap<String, String> papers = new TreeMap();
//		for (Entry<String, String> e : aligner.getPaperTitles().entrySet()) {
//			papers.put(e.getKey(), e.getValue());
//		}
//
//		System.out.println("number of papers in model: " + papers.size());
//
//		for (Entry<String, String> t : papers.entrySet()) {
//			System.out.println(t.getKey() + "\t" + t.getValue());
//		}
//
//		System.out.println("number of events in model: "
//				+ aligner.getAllEvents().size());
//
//		Map<String, String> events = aligner.getPaperEvents();
//
//		// for (Entry<String, String> t : events.entrySet()){
//		// System.out.println(t.getValue() + " presents paper: "+t.getKey());
//		// }
//
//		for (Entry<String, String> t : papers.entrySet()) {
//			if (events.keySet().contains(t.getKey())) {
//				// TODO all the content of this if block is just to test
//				Resource paper = model.createResource(t.getKey());
//				//
//				// Property predicate = model.createProperty("isPresented");
//
//				Property isPresentedPredicate = model
//						.createProperty("http://www.ontologydesignpatterns.org/ont/eswc/ontology.owl#isPresented");
//				Property presentsPredicate = model
//						.createProperty("http://www.ontologydesignpatterns.org/ont/eswc/ontology.owl#presents");
//
//				String eventId = aligner.getPaperEvents().get(t.getKey());
//
//				/*
//				 * 
//				 * String rep =
//				 * "file:///Users/annalisa/Documents/LODIEws/ESWC2014/data/out/mainConfCalendar.rdf"
//				 * ; id = id.substring(rep.length());
//				 */
//				Resource event = model.createResource(eventId);
//				System.out.println(t.getKey() + "\t" + event.getURI());
//
//				event.addProperty(presentsPredicate, paper);
//				paper.addProperty(isPresentedPredicate, event);
//
//				// Resource object = model.createResource(l.getString());
//				// model.addLiteral(subjet, predicate, l);
//				// Statement s = model.createLiteralStatement(subjet, predicate,
//				// l);
//
//				// TODO Andrea help me :)
//				// model.add(s);
//			} else {
//				// System.err.println(t.getKey()+"\t"+t.getValue());
//			}
//
//		}
//
//		OutputStream out = null;
//		try {
//			out = new FileOutputStream("./out/testAddCalendar.rdf");
//			model.write(out);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		// aligner.generateSingleEvents("in/ESWC2014 - main conference_iqll2vaabv1khlfmlt9mq9co48@group.calendar.google.com.ics",
//		// "calendarSingle");
//		// TODO we need data of workshop papers in the rdf to generate the
//		// single calebdar files
//		// aligner.generateSingleEvents("in/ESWC 2014 - workshops_sg200d1e8pk7qpvah40it0s6dg@group.calendar.google.com.ics",
//		// "calendarSingleWS");
//
//	}
//
//	public static Map<String, String> getWidget(CSVReader csvReader) {
//		int column = 4;
//		Map<String, String> ids = getWidget(csvReader, column);
//		return ids;
//	}
//
//	public static Map<String, String> getWidget(CSVReader csvReader, int column) {
//
//		Map<String, String> ids = new HashMap<String, String>();
//
//		try {
//			String[] line = null;
//			while ((line = csvReader.readNext()) != null) {
//				String id = line[0].trim();
//				String hashtag = line[column].trim();
//
//				ids.put(id, hashtag);
//
//			}
//			// csvReader.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return ids;
//
//	}
//
//	public void populateTwitterPD(CSVReader csvReader, int h, int c, int w) {
//
//		String prefix = OfficialNameSpace.mainTrackPaperNs;
//
//		try {
//			String[] line = null;
//			while ((line = csvReader.readNext()) != null) {
//				String id = line[0].trim();
//
//				if (line[c].trim().equalsIgnoreCase("P")) {
//					id = prefix + "poster/" + id;
//				} else {
//					id = prefix + "demo/" + id;
//				}
//
//				String hashtag = line[h].trim();
//				String widget = line[w].trim();
//
//				System.out.println("WIDGET " + widget);
//
//				this.hashtags.put(id, hashtag);
//				this.widgets.put(id, widget);
//
//			}
//			csvReader.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	public void populateTwitter(String prefix, CSVReader csvReader, int h, int w) {
//
//		try {
//			String[] line = null;
//			while ((line = csvReader.readNext()) != null) {
//				String id = line[0].trim();
//				id = prefix + id;
//				String hashtag = line[h].trim();
//				String widget = line[w].trim();
//
//				System.out.println("WIDGET " + widget);
//
//				this.hashtags.put(id, hashtag);
//				this.widgets.put(id, widget);
//
//			}
//			// csvReader.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	public void populateTwitterMain(CSVReader csvReader, int h, int w, int c) {
//
//		String prefix = OfficialNameSpace.mainTrackPaperNs;
//
//		try {
//			String[] line = null;
//			while ((line = csvReader.readNext()) != null) {
//				String id = line[0].trim();
//				if (line[c].trim().equalsIgnoreCase("research")) {
//					id = prefix + "research/" + id;
//				} else {
//					id = prefix + "in-use/" + id;
//				}
//
//				String hashtag = line[h].trim();
//				String widget = line[w].trim();
//
//				this.hashtags.put(id, hashtag);
//				this.widgets.put(id, widget);
//
//			}
//			// csvReader.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	public static Map<String, String> getHashtags(CSVReader csvReader) {
//		int column = 3;
//		Map<String, String> ids = getHashtags(csvReader, column);
//		return ids;
//	}
//
//	public static Map<String, String> getHashtags(CSVReader csvReader,
//			int column) {
//
//		Map<String, String> ids = new HashMap<String, String>();
//
//		try {
//			String[] line = null;
//			while ((line = csvReader.readNext()) != null) {
//				String id = line[0].trim();
//				String hashtag = line[column].trim();
//
//				ids.put(id, hashtag);
//
//			}
//			// csvReader.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return ids;
//
//	}
//
//	public Model addTwitterWidget(Model model) {
//		try {
//
//			// Model model =
//			// FileManager.get().loadModel("out/eswc_data_final_swdf.rdf");
//
//			Map<String, CSVReader> cvs = new HashMap<String, CSVReader>();
//			Set<String> allPapers = new HashSet<String>();
//
//			File wsFolder = new File("WScsv");
//
//			CSVReader csvReader1 = new CSVReader(new InputStreamReader(
//					new FileInputStream(new File(
//							"in/allDataCleaned/eswc_linkedUp.txt")), "UTF-8"),
//					'\t');
//			CSVReader csvReader2 = new CSVReader(new InputStreamReader(
//					new FileInputStream(new File(
//							"in/allDataCleaned/eswc_mashUp.txt")), "UTF-8"),
//					'\t');
//			CSVReader csvReader3 = new CSVReader(
//					new InputStreamReader(new FileInputStream(new File(
//							"in/allDataCleaned/eswc_posterDemo.txt")), "UTF-8"),
//					'\t');
//			CSVReader csvReader4 = new CSVReader(new InputStreamReader(
//					new FileInputStream(new File(
//							"in/allDataCleaned/eswc_main.txt")), "UTF-8"), '\t');
//			CSVReader csvReader5 = new CSVReader(new InputStreamReader(
//					new FileInputStream(new File(
//							"in/allDataCleaned/eswc_phdSymp.txt")), "UTF-8"),
//					'\t');
//			CSVReader csvReader6 = new CSVReader(new InputStreamReader(
//					new FileInputStream(new File(
//							"in/allDataCleaned/semeval.txt")), "UTF-8"), '\t');
//
//			cvs.put(OfficialNameSpace.mainTrackPaperNs + "linkedUp/",
//					csvReader1);
//			cvs.put(OfficialNameSpace.mainTrackPaperNs + "mashUp/", csvReader2);
//			cvs.put(OfficialNameSpace.mainTrackPaperNs + "phd-symposium/",
//					csvReader5);
//			cvs.put(OfficialNameSpace.mainTrackPaperNs + "semeval/", csvReader6);
//
//			for (Entry<String, CSVReader> c : cvs.entrySet()) {
//				populateTwitter(c.getKey(), c.getValue(), 3, 4);
//
//			}
//
//			populateTwitterMain(csvReader4, 4, 5, 1);
//			populateTwitterPD(csvReader3, 4, 3, 5);
//
//			//
//			// for (Entry<String, String> h : hashtags.entrySet()){
//			// System.out.println(h.getKey()+"--> "+h.getValue());
//			// }
//			//
//			// for (Entry<String, String> h : widgets.entrySet()){
//			// System.out.println(h.getKey()+"--> "+h.getValue());
//			// }
//			//
//			System.out.println("widgets before--> " + widgets.size());
//			System.out.println("hashtags before--> " + hashtags.size());
//
//			if (wsFolder.isDirectory()) {
//				for (File f : wsFolder.listFiles()) {
//					if (f.getName().endsWith(".txt")) {
//						String wsName = f.getName().substring(0,
//								f.getName().lastIndexOf(".txt"));
//
//						try {
//							CSVReader csvReader = new CSVReader(
//									new InputStreamReader(
//											new FileInputStream(f), "UTF-8"),
//									'\t');
//							populateTwitter(OfficialNameSpace.mainTrackPaperNs
//									+ "ws/" + wsName + "/", csvReader, 3, 4);
//							// OutputStream out = new FileOutputStream(new
//							// File(wsOUTFolder+File.separator+wsName+".rdf"));
//							// model.write(out);
//						} catch (FileNotFoundException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//				}
//			} else {
//				System.out
//						.println("Indicate the workshop folder, with a CSV file for each workshop");
//			}
//
//			System.out.println("widgets after--> " + widgets.size());
//			System.out.println("hashtags after --> " + hashtags.size());
//			// populate
//
//			for (Entry<String, String> t : hashtags.entrySet()) {
//
//				Resource paper = model.createResource(t.getKey());
//				//
//				// Property predicate = model.createProperty("isPresented");
//
//				Property hashtagPredicate = model
//						.createProperty("http://www.ontologydesignpatterns.org/ont/eswc/ontology.owl#hashtag");
//				Property widgetPredicate = model
//						.createProperty("http://www.ontologydesignpatterns.org/ont/eswc/ontology.owl#widget");
//
//				paper.removeAll(hashtagPredicate);
//				paper.addProperty(hashtagPredicate, t.getValue());
//				System.out.println(t.getKey() + "\t" + paper.getURI());
//
//				if (widgets.get(t.getKey()) != null) {
//					paper.addProperty(widgetPredicate, widgets.get(t.getKey()));
//				} else {
//					System.out.println(t.getKey() + "\t has no widget");
//				}
//
//			}
//
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return model;
//
//	}
//
//	public static void main(String[] args) {
//
//		// TODO Auto-generated method stub
//		try {
//
//			Model model = FileManager.get().loadModel(
//					"out/eswc_data_final_swdf.rdf");
//			Model model2 = ModelFactory.createDefaultModel();
//
//			Map<String, CSVReader> cvs = new HashMap<String, CSVReader>();
//			Set<String> allPapers = new HashSet<String>();
//
//			CSVReader csvReader1 = new CSVReader(new FileReader(new File(
//					"in/allDataCleaned/eswc_linkedUp.txt")), '\t');
//			CSVReader csvReader2 = new CSVReader(new FileReader(new File(
//					"in/allDataCleaned/eswc_mashUp.txt")), '\t');
//			CSVReader csvReader3 = new CSVReader(new FileReader(new File(
//					"in/allDataCleaned/eswc_posterDemo.txt")), '\t');
//			CSVReader csvReader4 = new CSVReader(new FileReader(new File(
//					"in/allDataCleaned/eswc_main.txt")), '\t');
//			CSVReader csvReader5 = new CSVReader(new FileReader(new File(
//					"in/allDataCleaned/eswc_phdSymp.txt")), '\t');
//			CSVReader csvReader6 = new CSVReader(new FileReader(new File(
//					"in/allDataCleaned/semeval.txt")), '\t');
//
//			cvs.put(OfficialNameSpace.mainTrackPaperNs + "linkedUp/",
//					csvReader1);
//			cvs.put(OfficialNameSpace.mainTrackPaperNs + "mashUp/", csvReader2);
//			cvs.put(OfficialNameSpace.mainTrackPaperNs + "phd-symposium/",
//					csvReader5);
//			cvs.put(OfficialNameSpace.mainTrackPaperNs + "semeval/", csvReader6);
//
//			TwitterDataGenerator tw = new TwitterDataGenerator();
//
//			for (Entry<String, CSVReader> c : cvs.entrySet()) {
//				tw.populateTwitter(c.getKey(), c.getValue(), 3, 4);
//
//			}
//
//			tw.populateTwitterMain(csvReader4, 4, 5, 1);
//			tw.populateTwitterPD(csvReader3, 4, 3, 5);
//
//			for (Entry<String, String> h : tw.hashtags.entrySet()) {
//				System.out.println(h.getKey() + "--> " + h.getValue());
//			}
//
//			for (Entry<String, String> h : tw.widgets.entrySet()) {
//				System.out.println(h.getKey() + "--> " + h.getValue());
//			}
//
//			for (Entry<String, String> t : tw.hashtags.entrySet()) {
//
//				Resource paper = model2.createResource(t.getKey());
//				//
//				// Property predicate = model.createProperty("isPresented");
//
//				Property hashtagPredicate = model
//						.createProperty("http://www.ontologydesignpatterns.org/ont/eswc/ontology.owl#hashtag");
//				Property widgetPredicate = model
//						.createProperty("http://www.ontologydesignpatterns.org/ont/eswc/ontology.owl#widget");
//
//				paper.removeAll(hashtagPredicate);
//				paper.addProperty(hashtagPredicate, t.getValue());
//				System.out.println(t.getKey() + "\t" + paper.getURI());
//
//				if (tw.widgets.get(t.getKey()) != null) {
//					paper.addProperty(widgetPredicate,
//							tw.widgets.get(t.getKey()));
//				} else {
//					System.out.println(t.getKey() + "\t has no widget");
//				}
//
//			}
//
//			OutputStream out = null;
//			try {
//				out = new FileOutputStream(
//						"./out/completeWithTwitterWidget.rdf");
//				model2.setNsPrefixes(model.getNsPrefixMap());
//				model2.add(model);
//				model2.write(out);
//
//				// model2.write(System.out, "RDF/JSON") ;
//
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	public Map<String, String> getPaperEvents() {
//		return paperEvents;
//	}
//
//	public void setPaperEvents(Map<String, String> paperEvents) {
//		this.paperEvents = paperEvents;
//	}
//
//}
