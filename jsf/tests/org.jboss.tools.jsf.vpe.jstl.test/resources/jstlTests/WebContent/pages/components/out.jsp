<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>

<html>
<head>
<title>JSTL</title>
</head>
<body>
	<h1>c:out</h1>
	<c:out value="cout_value" id="id1">
		some out
	</c:out>

	<c:out value="escaped symbols defult behavior ==[ <,>,&,' ]==" id="id2"/>
	<c:out value='escaped quotes defult behavior ==[ " ]==' id="id3" />
	<c:out value="escaped symbols attr set to false ==[ <,>,&,' ]==" escapeXml="false" id="id4" />
	<c:out value='escaped quotes attr set to false ==[ " ]==' escapeXml="false" id="id5" />
	
	<c:out value="escaped symbols defult behavior ==[ <xml><book> some&xml'inside </book></xml> ]==" id="id6" />
	<c:out value='escaped quotes defult behavior ==[ <xml><book> "some xml inside" </book></xml> ]==' id="id7" />
	<c:out value="escaped symbols attr set to false ==[ <xml><book> some&xml'inside </book></xml> ]==" escapeXml="false" id="id8" />
	<c:out value='escaped quotes attr set to false ==[ <xml><book> "some xml inside" </book></xml> ]==' escapeXml="false" id="id9" />
	
</body>
</html>
