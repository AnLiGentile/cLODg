package it.istc.cnr.stlab.clodg.form;

import au.com.bytecode.opencsv.CSVReader;

import org.apache.jena.rdf.model.Model;

public interface FormData {

	Model toRDF(CSVReader csvReader);

}
