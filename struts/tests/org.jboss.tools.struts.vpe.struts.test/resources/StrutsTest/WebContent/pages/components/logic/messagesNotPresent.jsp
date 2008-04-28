<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic" prefix="logic" %>
<html:html>
<head>
	<title></title>
</head>
<body>
    <html:form action="">
    	<logic:messagesNotPresent property="noSuchMessage">
    		MessagesNotPresent
		</logic:messagesNotPresent>
    </html:form>
</body>
</html:html>
