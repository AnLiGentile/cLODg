package it.istc.cnr.stlab.clodg.quality;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.function.FunctionRegistry;
import org.apache.jena.util.FileManager;

public class ModelTest {

    public ModelTest() {
        FunctionRegistry.get().put("http://data.semanticweb.org/function/localName", LocalName.class);
    }
    
    public void test(){
        String sparql = "prefix swrc: <http://swrc.ontoware.org/ontology#> " +
                        "prefix bibo: <http://purl.org/ontology/bibo/> " +
                        "prefix cont: <http://www.ontologydesignpatterns.org/ont/conference.owl#> " +
                        "prefix dc: <http://purl.org/dc/elements/1.1/> " +
                        "prefix fun: <http://data.semanticweb.org/function/> " +
                        "prefix fabio: <http://purl.org/spar/fabio/> " +
                        "construct { " +
                        "?paper a fabio:ConferencePaper . " +
                        "?paper cont:hasAuthorList ?authorList . " +
                        "?paper cont:hasAuthor ?author . " +
                        "?author a cont:Person . " +
                        "?author cont:hasRole ?authorRole . " +
                        "?authorRole cont:atEvent <http://data.semanticweb.org/conference/eswc/2014> . " +
                        "<http://data.semanticweb.org/conference/eswc/2014> a cont:Conference " +
                        "} " +
                        "where{ " +
                        "?paper a swrc:InProceedings . " +
                        "?paper bibo:authorList ?authorList . " +
                        "?paper dc:creator ?author . " +
                        "bind(IRI(concat('http://data.semanticweb.org/role/author/',fun:localName(?author))) AS ?authorRole) " +
                        "}";
        
        Model model = FileManager.get().loadModel("rext.rdf");
        Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
        
        Model out = queryExecution.execConstruct();
        
        out.write(System.out);
    }
    
    public static void main(String[] args) {
        ModelTest modelTest = new ModelTest();
        modelTest.test();
    }
    
}
