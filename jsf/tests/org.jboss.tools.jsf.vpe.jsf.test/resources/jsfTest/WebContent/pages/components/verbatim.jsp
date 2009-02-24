<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="verbatim" /></h1>
	<f:verbatim id="verbatim1">
		<input type="button" value="button" />
	</f:verbatim>
	<f:verbatim escape="true" id="verbatim2">
		<input type="button" value="button" />
	</f:verbatim>
	<f:verbatim escape="true" id="verbatim3"
	><input type="button" value="button" /></f:verbatim>
</f:view>
</body>
</html>