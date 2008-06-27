<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="facet" /></h1>
	<h:dataTable value="data">

		<h:column>
			<f:facet name="header">
				<h:outputText value="Last Name" />
			</f:facet>


			<h:outputText value="Dupont" />

			<f:facet name="footer">
				<h:outputText value="footer" />
			</f:facet>

		</h:column>

		<h:column>
			<f:facet name="header">
				<h:outputText value="First Name" />
			</f:facet>

			<h:outputText value="William" />

			<f:facet name="footer">
				<h:outputText value="footer" />
			</f:facet>

		</h:column>
	</h:dataTable>
</f:view>
</body>
</html>