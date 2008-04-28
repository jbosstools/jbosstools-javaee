<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="subview" /></h1>
	<f:subview id="subviewId">
		<h:outputText value="subview content" />
	</f:subview>
</f:view>
</body>
</html>