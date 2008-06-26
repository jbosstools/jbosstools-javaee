<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<html>
<head>
</head>
<body>
	<f:view>
		<x:selectManyCheckbox>
			<f:selectItem itemLabel="check 1" value="check 1"/>
			<f:selectItem itemLabel="check 2" value="check 2"/>
			<f:selectItem itemLabel="check 3" value="check 3"/>
		</x:selectManyCheckbox>
	</f:view>
</body>
</html>