<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="messages" /></h1>

	<h:messages style="color: red"/>
	<h:inputText required="true" />

</f:view>
</body>
</html>