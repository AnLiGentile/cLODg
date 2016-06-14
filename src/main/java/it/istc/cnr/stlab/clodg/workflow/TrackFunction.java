package it.istc.cnr.stlab.clodg.workflow;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;

public class TrackFunction extends FunctionBase1 {

	public TrackFunction() {

	}

	@Override
	public NodeValue exec(NodeValue v) {
		if (v.isIRI()) {
			String uri = v.asUnquotedString();
			if(uri.contains("/poster/")) return NodeValue.makeString("poster");
			else return NodeValue.makeString("demo");
		}
		return null;
	}

}