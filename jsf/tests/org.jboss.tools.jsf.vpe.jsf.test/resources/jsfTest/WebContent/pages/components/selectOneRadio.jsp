<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="selectOneRadio" /></h1>

	<h:outputText value="selectOneRadio:" />

	<h:selectOneRadio layout="pageDirection" border="1"
		id="selectOneRadio1">

		<f:selectItem itemLabel="value1" />
		<f:selectItem itemLabel="value2" />
		<f:selectItem itemLabel="value3" />
		<f:selectItem itemLabel="value4" />

	</h:selectOneRadio>

	<h:selectOneRadio dir="ltr" id="selectOneRadio2">

		<f:selectItem itemLabel="value1" />
		<f:selectItem itemLabel="value2" />
		<f:selectItem itemLabel="value3" />
		<f:selectItem itemLabel="value4" />

	</h:selectOneRadio>

</f:view>
</body>
</html>