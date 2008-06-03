<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>
<html:html>
<head>
	<title></title>
</head>
<body>
   	<tiles:insert attribute="header">
   		<tiles:put name="title" beanName="title" direct="true"/>
   	</tiles:insert>
</body>
</html:html>
