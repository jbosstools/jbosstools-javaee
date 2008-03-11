<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<html:html>
<head>
	<title></title>
</head>
<body>
    <html:form action="">
    	<html:select property="colors" size="6" multiple="true">
    		<html:options collection="ColorCollection" property="value" labelProperty="label"/>		</html:select>
    </html:form>
</body>
</html:html>
