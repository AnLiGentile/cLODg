package org.scholarlydata.clodg.eswc2016;

import org.scholarlydata.clodg.Clodg;
import org.scholarlydata.clodg.RDFRender;
import org.scholarlydata.clodg.app.AppDataGenerator;

import com.hp.hpl.jena.util.FileManager;
import com.sun.jmx.snmp.Timestamp;

public class Main {

	public static void main(String[] args) {
		
		String[] arguments = new String[]{"-c", "eswc2016.properties", "-o", "eswc2016.ttl"};
		Clodg.main(arguments);
		
		arguments = new String[]{"-c", "eswc2016_pd.properties", "-o", "eswc2016_all.ttl", "-i", "eswc2016.ttl"};
		Clodg.main(arguments);
		
		RDFRender.rdfXml("eswc2016_all.ttl", "eswc2016.rdf");
		
		AppDataGenerator appDataGenerator = new AppDataGenerator(FileManager.get().loadModel("eswc2016.rdf"));
		appDataGenerator.generateAppData();
		
		System.out.println(new Timestamp(System.currentTimeMillis()).toString());
	}
	
}
