<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="selectManyCheckbox" /></h1>

	<h:outputText value="selectManyCheckbox:" />

	<h:selectManyCheckbox value="someValue" layout="pageDirection"
		id="selectManyCheckbox1">
		<f:selectItem itemLabel="check1" />
		<f:selectItem itemLabel="check2" />
		<f:selectItem itemLabel="check3" />
	</h:selectManyCheckbox>

	<h:selectManyCheckbox value="someValue" layout="lineDirection"
		style="color:red" styleClass="myClass" border="2"
		id="selectManyCheckbox2">
		<f:selectItem itemLabel="check1" />
		<f:selectItem itemLabel="check2" />
		<f:selectItem itemLabel="check3" />
	</h:selectManyCheckbox>

</f:view>
</body>
</html>