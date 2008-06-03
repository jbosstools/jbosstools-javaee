<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="setPropertyActionListener" /></h1>
	<h:form>
		<h:commandButton value="click">
			<f:setPropertyActionListener target="#{myBean.currentPage}" value="1" />
		</h:commandButton>
	</h:form>
</f:view>
</body>
</html>