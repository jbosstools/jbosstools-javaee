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
    <logic:lessEqual name="numberIntValue" value="90200" scope="request">
    	lessEqual
	</logic:lessEqual>
</body>
</html:html>
