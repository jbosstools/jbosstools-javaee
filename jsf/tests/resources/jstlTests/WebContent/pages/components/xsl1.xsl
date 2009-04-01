<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">

	<xsl:template match="/">
		<html>
			<body>
				<table border="2" bgcolor="yellow">
					<tr>
						<th>Name</th>
						<th>Place</th>
						<th>Number</th>
						<th>Mark</th>
					</tr>

					<xsl:for-each select="students/student">
						<tr>
							<td>
								<xsl:value-of select="name" />
							</td>
							<td>
								<xsl:value-of select="place" />
							</td>
							<td>
								<xsl:value-of select="number" />
							</td>
							<td>
								<xsl:value-of select="mark" />
							</td>
						</tr>
					</xsl:for-each>

				</table>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>