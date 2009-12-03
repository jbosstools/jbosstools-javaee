<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<f:loadBundle var="Message" basename="demo.Messages"/>

<html>
<head>

</head>

<body>
<f:view locale="de">
<div id="localeText1">#{Message.hello_message}</div>
</f:view>
<f:view>
<div id="localeText2">#{Message.hello_message}</div>
</f:view>
<f:view locale="en_US">
<div id="localeText">#{Message.hello_message}</div>
</f:view>
</body>

</html>
