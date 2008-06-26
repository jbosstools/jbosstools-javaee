<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<html>
<head>
</head>
<body>
<f:view>
	<h:outputLink>
	enabled link 
	</h:outputLink>
	<h:outputLink disabled="true">
		disabled link
	</h:outputLink>
</f:view>
</body>
</html>