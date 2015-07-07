package it.istc.cnr.stlab.clodg.util;

import org.apache.commons.lang3.StringUtils;

import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase2;

public class SwdfIRI extends FunctionBase2 {

    @Override
    public NodeValue exec(NodeValue v1, NodeValue v2) {
        if(v1.isLiteral() && v2.isLiteral()){
            String v1String = v1.asNode().getLiteralLexicalForm().replaceAll("\\_", "-").replace(" ", "-");
            String v2String = v2.asNode().getLiteralLexicalForm().replaceAll("\\_", "-").replace(" ", "-");
            
            String id = StringUtils.stripAccents(v1String) + "-" + StringUtils.stripAccents(v2String);
            
            return NodeValue.makeString(OfficialNameSpace.personNs + id.toLowerCase());
            
        }
        return null;
    }

}
