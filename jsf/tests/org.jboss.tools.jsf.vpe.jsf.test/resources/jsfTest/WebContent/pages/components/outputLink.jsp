<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="outputLink" /></h1>

	<h:outputLink value="" id="outputLink1" dir="rtl">
		outputLink1
	</h:outputLink>
	<h:outputLink disabled="true" value="" id="outputLink2">
		outputLink1
	</h:outputLink>
	<h:outputLink disabled="false" value="" id="outputLink3">
		outputLink1
	</h:outputLink>
</f:view>
</body>
</html>