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

	<h1>x:forEach & x:out</h1>
	<x:forEach var="n" select="$doc/books/book">
		<x:out select="$n/title" id="id1"/>
		<br>
		<x:out select="$n/author" />
		<br>
 		========  
 		<br>
	</x:forEach>
	
	<h1>x:out</h1>
	<x:out select="$doc/books" id="id2"/>
</body>
</html>
