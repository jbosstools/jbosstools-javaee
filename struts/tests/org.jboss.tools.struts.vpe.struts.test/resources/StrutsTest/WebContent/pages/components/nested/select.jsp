<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<html:html>
<head>
	<title></title>
</head>
<body>
    <nested:nest>
    	Gender: <nested:select property="gender">
    		<html:option value="MALE">Male</html:option>
    		<html:option value="FEMALE">Female</html:option>
    	</nested:select>
    </nested:nest>
</body>
</html:html>
