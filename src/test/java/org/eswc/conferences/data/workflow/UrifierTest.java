package org.eswc.conferences.data.workflow;

import org.eswc.conferences.data.util.Urifier;
import org.junit.Test;

/**
 * @author andrea.nuzzolese
 *
 */
public class UrifierTest {
    
    @Test
    public void testLabel2Uri(){
        String label = "STLab, ISTC-CNR";
        label = Urifier.toURI(label);
        System.out.println(label);
        
        label = "STLààb, ISTC;sa_-CNR";
        label = Urifier.toURI(label);
        System.out.println(label);
    }
    
}
