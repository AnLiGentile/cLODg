package it.istc.cnr.stlab.clodg.quality;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;

public class LocalName extends FunctionBase1 {

    public LocalName() {
        super();
    }
    
    @Override
    public NodeValue exec(NodeValue v) {
        String localName = null;
        
        System.out.println("exec");
        if(v.isIRI()){
            String uri = v.getNode().getURI();
            int lastSlash = uri.lastIndexOf("/");
            int lastHash = uri.lastIndexOf("#");
            
            
            if(lastSlash > lastHash) localName = uri.substring(lastSlash + 1);
            else localName = uri.substring(lastHash + 1);
            
        }
        if(localName != null) return NodeValue.makeString(localName);
        else return null;
    }
    
}