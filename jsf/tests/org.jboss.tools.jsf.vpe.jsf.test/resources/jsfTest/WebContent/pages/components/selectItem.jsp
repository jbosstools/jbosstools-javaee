<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="selecItem" /></h1>

	<h:outputText value="selectitem:" />

	<h:selectManyCheckbox value="someValue">
		<f:selectItem itemLabel="check1" />
		<f:selectItem itemLabel="check2" />
		<f:selectItem itemLabel="check3" />
	</h:selectManyCheckbox>

</f:view>
</body>
</html>