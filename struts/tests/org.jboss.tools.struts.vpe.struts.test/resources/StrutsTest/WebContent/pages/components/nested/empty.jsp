<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<html:html>
<head>
	<title></title>
</head>
<body>
    <html:form action="">
    	<nested:empty name="abc" property="value1">
    		Empty
		</nested:empty>
    </html:form>
</body>
</html:html>
