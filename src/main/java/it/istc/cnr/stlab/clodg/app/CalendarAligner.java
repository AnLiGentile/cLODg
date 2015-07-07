package it.istc.cnr.stlab.clodg.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.Map.Entry;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.VEvent;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.FileManager;

import edu.emory.mathcs.backport.java.util.TreeMap;

/**
 * @author annalisa
 *
 */
@Deprecated
// use CalendarAlignerWithSessions instead
public class CalendarAligner implements Aligner {

    public CalendarAligner(){
        
    }
    
	public CalendarAligner(Model paperRdf) {
		super();
		this.paperRdf = paperRdf;
		this.paperTitles =this.getTitles(this.paperRdf);
	}
	
	public CalendarAligner(Model paperRdf, Model [] calendarRdf) {		
		super();
		this.paperRdf = paperRdf;
		this.calendarRdf = calendarRdf;
		this.paperTitles =this.getTitles(this.paperRdf);
		this.allEvents =this.getSummaries(this.calendarRdf);
		this.paperEvents = this.generateEventPaperAlignment();
	}
	
	private Map<String, String> generateEventPaperAlignment() {
		Map<String, String> pe = new HashMap<String, String>();
		
//        System.out.println("number of events in model: "+this.getAllEvents().size());

        for (Entry<String, String> t : this.getAllEvents().entrySet()){
//        System.out.println(t.getValue());
//        System.out.println(this.getIdForPaper(t.getValue()) + " has event: "+t.getKey());
        String paperId = this.getIdForPaper(t.getValue());
        
        if (paperId!=null){
        	pe.put(paperId, t.getKey());
        }else{
        	System.err.println(t.getValue() +" is not a paper presentation");
        }
        }
        return pe;
        
	}

	Map<String,String> paperTitles;		// paper URI, paper title
	Map<String,String> allEvents;		// eventID, summary
	Map<String,String> paperEvents;		// paper URI, eventID

	
	public Map<String, String> getAllEvents() {
		return allEvents;
	}

	public void setAllEvents(Map<String, String> paperEvents) {
		this.allEvents = paperEvents;
	}


	Model paperRdf;
	Model [] calendarRdf;

	public Map<String, String> getPaperTitles() {
		return paperTitles;
	}


	public void setPaperTitles(Map<String, String> paperTitles) {
		this.paperTitles = paperTitles;
	}

	
	public String getIdForPaper(String paper){
		paper = paper.toLowerCase();
		paper = paper.replace(" +", " ");
		for (Entry<String, String> s : this.getPaperTitles().entrySet()){
			String tit = s.getValue().toLowerCase();
			if (paper.contains(tit))
				return s.getKey();
			
		}
		return null;
	}

	
	/**
	 * generates the map of paperId - paperTitle
	 * @param model
	 * @return
	 */
	public Map<String,String> getTitles(Model model){
        Map<String,String> titlesMap = new HashMap<String,String>();
        //TODO check why we have two different expressions for titles
        String sparql = "PREFIX dc: <http://purl.org/dc/elements/1.1/> " +
        				"PREFIX dc-terms: <http://purl.org/dc/terms/> " +
        				"SELECT * WHERE {"+
        				
        				"{"+
        		        "SELECT ?paper ?title " +
        		        "WHERE{" +
        		        "    ?paper dc:title ?title" +
        		        "}"+
        		        "}"+
        		        "UNION"+

        				"{"+
        		        "SELECT ?paper ?title " +
        		        "WHERE{" +
        		        "    ?paper dc-terms:title ?title" +
        		        "}"+      
        		        "}"+
        "}";

        Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
        ResultSet resultSet = queryExecution.execSelect();
        while(resultSet.hasNext()){
            QuerySolution querySolution = resultSet.next();
            Resource paper = querySolution.getResource("paper");
            Literal title = querySolution.getLiteral("title");
            String titleString = title.getLexicalForm().replaceAll(" +", " ");
            if (titleString.endsWith("."))
            	titleString = titleString.substring(0, titleString.length()-1);
            titlesMap.put(paper.getURI(), titleString);
        }
        
        return titlesMap;
    }
	
	
	/**
	 * generates the map of paperId - paperTitle
	 * @param model
	 * @return
	 */
	public Map<String,String> getSummaries(Model [] models){
		
        Map<String,String> titlesMap = new HashMap<String,String>();
        for(Model model : models){
        String sparql = "PREFIX ical: <http://www.w3.org/2002/12/cal/icaltzd#> " +
        		        "SELECT ?event ?summary " +
        		        "WHERE{" +
        		        "    ?event ical:summary ?summary" +
        		        "}";
        Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
        ResultSet resultSet = queryExecution.execSelect();
        while(resultSet.hasNext()){
            QuerySolution querySolution = resultSet.next();
            Resource paper = querySolution.getResource("event");
            Literal title = querySolution.getLiteral("summary");
            String titleString = title.getLexicalForm().replaceAll(" +", " ");
            titlesMap.put(paper.getURI(), titleString);
        }
        }
        return titlesMap;
    }
	
