package it.istc.cnr.stlab.clodg;

import java.io.File;

import org.apache.jena.rdf.model.Model;

public interface RDFGraphBuilder {

	Model buildRDF(File directory);
}
