//package it.istc.cnr.stlab.clodg.form;
//
//import it.istc.cnr.stlab.clodg.util.OfficialNameSpace;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.commons.lang3.StringUtils;
//
//import au.com.bytecode.opencsv.CSVReader;
//
//import com.hp.hpl.jena.rdf.model.Model;
//import com.hp.hpl.jena.rdf.model.ModelFactory;
//import com.hp.hpl.jena.rdf.model.Resource;
//import com.hp.hpl.jena.sparql.vocabulary.FOAF;
//import com.hp.hpl.jena.vocabulary.RDF;
//import com.hp.hpl.jena.vocabulary.RDFS;
//
//@Deprecated
//public class PhdSymposiumFormData implements FormData {
//
//	public OfficialNameSpace ns;
//
//    @Override
//    public Model toRDF(CSVReader csvReader) {
//        Map<String, Person> complexNouns = new HashMap<String,Person>();
//        complexNouns.put("Cheikh Kacfah Emani", new Person("Cheikh", "Kacfah Emani"));
//        complexNouns.put("Mohamed Lamine Mouhoub", new Person("Mohamed Lamine", "Mouhoub"));
//        
//        String paperNS = OfficialNameSpace.mainTrackPaperNs+"phd-symposium/";        
//        String personNS = OfficialNameSpace.personNs;
//        
//        Model model = ModelFactory.createDefaultModel();
//        
//        try {
//            String[] line;
//            while((line = csvReader.readNext()) != null){
//                
//                String content = line[0];
//                String hashtag = line[1];
//                System.out.println(content);
//                System.out.println(hashtag);
//                String[] parts = content.split(" - ");
//                int id = Integer.valueOf(parts[0].trim());
//                
//                String rest = "";
//                for(int i=1; i<parts.length; i++){
//                    if(i>1){
//                        rest += " - ";
//                    }
//                    rest += parts[i];
//                }
//                
//                
//                parts = rest.split("\\. ");
//                String authors = parts[0].replace(" and ", ", ");
//                
//                String[] authorsParts = authors.split(", ");
//                
//                Resource authorlist = model.createResource(paperNS + id + "/authorlist");
//                int i = 1;
//                for(String author : authorsParts){
//                    author = author.trim();
//                    Person person = complexNouns.get(author);
//                    String firstname ="";
//                    String lastname="";
//                    if(person != null){
//                        firstname = person.getFirstname();
//                        lastname = person.getLastname();
//                    }else{
//                        String[] names = author.split(" ");
//                        for (int j = 0; j<names.length; j++){
//                        	names [j] = names[j].trim();
//                        	if (names [j].endsWith("."))
//                        		names [j] = names [j].substring(0, names [j].length()-1);
//                        	}
//                        
//                        if (names.length == 2){
//                        	firstname = names[0].trim();
//                        	lastname = names[1].trim();
//                        }else if (names.length == 3){
//                        	firstname = names[0].trim() +" "+names[1].trim();
//                        	lastname = names[2].trim();
//                        }else if (names.length == 4){
//                        	firstname = names[0].trim() +" "+names[1].trim();
//                        	lastname = names[2].trim() +" "+names[3].trim();
//                        }else if (names.length > 4){
//                        	firstname = names[0].trim() +" "+names[1].trim();
//                            for (int j = 0; j<names.length; j++){
//                            	lastname = lastname+ " "+ names[j].trim();
//                            	lastname = lastname.trim();
//                            	}
//                        }
//                    }
//                    
//                    
//                    String personURI = personNS + StringUtils.stripAccents(firstname.toLowerCase().replaceAll(" ", "-").replaceAll("\\.", "")) + "-" + StringUtils.stripAccents(lastname.toLowerCase().replaceAll(" ", "-").replaceAll("\\.", "")); 
//                    
//                    Resource personResource = model.createResource(personURI, FOAF.Person);
//                    personResource.addLiteral(FOAF.firstName, firstname);
//                    personResource.addLiteral(model.createProperty("http://xmlns.com/foaf/0.1/lastName"), lastname);
//                    personResource.addLiteral(FOAF.name, firstname + " " + lastname);
//                    authorlist.addProperty(model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#_"+i), personResource);
//                    i += 1;
//                }
//                
//                String title = "";
//                for(i=1; i<parts.length; i++){
//                    if(i>1){
//                        title += ". ";
//                    }
//                    title += parts[i];
//                }
//                
//                List<Integer> inUse = new ArrayList<Integer>();
//                inUse.add(224);
//                inUse.add(205);
//                inUse.add(36);
//                inUse.add(244);
//                inUse.add(31);
//                inUse.add(189);
//                inUse.add(193);
//                inUse.add(47);
//                inUse.add(16);
//                
//                Resource paper = model.createResource(paperNS + id);
//                paper.addProperty(model.createProperty("http://purl.org/ontology/bibo/authorList"), authorlist);
//                paper.addLiteral(RDFS.label, id);
//                paper.addProperty(RDF.type, model.createResource("http://purl.org/spar/fabio/PhDSymposiumPaper"));
//                
//                paper.addLiteral(model.createProperty("http://purl.org/dc/elements/1.1/title"), title);
//                paper.addLiteral(model.createProperty("http://2014.eswc-conferences.org/ontology/hashtag"), hashtag);
//            }
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        
//        return model;
//        
//    }
//    
//    public static void main(String[] args){
//        PhdSymposiumFormData researchFormData = new PhdSymposiumFormData();
//        try {
//            CSVReader csvReader = new CSVReader(new FileReader(new File("in/phdsymposium.csv")));
//            Model model = researchFormData.toRDF(csvReader);
//            csvReader.close();
//            OutputStream out = new FileOutputStream(new File("out/eswcphd2014.rdf"));
//            model.write(out);            
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//}
