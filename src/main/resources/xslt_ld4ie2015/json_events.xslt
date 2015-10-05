<?xml version="1.0" encoding="UTF-8"?>
<!--
	This XSLT stylesheet allows to generate the JSON representation of the data about articles
	for the ConferenceLive APP.
	The data comes from an RDF representation format.
	
	author: andrea.nuzzolese
-->
<xsl:stylesheet version="2.0"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:php="http://php.net/xsl"
	xmlns:cnr="http://data.cnr.it/functions/"
	xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:owl="http://www.w3.org/2002/07/owl#"
	xmlns:dcterms="http://purl.org/dc/terms/creator"
	xmlns:foaf="http://xmlns.com/foaf/0.1/"
	xmlns:frbr="http://purl.org/vocab/frbr/core#"
	xmlns:swc="http://data.semanticweb.org/ns/swc/ontology#"
	xmlns:swrc="http://swrc.ontoware.org/ontology#"
	xmlns:bibo="http://purl.org/ontology/bibo/"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:dbpedia-owl="http://dbpedia.org/ontology/"
	xmlns:icaltzd="http://www.w3.org/2002/12/cal/icaltzd#"
	xmlns:eswc-owl="http://www.ontologydesignpatterns.org/ont/eswc/ontology.owl#"
	exclude-result-prefixes="xsl php">
	
	<xsl:output method="text" encoding="utf-8" indent="no" />
	<xsl:strip-space elements="*" />
	<xsl:preserve-space elements="*"/>
	
		<xsl:template match="rdf:RDF">
	    	<xsl:text>{ "events" :[</xsl:text>
	
	    	<!-- Select all the swrc:InpProceedings objects from the RDF model and iterate over them. -->
	    	<xsl:for-each select="rdf:Description/rdf:type[@rdf:resource='http://www.w3.org/2002/12/cal/icaltzd#Vevent']/.. | rdf:Description/rdf:type[@rdf:resource='http://data.semanticweb.org/ns/swc/ontology#ConferenceEvent']/.. | rdf:Description/rdf:type[@rdf:resource='http://data.semanticweb.org/ns/swc/ontology#TalkEvent']/..">
	            <xsl:if test="(position( )) > 1">
	            	<xsl:text>,</xsl:text>
	            </xsl:if>
	            <xsl:text>{</xsl:text>
	            
	    		<xsl:variable name="uri" select="@rdf:about" />
	    		<xsl:variable name="uri" select="replace($uri, 'http://wit.istc.cnr.it/eswc2015/session-calendar.rdf#', 'http://data.semanticweb.org/conference/eswc/2015/session/')" />
				<xsl:variable name="uri" select="replace($uri, 'http://wit.istc.cnr.it/eswc2015/main-calendar.rdf#', 'http://data.semanticweb.org/conference/eswc/2015/talk/')" />
	    		
	            <xsl:variable name="jsonuri" select="replace($uri,'/', '\\/')" />
	            
	            <!-- add the ID of the event -->
	            <xsl:text>"id": "</xsl:text>
	            <xsl:value-of select="$jsonuri" />
	            <xsl:text>",</xsl:text>
	            
	            <!-- add the name of the event -->
	            <xsl:text>"name": "</xsl:text>
	    		<xsl:value-of select="replace(icaltzd:summary, '&quot;', '\\&quot;')" />
	            <xsl:text>",</xsl:text>
	            
	            <!-- add the description of the event -->
	            <xsl:text>"description": "</xsl:text>
	    		<xsl:value-of select="replace(icaltzd:description, '&quot;', '\\&quot;')" />
	            <xsl:text>",</xsl:text>
	            
	            <!-- add the created_at attribute of the event -->
	            <xsl:text>"createdAt": "</xsl:text>
	    		<xsl:value-of select="icaltzd:lastModified" />
	            <xsl:text>",</xsl:text>
	            
	            <!-- add the start_at attribute of the event -->
	            <xsl:text>"startsAt": "</xsl:text>
	    		<xsl:value-of select="icaltzd:dtstart" />
	            <xsl:text>",</xsl:text>
	            
	            <!-- add the end_at attribute of the event -->
	            <xsl:text>"endsAt": "</xsl:text>
	    		<xsl:value-of select="icaltzd:dtend" />
	            <xsl:text>",</xsl:text>
	            
	            <!-- add the resources attribute of the event (NULL by default) -->
	            <xsl:text>"resource": null,</xsl:text>
	            
	            <!-- add the duration attribute of the event -->
	            <xsl:text>"duration": "</xsl:text>
	    		<xsl:value-of select="xs:dateTime(icaltzd:dtend)-xs:dateTime(icaltzd:dtstart)" />
	            <xsl:text>",</xsl:text>
	            
	            <!-- add the last_modified_at attribute of the event -->
	            <xsl:text>"lastModifiedAt": "</xsl:text>
	    		<xsl:value-of select="icaltzd:lastModified" />
	            <xsl:text>",</xsl:text>
	            
	            <!-- add the homepage attribute of the event (NULL by default) -->
	            <xsl:text>"homepage": </xsl:text>
	            <xsl:choose>
	    			<xsl:when test="foaf:homepage">
	    				<xsl:text>"</xsl:text>
	    				<xsl:value-of select="foaf:homepage/@rdf:resource" />
	    				<xsl:text>"</xsl:text>
	    			</xsl:when>
	    			<xsl:otherwise>
	    				<xsl:text>null</xsl:text>
	    			</xsl:otherwise>
	    		</xsl:choose>
	    		<xsl:text>,</xsl:text>
	            
	            <!-- add the twitterWidgetToken attribute of the event (#ISWC2015 by default) -->
	            <xsl:text>"twitterWidgetToken": "#ISWC2015",</xsl:text>
	            
	            <!-- add the location attribute of the event (Portorož, Slovenia by default) -->
	            <xsl:text>"locations": </xsl:text>
	            <xsl:choose>
	    			<xsl:when test="icaltzd:location">
	    				<xsl:text>[</xsl:text>
        				<xsl:for-each select="icaltzd:location/@rdf:resource">
	            			<xsl:if test="(position( )) > 1">
	            				<xsl:text>,</xsl:text>
							</xsl:if>
	            			<xsl:text>"</xsl:text>
            				<xsl:value-of select="." />
            				<xsl:text>"</xsl:text>
            			</xsl:for-each>
            			<xsl:text>]</xsl:text>
	    			</xsl:when>
	    			<xsl:otherwise>
	    				<xsl:text>null</xsl:text>
	    			</xsl:otherwise>
	    		</xsl:choose>
	    		<xsl:text>,</xsl:text>
	            
	            <!-- add the parent attribute of the event -->
	            <xsl:text>"parent": </xsl:text>
	            <xsl:choose>
	    			<xsl:when test="swc:isSubEventOf">
	    				<xsl:text>"</xsl:text>
	    				<xsl:variable name="subEventOf" select="replace(swc:isSubEventOf[1]/@rdf:resource, 'http://wit.istc.cnr.it/eswc2015/sessions-calendar.rdf#', 'http://data.semanticweb.org/conference/eswc/2015/session/')" />
	    				<xsl:variable name="subEventOf" select="replace($subEventOf, 'http://wit.istc.cnr.it/eswc2015/main-calendar.rdf#', 'http://data.semanticweb.org/conference/eswc/2015/talk/')" />
	    				<xsl:variable name="subEventOf" select="replace($subEventOf,'/', '\\/')" />
	    				<xsl:value-of select="$subEventOf" />
	    				<xsl:text>"</xsl:text>
	    			</xsl:when>
	    			<xsl:otherwise>
	    				<xsl:text>null</xsl:text>
	    			</xsl:otherwise>
	    		</xsl:choose>
	    		<xsl:text>,</xsl:text>
	            
	            <!-- add the children attribute of the event -->
	            <xsl:text>"children": </xsl:text>
	            <xsl:choose>
	    			<xsl:when test="swc:isSuperEventOf">
	    				<xsl:text>[</xsl:text>
        				<xsl:for-each select="swc:isSuperEventOf/@rdf:resource">
	            			<xsl:if test="(position( )) > 1">
	            				<xsl:text>,</xsl:text>
							</xsl:if>
	            			<xsl:text>"</xsl:text>
	            			<xsl:variable name="superEventOf" select="replace(., 'http://wit.istc.cnr.it/eswc2015/sessions-calendar.rdf#', 'http://data.semanticweb.org/conference/eswc/2015/session/')" />
	    					<xsl:variable name="superEventOf" select="replace($superEventOf, 'http://wit.istc.cnr.it/eswc2015/main-calendar.rdf#', 'http://data.semanticweb.org/conference/eswc/2015/talk/')" />
	    					<xsl:variable name="superEventOf" select="replace($superEventOf,'/', '\\/')" />
	    					<xsl:value-of select="$superEventOf" />
            				<xsl:text>"</xsl:text>
            			</xsl:for-each>
            			<xsl:text>]</xsl:text>
	    			</xsl:when>
	    			<xsl:otherwise>
	    				<xsl:text>null</xsl:text>
	    			</xsl:otherwise>
	    		</xsl:choose>
	    		<xsl:text>,</xsl:text>
	            
	            <!-- add the topics attribute of the event -->
	            <xsl:text>"topics": null,</xsl:text>
	            
	            <!-- add the categories attribute of the event -->
	            <xsl:text>"categories": [</xsl:text> 
	            <xsl:choose>
	    			<xsl:when test="$uri='http://data.semanticweb.org/conference/eswc/2015'">
	    				<xsl:text>"</xsl:text>
	    				<xsl:value-of select="concat($'category\/conference-event')" />http:\/\/data.semanticweb.org\/conference\/eswc\/2015\/"</xsl:text>
	    			</xsl:when>
	    			<xsl:when test="contains(icaltzd:summary, 'Research ')">
	    				<xsl:text>"http:\/\/data.semanticweb.org\/conference\/eswc\/2015\/category\/research-event"</xsl:text>
	    			</xsl:when> 
	    			<xsl:when test="contains(icaltzd:summary, 'PhDSymposium:')">
	    				<xsl:text>"http:\/\/data.semanticweb.org\/conference\/eswc\/2015\/category\/phd-event"</xsl:text>
	    			</xsl:when> 	    			
	    			<xsl:when test="contains(icaltzd:summary, 'In-Use ')">
	    				<xsl:text>"http:\/\/data.semanticweb.org\/conference\/eswc\/2015\/category\/in-use-event"</xsl:text>
	    			</xsl:when> 
	    			<xsl:when test="contains($uri, '/session/')">
	    				<xsl:text>"http:\/\/data.semanticweb.org\/conference\/eswc\/2015\/category\/session-event"</xsl:text>
	    			</xsl:when>
	    			<xsl:when test="contains($uri, '/keynote/')">
	    				<xsl:text>"http:\/\/data.semanticweb.org\/conference\/eswc\/2015\/category\/keynote-event"</xsl:text>
	    			</xsl:when>
	    			<xsl:when test="icaltzd:summary='Plenary: Posters session with breakfast'">
	    				<xsl:text>"http:\/\/data.semanticweb.org\/conference\/eswc\/2015\/category\/poster-event"</xsl:text>
	    			</xsl:when>
	    			<xsl:when test="icaltzd:summary='Plenary: Demo session with breakfast'">
	    				<xsl:text>"http:\/\/data.semanticweb.org\/conference\/eswc\/2015\/category\/demo-event"</xsl:text>
	    			</xsl:when>
	    			<xsl:when test="contains($uri, '/workshop/')">
	    				<xsl:text>"http:\/\/data.semanticweb.org\/conference\/eswc\/2015\/category\/workshop-event"</xsl:text>
	    			</xsl:when>
	    			<xsl:when test="contains($uri, '/tutorial/')">
	    				<xsl:text>"http:\/\/data.semanticweb.org\/conference\/eswc\/2015\/category\/tutorial-event"</xsl:text>
	    			</xsl:when>
	    			<xsl:when test="contains(icaltzd:summary, 'Coffee Break + Challenges Posters')">
	    				 <xsl:text>"http:\/\/data.semanticweb.org\/conference\/eswc\/2015\/category\/challenge-event"</xsl:text>
	    			</xsl:when>	    			
	    			<xsl:when test="contains(icaltzd:summary, 'Break:')">
	    				<xsl:text>"http:\/\/data.semanticweb.org\/conference\/eswc\/2015\/category\/break-event"</xsl:text>
	    			</xsl:when> 
	    			<xsl:when test="contains(icaltzd:summary, 'Social:')">
	    				<xsl:text>"http:\/\/data.semanticweb.org\/conference\/eswc\/2015\/category\/social-event"</xsl:text>
	    			</xsl:when> 
	    			<xsl:when test="contains(icaltzd:summary, 'Plenary:')">
	    				<xsl:text>"http:\/\/data.semanticweb.org\/conference\/eswc\/2015\/category\/plenary-event"</xsl:text>
	    			</xsl:when>
	    			<xsl:when test="contains(icaltzd:summary, 'Networking:')">
	    				<xsl:text>"http:\/\/data.semanticweb.org\/conference\/eswc\/2015\/category\/networking-event"</xsl:text>
	    			</xsl:when>




	    		
	    				    			
	    			<!-- <xsl:when test="icaltzd:summary='Break: Coffee Break'">
	    				<xsl:text>"http:\/\/data.semanticweb.org\/conference\/eswc\/2015\/category\/break-event"</xsl:text>
	    			</xsl:when>
	    			<xsl:when test="icaltzd:summary='Break: PhD Lunch'">
	    				<xsl:text>"http:\/\/data.semanticweb.org\/conference\/eswc\/2015\/category\/break-event"</xsl:text>
	    			</xsl:when>
	    			<xsl:when test="icaltzd:summary='Break: Lunch Break'">
	    				<xsl:text>"http:\/\/data.semanticweb.org\/conference\/eswc\/2015\/category\/break-event"</xsl:text>
	    			</xsl:when>
	    			<xsl:when test="icaltzd:summary='Social: Gala dinner'">
	    				<xsl:text>"http:\/\/data.semanticweb.org\/conference\/eswc\/2015\/category\/social-event"</xsl:text>
	    			</xsl:when>
	    			-->	    			
	    			
	    			
        			<xsl:otherwise>
	    				<xsl:text>"http:\/\/data.semanticweb.org\/conference\/eswc\/2015\/category\/presentation-event"</xsl:text>
	    			</xsl:otherwise>
      
	    		</xsl:choose>
	            <xsl:text>], </xsl:text>
	            
	            
	            <!-- add the roles attribute of the event -->
	            <xsl:text>"roles": null,</xsl:text>
	            
	            <!-- add the papers attribute of the event -->
	            <xsl:text>"papers": </xsl:text>
	            <xsl:choose>
	    			<xsl:when test="eswc-owl:presents">
	    				<xsl:text>[</xsl:text>
	    				<xsl:for-each select="eswc-owl:presents">
	            			<xsl:if test="(position( )) > 1">
	            				<xsl:text>,</xsl:text>
							</xsl:if>
							<xsl:text>"</xsl:text>
	    					<xsl:value-of select="replace(@rdf:resource,'/', '\\/')" />
	    					<xsl:text>"</xsl:text>
						</xsl:for-each>
	    				<xsl:text>]</xsl:text>
	    			</xsl:when>
	            	<xsl:otherwise>
	    				<xsl:text>null</xsl:text>
	    			</xsl:otherwise>
	    		</xsl:choose>
	            <xsl:text>}</xsl:text>
	    		
	    	</xsl:for-each> 
	        <xsl:text>]}</xsl:text>
	    </xsl:template>
</xsl:stylesheet>