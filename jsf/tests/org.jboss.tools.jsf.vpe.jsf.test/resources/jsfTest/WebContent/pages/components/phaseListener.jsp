<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="phaseListener" /></h1>

	<f:phaseListener type="phaseListenerId" id="phaseListener"/>
	
</f:view>
</body>
</html>