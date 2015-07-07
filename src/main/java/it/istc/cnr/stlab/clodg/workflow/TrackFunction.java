package it.istc.cnr.stlab.clodg.workflow;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase1;

public class TrackFunction extends FunctionBase1 {

	public TrackFunction() {

	}

	@Override
	public NodeValue exec(NodeValue v) {
		if (v.isIRI()) {
			String uri = v.asUnquotedString();
			Pattern pattern = Pattern.compile("/[a-zA-Z0-9_-]+/[0-9]+$");
			Matcher matcher = pattern.matcher(uri);
			if (matcher.find()) {
				String foundMatch = uri.substring(matcher.start(),
						matcher.end());
				return NodeValue.makeString(foundMatch.substring(1,
						foundMatch.indexOf("/", 1)));
			} else
				return null;
		}
		return null;
	}

}