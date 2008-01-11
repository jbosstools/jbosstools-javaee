<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean" prefix="bean" %>
<%@ page import="java.util.HashMap" %>
<html:html>
<head>
	<title></title>
</head>
<body>
	<%
	HashMap<String,String> lines = new HashMap<String,String>();
	lines.put("1", "Line 1");
	lines.put("2", "Line 2");
	lines.put("3", "Line 3");
	request.setAttribute("lines", lines);
	%>
    <bean:size id="length" name="lines" />
    Line Count: <bean:write name="lenght"/>
</body>
</html:html>
