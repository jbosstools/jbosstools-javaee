<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<html>
<head>
<title>
(Locale: 3) Test change locale and refresh
</title>
</head>

<body>
<f:view locale="de" id="fviewid">
<f:loadBundle var="Message" basename="demo.Messages"/>
<h:outputText value="locale=de"/><br></br>
<div id="localeText">#{Message.hello_message}</div>
</f:view>
</body>

</html>
