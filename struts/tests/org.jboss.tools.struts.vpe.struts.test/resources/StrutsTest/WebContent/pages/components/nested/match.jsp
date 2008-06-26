<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<html:html>
<head>
	<title></title>
</head>
<body>
    <html:form action="">
    	<nested:match value="value1" property="property1">
    		Match
		</nested:match>
    </html:form>
</body>
</html:html>
