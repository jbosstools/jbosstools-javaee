<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<html>
<head>
</head>
<body>
<f:view>
	<b><h:outputFormat value="{0}"
	><f:param value="paramValue" 
	/></h:outputFormat></b>
</f:view>
</body>
</html>