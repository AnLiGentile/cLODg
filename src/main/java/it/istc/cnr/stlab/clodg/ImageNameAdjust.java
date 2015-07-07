package it.istc.cnr.stlab.clodg;
//package org.eswc.conferences.data;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import com.hp.hpl.jena.rdf.model.Model;
//import com.hp.hpl.jena.rdf.model.ModelFactory;
//import com.hp.hpl.jena.rdf.model.Property;
//import com.hp.hpl.jena.rdf.model.RDFNode;
//import com.hp.hpl.jena.rdf.model.Resource;
//import com.hp.hpl.jena.rdf.model.Statement;
//import com.hp.hpl.jena.rdf.model.StmtIterator;
//import com.hp.hpl.jena.sparql.vocabulary.FOAF;
//import com.hp.hpl.jena.util.FileManager;
//
//public class ImageNameAdjust {
//
//    public void adjustPersonImages(Model model, File file){
//        String wit = "http://wit.istc.cnr.it/eswc-stc/images/authors/resized/";
//        
//        Map<String,String> map = new HashMap<String,String>();
//        map.put("9fcea156083a205ba16319ec1fc822d4?s=200", "9fcea156083a205ba16319ec1fc822d4200.JPEG");
//        map.put("1506540?s=460", "1506540460.png.jpg");
//        map.put("citations?view_op=view_photo&user=dSMSH2wAAAAJ&citpid=3", "citationsview_opview_photo&userdSMSH2wAAAAJ&citpid3.JPEG");
//        
//        
//        Set<String> keys = map.keySet();
//        for(String key : keys){
//            Resource depiction = model.getResource(wit + key);
//            StmtIterator stmtIterator = model.listStatements(null, FOAF.depiction, depiction);
//            
//            if(stmtIterator.hasNext()){
//                Resource person = stmtIterator.next().getSubject();
//                
//                model.remove(person, FOAF.depiction, depiction);
//                
//                depiction = model.createResource(wit + map.get(key));
//                
//                model.add(person, FOAF.depiction, depiction);
//                
//                continue;
//            }
//        }
//        
//        StmtIterator stmtIterator = model.listStatements(null, FOAF.depiction, (RDFNode)null);
//        List<Statement> statements = new ArrayList<Statement>();
//        
//        while(stmtIterator.hasNext()){
//            Statement statement = stmtIterator.next();
//            statements.add(statement);
//        }
//        
//        for(Statement statement : statements){
//            Resource person = statement.getSubject();
//            Resource image = (Resource)statement.getObject();
//            String imageName = image.getURI().replace(wit, "");
//            
//            
//            boolean added = false;
//            File[] files = file.listFiles();
//            for(int i=0; i<files.length && !added; i++){
//                File f = files[i];
//                String name = f.getName();
//                
//                if(name.startsWith(imageName)){
//                    added = true;
//                    
//                    model.remove(person, FOAF.depiction, image);
//                    model.add(person, FOAF.depiction, model.createResource(wit + name));
//                }
//            }
//            
//            if(added)
//                System.out.println("Added " + person.getURI());
//            else System.out.println("NOT Added " + person.getURI());
//        }
//    }
//    
//    public void adjustPaperImages(Model model, File file){
//        
//        Property thumbnail = model.getProperty("http://dbpedia.org/ontology/thumbnail");
//        
//        for(File track : file.listFiles()){
//            String wit = "http://wit.istc.cnr.it/eswc-stc/images/papers/resized/" + track.getName() + "/";
//            
//            StmtIterator stmtIterator = model.listStatements(null, thumbnail, (RDFNode)null);
//            List<Statement> statements = new ArrayList<Statement>();
//            
//            while(stmtIterator.hasNext()){
//                Statement statement = stmtIterator.next();
//                statements.add(statement);
//            }
//            
//            for(Statement statement : statements){
//                Resource person = statement.getSubject();
//                if(person.getURI().contains("/" + track.getName() + "/")){
//                    Resource image = (Resource)statement.getObject();
//                    String imageName = image.getURI().replace(wit, "");
//                    
//                    model.remove(person, thumbnail, image);
//                    
//                    boolean added = false;
//                    File[] files = track.listFiles();
//                    for(int i=0; i<files.length && !added; i++){
//                        File f = files[i];
//                        String name = f.getName();
//                        if(name.startsWith(imageName)){
//                            added = true;
//                            model.add(person, thumbnail, model.createResource(wit + name));
//                        }
//                    }
//                    
//                    if(added)
//                        System.out.println("Added " + person.getURI());
//                    else System.out.println("NOT paper Added " + person.getURI());
//                }
//            }
//        }
//    }
//    
//    public static void main(String[] args){
//        Model model = FileManager.get().loadModel("out/eswc_data_final.rdf");
//        ImageNameAdjust imageNameAdjust = new ImageNameAdjust();
//        imageNameAdjust.adjustPersonImages(model, new File("in/img/persons"));
//        imageNameAdjust.adjustPaperImages(model, new File("in/img/papers"));
//        try {
//            OutputStream outputStream = new FileOutputStream(new File("out/eswc_data_final_img_OK.rdf"));
//            model.write(outputStream);
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//    
//}
