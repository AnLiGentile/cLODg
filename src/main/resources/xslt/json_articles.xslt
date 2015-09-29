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
	xmlns:eswc-owl="http://www.ontologydesignpatterns.org/ont/eswc/ontology.owl#"
	exclude-result-prefixes="xsl php">
	
	<xsl:output method="text" encoding="utf-8" indent="no" />
	<xsl:strip-space elements="*" />
	<xsl:preserve-space elements="*"/>
	
	    <xsl:template match="rdf:RDF">
	    	<xsl:text>{ "publications" :[</xsl:text>
	
	    	<!-- Select all the swrc:InpProceedings objects from the RDF model and iterate over them. -->
	    	<xsl:for-each select="rdf:Description/rdf:type[@rdf:resource='http://swrc.ontoware.org/ontology#InProceedings']/..">
	            <xsl:if test="(position( )) > 1">
	            	<xsl:text>,</xsl:text>
	            </xsl:if>
	            <xsl:text>{</xsl:text>
	            
	    		<xsl:variable name="uri" select="@rdf:about" />
	            <xsl:variable name="jsonuri" select="replace($uri,'/', '\\/')" />
	            
	            <!-- add the ID of the paper -->
	            <xsl:text>"id": "</xsl:text>
	            <xsl:value-of select="$jsonuri" />
	            <xsl:text>",</xsl:text>
	            
	            <!-- add the title of the paper -->
	            <xsl:text>"title": "</xsl:text>
	    		<xsl:value-of select="replace(dc:title, '&quot;', '\\&quot;')" />
	            <xsl:text>",</xsl:text>
	            
	            <!-- add the abstract -->
	            <xsl:text>"abstract":</xsl:text>
	            <xsl:choose>
	            	<xsl:when test="swrc:abstract">
	            		<xsl:text>"</xsl:text>
	            		<xsl:value-of select="replace(swrc:abstract, '&quot;', '\\&quot;')" />
	            		<xsl:text>"</xsl:text>
	            	</xsl:when>
	            	<xsl:otherwise>
	            		<xsl:text>null</xsl:text>
	            	</xsl:otherwise> 
	            </xsl:choose>
	            
	            <xsl:text>,</xsl:text>
	    		<xsl:text>"authors":</xsl:text>
	            
	            <!-- add authors -->
	    		
	    		<xsl:variable name="authorlistref" select="bibo:authorList/@rdf:resource" />
	            <xsl:choose>
	            	<xsl:when test="$authorlistref">
	            		<xsl:text>[</xsl:text>
	            		<xsl:variable name="authorlist" select="/rdf:RDF/rdf:Description[@rdf:about=$authorlistref]" />
	            		<xsl:for-each select="$authorlist/*[name() != 'rdf:type']">
							<!-- sort the element of the rdf:Bag (rdf:_1, rdf:_2,..., rdf:n) -->
							<xsl:sort select="xs:integer(replace(local-name(), '_', ''))" data-type="number"/>
							
							<xsl:if test="(position( )) > 1">
	            				<xsl:text>,</xsl:text>
	            			</xsl:if>
	            			<xsl:text>"</xsl:text>
							<xsl:value-of select="@rdf:resource" />
							<xsl:text>"</xsl:text>
						</xsl:for-each>             		
	            		<xsl:text>]</xsl:text>
	            	</xsl:when>
	            	<xsl:otherwise>
	            		<xsl:text>null</xsl:text>
	            	</xsl:otherwise>
	            </xsl:choose>
	    		
	    		<!-- add the Twitter hanhtag -->
	    		<xsl:text>,"hashtag":"#iswc2015",</xsl:text>
	    		
	    		<!-- add the thumbnail if any exists -->
	    		<xsl:text>"thumbnail":</xsl:text>
	    		<xsl:choose>
	    			<xsl:when test="dbpedia-owl:thumbnail">
	    				<xsl:text>"</xsl:text>
	    				<xsl:value-of select="replace(dbpedia-owl:thumbnail/@rdf:resource, 'http://wit.istc.cnr.it/eswc-form/imgresized/', '')" />
	    				<xsl:text>"</xsl:text>
	    			</xsl:when>
	    			<xsl:otherwise>
	    				<xsl:text>null</xsl:text>
	    			</xsl:otherwise>
	    		</xsl:choose>
	    		<xsl:text>,</xsl:text>
	    		
	    		<!-- add the thumbnail if any exists -->
	    		<xsl:text>"presentedIn":</xsl:text>
	    		<xsl:choose>
	    			<xsl:when test="eswc-owl:isPresented">
	    				<xsl:text>"</xsl:text>
	    				<xsl:value-of select="eswc-owl:isPresented/@rdf:resource" />
	    				<xsl:text>"</xsl:text>
	    			</xsl:when>
	    			<xsl:otherwise>
	    				<xsl:text>null</xsl:text>
	    			</xsl:otherwise>
	    		</xsl:choose>
	    		<xsl:text>,</xsl:text>
	    		
	    		<!-- add track information -->
	    		<xsl:text>"track": "</xsl:text>
	    		<!-- 
	    			remove the namespace from paper's URI, e.g.:
	    			INPUT STRING http://data.semanticweb.org/conference/eswc/2015/paper/research/175
	    			OUTPUT STRING research/175
	    		-->
	    		<xsl:variable name="track" select="replace($uri, '^http://data.semanticweb.org/conference/eswc/2015/paper/', '')" />
	    		<!-- 
	    			remove the id of the paper from the track string, e.g.: 
	    			INPUT STRING research/175
	    			OUTPUT STRING research
	    		-->
	    		<xsl:variable name="track" select="replace($track, '/[0-9]+$', '')" />
	    		<!-- 
	    			The name of the track must start with the capital case, e.g.: 
	    			INPUT STRING research
	    			OUTPUT STRING Research
	    		-->
	    		<xsl:variable name="track" select="concat(upper-case(substring($track, 1, 1)), substring($track, 2))" />
	    		<!-- 
	    			The name of the track must ent with the term "Track", e.g.: 
	    			INPUT STRING Research
	    			OUTPUT STRING Research Track
	    			
	    			add the output string to the final JSON
	    		-->
	    		<xsl:value-of select="concat($track, ' Track')" />
	    		<xsl:text>",</xsl:text>
	    		
	    		<!-- add the URI -->
	    		<xsl:text>"uri": "</xsl:text>
	    		<xsl:value-of select="$jsonuri" />
	    		<xsl:text>"</xsl:text>
	    		
	    		<xsl:text>}</xsl:text>
	    		
	    	</xsl:for-each> 
	        <xsl:text>]}</xsl:text>
	    </xsl:template>
</xsl:stylesheet>