<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<html>
<head>
</head>
<body>
	<f:view>
		<h:inputText value="regExprValue">
    		<x:validateRegExpr pattern='\d{5}'/>
		</h:inputText>
	</f:view>
</body>
</html>