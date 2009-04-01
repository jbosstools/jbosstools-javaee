<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>

<html>
<head>
<title>JSTL</title>
</head>
<body>
<h1>c:choose c:when c: otherwise</h1>
	<c:choose>
		<c:when test="${mister}" id="id1">
			Mr.
		</c:when>
		<c:otherwise>
			Ms.
		</c:otherwise>
	</c:choose>
</body>
</html>
