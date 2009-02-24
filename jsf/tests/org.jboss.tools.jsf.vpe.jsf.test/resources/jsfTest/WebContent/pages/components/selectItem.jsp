<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="selecItem" /></h1>

	<h:outputText value="selectitem:" />

	<h:selectManyCheckbox value="someValue" disabledClass="myClass"
		enabledClass="myClass1">
		<f:selectItem itemLabel="check1" id="selectItem1" />
	</h:selectManyCheckbox>

	<h:selectManyListbox value="someValue">
		<f:selectItem itemLabel="value1" itemValue="value1" id="selectItem2" />
		<f:selectItem itemLabel="value2" id="selectItem2_1" itemDisabled="true" />
		<f:selectItem itemLabel="<b>value3</b>" id="selectItem2_2" escape="false" />
	</h:selectManyListbox>

	<h:selectOneRadio>

		<f:selectItem itemLabel="value1" id="selectItem3"  />
		<f:selectItem itemLabel="value2" id="selectItem3_1"
			itemDisabled="true" dir="ltr" />
		<f:selectItem itemValue="itemValue1" id="selectItem3_2"
			 />
		<f:selectItem value="value" id="selectItem3_3"  />
			

	</h:selectOneRadio>
	
	<h:selectManyCheckbox value="someValue" disabledClass="myClass"
		enabledClass="myClass1" dir="ltr" disabled="true">
		<f:selectItem itemLabel="check1" id="selectItem4" />
	</h:selectManyCheckbox>

</f:view>
</body>
</html>