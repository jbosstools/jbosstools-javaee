<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<html>
<head>
</head>
<body>
	<f:view>
		<x:panelNavigation>
  			<x:navigationMenuItem id="nav_1" itemLabel="nav_1" action="go_home" />
        	<x:navigationMenuItem id="nav_2" itemLabel="nav_2" />
        	<x:navigationMenuItem id="nav_3" itemLabel="nav_3" />
        	<x:navigationMenuItem id="nav_4" itemLabel="nav_4"/>
	        <x:navigationMenuItem id="nav_5" value="nav_5" />
   		</x:panelNavigation>
	</f:view>
</body>
</html>