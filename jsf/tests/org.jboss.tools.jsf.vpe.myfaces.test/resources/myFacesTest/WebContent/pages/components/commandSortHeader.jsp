<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<html>
<head>
</head>
<body>
	<f:view>
		<x:dataTable var="var">
			<h:column>
				<f:facet name="header">
					<h:outputText value="car.id" />
				</f:facet>
				<h:outputText value="car.id" />
			</h:column>
			<h:column>
				<f:facet name="header">
    				<x:commandSortHeader columnName="type" 
        				arrow="true" immediate="false">
    				<h:outputText value="commandSortHeader1" />
    				</x:commandSortHeader>
				</f:facet>
				<h:outputText value="car.type" />
			</h:column>
			<h:column>
				<f:facet name="header">
    				<x:commandSortHeader columnName="color" 
        				arrow="true" immediate="false">
         			<h:outputText value="commandSortHeader2" />
    				</x:commandSortHeader>
				</f:facet>
				<h:inputText value="car.color" >
    				<f:validateLength maximum="5"/>
				</h:inputText>
			</h:column>
		</x:dataTable>
	</f:view>
</body>
</html>