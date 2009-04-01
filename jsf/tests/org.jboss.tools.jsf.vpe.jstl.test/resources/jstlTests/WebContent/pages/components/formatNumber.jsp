<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>

<html>
<head>
<title>JSTL</title>
</head>
<body>
	<h1>fmt:formatNumber</h1>
	<fmt:formatNumber value="12" scope="session" var="num1"
		maxIntegerDigits="4" groupingUsed="true" id="id1">
	</fmt:formatNumber>
</body>
</html>
