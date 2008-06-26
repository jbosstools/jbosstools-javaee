<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<html:html>
<head>
	<title></title>
</head>
<body>
    <html:form action="">
    	<nested:hidden property="prop1" value="value1">
    		Hidden
		</nested:hidden>
    </html:form>
</body>
</html:html>
