<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<f:loadBundle var="Message" basename="demo.Messages" />

<html>
	<body>
		<f:view locale="en">
			<span id="localeText">#{Message.hello_message}</span>
		</f:view>
	</body>
</html>
