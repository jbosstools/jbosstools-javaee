<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
	<head>
		<title></title>
	</head>
	<body>
		<f:view>
			<h:outputText value="Test value is #{abc.value}!" />
			<h:outputText value="Test value is #{cba.value}!" />
		</f:view>
	</body>	
</html>  
