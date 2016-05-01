package it.istc.cnr.stlab.clodg.workflow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;

public class Heiko {

    public static void main(String[] args) {
        
        String ns = "http://data.semanticweb.org/ISWC2015PD/submission/submission-";
        String demoNs = "http://data.semanticweb.org/ISWC2015demo/submission/submission-";
        String posterNs = "http://data.semanticweb.org/ISWC2015poster/submission/submission-";
        
        Model model = FileManager.get().loadModel("iswc2015_metadata_preview.ttl");
        
        try {
            CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream("poster_demo_iswc2015.csv")), ';');
            
            String[] row = null;
            boolean header = true;
            
            Resource poster = model.createResource("http://purl.org/spar/fabio/PosterPaper");
            Resource demo = model.createResource("http://purl.org/spar/fabio/DemoPaper");
            
            while((row = reader.readNext()) != null){
                if(!header) {
                    String id = row[0].trim();
                    String track = row[2].trim().toLowerCase();
                    System.out.println(id + ": " + row[2]);
                    Resource paper = model.createResource(ns + id);
                    
                    Resource newPaper = null;
                    if(track.equals("poster")) newPaper = model.createResource(posterNs + id, poster);
                    else newPaper = model.createResource(demoNs + id, demo);
                    
                    List<Statement> stmts = new ArrayList<Statement>();
                    StmtIterator it = model.listStatements(paper, null, (RDFNode)null);
                    while(it.hasNext()){
                        Statement stmt = it.next();
                        stmts.add(stmt);
                        model.add(newPaper, stmt.getPredicate(), stmt.getObject());
                    }
                    
                    it = model.listStatements(null, null, paper);
                    while(it.hasNext()){
                        Statement stmt = it.next();
                        stmts.add(stmt);
                        model.add(stmt.getSubject(), stmt.getPredicate(), newPaper);
                    }
                    
                    model.remove(stmts);
                    
                }
                else header = false;
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try {
            model.write(new FileOutputStream(new File("iswc2015.rdf")));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
