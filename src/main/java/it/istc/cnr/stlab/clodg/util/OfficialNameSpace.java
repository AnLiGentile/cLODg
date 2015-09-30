package it.istc.cnr.stlab.clodg.util;

public class OfficialNameSpace {

	//	public String year = "2015";
//	public String baseDomainNs = "http://data.semanticweb.org/";
//	public String baseEswc = baseDomainNs+"conference/eswc/"
//			+ year + "/";
//	public String baseConference = baseDomainNs+ "conference/eswc/"
//			+ year + "/";
//	public String basePaperEswcNs = baseConference+ "paper/";
//
//
//	public String mainTrackPaperNs = basePaperEswcNs + "research/";
//	public String inusePaperNs = basePaperEswcNs + "in-use/";
//	public String posterPaperNs = basePaperEswcNs + "poster/";
//	public String demoPaperNs = basePaperEswcNs + "demo/";
//	public String phdPaperNs = basePaperEswcNs + "phDSymposium/";
//	public String challengePaperNs = basePaperEswcNs + "challenge/";
//
//	public String personNs = baseDomainNs+"person/";
	public String year;
	public String baseDomainNs;
	public String baseEswc;
	public String baseConference;
	public String basePaperEswcNs;


	public String mainTrackPaperNs;
	public String inusePaperNs;
	public String posterPaperNs;
	public String demoPaperNs;
	public String phdPaperNs;
	public String challengePaperNs;

	public String personNs;
		
	public static String eswcOntology = "http://www.ontologydesignpatterns.org/ont/eswc/ontology.owl#";
	public static String semDogPerson = "http://data.semanticweb.org/person/";

	public static String HOLDS_ROLE_PROP = "http://data.semanticweb.org/ns/swc/ontology#holdsRole";
	public static final String FOAF_LAST_NAME = "http://xmlns.com/foaf/0.1/lastName";
	public static final String FOAF_ACCOUNT = "http://xmlns.com/foaf/0.1/account";

	
	
    public String PLENARY_EVENTS_CALENDAR_NS ;
    public String MAIN_CONFERENCE_CALENDAR_NS;
    public String WORKSHOPS_CALENDAR_NS ;
    public String SESSIONS_CALENDAR_NS ;
    public String TUTORIALS_CALENDAR_NS;
    public String PHD_CALENDAR_NS;
    
    
    
	
	public OfficialNameSpace(String year, String baseDomainNs, String conference,
			String mainTrackPaper, String inusePaper,
			String posterPaper, String demoPaper, String phdPaper,
			String challengePaper) {
		super();

		init(year, baseDomainNs, conference, mainTrackPaper, inusePaper,
				posterPaper, demoPaper, phdPaper, challengePaper);

		

	}

	private void init(String year, String baseDomainNs, String conference,
			String mainTrackPaper, String inusePaper, String posterPaper,
			String demoPaper, String phdPaper, String challengePaper) {
		if(!baseDomainNs.endsWith("/"))
			baseDomainNs =baseDomainNs+"/";
		if(!conference.endsWith("/"))
			conference =conference+"/";
		if(!mainTrackPaper.endsWith("/"))
			mainTrackPaper =mainTrackPaper+"/";
		if(!inusePaper.endsWith("/"))
			inusePaper =inusePaper+"/";
		if(!posterPaper.endsWith("/"))
			posterPaper =posterPaper+"/";
		if(!demoPaper.endsWith("/"))
			demoPaper =demoPaper+"/";
		if(!phdPaper.endsWith("/"))
			phdPaper =phdPaper+"/";
		if(!challengePaper.endsWith("/"))
			challengePaper =challengePaper+"/";
		
		this.year = year;
		this.baseDomainNs = baseDomainNs;
		//TODO use only baseConference instead
		this.baseEswc = baseDomainNs+"conference/eswc/"
				+ year + "/";
		this.baseConference = baseDomainNs+ conference
				+ year + "/";
		this.basePaperEswcNs = baseConference+ "paper/";
		this.personNs = baseDomainNs+"person/";


		this.mainTrackPaperNs = basePaperEswcNs + mainTrackPaper;
		this.inusePaperNs = basePaperEswcNs + inusePaper;
		this.posterPaperNs = basePaperEswcNs + posterPaper;
		this.demoPaperNs = basePaperEswcNs + demoPaper;
		this.phdPaperNs = basePaperEswcNs + phdPaper;
		this.challengePaperNs = basePaperEswcNs + challengePaper;
				
		
	    this.PLENARY_EVENTS_CALENDAR_NS = this.baseConference+"event/";
	    this.MAIN_CONFERENCE_CALENDAR_NS = this.baseConference+"talk/";
	    this.WORKSHOPS_CALENDAR_NS = this.baseConference+"workshop/";
	    this.SESSIONS_CALENDAR_NS = this.baseConference+"session/";
	    this.TUTORIALS_CALENDAR_NS = this.baseConference+"tutorial/";
	    this.PHD_CALENDAR_NS = this.baseConference+"PhDSymposium/";
	    
	}
	
	public OfficialNameSpace() {
		super();
		String year = "2015";
		String baseDomain = "http://data.semanticweb.org/";
		String conference = "conference/eswc/";
		String mainTrackPaper =  "research/";
		String inusePaper =  "in-use/";
		String posterPaper =  "poster/";
		String demoPaper =  "demo/";
		String phdPaper =  "phDSymposium/";
		String challengePaper =  "challenge/";


		init(year, baseDomain, conference,
				mainTrackPaper, inusePaper,
				posterPaper, demoPaper, phdPaper,
				challengePaper);
					}


}
