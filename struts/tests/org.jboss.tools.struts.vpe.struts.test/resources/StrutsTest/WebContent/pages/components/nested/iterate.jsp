<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<html:html>
<head>
	<title></title>
</head>
<body>
    <html:form action="">
    	<nested:iterate id="gname" indexId="ind" name="givenNames">
		</nested:iterate>
    </html:form>
</body>
</html:html>
