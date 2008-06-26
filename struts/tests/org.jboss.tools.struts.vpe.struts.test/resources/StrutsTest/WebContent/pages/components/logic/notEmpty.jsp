<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic" prefix="logic" %>
<html:html>
<head>
	<title></title>
</head>
<body>
    <%
    	request.setAttribute("goodThingsAbouteTheYankees", "goodThings");
    %>
    <logic:notEmpty name="goodThingsAbouteTheYankees">
    	New Yorker, eh?
    </logic:notEmpty>
</body>
</html:html>
