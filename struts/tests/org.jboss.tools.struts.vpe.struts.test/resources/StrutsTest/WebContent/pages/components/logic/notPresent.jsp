<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic" prefix="logic" %>
<html:html>
<head>
	<title></title>
</head>
<body>
    <html:form action="">
    	<logic:notPresent name="name" scope="scope" property="prop">
    		NotPresent
		</logic:notPresent>
    </html:form>
</body>
</html:html>