	/**
	 * this method takes as input a calendar file and a RDF model
	 * for each paper in the RDF model, it searches for an event with SUMMARY containing the paper title,
	 * if so, saves a single calendar event named as the paper URI
	 * @param caledarFilePath
	 * @param model
	 * @param saveTo path where to save single events

	 */
	public void generateSingleEvents(String caledarFilePath, String saveTo){

		new File(saveTo).mkdirs();
		String OPEN ="BEGIN:VCALENDAR";

		String CLOSE ="END:VCALENDAR";

		FileInputStream fin;
		try {
			fin = new FileInputStream(caledarFilePath);

		CalendarBuilder builder = new CalendarBuilder();

		Calendar calendar = builder.build(fin);
		

		ComponentList c = calendar.getComponents();
		System.out.println("Calendar contains "+c.size()+" events");
		System.out.println(calendar.getProperties());


		Iterator it = c.iterator();
	
			while (it.hasNext()){
				VEvent e = (VEvent) it.next();
//				System.out.println(e.getSummary());
				String summary = e.getSummary().getValue();
				summary = summary.replaceAll(" +", " ");

				String id = this.getIdForPaper(summary);
				if (id!=null){
//					System.out.println(summary);
//					System.out.println(id);

					//TODO PrinterOut 
					String fileName = id.substring(id.indexOf("/eswc/2014/paper"));
					fileName= fileName.replaceAll(File.separator, "_");
					PrintWriter out = new PrintWriter(new FileWriter(saveTo+File.separator+fileName+".ics"));
					out.write(OPEN+"\n");

					
					out.write(calendar.getProperties().toString());
					out.write(e.toString());
					out.write(CLOSE);
					out.close();


					}else{
						System.out.println("*** no id for "+summary);


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

		
//		BufferedReader calRead;
//
//		new File (saveTo).mkdirs();
//		String HEADER ="";
//		String CONTENT ="";
//		String CLOSE ="END:VCALENDAR";
//
//		Map<String, String> allEvents = new HashMap<String,String>();
//	
//
//			try {
//				calRead = new BufferedReader(new FileReader(caledarFilePath));
//
//		String nextLine;
//		
//		while (((nextLine = calRead.readLine()) != null)&(!nextLine.startsWith("BEGIN:VEVENT"))) {
//				HEADER = HEADER+nextLine+"\n";
//			}
//		System.out.println(HEADER);
//
//		if (nextLine.startsWith("BEGIN:VEVENT")){
//		CONTENT = CONTENT+nextLine+"\n";
//		
//		while (((nextLine = calRead.readLine()) != null)&(!nextLine.startsWith("END:VEVENT"))) {
//			CONTENT = CONTENT+nextLine+"\n";
//		}
//	System.out.println(CONTENT);
//		}
//		
////		while ((nextLine = calRead.readLine()) != null) {
////
////				String id = nextLine.substring(1,nextLine.indexOf(" "));
////				try{
////				if (this.BL1_noHit.contains(id)){
////
////					continue;
////				}else if(this.BL1.containsKey(id)){
////					if(this.BL1.get(id) == this.BL2.get(id)){
////						continue;
////					}else if(this.BL1.get(id) < this.BL2.get(id)){
////						continue;
////					}
////				}
////				}catch (Exception e) {
////
////					System.err.println("errors with "+id);
////				}
////				
////			}
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//
//		
		
	}
	
	
	//TODO change this to extract calendar ids
    @Override
    public Model align(JSONObject jsonObject, Model model) {
        try {
            JSONArray articles = jsonObject.getJSONArray("articles");
            
            Property property = model.createProperty("http://2014.eswc-conferences.org/ontology/calendarWidget");
            for(int i=0, j=articles.length(); i<j; i++){
                JSONObject article = articles.getJSONObject(i);
                
                String id = article.getString("id");
                String twitterWidget = article.getString("calendar_widget");
                
                //TODO must be done for phdSymp, poster and demo, and WS

                String sparql = "SELECT ?article WHERE{?article a <http://swrc.ontoware.org/ontology#InProceedings> . FILTER(STR(?article) = \"http://data.semanticweb.org/conference/eswc/2014/paper/research/" + id +"\" || STR(?article) = \"http://data.semanticweb.org/conference/eswc/2014/paper/in-use/" + id + "\") }";
                
                Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
                QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
                ResultSet resultSet = queryExecution.execSelect();
                
                if(resultSet.hasNext()){
                    QuerySolution querySolution = resultSet.next();
                    Resource articleResource = querySolution.getResource("article");
                    if(articleResource != null){
                        articleResource.addLiteral(property, twitterWidget);
                    }
                }
                
                
                /*
                String uri = "http://data.semanticweb.org/conference/eswc/2014/paper/research/" + id;
                
                Resource articleResource = model.getResource(uri);
                
                System.out.println(uri);
                
                if(articleResource != null){
                    articleResource.addLiteral(property, twitterWidget);
                }
                */
            }
            
            
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return model;
    }
    
    
    public void addCalendar(Model model, Model calendarModel){
        
        
          
          
          model.add(calendarModel);
        //  calendarmodel[0] = FileManager.get().loadModel("out/workshopsCalendar.rdf");
              
          
              //TODO for each paper in the final rdf
              // serach the rdf calendar for a related evant
              // use the function getTitles(Model model) to id-titles
              // and generate properties
              
              
              CalendarAligner aligner = new CalendarAligner(model, new Model[]{calendarModel});
              
              SortedMap<String, String> papers = new TreeMap();
              for (Entry<String, String> e  :aligner.getPaperTitles().entrySet()){
                  papers.put(e.getKey(), e.getValue());
              }
              
              System.out.println("number of papers in model: "+papers.size());
        
              for (Entry<String, String> t : papers.entrySet()){
              System.out.println(t.getKey()+"\t"+t.getValue());
              }
              
              
              System.out.println("number of events in model: "+aligner.getAllEvents().size());
        
        
              
              Map<String, String> events = aligner.getPaperEvents();
              
        //      for (Entry<String, String> t : events.entrySet()){
        //      System.out.println(t.getValue() + " presents paper: "+t.getKey());
        //      }
              
            for (Entry<String, String> t : papers.entrySet()){
                if (events.keySet().contains(t.getKey())){
                //TODO all the content of this if block is just to test
                      Resource paper = model.createResource(t.getKey());
        //
        //                Property predicate = model.createProperty("isPresented");
                      
                      Property isPresentedPredicate = model.createProperty("http://www.ontologydesignpatterns.org/ont/eswc/ontology.owl#isPresented");
                      Property presentsPredicate = model.createProperty("http://www.ontologydesignpatterns.org/ont/eswc/ontology.owl#presents");
                      
                      String eventId = aligner.getPaperEvents().get(t.getKey());
                      
                      /*
        
                      String rep = "file:///Users/annalisa/Documents/LODIEws/ESWC2014/data/out/mainConfCalendar.rdf";
                      id = id.substring(rep.length());
                      */
                      Resource event = model.createResource(eventId);
                      System.out.println(t.getKey()+"\t" + event.getURI());
        
                      event.addProperty(presentsPredicate, paper);
                      paper.addProperty(isPresentedPredicate, event);
        
        //            Resource object = model.createResource(l.getString());
        //                model.addLiteral(subjet, predicate, l);
                      //Statement s = model.createLiteralStatement(subjet, predicate, l);
        
                      //TODO Andrea help me :)
                      //model.add(s);
                }
                else{
        //    System.err.println(t.getKey()+"\t"+t.getValue());
                    }
        
            }
        
          
            OutputStream out = null;
            try {
                out = new FileOutputStream("./out/testAddCalendar.rdf");
                model.write(out);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            
        //      aligner.generateSingleEvents("in/ESWC2014 - main conference_iqll2vaabv1khlfmlt9mq9co48@group.calendar.google.com.ics", "calendarSingle");
              //TODO we need data of workshop papers in the rdf to generate the single calebdar files
        //      aligner.generateSingleEvents("in/ESWC 2014 - workshops_sg200d1e8pk7qpvah40it0s6dg@group.calendar.google.com.ics", "calendarSingleWS");



        
    }
    
    public static void main(String[] args){
            
            
//            Model model = FileManager.get().loadModel("out/eswc_data_final.rdf");
        Model model = FileManager.get().loadModel("calendar2015/dog-food-data-2015.rdf");

        Model calendarmodel[] = new Model[2] ;

        calendarmodel[0] = FileManager.get().loadModel("calendar2015/main-calendar2015.rdf");
        
        model.add(calendarmodel[0]);
        calendarmodel[1] = FileManager.get().loadModel("calendar2015/sessions-calendar2015.rdf");
//      calendarmodel[0] = FileManager.get().loadModel("out/workshopsCalendar.rdf");
        model.add(calendarmodel[1]);

        
            //TODO for each paper in the final rdf
            // serach the rdf calendar for a related evant
            // use the function getTitles(Model model) to id-titles
            // and generate properties
            
            
            CalendarAligner aligner = new CalendarAligner(model, calendarmodel);
            
            SortedMap<String, String> papers = new TreeMap();
            for (Entry<String, String> e  :aligner.getPaperTitles().entrySet()){
            	papers.put(e.getKey(), e.getValue());
            }
            
            System.out.println("number of papers in model: "+papers.size());

            for (Entry<String, String> t : papers.entrySet()){
            System.out.println(t.getKey()+"\t"+t.getValue());
            }
            
            
            System.out.println("number of events in model: "+aligner.getAllEvents().size());


            
            Map<String, String> events = aligner.getPaperEvents();
            
//            for (Entry<String, String> t : events.entrySet()){
//            System.out.println(t.getValue() + " presents paper: "+t.getKey());
//            }
            
          for (Entry<String, String> t : papers.entrySet()){
        	  if (events.keySet().contains(t.getKey())){
        	  //TODO all the content of this if block is just to test
        			Resource paper = model.createResource(t.getKey());
//
//          			Property predicate = model.createProperty("isPresented");
          			
          	        Property isPresentedPredicate = model.createProperty("http://www.ontologydesignpatterns.org/ont/eswc/ontology.owl#isPresented");
          	        Property presentsPredicate = model.createProperty("http://www.ontologydesignpatterns.org/ont/eswc/ontology.owl#presents");
          	        
          			String eventId = aligner.getPaperEvents().get(t.getKey());
          			
          			/*

          			String rep = "file:///Users/annalisa/Documents/LODIEws/ESWC2014/data/out/mainConfCalendar.rdf";
          			id = id.substring(rep.length());
          			*/
        			Resource event = model.createResource(eventId);
          		    System.out.println(t.getKey()+"\t" + event.getURI());

          		    event.addProperty(presentsPredicate, paper);
          		    paper.addProperty(isPresentedPredicate, event);

//        			Resource object = model.createResource(l.getString());
//          			model.addLiteral(subjet, predicate, l);
        			//Statement s = model.createLiteralStatement(subjet, predicate, l);

        			//TODO Andrea help me :)
        			//model.add(s);
        	  }
        	  else{
//          System.err.println(t.getKey()+"\t"+t.getValue());
        		  }
 
          }

      	
          OutputStream out = null;
          try {
              out = new FileOutputStream("./calendar2015/main-calendar2015.rdf");
              model.write(out);
          } catch (FileNotFoundException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
          }
          
          
//            aligner.generateSingleEvents("in/ESWC2014 - main conference_iqll2vaabv1khlfmlt9mq9co48@group.calendar.google.com.ics", "calendarSingle");
            //TODO we need data of workshop papers in the rdf to generate the single calebdar files
//            aligner.generateSingleEvents("in/ESWC 2014 - workshops_sg200d1e8pk7qpvah40it0s6dg@group.calendar.google.com.ics", "calendarSingleWS");


    }

	public Map<String, String> getPaperEvents() {
		return paperEvents;
	}

	public void setPaperEvents(Map<String, String> paperEvents) {
		this.paperEvents = paperEvents;
	}

}
