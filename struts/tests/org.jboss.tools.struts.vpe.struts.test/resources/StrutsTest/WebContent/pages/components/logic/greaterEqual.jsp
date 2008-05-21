<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic" prefix="logic" %>
<html:html>
<head>
	<title></title>
</head>
<body>
	<%
	request.setAttribute("numberIntValue",new Integer(90210));
	%>
    <logic:greaterEqual name="numberIntValue" value="90211" scope="request">
    	greaterEqual
	</logic:greaterEqual>
</body>
</html:html>
