<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="inputText" /></h1>

	<h:inputText id="inputText1" value="inputText"/>
	
	<h:inputText id="inputText2" value="Test verbtim for h:inputSecret ">h:inputText</h:inputText>
</f:view>
</body>
</html>