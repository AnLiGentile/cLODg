package it.istc.cnr.stlab.clodg.app;

import java.io.File;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

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
