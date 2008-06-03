<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="loadBundle" /></h1>
	<f:loadBundle var="var" basename="someBaseName" />
</f:view>
</body>
</html>