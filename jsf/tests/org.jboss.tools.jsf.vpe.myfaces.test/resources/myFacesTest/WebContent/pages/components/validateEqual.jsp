<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<html>
<head>
</head>
<body>
	<f:view>
		<h:inputText value="equal"/>
		<h:inputText value="equal2">
    		<x:validateEqual for="equal" />
		</h:inputText>
	</f:view>
</body>
</html>