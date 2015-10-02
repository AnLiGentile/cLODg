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

import org.apache.jena.riot.RiotException;

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

	public static void doStuff(String pathOfCalendarFolder, OfficialNameSpace ns) {

	    File outFolder = new File("out");
	    if(!outFolder.exists()) outFolder.mkdirs();

		if (new File(pathOfCalendarFolder+File.separator+"main.ics").exists())
	    ical2RDF(pathOfCalendarFolder+File.separator+"main.ics", ns.MAIN_CONFERENCE_CALENDAR_NS,
				"./out/main_temp.rdf");
		else System.err.println("MISSING: "+pathOfCalendarFolder+File.separator+"main.ics");
		
		if (new File(pathOfCalendarFolder+File.separator+"sessions_main.ics").exists())
		ical2RDF(pathOfCalendarFolder+File.separator+"sessions_main.ics", ns.SESSIONS_CALENDAR_NS,
				"./out/sessions_temp.rdf");
		else System.err.println("MISSING: "+pathOfCalendarFolder+File.separator+"sessions_main.ics");

		
		if (new File(pathOfCalendarFolder+File.separator+"workshops.ics").exists())
		ical2RDF(pathOfCalendarFolder+File.separator+"workshops.ics", ns.WORKSHOPS_CALENDAR_NS,
		"./out/workhop_temp.rdf");
		else System.err.println("MISSING: "+pathOfCalendarFolder+File.separator+"workshops.ics");

		if (new File(pathOfCalendarFolder+File.separator+"tutorials.ics").exists())
		ical2RDF(pathOfCalendarFolder+File.separator+"tutorials.ics", ns.TUTORIALS_CALENDAR_NS,
				"./out/tutorials_temp.rdf");
		else System.err.println("MISSING: "+pathOfCalendarFolder+File.separator+"tutorials.ics");

		if (new File(pathOfCalendarFolder+File.separator+"plenary.ics").exists())
		ical2RDF(pathOfCalendarFolder+File.separator+"plenary.ics", ns.PLENARY_EVENTS_CALENDAR_NS,
				"./out/plenary_temp.rdf");
		else System.err.println("MISSING: "+pathOfCalendarFolder+File.separator+"plenary.ics");

		if (new File(pathOfCalendarFolder+File.separator+"phDSymp.ics").exists())
		ical2RDF(pathOfCalendarFolder+File.separator+"phDSymp.ics", ns.PLENARY_EVENTS_CALENDAR_NS,
				"./out/phd_temp.rdf");
		else System.err.println("MISSING: "+pathOfCalendarFolder+File.separator+"phDSymp.ics");

		
		Model modelMain = null;
		try{
		    modelMain = FileManager.get().loadModel("./out/main_temp.rdf");
		} catch(RiotException e){
		    System.err.println(e.getMessage());
		}
		
		if(modelMain != null){
    		refactoring(modelMain, CALENDAR.MAIN_CONFERENCE, ns);
    		try {
    
    			modelMain.write(new FileOutputStream(new File(pathOfCalendarFolder+File.separator+"main-calendar.rdf")));
    		} catch (FileNotFoundException e) {
    
    			e.printStackTrace();
    		}
		}
		
		Model modelSessions = null;
		try{
		    modelSessions = FileManager.get().loadModel("./out/sessions_temp.rdf");
		} catch(RiotException e){
            System.err.println(e.getMessage());
        }
		if(modelSessions != null){
    		refactoring(modelSessions, CALENDAR.SESSIONS, ns);
    		try {
    
    			modelSessions.write(new FileOutputStream(new File(pathOfCalendarFolder+File.separator+"sessions-calendar.rdf")));
    			//TODO also, this is a new one, please put online
    		} catch (FileNotFoundException e) {
    
    			e.printStackTrace();
    		}
		}
		
		Model modelWS = null;
		try{
		    modelWS = FileManager.get().loadModel("./out/workhop_temp.rdf");
		} catch(RiotException e){
            System.err.println(e.getMessage());
        }
		if(modelWS != null){
    		refactoring(modelWS, CALENDAR.WORKSHOPS, ns);
    		try {
    
    		    modelWS.write(new FileOutputStream(new File(pathOfCalendarFolder+File.separator+"workshops-calendar.rdf")));
    		    
    		} catch (FileNotFoundException e) {
    
    			e.printStackTrace();
    		}
		}
		
		Model modelTutorials = null;
		try{
		    modelTutorials = FileManager.get().loadModel("./out/tutorials_temp.rdf");
		} catch(RiotException e){
            System.err.println(e.getMessage());
        }
		if(modelTutorials != null){
    		refactoring(modelTutorials, CALENDAR.TUTORIALS, ns);
    		try {
    
    			modelTutorials.write(new FileOutputStream(new File(pathOfCalendarFolder+File.separator+"tutorials-calendar.rdf")));
    		    
    		} catch (FileNotFoundException e) {
    
    			e.printStackTrace();
    		}
		}
		
		//TODO Andrea ho aggiunto PHD e PLENARY all'enum
		Model modelPhD = null;
		try{
		    modelPhD = FileManager.get().loadModel("./out/phd_temp.rdf");
		} catch(RiotException e){
            System.err.println(e.getMessage());
        }
		if(modelPhD != null){
    		refactoring(modelPhD, CALENDAR.PHD, ns);
    		try {
    			modelPhD.write(new FileOutputStream(new File(pathOfCalendarFolder+File.separator+"phdSymp-calendar.rdf")));
    		    
    		} catch (FileNotFoundException e) {
    
    			e.printStackTrace();
    		}
    			
		}
		
		Model modelPlenary = null;
		try{
		    modelPlenary = FileManager.get().loadModel("./out/plenary_temp.rdf");
		} catch(RiotException e){
            System.err.println(e.getMessage());
        }
		if(modelPlenary != null){
    		refactoring(modelPlenary, CALENDAR.PLENARY, ns);
    		try {
    		    /*
    		     * FIXED
    		     */
    			modelPlenary.write(new FileOutputStream(new File(pathOfCalendarFolder+File.separator+"plenary-calendar.rdf")));
    		    
    		} catch (FileNotFoundException e) {
    
    			e.printStackTrace();
    		}
		}
		
	}
	
	
	public static void refactoring(Model model, CALENDAR calendar, OfficialNameSpace ns){
	    
	    String nameSp = null;
	    boolean renameCalendarResource = false;
	    switch (calendar) {
            case MAIN_CONFERENCE:
                nameSp =ns.MAIN_CONFERENCE_CALENDAR_NS;
                renameCalendarResource = true;
                break;
            case WORKSHOPS:
                nameSp = ns.WORKSHOPS_CALENDAR_NS;
                break;
            case SESSIONS:
                nameSp = ns.SESSIONS_CALENDAR_NS;
                break;
            case TUTORIALS:
                nameSp = ns.TUTORIALS_CALENDAR_NS;
                break;
            default:
                nameSp = ns.MAIN_CONFERENCE_CALENDAR_NS;
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
            String eventUriNew = event.getURI().replace(namespaceOld, nameSp);
            
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
                Resource calendarResourceNew = model.createResource(ns.MAIN_CONFERENCE_CALENDAR_NS + "#main_conference");
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
