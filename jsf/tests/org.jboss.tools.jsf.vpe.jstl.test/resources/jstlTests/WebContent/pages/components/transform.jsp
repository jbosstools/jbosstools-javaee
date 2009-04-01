<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>

<html>
<head>
<title>JSTL</title>
</head>
<body>
	<h1>x:transform</h1>
	<c:import url="students.xml" var="url" />
	<c:import url="xsl1.xsl" var="xsl" />
	<x:transform xml="${url}" xslt="${xsl}" id="id1"/>

</body>
</html>
