package it.istc.cnr.stlab.clodg.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

/**
 * @author annalisa
 *
 */
public class PythonConverters {
    
    public static enum CALENDAR {MAIN_CONFERENCE, WORKSHOPS, SESSIONS, TUTORIALS, PLENARY, PHD};
    
    public static final String PLENARY_EVENTS_CALENDAR_NS = OfficialNameSpace.baseEswc+"event/";
    public static final String MAIN_CONFERENCE_CALENDAR_NS = OfficialNameSpace.baseEswc+"talk/";
    public static final String WORKSHOPS_CALENDAR_NS = OfficialNameSpace.baseEswc+"workshop/";
    public static final String SESSIONS_CALENDAR_NS = OfficialNameSpace.baseEswc+"session/";
    public static final String TUTORIALS_CALENDAR_NS = OfficialNameSpace.baseEswc+"tutorial/";
    public static final String PHD_CALENDAR_NS = OfficialNameSpace.baseEswc+"PhDSymposium/";


	public static void ical2RDF(String inIcal, String base, String outRDF) {

		try {

			String[] callAndArgs = { "python", "./ical2RDF/fromIcal.py",
					"--base", base, inIcal, ">", outRDF };
			
			System.out.println("base : " + base);

			Process p = Runtime.getRuntime().exec(callAndArgs);

			BufferedReader stdInput = new BufferedReader(new

			InputStreamReader(p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new

			InputStreamReader(p.getErrorStream()));


			String s = "";
			PrintWriter fos = new PrintWriter(new FileWriter(outRDF));

			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
				fos.write(s);

			}

			fos.close();

			while ((s = stdError.readLine()) != null) {
				System.err.println(s);
			}

		}

		catch (IOException e) {

			System.out.println("exception occured");

			e.printStackTrace();

			System.exit(-1);

		}
	}

