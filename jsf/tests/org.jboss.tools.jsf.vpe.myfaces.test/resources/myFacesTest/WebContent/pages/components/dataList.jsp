<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<html>
<head>
</head>
<body>
	<f:view>
		<x:dataList var="var">
			<h:outputText value="Last Name" />
			<h:outputText value="Dupont" />
			<h:outputText value="First Name" />
			<h:outputText value="William" />
		</x:dataList>
	</f:view>
</body>
</html>