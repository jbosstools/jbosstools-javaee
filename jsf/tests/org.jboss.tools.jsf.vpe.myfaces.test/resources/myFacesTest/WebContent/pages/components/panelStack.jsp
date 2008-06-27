<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<html>
<head>
</head>
<body>
	<f:view>
		<x:panelStack selectedPanel="">
			<x:panelTabbedPane id="panel1">
		
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
		
		<x:panelTabbedPane id="panel2">
		
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
		</x:panelStack>
	</f:view>
</body>
</html>