<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="outputLabel" /></h1>

	<h:outputLabel value="outputLabel1" id="outputLabel1" />
	<h:outputLabel escape="true" value="outputLabel2" id="outputLabel2" />
	<h:outputLabel escape="false" value="outputLabel3" id="outputLabel3" />
	<h:outputLabel escape="false" binding="outputLabel4" id="outputLabel4" />
</f:view>
</body>
</html>