<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<html>
    <body>
	<f:view>
		<h:form>
			<f:converter converterId="testConverter" />
		</h:form>	
	</f:view>
    </body>
</html>