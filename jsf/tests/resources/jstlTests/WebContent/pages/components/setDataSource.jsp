<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>

<html>
<head>
<title>JSTL</title>
</head>
<body>
	<h1>sql:setDataSource</h1>
	<sql:setDataSource var="example"
		driver="oracle.jdbc.driver.OracleDriver"
		url="jdbc:oracle:thin:@insn104a.idc.oracle.com:1522:ora9idb"
		user="scott" password="tiger" 
		id="id1"/>
</body>
</html>
