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
		<f:selectItem itemLabel="check1" id="selectItem1" />
	</h:selectManyCheckbox>

	<h:selectManyListbox value="someValue">
		<f:selectItem itemLabel="value1" itemValue="value1" id="selectItem2" />
	</h:selectManyListbox>

	<h:selectOneRadio>

		<f:selectItem itemLabel="value1" id="selectItem3" />

	</h:selectOneRadio>

</f:view>
</body>
</html>