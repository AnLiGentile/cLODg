package org.eswc.conferences.data;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase3;

public class Replace extends FunctionBase3 {

	public Replace() {
		super();
	}

	@Override
	public NodeValue exec(NodeValue n1, NodeValue n2, NodeValue n3) {
		Node replace = n2.asNode();
		Node newVal = n3.asNode();
		Node node = n1.asNode();

		if (node.isLiteral() && replace.isLiteral() && newVal.isLiteral()) {
			String replaceStr = replace.getLiteralLexicalForm();
			String newValStr = newVal.getLiteralLexicalForm();

			String label = node.getLiteralLexicalForm();

			return NodeValue
					.makeString(label.replaceAll(replaceStr, newValStr));

		}

		return n1;
	}

}
