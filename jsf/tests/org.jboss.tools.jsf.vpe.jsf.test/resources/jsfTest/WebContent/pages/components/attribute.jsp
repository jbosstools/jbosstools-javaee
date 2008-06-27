<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="attribute" /></h1>
	<h:form id="form">
		<h:commandButton value="button">
			<f:attribute name="attributeName" value="attributeValue" />
		</h:commandButton>
	</h:form>
</f:view>
</body>
</html>