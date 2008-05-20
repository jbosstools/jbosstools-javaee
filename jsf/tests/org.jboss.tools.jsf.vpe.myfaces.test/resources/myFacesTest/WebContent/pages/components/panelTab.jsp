<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<html>
<head>
</head>
<body>
	<f:view>
		<x:panelTabbedPane>
		
			<x:panelTab>
				<x:outputText value="panelTab1" />
			</x:panelTab>

			<x:panelTab>
				<x:outputText value="panelTab2" />
			</x:panelTab>

			<x:panelTab>
				<x:outputText value="panelTab3" />
			</x:panelTab>

		</x:panelTabbedPane>
	</f:view>
</body>
</html>