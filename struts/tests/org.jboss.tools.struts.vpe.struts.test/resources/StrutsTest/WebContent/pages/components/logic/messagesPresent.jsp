<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic" prefix="logic" %>
<html:html>
<head>
	<title></title>
</head>
<body>
    <html:form action="">
    	<logic:messagesPresent property="anyMessage">
    		MessagesPresent
		</logic:messagesPresent>
    </html:form>
</body>
</html:html>
