<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<html:html>
<head>
	<title></title>
</head>
<body>
    <html:form action="">
    	<nested:checkbox property="prop1">Option1</nested:checkbox><BR>
    	<nested:checkbox property="prop2">Option2</nested:checkbox><BR>
    	<nested:checkbox property="prop3">Option3</nested:checkbox><BR>
    </html:form>
</body>
</html:html>
