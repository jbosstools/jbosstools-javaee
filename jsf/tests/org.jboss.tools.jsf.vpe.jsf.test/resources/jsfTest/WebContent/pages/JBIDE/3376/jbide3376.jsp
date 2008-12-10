<%@ taglib uri="http://java.sun.com/jsf/html" prefix="j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>

<html>
<head></head>
<body id="jbide3376">
<f:view >
	<j:form>
		<j:dataTable value="#{bookList.bookList}" var="book">
			<f:facet name="header">
				<j:outputText value="Header" />
			</f:facet>
			<f:facet name="footer">
				<j:outputText value="Header" />
			</f:facet>
			<j:column>
				<j:outputText value="#{book.name}" />
			</j:column>
			<j:column>
				<j:outputText value="#{book.name}" />
			</j:column>
		</j:dataTable>
		<j:commandLink action="none" value="Go next page:" />
		<br></br>
		<j:outputText value="Begin" />
	</j:form>
</f:view>
</body>
</html>
