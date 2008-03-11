<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean" prefix="bean" %>
<html:html>
<head>
	<title></title>
</head>
<body>
	<% request.setAttribute("session",session); %>
    <bean:define id="context" name="session" />
</body>
</html:html>
