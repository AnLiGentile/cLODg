package org.eswc.conferences.data;

import java.util.Iterator;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.base.file.Location;
import com.hp.hpl.jena.util.FileManager;

public class JenaTDBTriplestoreBuilder {

	public void add(Model model, String name, Location location) {
		Dataset dataset = TDBFactory.createDataset(location);

		dataset.addNamedModel(name, model);
	}

	public void empty(Location location) {

		Dataset dataset = TDBFactory.createDataset(location);
		Iterator<String> namesIt = dataset.listNames();
		while (namesIt.hasNext()) {
			dataset.removeNamedModel(namesIt.next());
		}
	}

	public static void main(String[] args) {

		JenaTDBTriplestoreBuilder triplestoreBuilder = new JenaTDBTriplestoreBuilder();
		Location location = new Location("out/triplestore");

		triplestoreBuilder.empty(location);

		Model swdfModel = FileManager.get()
				.loadModel("out/eswc_data_final.rdf");
		Model sparModel = FileManager.get()
				.loadModel("out/spar_data_final.rdf");

		triplestoreBuilder.add(swdfModel, "swdf", location);
		triplestoreBuilder.add(sparModel, "spar", location);

	}
}
