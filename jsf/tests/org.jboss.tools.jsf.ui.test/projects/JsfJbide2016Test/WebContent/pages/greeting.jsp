<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<html>
<head>
	<title>Facelets Greeting Page</title>
</head>
<body>
	<f:view>
		<h:dataTable value="#{myBean.users}" var="user">
           <h:column>
             <h:outputText value="#{user.}" />
           </h:column>
        </h:dataTable>

	</f:view>
</body>
</html>