<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>

<html>
<head>
<title>JSTL</title>
</head>
<body>
	<h1>x:set</h1>
	<x:set var="name" scope="request" select="$doc/books" id="id1"/>
</body>
</html>
