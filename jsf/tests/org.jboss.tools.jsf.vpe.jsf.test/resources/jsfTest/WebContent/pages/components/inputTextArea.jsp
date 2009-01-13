<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="inputTextArea" /></h1>

	<h:inputTextarea value="inputTextArea" id="inputTextArea1" />
	
	<h:inputTextarea value="Test text for h:inputTextarea " id="inputTextArea2">h:inputTextarea</h:inputTextarea>
	
</f:view>
</body>
</html>