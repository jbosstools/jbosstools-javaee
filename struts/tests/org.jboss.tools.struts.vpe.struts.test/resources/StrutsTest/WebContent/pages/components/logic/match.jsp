<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic" prefix="logic" %>
<html:html>
<head>
	<title></title>
</head>
<body>
    <%
    	request.setAttribute("authorName1", "James M. Turner");
    	request.setAttribute("authorName2", "Kevin Bedell");
    %>
    <logic:match name="authorName1" value="ames" scope="request">
    	match	</logic:match>
	<logic:match name="authorName2" value="ames" scope="request">
    	match
	</logic:match>
</body>
</html:html>
