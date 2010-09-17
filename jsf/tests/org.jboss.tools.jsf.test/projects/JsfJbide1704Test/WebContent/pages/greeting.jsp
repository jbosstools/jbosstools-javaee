<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<html>
<head>
	<title>Facelets Greeting Page</title>
</head>
<body>
	<f:view>
		<f:loadBundle basename="resources" var="msg" />
		
		<strong>
		<h:outputText value="#{msg.greeting}" />
		&nbsp;
		<h:outputText value="#{person.name}" />
		<h:outputText value="!" />
		</strong>
	</f:view>
</body>
</html>