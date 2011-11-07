<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
	<head>
		<title></title>
	</head>
	<body>
		<f:view>
			<h:outputText value="Test value is #{data.value}!" />
			<h:outputText value="Test value is #{model.value}!" />
		</f:view>
	</body>	
</html>  
