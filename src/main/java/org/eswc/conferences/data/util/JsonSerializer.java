package org.eswc.conferences.data.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

/**
 * @author annalisa
 *
 */
public class JsonSerializer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Model model =
		// FileManager.get().loadModel("out/eswc_data_final_swdf.rdf");

		Model model = FileManager.get().loadModel("out/twitterWidget.rdf");
		Model model2 = ModelFactory.createDefaultModel();

		OutputStream out = null;
		try {
			out = new FileOutputStream("./out/eswc.json");
			model2.setNsPrefixes(model.getNsPrefixMap());
			model2.add(model);
			// model2.write(out);

			// model2.write(out, "RDF/JSON") ;
			model2.write(out, "JSON-LD");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
