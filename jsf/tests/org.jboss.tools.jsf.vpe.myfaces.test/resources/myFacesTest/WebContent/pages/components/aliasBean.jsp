<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<html>
<head>
</head>
<body>
	<f:view>
		<x:aliasBeansScope>
   			<x:aliasBean alias="addressAlias" value="PersonEditBean.person.address" />
  		</x:aliasBeansScope>
	</f:view>
</body>
</html>