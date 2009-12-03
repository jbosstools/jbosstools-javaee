<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<f:loadBundle var="Message" basename="demo.Messages"/>

<html>
<head>

</head>

<body>
<f:view locale="de" id="fviewid">
<div id="localeText">#{Message.hello_message}</div>
</f:view>
</body>

</html>
