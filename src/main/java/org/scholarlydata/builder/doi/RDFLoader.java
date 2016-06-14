package org.scholarlydata.builder.doi;

import org.apache.jena.util.FileManager;

public class RDFLoader {

	public static void main(String[] args) {
		FileManager.get().loadModel("/Users/andrea/Desktop/ESWC2016data/mapping.ttl");
	}
	
}
