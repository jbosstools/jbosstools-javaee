<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="commandLink" /></h1>

	<h:form id="commandLinkForm">

		<h:commandLink id="commandLink1" value="commandLink1" disabled="true" />
		<h:commandLink id="commandLink2" value="commandLink2" disabled="false" />

	</h:form>
		<h:commandLink id="commandLink3" value="commandLink3" disabled="false" />
</f:view>
</body>
</html>