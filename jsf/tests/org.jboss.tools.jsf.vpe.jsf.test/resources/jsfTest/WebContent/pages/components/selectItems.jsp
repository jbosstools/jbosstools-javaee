<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="selecItems" /></h1>

	<h:outputText value="selectitems:" />

	<h:selectManyCheckbox value="someValue">
		<f:selectItems value="someValue" />
	</h:selectManyCheckbox>

</f:view>
</body>
</html>