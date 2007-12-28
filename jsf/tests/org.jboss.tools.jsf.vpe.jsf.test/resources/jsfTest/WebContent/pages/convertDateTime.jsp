<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="convertDateTime" /></h1>
	<h:inputText value="value">
		<f:convertDateTime pattern="MM/yyyy" />
	</h:inputText>
</f:view>
</body>
</html>