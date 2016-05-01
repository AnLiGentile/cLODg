package it.istc.cnr.stlab.clodg.app;

import java.io.File;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;

public class ImageAligner {

	public static void main(String[] args) {
		Model model = FileManager.get().loadModel("out/eswc_data_final.rdf");

		StmtIterator stmtIterator = model.listStatements(null,
				model.createProperty("http://dpedia.org/ontology/thumbnail"),
				(RDFNode) null);

		Model modelTmp = ModelFactory.createDefaultModel();

		while (stmtIterator.hasNext()) {
			Statement stmt = stmtIterator.next();

			Resource subject = stmt.getSubject();
			Property predicate = stmt.getPredicate();
			RDFNode object = stmt.getObject();

			String paperId = subject.getURI().substring(
					subject.getURI().lastIndexOf("/") + 1);
			String name = object.toString().replace(
					"http://wit.istc.cnr.it/eswc-stc/images/papers/resized/",
					"");
			String track = name.substring(0, name.lastIndexOf("/"));

			File file;
			if (!name.endsWith(".jpg"))
				file = new File("img/papers/" + name + ".jpg");
			else
				file = new File("img/papers/" + name);

			file.renameTo(new File("img/papers/" + track + "/" + paperId
					+ ".jpg"));

			Resource newImg = modelTmp
					.createResource("http://wit.istc.cnr.it/eswc-stc/images/papers/resized/"
							+ track + "/" + paperId + ".jpg");
			model.add(subject, predicate, newImg);

		}

		model.removeAll(null,
				model.createProperty("http://dpedia.org/ontology/thumbnail"),
				(RDFNode) null);
		model.add(modelTmp);
	}

}
