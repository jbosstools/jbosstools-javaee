<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>

<html>
<head>
<title>JSTL</title>
</head>
<body>
	<h1>sql:query</h1>
	<sql:query dataSource="someDataSource" var="queryResults" maxRows="10"
		scope="session" id="id1">
		SELECT * FROM customers
	</sql:query>
</body>
</html>
