<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h:outputFormat escape="false"
		value='<input type="button" value="outputFormat">' />
	<h:outputText escape="false"
		value="<input type='button' 
		value='outputText' />" />
	<h:outputLabel escape="false"
		value="<input type='button' 
		value='outputLabel' />" />
</f:view>
</body>
</html>