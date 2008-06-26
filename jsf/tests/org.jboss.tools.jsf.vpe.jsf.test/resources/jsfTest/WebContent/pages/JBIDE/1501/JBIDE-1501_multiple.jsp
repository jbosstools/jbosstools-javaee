<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>

	<h:selectManyMenu value="someValue">
		<f:selectItem itemLabel="value1" itemValue="value1" />
		<f:selectItem itemLabel="value2" itemValue="value2" />
		<f:selectItem itemLabel="value3" itemValue="value3" />
		<f:selectItem itemLabel="value4" itemValue="value4" />
	</h:selectManyMenu>

	<h:selectManyListbox value="someValue">
		<f:selectItem itemLabel="value1" itemValue="value1" />
		<f:selectItem itemLabel="value2" itemValue="value2" />
		<f:selectItem itemLabel="value3" itemValue="value3" />
		<f:selectItem itemLabel="value4" itemValue="value4" />
	</h:selectManyListbox>

</f:view>
</body>
</html>