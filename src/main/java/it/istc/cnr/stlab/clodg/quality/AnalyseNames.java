package it.istc.cnr.stlab.clodg.quality;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.RDFS;

public class AnalyseNames {

	private static final String GRAPH_NAME = "data/all-names.rdf";
	
	private static String SWDF_SPARQL_ENDPOINT = "http://data.semanticweb.org/sparql";

	public Model loadGraph() {
		return loadGraph(null);
	}

	public Model loadGraph(String path) {
		// load model from static dump (can find an example in
		// /cLODg/src/main/resources/data/all-names.rdf)

		Model m = ModelFactory.createDefaultModel();
		InputStream is = null;
		String syntax = null;
		if (path == null) {
			is = getClass().getClassLoader().getResourceAsStream(GRAPH_NAME);
			syntax = "RDF/XML";
		} else {
			try {
				is = new FileInputStream(new File(path));
				syntax = FileUtils.guessLang(path);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		if (is != null)
			m.read(is, null, syntax);

		return m;
	}

	public HashMap<Resource, Set<Literal>> extractPersonNames(Model nameGraph) {
		HashMap<Resource, Set<Literal>> names = new HashMap<Resource, Set<Literal>>();
		// person names populated as follows:
		// - KEY: person URI
		// - VALUES: all name lexicalizations for the person (label, name, first
		// name, last name ...)

		String sparql = "SELECT ?person ?name "
				+ "WHERE {"
				+ "  {?person <"
				+ FOAF.name
				+ "> ?name} "
				+ "  UNION "
				+ "  {?person <"
				+ FOAF.firstName
				+ "> ?name} "
				+ "  UNION "
				+ "  {?person <http://xmlns.com/foaf/0.1/lastName> ?name} "
				+ "  UNION "
				+ "  {?person <"
				+ RDFS.label
				+ "> ?name} "
				+ "  FILTER(REGEX(STR(?person), '^http://data.semanticweb.org/person/'))"
				+ "}";
		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				nameGraph);

		ResultSet resultSet = queryExecution.execSelect();

		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			Resource person = querySolution.getResource("person");
			Literal name = querySolution.getLiteral("name");

			Set<Literal> personNames = names.get(person);
			if (personNames == null) {
				personNames = new HashSet<Literal>();
				names.put(person, personNames);
			}
			personNames.add(name);
		}

		return names;

	}

	public HashMap<Resource, Set<Literal>> extractOrganizationNames(
			Model nameGraph) {
		HashMap<Resource, Set<Literal>> names = new HashMap<Resource, Set<Literal>>();
		// organization names populated as follows:
		// - KEY: person URI
		// - VALUES: all name lexicalizations for the person (label, name, first
		// name, last name ...)

		String sparql = "SELECT ?organization ?name "
				+ "WHERE {"
				+ "  {?organization <"
				+ FOAF.name
				+ "> ?name} "
				+ "  UNION "
				+ "  {?organization <"
				+ RDFS.label
				+ "> ?name} "
				+ "  FILTER(REGEX(STR(?organization), '^http://data.semanticweb.org/organization/'))"
				+ "}";
		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				nameGraph);

		ResultSet resultSet = queryExecution.execSelect();

		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			Resource organization = querySolution.getResource("organization");
			Literal name = querySolution.getLiteral("name");

