<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="form" /></h1>

	<div id="form_test">
	<h:form style="background-color:Red;" styleClass="myClass">
		Test1Text<div style="background-color: green">Test2<h:form>TestText3</h:form></div>
		<h:form>Test4Text</h:form>
	</h:form>
	</div>
	
</f:view>
</body>
</html>