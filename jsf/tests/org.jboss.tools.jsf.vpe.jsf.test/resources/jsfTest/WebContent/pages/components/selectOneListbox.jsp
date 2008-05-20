<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="selectOneListbox" /></h1>

	<h:outputText value="selectOneListbox:" />

		<h:selectOneListbox value="someValue" >
			<f:selectItem itemLabel="value1" itemValue="value1" />
			<f:selectItem itemLabel="value2" itemValue="value2" />
			<f:selectItem itemLabel="value3" itemValue="value3" />
		</h:selectOneListbox>

</f:view>
</body>
</html>