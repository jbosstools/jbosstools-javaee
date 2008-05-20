<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<html>
<head>
</head>
<body>
	<f:view>
		<x:outputLabel for="text">
			<h:outputText value="Texx: " />
		</x:outputLabel>
		<h:inputText id="text" value="text" />
	</f:view>
</body>
</html>