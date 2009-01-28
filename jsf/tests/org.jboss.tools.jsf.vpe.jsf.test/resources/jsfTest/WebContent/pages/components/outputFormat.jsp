<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="outputFormat" /></h1>

	<h:outputFormat value="outputFormat1" id="outputFormat1" />
	<h:outputFormat escape="true" value="outputFormat2" id="outputFormat2" />
	<h:outputFormat escape="false" value="outputFormat3" id="outputFormat3" />
	<h:outputFormat value="{0}" id="outputFormat4">
		<f:param value="outputFormat4" />
	</h:outputFormat>

</f:view>
</body>
</html>