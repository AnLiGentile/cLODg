package it.istc.cnr.stlab.clodg.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
    
    private static OfficialNameSpace officialNameSpace;
    
    public String conferenceName;
	public String year;
	public String month;
	public String baseDomainNs;
	public String baseEswc;
	public String baseConference;
	public String basePaperEswcNs;
	public String xsltFolder;


	public String mainTrackPaperNs;
	public String inusePaperNs;
	public String posterPaperNs;
	public String demoPaperNs;
	public String phdPaperNs;
	public String challengePaperNs;

	public String personNs;
	public String organizationNs;
	public String keynoteNs;

	public String easychairSnapshot;
	public String conferenceConfiguration;
	public String folderContainingIcsCalendars;
	public String jsFinalData;
	public String jsonFinalData;

	
		
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
    
    private void init(String year, String baseDomainNs, String conference,
			String mainTrackPaper, String inusePaper, String posterPaper,
			String demoPaper, String phdPaper, String challengePaper,
			String easychairSnapshot, String conferenceConfiguration, String folderContainingIcsCalendars,
			String jsFinalData, String jsonFinalData) {
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
		
		System.out.println("BASE CONFERENCE "+ baseConference);
		this.basePaperEswcNs = baseConference+ "paper/";
		
		this.personNs = baseDomainNs+"person/";
		this.organizationNs =  baseDomainNs+"organization/";
		this.keynoteNs =  baseDomainNs+"keynote/";

//		public static final String KEYNOTE_NS = "http://data.semanticweb.org/conference/eswc/2015/keynote/";

		
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
	    
	    this.easychairSnapshot = easychairSnapshot;
	    this.conferenceConfiguration = conferenceConfiguration;
	    this.folderContainingIcsCalendars = folderContainingIcsCalendars;
        this.jsFinalData = jsFinalData;
        this.jsonFinalData = jsonFinalData;
	    
	}
	
	private OfficialNameSpace() {
	    /*
		String year = "2015";
		String baseDomain = "http://data.semanticweb.org/";
		String conference = "conference/eswc/";
		String mainTrackPaper =  "research/";
		String inusePaper =  "in-use/";
		String posterPaper =  "poster/";
		String demoPaper =  "demo/";
		String phdPaper =  "phDSymposium/";
		String challengePaper =  "challenge/";
	     */
	    
	    InputStream is = null;
        try {
            is = new FileInputStream("conferenceProps.properties");
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
	    
	    Properties props = new Properties();
	    try {
            props.load(is);
        } catch (IOException e) {
            System.err.println("An error occurred while loading the properties from conferenceProps.properties.");
        }
	    
	    if(!props.isEmpty()){
    	    String year = props.getProperty("year");
    	    this.month = props.getProperty("month");
    	    this.conferenceName = props.getProperty("conferenceName");
    	    String baseDomain = props.getProperty("baseDomain");
    	    String conference = props.getProperty("conference");
    	    String mainTrackPaper = props.getProperty("mainTrackPaper");
    	    String inusePaper = props.getProperty("inusePaper");
    	    String posterPaper = props.getProperty("posterPaper");
    	    String demoPaper = props.getProperty("demoPaper");
    	    String phdPaper = props.getProperty("phdPaper");
    	    String challengePaper = props.getProperty("challengePaper");
    	    conference = props.getProperty("conference");
    	    
    	    String easychairSnapshot = props.getProperty("easychairSnapshot");
    	    String conferenceConfiguration = props.getProperty("conferenceConfiguration");
    	    String folderContainingIcsCalendars = props.getProperty("folderContainingIcsCalendars");
    	    String jsFinalData = props.getProperty("jsFinalData");
    	    String jsonFinalData = props.getProperty("jsonFinalData");
    
    		init(year, baseDomain, conference,
    				mainTrackPaper, inusePaper,
    				posterPaper, demoPaper, phdPaper,
    				challengePaper, easychairSnapshot, conferenceConfiguration, folderContainingIcsCalendars,
    				jsFinalData, jsonFinalData);
	    }
	}
	
	
	
	
	
	
	public static OfficialNameSpace getInstance(){
	    if(officialNameSpace == null){
	        officialNameSpace = new OfficialNameSpace();
	    }
	    return officialNameSpace;
	}
	
	public static String conferenceLabel(){
	    return officialNameSpace.conferenceName + officialNameSpace.year;
	}

	public void setXsltFolder(String xsltFolder){
	    this.xsltFolder = xsltFolder;
	}

}
