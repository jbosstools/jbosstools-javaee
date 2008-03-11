<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<html:html>
<head>
	<title></title>
</head>
<body>
    <nested:nest property="address">
    	Current nesting is: <nested:writeNesting/><BR>
    </nested:nest>
</body>
</html:html>
