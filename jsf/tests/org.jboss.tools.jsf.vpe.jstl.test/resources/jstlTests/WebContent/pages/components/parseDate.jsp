<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>

<html>
<head>
<title>JSTL</title>
</head>
<body>
	<h1>fmt:parseDate</h1>
	<fmt:parseDate value="03/30/2009 15:30:00"
		pattern="MM/dd/yyyy HH:mm:ss" id="id1">
	</fmt:parseDate>
</body>
</html>
