<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<html>
<head>
</head>
<body>
	<f:view>
		<x:selectOneMenu>
			<f:selectItem itemLabel="value1" itemValue="value1" />
			<f:selectItem itemLabel="value2" itemValue="value2" />
		</x:selectOneMenu>
	</f:view>
</body>
</html>