<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<html:html>
<head>
	<title></title>
</head>
<body>
    <html:form action="">
    	<html:select property="custId">
    		<html:optionsCollection property="customers" label="name" value="custId"/>
    	</html:select>
    </html:form>
</body>
</html:html>