			Set<Literal> organizationNames = names.get(organization);
			if (organizationNames == null) {
				organizationNames = new HashSet<Literal>();
				names.put(organization, organizationNames);
			}
			organizationNames.add(name);
		}

		return names;

	}
	
	
	private Set<Literal> extractPublicationsForAuthor(Resource publication){
		//query dog to build a map of publications for the input author
		//e.g. for http://data.semanticweb.org/person/andrea-giovanni-nuzzolese
		//one of the entries in the map has
		//KEY: http://data.semanticweb.org/conference/eswc/2014/paper/ws/SSA/5
		//VALUES: "Semantic Web-based Sentiment Analysis"
	    
	    Set<Literal> titleSet = new HashSet<Literal>();
	    
	    String sparql = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
	                    "PREFIX dc: <http://purl.org/dc/elements/1.1/> " +
	    		        "SELECT DISTINCT ?title WHERE {" +
	    		        "{<" + publication.getURI() + "> rdfs:label ?title} " +
	    		        "UNION " +
	    		        "{<" + publication.getURI() + "> dc:title ?title} " +
	    		        "}";
	    
	    String requestPath;
        try {
            requestPath = SWDF_SPARQL_ENDPOINT + "?query=" + URLEncoder.encode(sparql, "UTF-8");
            URLConnection connection = new URL(requestPath).openConnection();
            connection.addRequestProperty("Accept", "application/xml");
            
            InputStream is = connection.getInputStream();
            
            ResultSet resultSet = ResultSetFactory.fromXML(is);
            while(resultSet.hasNext()){
                QuerySolution querySolution = resultSet.next();
                Literal title = querySolution.getLiteral("title");
                titleSet.add(title);
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
	    
	    return titleSet;
		
	}
	

	private Set<Resource> extractAuthorsForPublication(Resource publication){
		//query dog to build a map of authors for the input publication
		//e.g. for http://data.semanticweb.org/conference/eswc/2014/paper/ws/SSA/5
		//one of the entries in the map has
		//KEY: http://data.semanticweb.org/conference/eswc/2014/paper/ws/SSA/5
		//VALUES: http://data.semanticweb.org/person/aldo-gangemi, http://data.semanticweb.org/person/diego-reforgiato, ...
	    
	    Set<Resource> authorSet = new HashSet<Resource>();
        
	    String sparql = "PREFIX dc: <http://purl.org/dc/elements/1.1/> " +
                        "SELECT DISTINCT ?author WHERE {" +
                        "<" + publication.getURI() + "> dc:creator ?author " +
                        "}";
        
        String requestPath;
        try {
            requestPath = SWDF_SPARQL_ENDPOINT + "?query=" + URLEncoder.encode(sparql, "UTF-8");
            URLConnection connection = new URL(requestPath).openConnection();
            connection.addRequestProperty("Accept", "application/xml");
            
            InputStream is = connection.getInputStream();
            
            ResultSet resultSet = ResultSetFactory.fromXML(is);
            while(resultSet.hasNext()){
                QuerySolution querySolution = resultSet.next();
                Resource author = querySolution.getResource("author");
                authorSet.add(author);
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        
        return authorSet;
		
	}
	
	//TODO there is a bug: saves "null" as resource for empty co-authorships
	//TODO Andrea please check e.g. for URI http://data.semanticweb.org/person/raphaeel-troncy saves the map {null=0}
	private Map<Resource, Integer> extractCoauthorsForAuthor(Resource author){
		// use extractAuthorsForPublication to collect all co-authors for a given resource
		// call extractAuthorsForPublication to get co-authors for each publication
		// and build a co-authorship weighted map
	    
	    Map<Resource, Integer> coauthorMap = new HashMap<Resource, Integer>();
        
	    String sparql = "PREFIX dc: <http://purl.org/dc/elements/1.1/> " +
                        "SELECT ?coauthor (COUNT(?coauthor) AS ?weight) WHERE{" +
                        "{SELECT distinct ?paper ?coauthor WHERE { " +
                        "?paper dc:creator <" + author.getURI() + "> . " +
                        "?paper dc:creator ?coauthor . " +
                        "FILTER(?coauthor != <" + author.getURI() + ">) " +
                        "}} " +
                        "} " +
                        "GROUP BY ?coauthor";
        
        String requestPath;
        try {
            requestPath = SWDF_SPARQL_ENDPOINT + "?query=" + URLEncoder.encode(sparql, "UTF-8");
            URLConnection connection = new URL(requestPath).openConnection();
            connection.addRequestProperty("Accept", "application/xml");
            
            InputStream is = connection.getInputStream();
            
            ResultSet resultSet = ResultSetFactory.fromXML(is);
            while(resultSet.hasNext()){
                QuerySolution querySolution = resultSet.next();
                Resource coauthor = querySolution.getResource("coauthor");
                if(coauthor != null){
                    Literal weight = querySolution.getLiteral("weight");
                    coauthorMap.put(coauthor, Integer.valueOf(weight.getLexicalForm()));
                }
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        
        return coauthorMap;
		
	}
	
	
	double authorSimilarity(Resource URI1, Resource URI2){
		Map<Resource, Integer> coauthorsMap1 = this.extractCoauthorsForAuthor(URI1);
		Map<Resource, Integer> coauthorsMap2 = this.extractCoauthorsForAuthor(URI2);
		

        	
        	
        	
        Set<Resource> couathors = coauthorsMap1.keySet();
        Set<Resource> couathors2 = coauthorsMap2.keySet();
        
        if (couathors!=null&couathors2!=null){

        
        Set<String> couathors1str = new HashSet<String>();

//        System.out.println("********COAUTHORS MAP OF "+URI1+" ********");
        for(Resource coauthor : couathors) {
        	couathors1str.add(coauthor.getURI().toString());
//        	System.out.println(coauthor.getURI() + " : " + coauthorsMap1.get(coauthor));
        }
        
        Set<String> couathors2str = new HashSet<String>();
//        System.out.println("********COAUTHORS MAP OF "+URI2+" ********");
        for(Resource coauthor : couathors2) {
        	couathors2str.add(coauthor.getURI().toString());
//        	System.out.println(coauthor.getURI() + " : " + coauthorsMap2.get(coauthor));
        }
        
		// TODO Annalisa: this is a baseline score
        
//        System.out.println("******** intersection "+  SetOperations.intersection(couathors1str, couathors2str));
//        System.out.println("******** union "+  SetOperations.union(couathors1str, couathors2str));
//        System.out.println("******** difference "+  SetOperations.difference(couathors1str, couathors2str));
//        System.out.println("******** difference "+  SetOperations.difference(couathors2str, couathors1str));
        
        if (couathors1str.size()>0&couathors2str.size()>0){
            // if the set of co-authors for one is fully contained in the other, set score to 1 
        	int diff1_2 = SetOperations.difference(couathors1str, couathors2str).size();
        	int diff2_1 = SetOperations.difference(couathors2str, couathors1str).size();
        	if (diff1_2==0|diff2_1==0){
        		return 1;
        		}else{
        			// TODO this is crap. If one set is a lot bigger than the other this measure is not a good indicator
        			int inters = SetOperations.intersection(couathors1str, couathors2str).size();
        			int uni = SetOperations.union(couathors1str, couathors2str).size();
        			return (double) inters/uni;
        			// TODO design a measure that checks diff1_2 and diff2_1
        		}
        }

        }else
        {
        	System.err.println("one of the URI has NULL coauthor map");
        }

		return 0;
		
	}
	
	/**
	 * bulds a map of lexicalizations and the URIs that have that lexicalization
	 * @param names
	 * @return
	 */
	public HashMap<String, Set<Resource>> buildAmbigousNames(HashMap<Resource, Set<Literal>> names) {
		HashMap<String, Set<Resource>> ambigousNames = new HashMap<String, Set<Resource>>();
			
		for (Entry<Resource, Set<Literal>> s : names.entrySet()) {
			for (Literal n : s.getValue()){
				String name = n.toString();
				if (ambigousNames.get(name)==null)
					ambigousNames.put(name, new HashSet<Resource> ());
				
				ambigousNames.get(name).add(s.getKey());
				
			}
		}
		
		return ambigousNames;

	}
	
	void printBibtekTable(HashMap<String, Set<Resource>> aNames){
		System.out.println("\\hline");

		for (Entry<String, Set<Resource>> s : aNames.entrySet()) {
			if (s.getValue().size()>1){
			System.out.print(s.getKey() + "& ");
			for (Resource u : s.getValue()){
				System.out.print("\\url{"+u+"} ");
			}
			System.out.println("\\\\");

			}
		}
		System.out.println("\\hline");

	}

	public static void main(String[] args) {

		AnalyseNames an = new AnalyseNames();
		Model m = an.loadGraph();
		HashMap<Resource, Set<Literal>> names = an.extractPersonNames(m);
		HashMap<Resource, Set<Literal>> orgs = an.extractOrganizationNames(m);

//		System.out.println("*****************PERSONS*****************");	
//		for (Entry<Resource, Set<Literal>> s : names.entrySet()) {
//			System.out.println(s.getKey() + "\t" + s.getValue());
//		}

		System.out.println("********AMBIGOUS*PERSONS*****************");
		HashMap<String, Set<Resource>> aNames = an.buildAmbigousNames(names);
		int countAmbigous =0;
		for (Entry<String, Set<Resource>> s : aNames.entrySet()) {
			if (s.getValue().size()>1){
				countAmbigous++;
			System.out.println(countAmbigous+"\t"+s.getKey() + "\t" + s.getValue());
			if (s.getValue().size()==2){
				Resource[] arr = s.getValue().toArray(new Resource[s.getValue().size()]);

//				 Iterator<Resource> it = s.getValue().iterator();
				 
				 Resource a1 = arr[0];
				 Resource a2 = arr[1];
			     System.out.println("SIMILARITY "+a1+" and "+a2+" --> "+an.authorSimilarity(a1, a2));

//		        Resource a1 = ModelFactory.createDefaultModel().createResource(it.next());
//		        Resource a2 = ModelFactory.createDefaultModel().createResource(it.next());
//		        System.out.println("SIMILARITY "+a1+" and "+a2+" --> "+an.authorSimilarity(a1, a2));
			}else{
				//TODO fill this block
				System.err.println("more than 2 ambigous, do something");
			}
			
			}
		}
		
//		an.printBibtekTable(aNames);
		
//		System.out.println("*****************ORGANIZATIONS*****************");
//
//		for (Entry<Resource, Set<Literal>> s : orgs.entrySet()) {
//			System.out.println(s.getKey() + "\t" + s.getValue());
//		}
		
		System.out.println("********AMBIGOUS*ORGANIZATIONS************");
		HashMap<String, Set<Resource>> oNames = an.buildAmbigousNames(orgs);
		int countAmbigousOrgs =0;
		for (Entry<String, Set<Resource>> s : oNames.entrySet()) {
			if (s.getValue().size()>1){
				countAmbigousOrgs++;
			System.out.println(countAmbigousOrgs+"\t"+s.getKey() + "\t" + s.getValue());
			}
		}
//		an.printBibtekTable(oNames);

		System.out.println("********TITLES OF http://data.semanticweb.org/conference/eswc/2014/paper/ws/SSA/5 ********");
		Set<Literal> titles = an.extractPublicationsForAuthor(ModelFactory.createDefaultModel().createResource("http://data.semanticweb.org/conference/eswc/2014/paper/ws/SSA/5"));
		for(Literal title : titles) System.out.println(title);
		System.out.println();
		
		System.out.println("********AUTHORS OF http://data.semanticweb.org/conference/eswc/2014/paper/ws/SSA/5 ********");
		Set<Resource> authors = an.extractAuthorsForPublication(ModelFactory.createDefaultModel().createResource("http://data.semanticweb.org/conference/eswc/2014/paper/ws/SSA/5"));
        for(Resource author : authors) System.out.println(author);
        System.out.println();
        
//        System.out.println("********COAUTHORS MAP OF http://data.semanticweb.org/person/andrea-giovanni-nuzzolese ********");
//        Map<Resource,Integer> coauthorsMap = an.extractCoauthorsForAuthor(ModelFactory.createDefaultModel().createResource("http://data.semanticweb.org/person/andrea-giovanni-nuzzolese"));
//        Set<Resource> couathors = coauthorsMap.keySet();
//        for(Resource coauthor : couathors) System.out.println(coauthor.getURI() + " : " + coauthorsMap.get(coauthor));
//        System.out.println("********COAUTHORS MAP OF http://data.semanticweb.org/person/andrea-nuzzolese ********");
//        Map<Resource,Integer> coauthorsMap2 = an.extractCoauthorsForAuthor(ModelFactory.createDefaultModel().createResource("http://data.semanticweb.org/person/andrea-nuzzolese"));
//        Set<Resource> couathors2 = coauthorsMap2.keySet();
//        for(Resource coauthor : couathors2) System.out.println(coauthor.getURI() + " : " + coauthorsMap2.get(coauthor));
        
        Resource a1 = ModelFactory.createDefaultModel().createResource("http://data.semanticweb.org/person/andrea-giovanni-nuzzolese");
        Resource a2 = ModelFactory.createDefaultModel().createResource("http://data.semanticweb.org/person/raphaeel-troncy");

        System.out.println("SIMILARITY "+a1+" and "+a2+" --> "+an.authorSimilarity(a1, a2));
		
	}

}
