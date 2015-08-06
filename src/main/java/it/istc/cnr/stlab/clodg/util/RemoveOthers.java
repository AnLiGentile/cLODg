package it.istc.cnr.stlab.clodg.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import au.com.bytecode.opencsv.CSVReader;

public class RemoveOthers {

	public static Map<String, String> getNames() {
		Map<String, String> mainC = new HashMap<String, String>();

		mainC.put("In-Use & Industrial III", "Polymnia/Erato/Kalia/Melpo");
		mainC.put("Posters and Demos", "Polymnia/Erato/Kalia/Melpo");
		mainC.put("Annoucements", "Hermes/Apollon");
		mainC.put("Closing Best Workshop papers", "Thalia/Efterpi/Clio");
		mainC.put("Cognition and Semantic Web", "Polymnia/Erato/Kalia/Melpo");
		mainC.put("Panel: Data protection and security on the Web",
				"Hermes/Apollon");
		mainC.put("In-Use papers In-Use and Industrial I",
				"Polymnia/Erato/Kalia/Melpo");
		mainC.put("In-Use and Industrial I", "Polymnia/Erato/Kalia/Melpo");
		mainC.put("In-Use papers In-Use & Industrial II",
				"Polymnia/Erato/Kalia/Melpo");
		mainC.put("In-Use and Industrial III", "Polymnia/Erato/Kalia/Melpo");

		mainC.put("In-Use papers In-Use & Industrial III",
				"Polymnia/Erato/Kalia/Melpo");

		mainC.put("Keynote Speech - Lise Getoo", "Hermes/Apollon");
		mainC.put("Linked Open Data III", "Hermes/Apollon");
		mainC.put("Challenges ESWC Challenges", "Polymnia/Erato");
		mainC.put("Challenges Posters and Challenges", "Polymnia/Erato");

		mainC.put("Linked Open Data IV", "Hermes/Apollon");
		// mainC.put("Closing and Awards Ceremony", "Hermes/Apollon");
		mainC.put("Closing Closing Ceremony", "Hermes/Apollon");

		mainC.put("LinkedUp Challenge", "Thalia/Efterpi");
		mainC.put("Coffee Break", "Conference Center Lobby I and Lobby II");
		mainC.put("Vocabularies, Schemas, Ontologies",
				"Polymnia/Erato/Kalia/Melpo");
		mainC.put(
				"EU Project posters, ESWC Challenges, AI Mashup Challenge, LinkedUp Challenge",
				"Beach Bar");
		mainC.put("Gala Gala Dinner", "Aldemar Knossos Main Pool");
		mainC.put("Social Web and Web Science", "Polymnia/Erato/Kalia/Melpo");
		mainC.put("Data Management I", "Hermes/Apollon");
		mainC.put("Services, Processes and Cloud Computing", "Hermes/Apollon");
		mainC.put("Linked Open Data II", "Hermes/Apollon");
		mainC.put("Closing Wrap-up session", "Thalia/Efterpi/Clio");
		mainC.put("EU Project EU Project Networking Session", "Orpheas");
		mainC.put("Linked Open Data I", "Hermes/Apollon");
		mainC.put("Lunch", "Main Restaurant");
		mainC.put("Keynote Speech: Steffen Staab", "Hermes/Apollon");
		mainC.put("Semantic Web Policies, Rights and Governance",
				"Polymnia/Erato/Kalia/Melpo");
		mainC.put("Keynote Speech: Luciano Floridi", "Hermes/Apollon");
		mainC.put("Reasoning", "Hermes/Apollon");
		// mainC.put("Opening Ceremony", "Hermes/Apollon");
		mainC.put("Opening Opening", "Hermes/Apollon");
		mainC.put("Announcements Announcements", "Hermes/Apollon");
		mainC.put("Panel session Panel session", "Hermes/Apollon");

		mainC.put("Posters and Demos Posters and Demos",
				"Ourania/Polymnia/Erato/Kalia/Melpo/Thalia/Efterpi/Clio");
		mainC.put("AI MashUp challenge", "Thalia/Efterpi");
		mainC.put("Machine Learning", "Polymnia/Erato/Kalia/Melpo");
		mainC.put("Mobile, Sensor and Semantic Streams", "Hermes/Apollon");
		mainC.put("Data Management II", "Hermes/Apollon");
		mainC.put("Natural Language Processing", "Polymnia/Erato/Kalia/Melpo");
		return mainC;
	}

