<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<html>
<head>
</head>
<body>
	<f:view>
		<x:panelTabbedPane>
    		<x:panelTab id="myTab1" label="Information1">
        		<x:outputText value="text 1" />
    		</x:panelTab>
    		<x:panelTab id="myTab1" label="Information1">
        		<x:outputText value="text 1" />
    		</x:panelTab>
    		<x:panelTab id="myTab1" label="Information1">
        		<x:outputText value="text 1" />
    		</x:panelTab>
    		<x:tabChangeListener type="myproject.MyTabChangeListener" />
		</x:panelTabbedPane>
	</f:view>
</body>
</html>