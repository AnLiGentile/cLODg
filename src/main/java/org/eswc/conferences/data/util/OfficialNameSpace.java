package org.eswc.conferences.data.util;

public class OfficialNameSpace {
	public static String year = "2015";

	public static String basePaperEswcNs = "http://data.semanticweb.org/conference/eswc/"
			+ year + "/paper/";
	public static String baseEswc = "http://data.semanticweb.org/conference/eswc/"
			+ year + "/";

	public static String mainTrackPaperNs = basePaperEswcNs + "research/";
	public static String inusePaperNs = basePaperEswcNs + "in-use/";
	public static String posterPaperNs = basePaperEswcNs + "poster/";
	public static String demoPaperNs = basePaperEswcNs + "demo/";
	public static String phdPaperNs = basePaperEswcNs + "phDSymposium/";
	public static String challengePaperNs = basePaperEswcNs + "challenge/";

	public static String personNs = "http://data.semanticweb.org/person/";
	public static String eswcOntology = "http://www.ontologydesignpatterns.org/ont/eswc/ontology.owl#";

}
