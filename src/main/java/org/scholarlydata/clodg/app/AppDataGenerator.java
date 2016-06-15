package org.scholarlydata.clodg.app;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;

public class AppDataGenerator {
    
    private Model dogFoodData;
    private ClassLoader classLoader;
    
    public AppDataGenerator(Model dogFoodData) {
        getClassLoader();
        this.dogFoodData = dogFoodData;
    }
    
    public AppJsonModels generateJsonModels(){
        AppJsonModels appJsonModels = null;
     
        String xsltFolder = "xslt";
        //TODO Andrea, xslt is still hardcoded here
        JSONObject contacts = generateJsonModel(xsltFolder + "/json_contacts.xslt");
        JSONObject organizations = generateJsonModel(xsltFolder + "/json_organizations.xslt");
        JSONObject articles = generateJsonModel(xsltFolder + "/json_articles.xslt");
        
        /*
        try {
            System.out.println(contacts.toString(4));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        */
        
        CalendarData calendarData = new CalendarData(dogFoodData);
        
        System.out.println("Generating data...");
        JSONObject events = calendarData.generateJsonModel();
        JSONObject locations = null;
        JSONObject categories = null;
        
        BufferedReader bufferedReader;
        try {
        	File f = new File("locations.json");
        	if(f.exists()){
	            bufferedReader = new BufferedReader(new FileReader(f));
	            String jsonContent = "";
	            String line = null;
	            while((line = bufferedReader.readLine()) != null) jsonContent += line;
	            
	            locations = new JSONObject(jsonContent);
	            
	            bufferedReader.close();
                
            }
            else {
                locations = new JSONObject();
                locations.put("locations", new JSONArray());
            }
                
            //URL categoriesURL = classLoader.getResource("data/categories.json");
            //if(categoriesURL != null){
        	f = new File("categories.json");
        	if(f.exists()){
                bufferedReader = new BufferedReader(new FileReader(f));
                
                String jsonContent = "";
                String line = null;
                while((line = bufferedReader.readLine()) != null) jsonContent += line;
                
                System.out.println("Category content " + jsonContent);
                
                categories = new JSONObject(jsonContent);
                
                bufferedReader.close();
            }
        	else {
                locations = new JSONObject();
                locations.put("categories", new JSONArray());
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        
        JSONObject talks = null;
        appJsonModels = new AppJsonModels(contacts, organizations, articles, events, categories, locations);
        
        return appJsonModels;
    }
    
    
    private JSONObject generateJsonModel(String xslt){
        JSONObject jsonObject = null;
        
        InputStream xsltStream = classLoader.getResourceAsStream(xslt);
        TransformerFactory tFactory = TransformerFactory.newInstance();
            
        Transformer transformer = null;
        try {
            transformer = tFactory.newTransformer(new StreamSource(xsltStream));
        } catch (TransformerConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(transformer != null){
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            dogFoodData.write(out);
            
            InputStream inputStream = new ByteArrayInputStream(out.toByteArray());
            try {
                out.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            
            StreamSource streamSource = new StreamSource(inputStream);
            
            
            
            try {
                out = new ByteArrayOutputStream();
                transformer.transform(streamSource, new StreamResult(out));
                inputStream = new ByteArrayInputStream(out.toByteArray());
                out.close();
                
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8")); 
                StringBuilder responseStrBuilder = new StringBuilder();
                
                String line = null;
                while((line = streamReader.readLine()) != null)
                    responseStrBuilder.append(line);
                
                System.out.println(responseStrBuilder.toString());
                jsonObject = new JSONObject(responseStrBuilder.toString());
            } catch (TransformerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        return jsonObject;
    }
    
    private ClassLoader getClassLoader(){
        if(classLoader == null)
            classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader;
    }
    
    public void generateAppData(){

        //GenerateAppData generateAppData = new GenerateAppData(FileManager.get().loadModel("eswc2015.rdf"));
        
        AppJsonModels appJsonModels = generateJsonModels();
        
        String jsonFolder = "json";
        File jsonF = new File(jsonFolder);
        if(!jsonF.exists()) jsonF.mkdirs();
        OutputStream outputStream;
        JSONObject json = appJsonModels.getContacts();
        if(json != null){
            try {
                outputStream = new FileOutputStream(jsonFolder + File.separator + "contacts.json");
                outputStream.write(json.toString().getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        json = appJsonModels.getArticles();
        if(json != null){
            try {
                outputStream = new FileOutputStream(jsonFolder + File.separator + "articles.json");
                outputStream.write(json.toString().getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        json = appJsonModels.getOrganizations();
        if(json != null){
            try {
                outputStream = new FileOutputStream(jsonFolder + File.separator + "organizations.json");
                outputStream.write(json.toString().getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        
        json = appJsonModels.getEvents();
        if(json != null){
            try {
                outputStream = new FileOutputStream(jsonFolder + File.separator + "events.json");
                outputStream.write(json.toString().getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        
        try {
			appJsonModels.write(new FileOutputStream(jsonFolder + File.separator + "data_ESWC2016.js"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
    }

    
    public static void main(String[] args) {
        //GenerateAppData generateAppData = new GenerateAppData(FileManager.get().loadModel("eswc2015.rdf"));
        AppDataGenerator generateAppData = new AppDataGenerator(FileManager.get().loadModel("eswc2016.rdf"));
        AppJsonModels appJsonModels = generateAppData.generateJsonModels();
        
        String jsonFolder = "json";
        File jsonF = new File(jsonFolder);
        if(!jsonF.exists()) jsonF.mkdirs();
        OutputStream outputStream;
        JSONObject json = appJsonModels.getContacts();
        if(json != null){
            try {
                outputStream = new FileOutputStream(jsonFolder + File.separator + "contacts.json");
                outputStream.write(json.toString().getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        json = appJsonModels.getArticles();
        if(json != null){
            try {
                outputStream = new FileOutputStream(jsonFolder + File.separator + "articles.json");
                outputStream.write(json.toString().getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        json = appJsonModels.getOrganizations();
        if(json != null){
            try {
                outputStream = new FileOutputStream(jsonFolder + File.separator + "organizations.json");
                outputStream.write(json.toString().getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        
        json = appJsonModels.getEvents();
        if(json != null){
            try {
                outputStream = new FileOutputStream(jsonFolder + File.separator + "events.json");
                outputStream.write(json.toString().getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        
        try {
			appJsonModels.write(new FileOutputStream(jsonFolder + File.separator + "data_ESWC2016.js"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
