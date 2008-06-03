<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean" prefix="bean" %>
<html:html>
<head>
	<title></title>
</head>
<body>
    <bean:message bundle="demo.pythonquotes" key="ni"/>
    <bean:message bundle="demo.pythonquotes" name="rq" property="randomQuoteName"/>
</body>
</html:html>
