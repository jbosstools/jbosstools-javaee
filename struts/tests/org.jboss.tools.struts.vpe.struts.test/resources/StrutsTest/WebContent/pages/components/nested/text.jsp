<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<html:html>
<head>
	<title></title>
</head>
<body>
    <nested:nest property="address">
    	Street 1: <nested:text property="street1"/><BR>
    	Street 2: <nested:text property="street2"/><BR>
    	City: <nested:text property="city"/>
    </nested:nest>
</body>
</html:html>
