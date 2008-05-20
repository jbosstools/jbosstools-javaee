<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic" prefix="logic" %>
<html:html>
<head>
	<title></title>
</head>
<body>
    <%
    	request.setAttribute("goodThingsAbouteTheYankees", "");
    %>
    <logic:empty name="goodThingsAbouteTheYankees">
    	You must be a Red Sox fan!
    </logic:empty>
</body>
</html:html>
