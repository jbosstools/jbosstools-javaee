<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="outputLink" /></h1>

	<h:outputLink value="www.exadel.com"   >
		<h:outputText value="outputLink"/>
	</h:outputLink>
</f:view>
</body>
</html>