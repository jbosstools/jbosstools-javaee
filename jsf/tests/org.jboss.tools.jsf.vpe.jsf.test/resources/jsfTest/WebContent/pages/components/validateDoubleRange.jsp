<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="validateDoubleRange" /></h1>
	<h:form>
		<h:inputText value="value">
			<f:validateDoubleRange minimum="0" maximum="100" />
		</h:inputText>
	</h:form>
</f:view>
</body>
</html>