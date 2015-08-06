package it.istc.cnr.stlab.clodg.form;

import au.com.bytecode.opencsv.CSVReader;

import com.hp.hpl.jena.rdf.model.Model;

public interface FormData {

	Model toRDF(CSVReader csvReader);

}
