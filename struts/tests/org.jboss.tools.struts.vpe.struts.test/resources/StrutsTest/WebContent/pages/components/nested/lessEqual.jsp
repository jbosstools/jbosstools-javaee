<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<html:html>
<head>
	<title></title>
</head>
<body>
    <html:form action="">
    	<nested:lessEqual value="value1" property="prop1">
    		LessEqual
		</nested:lessEqual>
    </html:form>
</body>
</html:html>
