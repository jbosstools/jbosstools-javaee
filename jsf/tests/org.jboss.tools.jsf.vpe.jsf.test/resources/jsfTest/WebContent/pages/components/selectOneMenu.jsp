<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="selectOneMenu" /></h1>

	<h:outputText value="selectOneMenu:" />

	<h:selectOneMenu value="someValue" id="selectOneMenu"
		styleClass="myClass" dir="ltr" style="color:red;">

		<f:selectItem itemLabel="value1" itemValue="value1" />
		<f:selectItem itemLabel="value2" itemValue="value2" />

	</h:selectOneMenu>

	<h:selectOneMenu value="someValue" id="selectOneMenu1" disabled="true"
		disabledClass="myClass">

		<f:selectItem itemLabel="value1" itemValue="value1" />
		<f:selectItem itemLabel="value2" itemValue="value2" />

	</h:selectOneMenu>

</f:view>
</body>
</html>