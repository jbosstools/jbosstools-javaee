<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<f:loadBundle var="Message" basename="demo.Messages" />

<html>
	<body>
		<f:view id="view-id">
			<h:outputText value="#{Message.hello_message}1" />
			#{Message.hello_message}2
		</f:view>
	</body>
</html>
