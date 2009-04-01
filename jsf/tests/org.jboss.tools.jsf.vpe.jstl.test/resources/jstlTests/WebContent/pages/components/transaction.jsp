<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>

<html>
<head>
<title>JSTL</title>
</head>
<body>
	<h1>sql:transaction & sql:update</h1>
	<sql:transaction dataSource="${example}" id="id1">
		<sql:update var="newTable">
    		create table mytable (
      		nameid int primary key,
		     name varchar(80)
    		)
  		</sql:update>

		<h2>Inserting three rows into table</h2>
		<sql:update var="updateCount">
    		INSERT INTO mytable VALUES (1,'Paul Oakenfold')
  		</sql:update>
		<sql:update var="updateCount">
    		INSERT INTO mytable VALUES (2,'Timo Maas')
  		</sql:update>
		<sql:update var="updateCount">
    		INSERT INTO mytable VALUES (3,'Paul Adam')
  		</sql:update>

		<p>DONE: Inserting three rows into table</p>

		<sql:query var="deejays">
		    SELECT * FROM mytable
  		</sql:query>

	</sql:transaction>

</body>
</html>
