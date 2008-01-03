<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="param" /></h1>

	<h:form id="formId">

		<h:commandLink id="linkId">

			<h:outputText value="param" />

			<f:param id="paramId" name="param" value="someParamValue" />

		</h:commandLink>

	</h:form>
	
</f:view>
</body>
</html>