<?xml version="1.0"?>
<!DOCTYPE xsl:stylesheet [
<!ENTITY db_xsl_path        "../../support/docbook-xsl/">
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns="http://www.w3.org/TR/xhtml1/transitional"
    exclude-result-prefixes="#default">
    
    
    
    
    <!-- Import of the original stylesheet which "just" creates 
        a bunch of HTML files from any valid DocBook instance -->
    <xsl:import href="http://docbook.sourceforge.net/release/xsl/current/html/chunk.xsl"/>
    
    <xsl:include href="./highlight.xsl"></xsl:include>

    <!--START HTML_CHUNK -->

    <!--###################################################
        HTML Settings
        ################################################### -->   
    
    <xsl:param name="chunk.section.depth">'5'</xsl:param>
    <xsl:param name="use.id.as.filename">'1'</xsl:param>
    <xsl:param name="html.stylesheet">css/html.css</xsl:param>
    
    <!-- These extensions are required for table printing and other stuff -->
    <xsl:param name="use.extensions">1</xsl:param>
    <xsl:param name="tablecolumns.extension">0</xsl:param>
    <xsl:param name="callout.extensions">1</xsl:param>
    <xsl:param name="graphicsize.extension">0</xsl:param>
    
    <!--###################################################
        Table Of Contents
        ################################################### -->   
    
    <!-- Generate the TOCs for named components only -->
    <xsl:param name="generate.toc">
        book   toc
    </xsl:param>
    
    <!-- Show only Sections up to level 5 in the TOCs -->
    <xsl:param name="toc.section.depth">5</xsl:param>
    
    <!--###################################################
        Labels
        ################################################### -->   
    
    <!-- Label Chapters and Sections (numbering) -->
    <xsl:param name="chapter.autolabel">1</xsl:param>
    <xsl:param name="section.autolabel" select="1"/>
    <xsl:param name="section.label.includes.component.label" select="1"/>
    
    <!--###################################################
        Callouts
        ################################################### -->   
    
    <!-- Don't use graphics, use a simple number style -->
    <xsl:param name="callout.graphics">0</xsl:param>
    
    <!-- Place callout marks at this column in annotated areas -->
    <xsl:param name="callout.defaultcolumn">90</xsl:param>
    
    <!--###################################################
        Misc
        ################################################### -->   
    
    <!-- Placement of titles -->
    <xsl:param name="formal.title.placement">
        figure after
        example before
        equation before
        table before
        procedure before
    </xsl:param>    
    <xsl:template match="section[@role = 'NotInToc']//*"  mode="toc" />
    <xsl:template match="chapter[@role = 'NotInToc']//section//*"  mode="toc" />
    
    <xsl:param name="ignore.image.scaling" select="1"/>


<!--END HTML_CHUNK -->
    
    <!-- You must plug-in your custom templates here --> 
    <xsl:template match="/">
        <!-- Call original code from the imported stylesheet -->
        <xsl:apply-imports/>
        
        <!-- Call custom templates for the ToC and the manifest -->
        <xsl:call-template name="etoc"/>
        <xsl:call-template name="plugin.xml"/>
    </xsl:template>
    
    <!-- Template for creating auxiliary ToC file -->
    <xsl:template name="etoc">
        <xsl:call-template name="write.chunk">
            <xsl:with-param name="filename" select="'toc.xml'"/>
            <xsl:with-param name="method" select="'xml'"/>
            <xsl:with-param name="encoding" select="'utf-8'"/>
            <xsl:with-param name="indent" select="'yes'"/>
            <xsl:with-param name="content">
                
                <!-- Get the title of the root element -->
                <xsl:variable name="title">
                    <xsl:apply-templates select="/*" mode="title.markup"/>
                </xsl:variable>
                
                <!-- Get HTML filename for the root element -->
                <xsl:variable name="href">
                    <xsl:call-template name="href.target.with.base.dir">
                        <xsl:with-param name="object" select="/*"/>
                    </xsl:call-template>
                </xsl:variable>
                
                <!-- Create root element of ToC file -->
                <toc label="{$title}" topic="{$href}">
                    <!-- Get ToC for all children of the root element -->
                    <xsl:apply-templates select="/*/*" mode="etoc"/>
                </toc>
                
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <!-- Template which converts all DocBook containers into 
        one entry in the ToC file -->
    <xsl:template match="book|part|reference|preface|chapter|
        bibliography|appendix|article|glossary|
        section|sect1|sect2|sect3|sect4|sect5|
        refentry|colophon|bibliodiv|index" 
        mode="etoc">
        <!-- Get the title of the current element -->
        <xsl:variable name="title">
            <xsl:apply-templates select="." mode="title.markup"/>
        </xsl:variable>
        
        <!-- Get HTML filename for the current element -->
        <xsl:variable name="href">
            <xsl:call-template name="href.target.with.base.dir"/>
        </xsl:variable>
        
        <!-- Create ToC entry for the current node and process its 
            container-type children further -->
        <topic label="{$title}" href="{$href}">
            <xsl:apply-templates select="part|reference|preface|chapter|
                bibliography|appendix|article|
                glossary|section|sect1|sect2|
                sect3|sect4|sect5|refentry|
                colophon|bibliodiv|index" 
                mode="etoc"/>
        </topic>
        
    </xsl:template>
    
    <!-- Default processing in the etoc mode is no processing -->
    <xsl:template match="text()" mode="etoc"/>
    
    <!-- Template for generating the manifest file -->
    <xsl:template name="plugin.xml">
        <xsl:call-template name="write.chunk">
            <xsl:with-param name="filename" select="'plugin.xml'"/>
            <xsl:with-param name="method" select="'xml'"/>
            <xsl:with-param name="encoding" select="'utf-8'"/>
            <xsl:with-param name="indent" select="'yes'"/>
            <xsl:with-param name="content">
                <plugin name="{$eclipse.plugin.name}"
                    id="{$eclipse.plugin.id}"
                    version="1.0"
                    provider-name="{$eclipse.plugin.provider}">
                    <extension point="org.eclipse.help.toc">
                        <toc file="toc.xml" primary="true"/>
                    </extension>
                </plugin>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <!-- Customization parameters for the manifest file -->
    <xsl:param name="eclipse.plugin.name">DocBook Online Help Sample</xsl:param>
    <xsl:param name="eclipse.plugin.id">com.example.help</xsl:param>
    <xsl:param name="eclipse.plugin.provider">Example provider</xsl:param>
    
</xsl:stylesheet>