<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>

<html>
<head>
<title>JSTL</title>
</head>
<body>
	<h1>fmt:param</h1>
	<fmt:bundle basename="demo.Messages">
		<fmt:message key="header">
			<fmt:param value="YourName" id="id1"/>
		</fmt:message>
		<br>
		<br>
		<center><a href="<c:url value='/index.jsp'/>"><fmt:message
			key="com.taglib.weblog.Greeting.return" /></a></center>
	</fmt:bundle>
</body>
</html>
