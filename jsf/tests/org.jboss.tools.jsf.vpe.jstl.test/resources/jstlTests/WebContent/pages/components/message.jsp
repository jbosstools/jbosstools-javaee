<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>

<html>
<head>
<title>JSTL</title>
</head>
<body>
	<h1>fmt:message</h1>
	<fmt:message
		key="header" scope="session" var="bundleMessageVar" id="id1">
	Use ${bundleMessageVar} in the JSP.	
	</fmt:message>
</body>
</html>
