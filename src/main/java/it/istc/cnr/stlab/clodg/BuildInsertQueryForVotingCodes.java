package it.istc.cnr.stlab.clodg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import au.com.bytecode.opencsv.CSVReader;

public class BuildInsertQueryForVotingCodes {

	public static void main(String[] args) {
		try {
			CSVReader csvReader = new CSVReader(new InputStreamReader(
					new FileInputStream(new File("in/eswc_voting_codes.csv")),
					"UTF-8"));
			String[] row = null;
			System.out.println("SPARQL INSERT IN GRAPH <eswc_user_code> {");
			while ((row = csvReader.readNext()) != null) {
				String votingId = row[0];
				System.out
						.println(" <http://2014.eswc-conferences.org> <http://2014.eswc-conferences.org/code> \""
								+ votingId + "\" . ");
			}
			System.out.println("}");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
