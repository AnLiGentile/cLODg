package org.scholarlydata.builder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;

import au.com.bytecode.opencsv.CSVReader;

public class RolesTDBScript {

	
	public static void main(String[] args) {
		Dataset dataset = TDBFactory.createDataset("role_mappings");
		Model model = dataset.getDefaultModel();
		
		RoleMappings roleMappings = RoleMappings.getInstance();
		
		for(Resource dogFoodRole : roleMappings.getDogFoodRoles()){
			Resource confRole = roleMappings.getConfRole(dogFoodRole);
			dataset.begin(ReadWrite.WRITE);
			
			model.add(dogFoodRole, SKOS.closeMatch, confRole);
			
			dataset.end();
			
		}
		
		try {
			CSVReader reader = new CSVReader(new FileReader("ontologies/roles.csv"), ';');
			String[] row = null;
			while((row = reader.readNext()) != null){
				String role = row[0];
				String roleType1 = row[3];
				String roleType2 = row[4];
				
				dataset.begin(ReadWrite.WRITE);
				
				
				
				
				if(roleType1 != null){
					Resource rt1 = model.createResource(roleType1);
					if(roleType1.trim().isEmpty()) rt1 = model.createResource("http://data.semanticweb.org/ns/swc/ontology#Role");
					else rt1 = model.createResource(roleType1);
					Resource r = model.createResource(role, rt1);
					
					if(roleType2 != null && !roleType2.trim().isEmpty()){
						r.addProperty(RDF.type, model.createResource(roleType2));
					}
				}
				
				
				
				dataset.end();
			}
			
			reader.close();
			
			
			model.write(System.out);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
