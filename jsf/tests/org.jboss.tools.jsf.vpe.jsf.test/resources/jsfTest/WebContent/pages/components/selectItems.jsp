<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="selectItems" /></h1>

	<h:outputText value="selectitems:" />

	<h:selectManyCheckbox>
		<f:selectItems value="someValue" id="selectItems1" />
	</h:selectManyCheckbox>

	<h:selectManyListbox>
		<f:selectItems value="someValue" id="selectItems2" />
	</h:selectManyListbox>

	<h:selectOneRadio>
		<f:selectItems value="someValue" id="selectItems3" />
		<f:selectItems value="someValue" id="selectItems3" />
		<f:selectItems value="someValue" id="selectItems3" />
		<f:selectItems value="someValue" id="selectItems3" />
	</h:selectOneRadio>

</f:view>
</body>
</html>