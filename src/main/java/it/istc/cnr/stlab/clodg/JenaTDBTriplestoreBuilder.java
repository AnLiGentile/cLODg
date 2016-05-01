package it.istc.cnr.stlab.clodg;

import java.util.Iterator;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.util.FileManager;

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
		Location location = Location.create("out/triplestore");

		triplestoreBuilder.empty(location);

		Model swdfModel = FileManager.get()
				.loadModel("out/eswc_data_final.rdf");
		Model sparModel = FileManager.get()
				.loadModel("out/spar_data_final.rdf");

		triplestoreBuilder.add(swdfModel, "swdf", location);
		triplestoreBuilder.add(sparModel, "spar", location);

	}
}
