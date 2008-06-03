<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="converter" /></h1>
	<h:inputText value="value">
		<f:converter converterId="someConverterId" />
	</h:inputText>
</f:view>
</body>
</html>