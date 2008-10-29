<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<html>
	<head>
		<title></title>
	</head>
	<body>
	<f:loadBundle basename="demo.resources" var="msg" />
		<f:view>
			<H1> JSP Test Page </H1> 	
			<h:outputText value="#{msg.header}" />
		</f:view>
	</body>	
</html> 
