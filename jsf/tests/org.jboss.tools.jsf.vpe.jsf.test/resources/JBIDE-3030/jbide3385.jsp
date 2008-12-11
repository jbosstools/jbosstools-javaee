<!doctype html public "-//w3c//dtd html 4.0 transitional//en">
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
<style type="text/css">

.row1{
	font-family: Times;
	background-color: #D7E6E3;
}
.row2{
	font-family: Times;
	background-color: #8D9E9B;
}
.name {
}
.path {
font-size: x-small;
}
.server {
font-size: small;
}
.fileSize {
}
.scanDate{
font-size: x-small;
}
</style>
<title>Net search</title>
</head>
<body>

<f:view>
	<h1><h:outputText value="Net Search" /></h1>
	<h:form  id="searchFilter">
		<h:panelGrid id="filter" columns="11" border="1">
			<h:outputText id="name" value="Name:" />
			<h:inputText value="#{user.name}"></h:inputText>
			<h:outputText id="path" value="Path:" />
			<h:inputText value="#{user.path}"></h:inputText>
			<h:outputText id="server" value="Server:" />
			<h:inputText value="#{user.server}"></h:inputText>
			<h:outputText id="maxSize" value="Max Size:" />
			<h:inputText value="#{user.sizeMax}"></h:inputText>
			<h:outputText id="minSize" value="Min Size:" />
			<h:inputText value="#{user.sizeMin}"></h:inputText>
			<h:commandButton style="align:right;"
				value="Submit" />
		</h:panelGrid>
		<h:outputText value="Founde Items: #{user.numberOfItems}, shows only first 300 items"></h:outputText>
		<h:dataTable value="#{user.resources}" rowClasses="row1, row2" columnClasses="name, path, server,fileSize,scanDate" var="row">
			<h:column>
				<f:facet name="header">
					<h:commandLink>
						<h:outputText value="Name"></h:outputText>
						<f:param name="orderBy" value="name"></f:param>
						<f:param name="orderDirection" value="#{user.orderDirection}"></f:param>
					 </h:commandLink>
				</f:facet>
				<h:outputText value="#{row.name}" />
			</h:column>
			<h:column>
				<f:facet name="header">
				<h:commandLink>
					<h:outputText value="Path" />
					<f:param name="orderBy" value="path"></f:param>
					<f:param name="orderDirection" value="#{user.orderDirection}"></f:param>
				</h:commandLink>
				</f:facet>
				<h:outputLink charset="UTF-8" value="#{facesContext.externalContext.requestContextPath}/downloadServlet">
					<h:outputText  value="#{row.path}" />
					<f:param   name="downloadPath" value="#{row.path}"></f:param>
				</h:outputLink>
			</h:column>
			<h:column>
				<f:facet name="header">
				<h:commandLink>
					<h:outputText value="Server" />
					<f:param name="orderBy" value="server"></f:param>
					<f:param name="orderDirection" value="#{user.orderDirection}"></f:param>
				</h:commandLink>
				</f:facet>
				<h:outputText value="#{row.server}" />
			</h:column>
			<h:column>
				<f:facet name="header">
				<h:commandLink>
					<h:outputText value="File Size" />
					<f:param name="orderBy" value="fileSize"></f:param>
					<f:param name="orderDirection" value="#{user.orderDirection}"></f:param>
				</h:commandLink>
				</f:facet>
				<h:outputText value="#{row.fileSize}" />
			</h:column>
			<h:column>
				<f:facet name="header">
				<h:commandLink>
					<h:outputText value="Scan Date" />
					<f:param name="orderBy" value="scanDate"></f:param>
					<f:param name="orderDirection" value="#{user.orderDirection}"></f:param>
				</h:commandLink>
				</f:facet>
				<h:outputText value="#{row.scanDate}" />
			</h:column>		
		</h:dataTable>
		</h:form>
</f:view>
</body>
</html>
