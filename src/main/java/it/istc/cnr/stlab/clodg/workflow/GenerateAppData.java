package it.istc.cnr.stlab.clodg.workflow;

import it.istc.cnr.stlab.clodg.models.AppJsonModels;

import java.io.BufferedOutputStream;
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
import java.net.URL;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;

public class GenerateAppData {
    
    private Model dogFoodData;
    private ClassLoader classLoader;
    
    public GenerateAppData(Model dogFoodData) {
        getClassLoader();
        this.dogFoodData = dogFoodData;
    }
    
    public AppJsonModels generateJsonModels(){
        AppJsonModels appJsonModels = null;
     
        JSONObject contacts = generateJsonModel("xslt/json_contacts.xslt");
        JSONObject organizations = generateJsonModel("xslt/json_organizations.xslt");
        JSONObject articles = generateJsonModel("xslt/json_articles.xslt");
        
        /*
        try {
            System.out.println(contacts.toString(4));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        */
        
        GenerateCalendarData generateAppData = new GenerateCalendarData(FileManager.get().loadModel("calendar2015/main-with-calendar2015.rdf"));
        
        System.out.println("Generating data...");
        JSONObject events = generateAppData.generateJsonModel();
        JSONObject locations = null;
        JSONObject categories = null;
        
        BufferedReader bufferedReader;
        try {
            URL locationsURL = classLoader.getResource("data/locations.json");
            if(locationsURL != null){
                bufferedReader = new BufferedReader(new FileReader(locationsURL.getFile()));
                String jsonContent = "";
                String line = null;
                while((line = bufferedReader.readLine()) != null) jsonContent += line;
                
                locations = new JSONObject(jsonContent);
                
                bufferedReader.close();
                
                bufferedReader = new BufferedReader(new FileReader(classLoader.getResource("data/categories.json").getFile()));
                
                jsonContent = "";
                line = null;
                while((line = bufferedReader.readLine()) != null) jsonContent += line;
                
                categories = new JSONObject(jsonContent);
                
                bufferedReader.close();
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

    
    public static void main(String[] args) {
        //GenerateAppData generateAppData = new GenerateAppData(FileManager.get().loadModel("eswc2015.rdf"));
        GenerateAppData generateAppData = new GenerateAppData(FileManager.get().loadModel("rdf/form-data.rdf"));
        AppJsonModels appJsonModels = generateAppData.generateJsonModels();
        
        String jsonFolder = "json";
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
    }

}