	public static void doStuff(String pathOfCalendarFolder) {


		ical2RDF(pathOfCalendarFolder+File.separator+"main.ics", MAIN_CONFERENCE_CALENDAR_NS,
				"./out/main_temp.rdf");

		ical2RDF(pathOfCalendarFolder+File.separator+"sessions_main.ics", SESSIONS_CALENDAR_NS,
				"./out/sessions_temp.rdf");
		
		ical2RDF(pathOfCalendarFolder+File.separator+"workshops.ics", WORKSHOPS_CALENDAR_NS,
		"./out/workhop_temp.rdf");
		
		ical2RDF(pathOfCalendarFolder+File.separator+"tutorials.ics", TUTORIALS_CALENDAR_NS,
				"./out/tutorials_temp.rdf");
		
		ical2RDF(pathOfCalendarFolder+File.separator+"plenary.ics", PLENARY_EVENTS_CALENDAR_NS,
				"./out/plenary_temp.rdf");
		
		ical2RDF(pathOfCalendarFolder+File.separator+"phDSymp.ics", PLENARY_EVENTS_CALENDAR_NS,
				"./out/phd_temp.rdf");
		
		Model modelMain = FileManager.get().loadModel("./out/main_temp.rdf");
		refactoring(modelMain, CALENDAR.MAIN_CONFERENCE);
		try {

			modelMain.write(new FileOutputStream(new File(pathOfCalendarFolder+File.separator+"main-calendar.rdf")));
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		
		Model modelSessions = FileManager.get().loadModel("./out/sessions_temp.rdf");
		refactoring(modelSessions, CALENDAR.SESSIONS);
		try {

			modelSessions.write(new FileOutputStream(new File(pathOfCalendarFolder+File.separator+"sessions-calendar.rdf")));
			//TODO also, this is a new one, please put online
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		
		Model modelWS = FileManager.get().loadModel("./out/workhop_temp.rdf");
		refactoring(modelWS, CALENDAR.WORKSHOPS);
		try {

		    modelWS.write(new FileOutputStream(new File(pathOfCalendarFolder+File.separator+"workshops-calendar.rdf")));
		    
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		
		Model modelTutorials = FileManager.get().loadModel("./out/tutorials_temp.rdf");
		refactoring(modelTutorials, CALENDAR.TUTORIALS);
		try {

			modelTutorials.write(new FileOutputStream(new File(pathOfCalendarFolder+File.separator+"tutorials-calendar.rdf")));
		    
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		
		//TODO Andrea ho aggiunto PHD e PLENARY all'enum

		Model modelPhD = FileManager.get().loadModel("./out/phd_temp.rdf");
		refactoring(modelPhD, CALENDAR.PHD);
		try {
			modelPhD.write(new FileOutputStream(new File(pathOfCalendarFolder+File.separator+"phdSymp-calendar.rdf")));
		    
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
				
		
	
		Model modelPlenary = FileManager.get().loadModel("./out/plenary_temp.rdf");
		refactoring(modelPlenary, CALENDAR.PLENARY);
		try {
		    /*
		     * FIXED
		     */
			modelPlenary.write(new FileOutputStream(new File(pathOfCalendarFolder+File.separator+"plenary-calendar.rdf")));
		    
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		
	}
	
	
	public static void refactoring(Model model, CALENDAR calendar){
	    
	    String ns = null;
	    boolean renameCalendarResource = false;
	    switch (calendar) {
            case MAIN_CONFERENCE:
                ns = MAIN_CONFERENCE_CALENDAR_NS;
                renameCalendarResource = true;
                break;
            case WORKSHOPS:
                ns = WORKSHOPS_CALENDAR_NS;
                break;
            case SESSIONS:
                ns = SESSIONS_CALENDAR_NS;
                break;
            case TUTORIALS:
                ns = TUTORIALS_CALENDAR_NS;
                break;
            default:
                ns = MAIN_CONFERENCE_CALENDAR_NS;
                renameCalendarResource = true;
                break;
        }
	    
	    String sparql = "SELECT ?event " +
                "WHERE{" +
                "    {?event ?p1 ?o} UNION {?s ?p2 ?event} . " +
                "    FILTER(REGEX(STR(?event), \"^file:\"))" +
                "}";

        Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
        
        Set<Resource> toModify = new HashSet<Resource>();
        ResultSet resultSet = queryExecution.execSelect();
        
        while(resultSet.hasNext()){
            toModify.add(resultSet.next().getResource("event"));
        }
            
        
        for(Resource event : toModify){
            String namespaceOld = RDFUtils.getNameSpace(event);
            String eventUriNew = event.getURI().replace(namespaceOld, ns);
            
            Resource eventNew = model.createResource(eventUriNew);
            
            StmtIterator stmtIterator = model.listStatements(event, null, (RDFNode)null);
            while(stmtIterator.hasNext()){
                Statement statement = stmtIterator.next();
                Property property = statement.getPredicate();
                RDFNode object = statement.getObject();
                model.add(eventNew, property, object);
            }
            model.removeAll(event, null, (RDFNode)null);
        }
        
        if(renameCalendarResource){
            sparql = "SELECT ?event " +
                     "WHERE{" +
                     "    ?event a <http://www.w3.org/2002/12/cal/icaltzd#Vcalendar> " +
                     "}";
            
            query = QueryFactory.create(sparql, Syntax.syntaxARQ);
            queryExecution = QueryExecutionFactory.create(query, model);
            resultSet = queryExecution.execSelect();
            
            Resource calendarResource = null;
            if(resultSet.hasNext()){
                calendarResource = resultSet.next().getResource("event");
            }
            
            if(calendarResource != null){
                Resource calendarResourceNew = model.createResource(MAIN_CONFERENCE_CALENDAR_NS + "#main_conference");
                StmtIterator stmtIterator = model.listStatements(calendarResource, null, (RDFNode)null);
                while(stmtIterator.hasNext()){
                    Statement statement = stmtIterator.next();
                    model.add(calendarResourceNew, statement.getPredicate(), statement.getObject());
                }
                
                model.removeAll(calendarResource, null, (RDFNode)null);
            }
        }
        
	}

}
