<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<html>
<head>
</head>
<body>
	<f:view>
		<x:selectOneRadio>
			<f:selectItem itemLabel="value1"/>
			<f:selectItem itemLabel="value2" />
			<f:selectItem itemLabel="value3" />
			<f:selectItem itemLabel="value4" />
		</x:selectOneRadio>
	</f:view>
</body>
</html>