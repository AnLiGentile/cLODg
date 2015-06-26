<?xml version="1.0" encoding="UTF-8"?>
<!--
	This XSLT stylesheet allows to generate the JSON representation of the data about contacts
	for the ConferenceLive APP.
	The data comes from an RDF representation format.
	
	author: andrea.nuzzolese
-->
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
xmlns:swc="http://data.semanticweb.org/ns/swc/ontology#"
xmlns:swrc="http://swrc.ontoware.org/ontology#"
exclude-result-prefixes="xsl php">
<xsl:output method="text" encoding="utf-8" indent="no" />
<xsl:strip-space elements="*" />
<xsl:preserve-space elements="*"/>

    <xsl:template match="rdf:RDF">
        <xsl:text>{ "organizations" :[</xsl:text>

			<!-- Select all the foaf:Organization from the RDF model and iterate over them. -->
	        <xsl:for-each select="rdf:Description/rdf:type[@rdf:resource='http://xmlns.com/foaf/0.1/Organization']/..">
	            <xsl:if test="(position( )) > 1">
	            	<xsl:text>,</xsl:text>
	            </xsl:if>
	            <xsl:text>{</xsl:text>
	            
		            <xsl:variable name="uri" select="replace(@rdf:about,'/', '\\/')" />
		            
		            <!-- 
		            	Add the ID field.
		            	The ID is set to the URI of the entity coming from its RDF representation.  
		            -->
		            <xsl:text>"id": "</xsl:text><xsl:value-of select="$uri" />
		        	<xsl:text>",</xsl:text>
		        	
		        	<!--
		        		Add the contact's name.
		        		The name comes from foaf:name statements.
		        	-->
		            <xsl:text>"label": </xsl:text> "<xsl:value-of select="replace(rdfs:label[1], '&quot;', '\\&quot;')" />"<xsl:text>,</xsl:text>
		        	
		        	<!--
		        		Add the list of papers that a contact is author of.
		        		Authoring information can be fetched by harvesting foaf:made statements.
		        	-->
		            <xsl:text>"members": </xsl:text>
		            <xsl:choose>
		            	<xsl:when test="foaf:member">
		            		<xsl:text>[</xsl:text>
		            		<xsl:for-each select="foaf:member">
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