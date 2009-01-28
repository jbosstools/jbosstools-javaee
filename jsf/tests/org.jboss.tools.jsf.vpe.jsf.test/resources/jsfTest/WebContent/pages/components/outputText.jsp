<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="outputText" /></h1>

	<h:outputText value="outputText1" id="outputText1" />
	<h:outputText value="outputText2" id="outputText2" escape="true" />
	<h:outputText value="outputText3" id="outputText3" escape="false" />

</f:view>
</body>
</html>