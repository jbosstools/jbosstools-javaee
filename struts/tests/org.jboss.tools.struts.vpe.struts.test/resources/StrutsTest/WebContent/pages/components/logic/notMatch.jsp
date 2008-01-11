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
    <logic:notMatch name="authorName1" value="qwerty" scope="request">
    	notMatch	</logic:notMatch>
	<logic:notMatch name="authorName2" value="qwerty" scope="request">
    	notMatch
	</logic:notMatch>
</body>
</html:html>
