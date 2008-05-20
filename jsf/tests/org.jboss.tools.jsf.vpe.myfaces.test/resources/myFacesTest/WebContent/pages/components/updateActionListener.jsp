<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<html>
<head>
</head>
<body>
	<f:view>
		<x:commandLink action="myBean1.updateMethod">
   			<x:updateActionListener
	       		property="myBean2.myProperty"
	       		value="myBean1.mySecondProperty"/>
		</x:commandLink>
	</f:view>
</body>
</html>