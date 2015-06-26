package org.eswc.conferences.data;

import java.io.File;

import com.hp.hpl.jena.rdf.model.Model;

public interface RDFGraphBuilder {

	Model buildRDF(File directory);
}
