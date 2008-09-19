<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="selectBooleanCheckbox" /></h1>

	<h:outputText value="selectBooleanCheckbox:" />
	<h:selectBooleanCheckbox value="false" id="selectBooleanCheckbox"/>

</f:view>
</body>
</html>