	public static Map<String, String> getMainConfLocations() {

		File f = new File(
				"/Users/annalisa/Desktop/ESWC2014/planner/main/sessions.csv");
		Map<String, String> main = new HashMap<String, String>();

		try {

			CSVReader csvReader = new CSVReader(new InputStreamReader(
					new FileInputStream(f), "UTF-8"), ';');

			String[] line = null;
			while ((line = csvReader.readNext()) != null) {
				String id1 = line[3].trim();
				// String id2 = null;
				// try {
				// id2 = line[4].trim();
				// } catch (Exception e) {
				// // TODO Auto-generated catch block
				// System.out.println(id1);
				// e.printStackTrace();
				// }
				//
				// String event = id1 +" "+id2;

				String location = line[5].trim();

				main.put(id1, location);

			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return main;

	}

	public static void main(String[] args) {

		// System.out.println(getMainConfLocations());
		// for (Entry<String, String> e : getMainConfLocations().entrySet()){
		// System.out.println("mainC.put(\""+e.getKey()+"\", \""+e.getValue()+"\");");
		// }

		JSONParser parser = new JSONParser();

		JSONArray finalEv = new JSONArray();

		try {
			FileReader r = new FileReader("out/json/talks.json");
			Object obj = parser.parse(r);
			r.close();

			FileReader e = new FileReader("out/json/events.js");
			JSONObject objev = (JSONObject) parser.parse(e);
			e.close();

			JSONObject jsonObject = (JSONObject) obj;
			JSONArray listTalk = (JSONArray) jsonObject.get("talks");
			JSONArray listEvents = (JSONArray) objev.get("events");

			JSONArray finalTalks = new JSONArray();

			Set<Integer> indexes = new HashSet<Integer>();

			for (int i = 0; i < listTalk.size(); i++) {
				JSONObject jsonObjectIN = (JSONObject) listTalk.get(i);

				Object pidObject = jsonObjectIN.get("paper");

				long pid;
				if (pidObject instanceof Long) {
					pid = (Long) jsonObjectIN.get("paper");
				} else {
					pid = Long.valueOf(jsonObjectIN.get("paper").toString());
				}

				if (pid == 777) {

					String startOtherTalk = (String) jsonObjectIN.get("start");
					String endOtherTalk = (String) jsonObjectIN.get("end");

					long id = (Long) jsonObjectIN.get("event");
					// TODO from objev, find event with id = id
					String startevent = "";
					String endevent = "";

					boolean interrupt = false;
					for (int j = 0; j < listEvents.size() && !interrupt; j++) {
						JSONObject jsonObjectEV = (JSONObject) listEvents
								.get(j);
						// System.err.println(jsonObjectEV.get("id")+"--->"+id);
						long eventId = (Long) jsonObjectEV.get("id");

						if (id == eventId) {
							startevent = (String) jsonObjectEV.get("start");
							endevent = (String) jsonObjectEV.get("end");
							interrupt = true;
						}
					}

					try {
						if (!startevent.equals(startOtherTalk)
								&& !endevent.equals(endOtherTalk)) {
							System.out.println(startevent + " : "
									+ startOtherTalk + " - " + endevent + " : "
									+ endOtherTalk);
							finalTalks.add(jsonObjectIN);
						}
					} catch (Exception e1) {
						System.err.println(jsonObjectIN + "--->" + id);

						System.err.println(startevent);

						System.err.println(endevent);
						e1.printStackTrace();
					}

					indexes.add(i);
				} else {
					finalTalks.add(jsonObjectIN);
				}
			}

			jsonObject.put("talks", finalTalks);

			File fileT = new File("out/json/talks.js");
			FileWriter fileWriterT = new FileWriter(fileT);

			fileWriterT.write(jsonObject.toJSONString());
			fileWriterT.flush();
			fileWriterT.close();

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Object obj = parser.parse(new
		// FileReader("/Users/annalisa/Desktop/ESWC2014/demo/contacts.json"));

	}
}
