package it.istc.cnr.stlab.clodg.workflow;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;

public class LocalNameFunction extends FunctionBase1 {

    @Override
    public NodeValue exec(NodeValue v) {
        if(v.isIRI()){
            Node node = v.getNode();
            String uri = node.getURI();
            
            int lastSlash = uri.lastIndexOf("/");
            int lastHash = uri.lastIndexOf("#");
            
            String localName = null;
            if(lastHash > lastHash) localName = uri.substring(lastHash+1);
            else localName = uri.substring(lastSlash+1);
            return NodeValue.makeString(localName);
        }
            
        return null;
    }

}
