<!-- 4/26/20004 -->


<xsl:stylesheet	version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method ="html" indent="yes"  />

<!-- Will need to be adjusted for different systems, but
		please don't commit just for this -->
<xsl:variable name="pathToHelp">file://d:/CVSProjects/Exadel6/eclipse/plugins/org.jboss.tools.jsf.doc.ui</xsl:variable>

<xsl:template match="/">
	<html>
		<head>
			<title><xsl:value-of select="properties/@filename"/></title>
        <link rel="stylesheet" type="text/css" href="{concat($pathToHelp, '/help.css')}" />
		</head>
    <body>
			<h1><xsl:value-of select="properties/@filename"/></h1>
        <ul>
        	<li><a href="#Unsorted">Unsorted</a></li>
            <li><a href="#Sorted">Sorted</a></li>
        </ul>
        <h2><a name="Unsorted"/>Unsorted</h2>
        <table>
        	<xsl:apply-templates/>
        </table>
        <h2><a name="Sorted"/>Sorted</h2>
        <table>
				<xsl:apply-templates mode="sortedByName"/>
        </table>
    </body>
  </html>
</xsl:template>

<xsl:template match="properties" mode="sortedByName">
	<xsl:for-each select="property">
   	<xsl:sort select="@name"/>
    <tr>
  		<td class="stub">
           	<xsl:value-of select="@name"/>
        </td>
        <xsl:choose>
        	<xsl:when test="substring-before(@name, '.') = ''">
           		<td><a href="{concat($pathToHelp,@value)}" target="NEW"><xsl:value-of select="@value"/></a></td>
                <td/>
            </xsl:when>
				<xsl:otherwise>
            	<td/>
        		<td><xsl:value-of select="@value"/></td>
            </xsl:otherwise>
        </xsl:choose>
    </tr>
	</xsl:for-each>
</xsl:template>

<xsl:template match="property">
    <tr>
   		<td class="stub"><xsl:value-of select="@name"/></td>
        <xsl:choose>
        	<xsl:when test="substring-before(@name, '.') = ''">
           		<td><a href="{concat($pathToHelp,@value)}" target="NEW"><xsl:value-of select="@value"/></a></td>
                <td/>
            </xsl:when>
				<xsl:otherwise>
            	<td/>
        		<td><xsl:value-of select="@value"/></td>
            </xsl:otherwise>
        </xsl:choose>
    </tr>
</xsl:template>

<xsl:template match="comment() | processing-instruction() | text()"/>

</xsl:stylesheet>