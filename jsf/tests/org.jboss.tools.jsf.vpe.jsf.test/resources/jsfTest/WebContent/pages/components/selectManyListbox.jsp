<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="selectManyListbox" /></h1>

	<h:outputText value="selectManyListbox:" />

	<h:selectManyListbox value="someValue">
		<f:selectItem itemLabel="value1" itemValue="value1" />
		<f:selectItem itemLabel="value2" itemValue="value2" />
	</h:selectManyListbox>

</f:view>
</body>
</html>