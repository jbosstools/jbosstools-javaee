<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="convertNumber" /></h1>
	<h:inputText value="value">
		<f:convertNumber minFractionDigits="2"/>
	</h:inputText>
</f:view>
</body>
</html>