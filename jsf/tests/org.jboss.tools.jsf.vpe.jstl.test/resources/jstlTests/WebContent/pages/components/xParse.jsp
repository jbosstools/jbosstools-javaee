<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>

<html>
<head>
<title>JSTL</title>
</head>
<body>
	<h1>c:import</h1>
	<c:import url="books.xml" var="url" />
	-----------------------------------------------<br>
	<h1>x:parse</h1>
	<x:parse xml="${url}" var="doc" id="id1"/>
</body>
</html>
