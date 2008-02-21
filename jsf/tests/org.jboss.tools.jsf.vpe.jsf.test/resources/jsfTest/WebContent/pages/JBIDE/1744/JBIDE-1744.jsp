<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>

<style type="text/css">
.caption-class {
	width: 200px;
	background: blue;
}

.footer-class {
	background: brown;
	text-align: left;
}

.footer-class-alt {
	background: blue;
	text-align: left;
}

.header-class-alt {
	background: cyan;
}

.header-class {
	background: green;
}
</style>
</head>

<body>
<f:view>


	<h:dataTable value="#{users}" var="item" border="2" width="500"
		captionClass="caption-class" captionStyle="color: grey;"
		footerClass="footer-class" headerClass="header-class">


		<f:facet name="header">
			<h:outputText value="header" />
		</f:facet>

		<f:facet name="footer">
			<h:outputText value="footer" />
		</f:facet>

		<f:facet name="caption">
			<h:outputText value="caption" />
		</f:facet>

		<h:column footerClass="footer-class-alt"
			headerClass="header-class-alt">
			<f:facet name="header">
				<h:outputText value="name1head" />
			</f:facet>
			<f:facet name="footer">
				<h:outputText value="name1foot" />
			</f:facet>
			<h:outputText value="zz1" />
		</h:column>
		
		<h:column>
			<f:facet name="header">
				<h:outputText value="name22" />
			</f:facet>
			<h:outputText value="zz2" />
		</h:column>
		
		<h:column>
			<f:facet name="header">
				<h:outputText value="name333" />
			</f:facet>
			<h:outputText value="zz3" />
		</h:column>
		
	</h:dataTable>
</f:view>
</body>

</html>