package it.istc.cnr.stlab.clodg.models;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class AppJsonModels {

	private JSONObject contacts;
	private JSONObject organizations;
	private JSONObject articles;
	private JSONObject events;
	private JSONObject categories;
	private JSONObject locations;

	public AppJsonModels(JSONObject contacts, JSONObject organizations,
			JSONObject articles, JSONObject events, JSONObject categories,
			JSONObject locations) {
		this.contacts = contacts;
		this.organizations = organizations;
		this.articles = articles;
		this.events = events;
		this.locations = locations;
		this.categories = categories;
	}

	public JSONObject getArticles() {
		return articles;
	}

	public JSONObject getOrganizations() {
		return organizations;
	}

	public JSONObject getContacts() {
		return contacts;
	}

	public JSONObject getEvents() {
		return events;
	}

	public JSONObject getCategories() {
		return categories;
	}

	public JSONObject getLocations() {
		return locations;
	}

	public void write(OutputStream outputStream) throws IOException {

		String newLine = System.getProperty("line.separator");
		BufferedOutputStream out = new BufferedOutputStream(outputStream);
		out.write(("define({" + newLine).getBytes());
		out.flush();

		try {
		    JSONArray jsonArray = null;
		    
		    if(organizations != null){
    		    jsonArray = organizations.getJSONArray("organizations");
    		    
    		    if(jsonArray != null)
        			out.write(("\"organizations\":"
        					+ jsonArray.toString()
        					+ "," + newLine).getBytes());
    		    else out.write(("\"organizations\":"
                        + JSONObject.NULL.toString()
                        + "," + newLine).getBytes());
    		    
    		    out.flush();
		    }
		    
		    if(contacts != null){
    		    jsonArray = contacts.getJSONArray("persons");
    		    if(jsonArray != null)
        			out.write(("\"persons\":"
        					+ jsonArray.toString() + "," + newLine)
        					.getBytes());
        		else out.write(("\"persons\":"
                        + JSONObject.NULL.toString()
                        + "," + newLine).getBytes());
    		    
    		    out.flush();
		    }

		    if(articles != null){
    		    jsonArray = articles.getJSONArray("publications");
    		    
    		    if(jsonArray != null)
        			out.write(("\"publications\":"
        					+ jsonArray.toString() + "," + newLine)
        					.getBytes());
        		else out.write(("\"publications\":"
                        + JSONObject.NULL.toString()
                        + "," + newLine).getBytes());
    		    
    		    out.flush();
		    }

		    if(events != null){
    		    jsonArray = events.getJSONArray("events");
    		    
    		    if(jsonArray != null)
        			out.write(("\"events\":" + jsonArray.toString()
        					+ "," + newLine).getBytes());
    		    else out.write(("\"events\":"
                        + JSONObject.NULL.toString()
                        + "," + newLine).getBytes());
        		
    		    out.flush();
		    }

		    if(locations != null){
    		    jsonArray = locations.getJSONArray("locations");
    		    if(jsonArray != null)
        			out.write(("\"locations\":"
        					+ jsonArray.toString() + "," + newLine)
        					.getBytes());
    		    else out.write(("\"locations\":"
                        + JSONObject.NULL.toString()
                        + "," + newLine).getBytes());
    		    
    		    out.flush();
		    }

		    if(categories != null){
    		    jsonArray = categories.getJSONArray("categories");
    		    
    		    if(jsonArray != null)
        			out.write(("\"categories\":"
        					+ jsonArray.toString() + newLine)
        					.getBytes());
    		    else out.write(("\"categories\":"
                        + JSONObject.NULL.toString()
                        + "," + newLine).getBytes());	
    		    
    		    out.flush();
    		    
		    }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		out.write("});".getBytes());
		out.flush();
		out.close();

	}

	public void writePureJson(OutputStream outputStream) throws IOException {

		String newLine = System.getProperty("line.separator");
		BufferedOutputStream out = new BufferedOutputStream(outputStream);
		out.write(("{" + newLine).getBytes());
		out.flush();

		try {
		    JSONArray jsonArray = null;
		    
		    if(organizations != null){
		        jsonArray = organizations.getJSONArray("organizations");
		        
		        if(jsonArray != null)
        			out.write(("\"organizations\":"
        					+ jsonArray.toString()
        					+ "," + newLine).getBytes());
		        else out.write(("\"organizations\":"
                            + JSONObject.NULL.toString()
                            + "," + newLine).getBytes());
		        
		        out.flush();
		    }

		    if(contacts != null){
		        jsonArray = contacts.getJSONArray("persons");
		        
		        if(jsonArray != null)
		            out.write(("\"persons\":"
		                    + jsonArray.toString() + "," + newLine)
		                    .getBytes());
		        else out.write(("\"persons\":"
                        + JSONObject.NULL.toString()
                        + "," + newLine).getBytes());
		        
    			out.flush();
		    }

		    if(articles != null){
		        jsonArray = articles.getJSONArray("publications");
		        
		        if(jsonArray != null)
        			out.write(("\"publications\":"
        					+ jsonArray.toString() + "," + newLine)
        					.getBytes());
		        else out.write(("\"publications\":"
                        + JSONObject.NULL.toString()
                        + "," + newLine).getBytes());
		        
    			out.flush();
		    }

		    if(events != null){
		        jsonArray = events.getJSONArray("events");
		        
		        if(jsonArray != null)
        			out.write(("\"events\":" + jsonArray.toString()
        					+ "," + newLine).getBytes());
		        else out.write(("\"events\":"
                        + JSONObject.NULL.toString()
                        + "," + newLine).getBytes());	
		        
		        out.flush();
		    }
		    
		    if(locations != null){
		        jsonArray = locations.getJSONArray("locations");
		        
		        if(jsonArray != null)
        			out.write(("\"locations\":"
        					+ jsonArray.toString() + "," + newLine)
        					.getBytes());
		        else out.write(("\"locations\":"
                        + JSONObject.NULL.toString()
                        + "," + newLine).getBytes());
		        
    			out.flush();
		    }

		    if(categories != null){
		        jsonArray = categories.getJSONArray("categories");
		        
		        if(jsonArray != null)
        			out.write(("\"categories\":"
        					+ jsonArray.toString() + newLine)
        					.getBytes());
		        else out.write(("\"categories\":"
                        + JSONObject.NULL.toString()
                        + "," + newLine).getBytes());
		        
    			out.flush();
		    }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		out.write("}".getBytes());
		out.flush();
		out.close();

	}

}
