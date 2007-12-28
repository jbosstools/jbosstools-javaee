<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="panelGroup" /></h1>
	<h:panelGrid columns="2" >

		<h:outputText value="column 1" />
		<h:outputText value="column 2" />

		<h:panelGroup>
			<h:outputText value="1" />
			<h:outputText value="2" />
		</h:panelGroup>
		<h:panelGroup>
			<h:outputText value="3" />
			<h:outputText value="4" />
		</h:panelGroup>
	</h:panelGrid>
</f:view>
</body>
</html>