package org.scholarlydata.clodg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;

public class Clodg {

	private static final String CONFIGURATION_FILE = "c";
    private static final String CONFIGURATION_FILE_LONG = "config";
    
    private static final String OUTPUT_FILE = "o";
    private static final String OUTPUT_FILE_LONG = "output";
    
    private static final String INPUT_MODEL = "i";
    private static final String INPUT_MODEL_LONG = "input";
	
	public static void main(String[] args) {
		
		/*
         * Set-up the options for the command line parser.
         */
        Options options = new Options();
        
        Builder optionBuilder = Option.builder(CONFIGURATION_FILE);
        Option configurationFileOption = optionBuilder.argName("file")
                                 .hasArg()
                                 .required(true)
                                 .desc("MANDATORY - Input file containing the app configuration.")
                                 .longOpt(CONFIGURATION_FILE_LONG)
                                 .build();
        
        optionBuilder = Option.builder(OUTPUT_FILE);
        Option outputFileOption = optionBuilder.argName("file")
                                 .hasArg()
                                 .required(false)
                                 .desc("OPTIONAL - Output file for the final RDF model. If no value is provided, then system out is used by default.")
                                 .longOpt(OUTPUT_FILE_LONG)
                                 .build();
        
        optionBuilder = Option.builder(INPUT_MODEL);
        Option inputModelOption = optionBuilder.argName("file")
                                 .hasArg()
                                 .required(false)
                                 .desc("OPTIONAL - The path to an input RDF model to merge with the output of cLODg.")
                                 .longOpt(INPUT_MODEL_LONG)
                                 .build();
        
        options.addOption(configurationFileOption);
        options.addOption(outputFileOption);
        options.addOption(inputModelOption);
        
        CommandLine commandLine = null;
        
        CommandLineParser cmdLineParser = new DefaultParser();
        try {
            commandLine = cmdLineParser.parse(options, args);
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "process", options );
        }
        
        if(commandLine != null){
            for(Option option : commandLine.getOptions()){
                System.out.println(option.getValue());
            }
            String configuration = commandLine.getOptionValue(CONFIGURATION_FILE);
            String outputFile = commandLine.getOptionValue(OUTPUT_FILE);
            String inputModelPath = commandLine.getOptionValue(INPUT_MODEL);
            
            if(configuration != null){
            	LDGenerator datasetLoader = LDGenerator.getInstance();
            	Model model = datasetLoader.generate(configuration);
            	
            	if(inputModelPath != null && !inputModelPath.trim().isEmpty())
            		model.add(FileManager.get().loadModel(inputModelPath));
            	
            	try {
            		if(outputFile != null && !outputFile.trim().isEmpty())
            			model.write(new FileOutputStream(new File(outputFile)), "TURTLE");
            		else model.write(System.out, "TURTLE");
    			} catch (FileNotFoundException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			
    			model.close();
            }
        }
		
	}
}
