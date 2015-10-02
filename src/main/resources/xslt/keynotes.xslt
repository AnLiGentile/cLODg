<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:php="http://php.net/xsl"
	xmlns:cnr="http://data.cnr.it/functions/"
	xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:owl="http://www.w3.org/2002/07/owl#" 
	xmlns:dcterms="http://purl.org/dc/terms/creator" 
	xmlns:foaf="http://xmlns.com/foaf/0.1/" 
	xmlns:frbr="http://purl.org/vocab/frbr/core#"
	exclude-result-prefixes="xsl php">
	<xsl:output method="xml" encoding="utf-8" indent="yes" />
	
	<xsl:template match="conferenceData">
		<rdf:RDF
			xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
		 	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
		 	xmlns:owl="http://www.w3.org/2002/07/owl#" 
		 	xmlns:dc="http://purl.org/dc/elements/1.1/" 
		 	xmlns:foaf="http://xmlns.com/foaf/0.1/" 
		 	xmlns:frbr="http://purl.org/vocab/frbr/core#"
		 	xmlns:swc="http://data.semanticweb.org/ns/swc/ontology#" 
		 	xmlns:icaltzd="http://www.w3.org/2002/12/cal/icaltzd#" >
			
			<xsl:for-each select="invitedTalks/invitedTalk">
				
				<rdf:Description>
					<xsl:attribute name="rdf:about">
		 				<xsl:value-of select="concat('http://data.semanticweb.org/conference/ld4ie/2015/keynote/', uriID/text())" />
		 			</xsl:attribute>
		 			
		 			<rdf:type> 
		 				<xsl:attribute name="rdf:resource">
		 					<xsl:value-of select="'http://data.semanticweb.org/ns/swc/ontology#TalkEvent'" />
		 				</xsl:attribute>
		 			</rdf:type>
		 			<swc:isSubEventOf>
		 				<xsl:attribute name="rdf:resource">
		 					<xsl:value-of select="'http://data.semanticweb.org/conference/ld4ie/2015'" />
		 				</xsl:attribute>
		 			</swc:isSubEventOf>
		 			
		 			<icaltzd:summary>
		 				<xsl:value-of select="label" />
		 			</icaltzd:summary>
		 			<rdfs:label>
		 				<xsl:value-of select="label" />
		 			</rdfs:label>
		 			<icaltzd:description>
		 				<xsl:value-of select="description" />
		 			</icaltzd:description>
		 			<icaltzd:dtstart rdf:datatype="http://www.w3.org/2002/12/cal/tzd/Europe/Ljubljana#tz">
		 				<xsl:value-of select="dtstart" />
		 			</icaltzd:dtstart>
		 			<icaltzd:dtend rdf:datatype="http://www.w3.org/2002/12/cal/tzd/Europe/Ljubljana#tz">
		 				<xsl:value-of select="dtend" />
		 			</icaltzd:dtend>
		 			
		 			<xsl:for-each select="location">
			 			<icaltzd:location>
			 				<xsl:attribute name="rdf:resource">
			 					<xsl:value-of select="." />
			 				</xsl:attribute>
			 			</icaltzd:location>
		 			</xsl:for-each>
		 			
				</rdf:Description>
			</xsl:for-each>
				
			
		</rdf:RDF>
	</xsl:template>
</xsl:stylesheet>	