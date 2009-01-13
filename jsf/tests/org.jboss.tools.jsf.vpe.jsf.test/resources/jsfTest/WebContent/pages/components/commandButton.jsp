<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="commandButton" /></h1>

	<h:form id="commandButtonForm">

		<h:commandButton value="commandButton" id="commandButton1" />

		<h:commandButton value="commandButton1" id="commandButton2" type="button" />

		<h:commandButton value="commandButton3" id="commandButton3" type="reset" />

		<h:commandButton value="commandButton4" id="commandButton4" type="submit" />

		<h:commandButton value="commandButton5" id="commandButton5" image="" />

		<h:commandButton value="Fight"  id="commandButton6"><p>h:commandButton</p></h:commandButton>
		
		<h:commandButton value="commandButton7" id="commandButton7" type="button" disabled="true"/>
		
	</h:form>
</f:view>
</body>
</html>