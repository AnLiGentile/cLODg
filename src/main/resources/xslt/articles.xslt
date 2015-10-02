<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:php="http://php.net/xsl"
	xmlns:clodg="it.istc.cnr.stlab.clodg.util.OfficialNameSpace"
	xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:owl="http://www.w3.org/2002/07/owl#" 
	xmlns:dcterms="http://purl.org/dc/terms/creator" 
	xmlns:foaf="http://xmlns.com/foaf/0.1/" 
	xmlns:frbr="http://purl.org/vocab/frbr/core#"
	exclude-result-prefixes="clodg xsl php">
	<xsl:output method="xml" encoding="utf-8" indent="yes" />
	
	<xsl:param name="conferenceLabel" />
	<xsl:param name="baseConference" />
	<xsl:param name="conferenceYear" />
	<xsl:param name="conferenceMonth" />
	
	<xsl:template match="snapshot">
	
		<rdf:RDF
			xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
		 	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
		 	xmlns:owl="http://www.w3.org/2002/07/owl#" 
		 	xmlns:dc="http://purl.org/dc/elements/1.1/" 
		 	xmlns:foaf="http://xmlns.com/foaf/0.1/" 
		 	xmlns:frbr="http://purl.org/vocab/frbr/core#"
		 	xmlns:bibo="http://purl.org/ontology/bibo/"
		 	xmlns:swc="http://data.semanticweb.org/ns/swc/ontology#" 
		 	xmlns:swrc="http://swrc.ontoware.org/ontology#"
		 	xmlns:icaltzd="http://www.w3.org/2002/12/cal/icaltzd#" >
			
			<xsl:for-each select="submissions/submission">
				
				
				<xsl:variable name="decisionID" select="decisionId"></xsl:variable>
				<xsl:if test="/snapshot/decisions/decision/id[text()=$decisionID]/../accepted='true'">
					<rdf:Description>
						<xsl:attribute name="rdf:about">
							<xsl:choose>
								<xsl:when test="trackId='118204'">
									<xsl:value-of select="concat($baseConference, '/paper/in-use/', number/text())" />
								</xsl:when>
			 					<xsl:otherwise>
			 						<xsl:value-of select="concat($baseConference, '/paper/research/', number/text())" />
			 					</xsl:otherwise>
			 				</xsl:choose>
		 				</xsl:attribute>
		 				<rdf:type>
		 					<xsl:attribute name="rdf:resource">
		 						<xsl:value-of select="'http://swrc.ontoware.org/ontology#InProceedings'" />
		 					</xsl:attribute>
		 				</rdf:type>
		 				<dc:title><xsl:value-of select="title" /></dc:title>
		 				<rdfs:label><xsl:value-of select="title" /></rdfs:label>
		 				
		 				<swrc:id><xsl:value-of select="number" /></swrc:id>

		 				<rdf:type>
		 					<xsl:attribute name="rdf:resource">
			 					<xsl:choose>
									<xsl:when test="trackId='118204'">
										<xsl:value-of select="'http://purl.org/spar/fabio/InUsePaper'" />
									</xsl:when>
				 					<xsl:otherwise>
				 						<xsl:value-of select="'http://purl.org/spar/fabio/ResearchPaper'" />
				 					</xsl:otherwise>
				 				</xsl:choose>
				 			</xsl:attribute>
			 			</rdf:type>

		 				
		 				<bibo:authorList>
		 					<xsl:attribute name="rdf:resource">
		 						<xsl:choose>
									<xsl:when test="trackId='118204'">
										<xsl:value-of select="concat($baseConference, '/paper/in-use/', number/text(), '/authorList')" />
									</xsl:when>
				 					<xsl:otherwise>
				 						<xsl:value-of select="concat($baseConference, '/paper/research/', number/text(), '/authorList')" />
				 					</xsl:otherwise>
				 				</xsl:choose>
		 					</xsl:attribute>
		 				</bibo:authorList>
		 				
		 				<swrc:abstract><xsl:value-of select="abstract" /></swrc:abstract>
		 				
		 				<xsl:for-each select="keywords/keyword">
		 					<dc:subject>
		 						<xsl:value-of select="." />
		 					</dc:subject>
		 				</xsl:for-each>	
		 				<swrc:year><xsl:value-of select="$conferenceYear"></xsl:value-of></swrc:year>
		 				<swrc:month><xsl:value-of select="$conferenceMonth"></xsl:value-of></swrc:month>
		 				
		 				<swc:isPartOf>
    <xsl:attribute name="rdf:resource">
        <xsl:value-of select="concat($baseConference,'/proceedings')" />
    </xsl:attribute>
</swc:isPartOf>

		 			</rdf:Description>
				</xsl:if>
				
				
		 			
		 			
					
				
			</xsl:for-each>
			
			<rdf:Description rdf:about="http://data.semanticweb.org/conference/ld4ie/2015">
    			<rdf:type rdf:resource="http://data.semanticweb.org/ns/swc/ontology#ConferenceEvent"/>
    			<swc:completeGraph rdf:resource="http://data.semanticweb.org/conference/ld4ie/2015/complete"/>
    			<swc:hasAcronym>
    				<xsl:value-of select="$conferenceLabel" />
    			</swc:hasAcronym>
    		</rdf:Description>
			
			<!-- rdf:Description rdf:about="http://data.semanticweb.org/conference/ld4ie/2015">
    			<rdf:type rdf:resource="http://data.semanticweb.org/ns/swc/ontology#ConferenceEvent"/>
    			<swc:completeGraph rdf:resource="http://data.semanticweb.org/conference/ld4ie/2015/complete"/>
    			<swc:hasAcronym>ESWC2015</swc:hasAcronym>
    			<swc:address>Grand hotel Bernardin, Obala 2, 6320 Portorož, Slovenia</swc:address>
    			<foaf:homepage rdf:resource="http://2015.eswc-conferences.org/"/>
    			<swc:hasRelatedDocument rdf:resource="http://data.semanticweb.org/conference/ld4ie/2015/proceedings"/>
    			<icaltzd:dtstart rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime">2015-05-31T09:00:00</icaltzd:dtstart>
    			<icaltzd:dtend rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime">2015-06-04T17:00:00</icaltzd:dtend>
    			<icaltzd:duration>
    				<xsl:value-of select="xs:dateTime('2015-06-04T17:00:00')-xs:dateTime('2015-05-31T09:00:00')" />
    			</icaltzd:duration>
    			<swc:hasRole rdf:resource="http://data.semanticweb.org/conference/ld4ie/2015/sponsor-chair"/>
    			<rdfs:label>The 12th Extented Semantic Web Conference</rdfs:label>
    			<swc:hasLogo rdf:resource="http://2015.eswc-conferences.org/sites/default/files/miniLogo_eswc15_red_0.png"/>
    			<icaltzd:location>Portoroz, Slovenia</icaltzd:location>
    			<icaltzd:summary>ESWC2015</icaltzd:summary>
    			<icaltzd:description>The 12th Extented Semantic Web Conference</icaltzd:description>

  			</rdf:Description-->
			
			
		</rdf:RDF>
	</xsl:template>
</xsl:stylesheet>	