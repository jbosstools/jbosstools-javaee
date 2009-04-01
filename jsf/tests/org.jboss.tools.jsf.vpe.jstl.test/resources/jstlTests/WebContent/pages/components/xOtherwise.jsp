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
	<x:parse xml="${url}" var="doc" />
	
	<h1>x:choose & x:when & x:otherwise</h1>
			<x:choose>
				<x:when select="$doc/books/book/title=selection2">
					When condition is true
				</x:when>
				<x:otherwise id="id1">
					Otherwise
				</x:otherwise>
			</x:choose>
</body>
</html>
