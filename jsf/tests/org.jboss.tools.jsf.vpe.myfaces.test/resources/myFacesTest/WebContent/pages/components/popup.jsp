<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<html>
<head>
</head>
<body>
	<f:view>
		<x:popup>
    		<h:outputText value="String1"/>
    		<f:facet name="popup">
        		<h:panelGroup>
           			<h:panelGrid columns="int">
               			<h:outputText value="String2"/>
           			</h:panelGrid>
        		</h:panelGroup>
    		</f:facet>
		</x:popup>
	</f:view>
</body>
</html>
