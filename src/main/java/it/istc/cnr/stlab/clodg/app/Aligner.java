package it.istc.cnr.stlab.clodg.app;

import org.codehaus.jettison.json.JSONObject;

import com.hp.hpl.jena.rdf.model.Model;

public interface Aligner {

	Model align(JSONObject jsonObject, Model model);
}
