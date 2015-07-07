package it.istc.cnr.stlab.clodg.workflow;

import java.net.URL;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;

/**
 * @author annalisa
 *
 */
public class PhDSympHandler extends DataHandler {

    public PhDSympHandler(Model phDSympModel) {
        this.dataModel = phDSympModel;
        
        //TODO adjust query for phDSymp
        String sparql = "PREFIX swrc: <http://swrc.ontoware.org/ontology#> " +
        		        "PREFIX fabio: <http://purl.org/spar/fabio/> " +
        		        "CONSTRUCT {?paper a swrc:InProceedings}" +
        		        "WHERE{{?paper a fabio:PhDSymposiumPaper}}";
        Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, phDSympModel);
        Model inProceedingsModel = queryExecution.execConstruct();
        
        phDSympModel.add(inProceedingsModel);
    }
    
    public static void main(String[] args) {
        /*
         * We add the data about Poster and Demo.
         */
        URL posterDemoModelURL = PhDSympHandler.class.getClassLoader().getResource("data/eswc2015_phd.rdf");
        if(posterDemoModelURL != null){
            String posterDemoModelPathName = posterDemoModelURL.getFile();
            Model posterDemoModel = FileManager.get().loadModel(posterDemoModelPathName);
            PhDSympHandler posterDemoHandler = new PhDSympHandler(posterDemoModel);
            posterDemoHandler.getDataModel().write(System.out);
        }
    }
    
}
