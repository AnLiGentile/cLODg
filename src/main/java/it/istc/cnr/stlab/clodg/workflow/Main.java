package it.istc.cnr.stlab.clodg.workflow;

import it.istc.cnr.stlab.clodg.app.CalendarAlignerWithSessions;
import it.istc.cnr.stlab.clodg.models.AppJsonModels;
import it.istc.cnr.stlab.clodg.util.OfficialNameSpace;
import it.istc.cnr.stlab.clodg.util.PythonConverters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.jena.riot.RiotException;
import org.codehaus.jettison.json.JSONObject;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;

/**
 * @author andrea
 *
 */
public class Main {

	public static void main(String[] args) {

//		String easychairSnapshot = "data/ESWC2015_snapshot_2015-04-08.xml";
	    String easychairSnapshot = "data/data_example2008.xml";
		String conferenceConfiguration = "data/eswc2015-config.xml";
		String folderContainingIcsCalendars = "";
		String jsFinalData ="data_CONFERENCE.js";
		String jsonFinalData ="data_CONFERENCE.json";

		
		String year = "2015";
		String baseDomain = "http://data.semanticweb.org/";
		String conference = "conference/eswc/";
		String mainTrackPaper =  "research/";
		String inusePaper =  "in-use/";
		String posterPaper =  "poster/";
		String demoPaper =  "demo/";
		String phdPaper =  "phDSymposium/";
		String challengePaper =  "challenge/";
	
	
		OfficialNameSpace ns = new OfficialNameSpace (year, baseDomain, conference,
				mainTrackPaper, inusePaper,
				posterPaper, demoPaper, phdPaper,
				challengePaper);
				
				
		
		Options options = new Options();
		options.addOption(
				"r",
				true,
				"Folder where to save the RDF file contaning the data to sent to the Semantic Web Dog Food.");
		options.addOption("j", true,
				"Folder where to save the JSON files contaning the data to use for the APP.");
		options.addOption("i", true,
                "Precomputed RDF file containing data compliant to the semantic web dog food format.");

		CommandLine commandLine = null;

		CommandLineParser cmdLinePosixParser = new PosixParser();
		try {
			commandLine = cmdLinePosixParser.parse(options, args);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (commandLine != null) {
			for (Option option : commandLine.getOptions()) {
				System.out.println(option.getValue());
			}
			String rdfFolder = commandLine.getOptionValue('r');
			String jsonFolder = commandLine.getOptionValue('j');
			String dogFoodInput = commandLine.getOptionValue('i');

			if (rdfFolder != null && jsonFolder != null) {

				File rdfFolderFile = new File(rdfFolder);
				if (!rdfFolderFile.exists())
					rdfFolderFile.mkdirs();

				File jsonFolderFile = new File(jsonFolder);
				if (!jsonFolderFile.exists())
					jsonFolderFile.mkdirs();

				/*
				 * Generate RDF data including thumbnails, depictions and
				 * decrypted mboxes.
				 */
				GenerateMainConferenceInitialGraph generateMainConferenceInitialGraph = new GenerateMainConferenceInitialGraph(
						easychairSnapshot, conferenceConfiguration, ns);
				
				Model dogFoodData = null;
				if(dogFoodInput != null){
				    try{
				        dogFoodData = FileManager.get().loadModel(dogFoodInput);
				    } catch(RiotException e){
				        System.err.println("The semantic web dog food compliant model is not valid.");
				    }
				}
				
				if(dogFoodData == null) dogFoodData = generateMainConferenceInitialGraph.generateArticlesRDFModel();

				generateMainConferenceInitialGraph.alignFormData(dogFoodData);

				/*
				 * Execute PythonConverters.
				 */
				PythonConverters.doStuff(folderContainingIcsCalendars, ns);

				/*
				 * Execute CalendarAlignerWithSessions.
				 */
				CalendarAlignerWithSessions.doStuff(dogFoodData);

				/*
				 * Save the RDF model
				 */
				OutputStream outputStream;
				try {
					outputStream = new FileOutputStream(new File(rdfFolder
							+ File.separator + "form-data.rdf"));
					dogFoodData.write(outputStream, null, "RDF/XML");
					outputStream.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				/*
				 * Convert the RDF to the a set of JSON files compliant to the
				 * model used by the ConferenceLive App.
				 */
				GenerateAppData generateAppData = new GenerateAppData(
						dogFoodData);
				AppJsonModels appJsonModels = generateAppData
						.generateJsonModels();

				/*
				 * Save the JSON files for the ConferenceLive App.
				 */
				JSONObject json = appJsonModels.getContacts();
				if (json != null) {
					try {
						outputStream = new FileOutputStream(jsonFolder
								+ File.separator + "contacts.json");
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
				if (json != null) {
					try {
						outputStream = new FileOutputStream(jsonFolder
								+ File.separator + "articles.json");
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
				if (json != null) {
					try {
						outputStream = new FileOutputStream(jsonFolder
								+ File.separator + "organizations.json");
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
				if (json != null) {
					try {
						outputStream = new FileOutputStream(jsonFolder
								+ File.separator + "events.json");
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

				/*
				 * Write the single javascript file containing all the data
				 * models.
				 */

				try {
					appJsonModels.write(new FileOutputStream(jsonFolder
							+ File.separator + jsFinalData));
					appJsonModels.writePureJson(new FileOutputStream(jsonFolder
							+ File.separator + jsonFinalData));

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				/*
				 * Remove thumbnails, depictions and decrypted mboxes in order
				 * to be compliant with the Semantic Web Dog Food
				 * recomemndations.
				 */
				generateMainConferenceInitialGraph
						.convertToSemanticWebDogFood(dogFoodData);

				/*
				 * Save the RDF model
				 */
				try {
					outputStream = new FileOutputStream(new File(rdfFolder
							+ File.separator + "dog-food-data.rdf"));
					dogFoodData.write(outputStream, null, "RDF/XML");
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
}
