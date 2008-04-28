<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<html>
<head>
</head>
<body>
	<f:view>
		<x:panelNavigation>
  			<x:outputText value="a1"/>
  			<x:commandNavigation id="menuClientList"
         		value="aa1"
         		action="aa1"/>
  			<x:commandNavigation id="menuClientNew"
         		value="aa2"
		         action="aa2"/>
   		</x:panelNavigation>
	</f:view>
</body>
</html>