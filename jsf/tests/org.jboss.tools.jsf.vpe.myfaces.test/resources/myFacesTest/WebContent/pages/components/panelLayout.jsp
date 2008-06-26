<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<html>
<head>
</head>
<body>
	<f:view>
		<x:panelLayout>
			<x:outputText value="Text1 "/>
			<x:outputText value="Text2 "/>
			<x:outputText value="Text3 " style=" height : 1px;"/>
		</x:panelLayout>
	</f:view>
</body>
</html>