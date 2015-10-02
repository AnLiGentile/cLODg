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
        <xsl:text>{ "persons" :[</xsl:text>

		<!-- Select all the foaf:Person from the RDF model and iterate over them. -->
        <xsl:for-each select="rdf:Description/rdf:type[@rdf:resource='http://xmlns.com/foaf/0.1/Person']/..">
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
            <xsl:text>"name": </xsl:text> "<xsl:value-of select="foaf:name" />"<xsl:text>,</xsl:text>
        	
        	<!--
        		Add the list of papers that a contact is author of.
        		Authoring information can be fetched by harvesting foaf:made statements.
        	-->
            <xsl:text>"made": </xsl:text>
            <xsl:choose>
            	<xsl:when test="foaf:made">
            		<xsl:text>[</xsl:text>
            		<xsl:for-each select="foaf:made">
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
            <xsl:text>,</xsl:text>
            
            <!--
            	Add contact's affiliations. It is a list of names.
            	The list is constructed by exploiting swrc:affiliation statements coming from the RDF representation
            	of the data.
            	It is important to remark that throught such statements we fetch the URIs the institutions a 
            	contact is affiliated with. Instead, we are interested into their names.
        		Hence, we exploit XSLT in order to gather the foaf:name of each institution.
            -->
            <xsl:text>"affiliation": </xsl:text>
            <xsl:choose>
            	<xsl:when test="swrc:affiliation">
            		<xsl:text>[</xsl:text>
            		<xsl:for-each select="swrc:affiliation">
            			<!--  xsl:variable name="affiliation" select="@rdf:resource" / -->
            			<xsl:if test="(position( )) > 1">
            				<xsl:text>,</xsl:text>
						</xsl:if>
            			<xsl:text>"</xsl:text>
            				<!-- xsl:value-of select="replace(/rdf:RDF/rdf:Description[@rdf:about=$affiliation]/foaf:name[1], '&quot;', '\\&quot;')" / -->
            				<xsl:value-of select="replace(@rdf:resource,'/', '\\/')" />
            			<xsl:text>"</xsl:text>
            		</xsl:for-each>
            		<xsl:text>]</xsl:text>
            	</xsl:when>
            	<xsl:otherwise>
            		<xsl:text>null</xsl:text>
            	</xsl:otherwise>
            </xsl:choose>
            <xsl:text>,</xsl:text>
        	
        	<!-- Add contact's depiction if any is available. -->
        	<xsl:text>"depiction":</xsl:text>
        	<xsl:choose>
        		<xsl:when test="foaf:depiction">
        			<xsl:text>"</xsl:text>
        			<xsl:value-of select="replace(foaf:depiction[1]/@rdf:resource, 'http://wit.istc.cnr.it/eswc-form/imgresized/', '')" />
        			<xsl:text>"</xsl:text>
        		</xsl:when>
        		<xsl:otherwise>null</xsl:otherwise>
        	</xsl:choose>
        	<xsl:text>,</xsl:text>
        	
        	<!-- Add contact's Twitter account.-->
        	<xsl:text>"accounts":</xsl:text>
        	<xsl:choose>
        		<xsl:when test="foaf:account">
        			<xsl:text>[</xsl:text>
        			<xsl:for-each select="foaf:account">
            			<xsl:variable name="account" select="@rdf:resource" />
            			<xsl:if test="(position( )) > 1">
            				<xsl:text>,</xsl:text>
						</xsl:if>
            			<xsl:text>"</xsl:text>
            				<xsl:value-of select="$account" />
            			<xsl:text>"</xsl:text>
            		</xsl:for-each>
            		<xsl:text>]</xsl:text>
        		</xsl:when>
        		<xsl:otherwise>null</xsl:otherwise>
        	</xsl:choose>
        	<xsl:text>,</xsl:text>
        	
        	<!-- Add contact's email.-->
        	<xsl:text>"mbox":</xsl:text>
        	<!-- xsl:choose>
        		<xsl:when test="foaf:mbox">
        			<xsl:text>[</xsl:text>
        			<xsl:for-each select="foaf:mbox">
        				<xsl:variable name="mbox" select="replace(@rdf:resource, 'mailto:', '')" />
            			<xsl:if test="(position( )) > 1">
            				<xsl:text>,</xsl:text>
						</xsl:if>
            			<xsl:text>"</xsl:text>
            				<xsl:value-of select="$mbox" />
            			<xsl:text>"</xsl:text>
            		</xsl:for-each>
            		<xsl:text>]</xsl:text>
        		</xsl:when>
        		<xsl:otherwise>null</xsl:otherwise>
        	</xsl:choose -->
        	<xsl:choose>
        		<xsl:when test="foaf:mbox_sha1sum">
        			<xsl:text>[</xsl:text>
        			<xsl:for-each select="foaf:mbox_sha1sum">
        				<xsl:if test="(position( )) > 1">
            				<xsl:text>,</xsl:text>
						</xsl:if>
            			<xsl:text>"</xsl:text>
            				<xsl:value-of select="." />
            			<xsl:text>"</xsl:text>
            		</xsl:for-each>
            		<xsl:text>]</xsl:text>
        		</xsl:when>
        		<xsl:otherwise>null</xsl:otherwise>
        	</xsl:choose>
        	<xsl:text>,</xsl:text>
        	
        	<!-- Add contact's homepage.-->
        	<xsl:text>"websites":</xsl:text>
        	<xsl:choose>
        		<xsl:when test="foaf:homepage">
        			<xsl:text>[</xsl:text>
        			<xsl:for-each select="foaf:homepage">
            			<xsl:variable name="homepage" select="@rdf:resource" />
            			<xsl:if test="(position( )) > 1">
            				<xsl:text>,</xsl:text>
						</xsl:if>
            			<xsl:text>"</xsl:text>
            				<xsl:value-of select="$homepage" />
            			<xsl:text>"</xsl:text>
            		</xsl:for-each>
            		<xsl:text>]</xsl:text>
        		</xsl:when>
        		<xsl:otherwise>null</xsl:otherwise>
        	</xsl:choose>
        	<xsl:text>,</xsl:text>
        	
        	<!--
            	Add contact's role. It is a list of names identifying valid roles available for the conference 
            	(e.g., General Chair, Author, Semantic Web Technology Coordinator, etc.).
            	The list is constructed by exploiting swc:holdsRole statements coming from the RDF representation
            	of the data.
            	It is important to remark that throught such statements we fetch the URIs a role a 
            	contact holds. Instead, we are interested into their labels.
        		Hence, we exploit XSLT in order to gather the rdfs:label of each role.
            -->
            <xsl:text>"holdsRole":[</xsl:text>
            <xsl:for-each select="swc:holdsRole">
            	<xsl:if test="(position( )) > 1">
            		<xsl:text>,</xsl:text>
				</xsl:if>
                <xsl:variable name="role" select="@rdf:resource" />
                <xsl:text>"</xsl:text><xsl:value-of select="/rdf:RDF/rdf:Description[@rdf:about=$role]/rdfs:label" /><xsl:text>"</xsl:text>
            </xsl:for-each>
            <xsl:text>],</xsl:text>
        	
        	<!-- Add the URI identifying a contact -->
            <!-- xsl:text>"uri": "</xsl:text><xsl:value-of select="$uri" /><xsl:text>"</xsl:text -->
            <xsl:text>}</xsl:text>
        </xsl:for-each>

        <xsl:text>]}</xsl:text>
    </xsl:template>
</xsl:stylesheet>