<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
<title>
(Locale: 1) Test locale attribute for f:view
</title>
</head>

<body>
<f:view locale="de">
<f:loadBundle var="Message" basename="demo.Messages"/>
<h:outputText value="locale=de"/><br></br>
<div id="localeText">#{Message.hello_message}</div>
</f:view>
</body>

</html>
