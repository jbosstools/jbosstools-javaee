<!-- 4/26/20004 -->


<xsl:stylesheet	version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method ="text"  />


<!-- If the property value is a help file location
	and is blank or has a placeholder value, then make
	its help file name based on the property name. Otherwise
  print out the name/values as text -->
<xsl:template match="property">
	<xsl:value-of select="@name"/>
	<xsl:text>=</xsl:text>
	<xsl:choose>
		<xsl:when test="substring-before(@name, '.') = '' and ((@value = '/jsf/noHelpYet.html') or (@value = ''))">
			<xsl:value-of select="concat('jsf/', @name, '.html')"/>
		</xsl:when>
		<xsl:otherwise><xsl:value-of select="@value"/></xsl:otherwise>
	</xsl:choose>
	<xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>