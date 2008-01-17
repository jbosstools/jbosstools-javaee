<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic" prefix="logic" %>
<html:html>
<head>
	<title></title>
</head>
<body>
    <html:form action="">
    	<logic:present name="name" scope="scope" property="prop">
    		Present
		</logic:present>
    </html:form>
</body>
</html:html>
