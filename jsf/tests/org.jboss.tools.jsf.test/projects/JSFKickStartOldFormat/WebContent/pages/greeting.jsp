<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<f:loadBundle basename="demo.bundle.Messages" var="Message"/>

<HTML>
<HEAD> <title>Greeting Page</title> </HEAD>
	
<body bgcolor="white">
	<f:view>
		<h3>
			<h:outputText value="#{Message.greeting_text}" />,
			<h:outputText value="#{nameBean.userName}" />!
		</h3>
	</f:view>
</body>	
</HTML>  
