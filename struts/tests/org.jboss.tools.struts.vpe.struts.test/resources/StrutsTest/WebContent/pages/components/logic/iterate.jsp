<%@ taglib uri="/WEB-INF/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean" prefix="bean" %>
<%@ page import="java.util.HashMap" %>
<html:html>
<head>
	<title>Logic iterate sample code</title>
</head>
<body>
    <%
    	HashMap<String, String[]> h = new HashMap<String, String[]>();
    	String jameses[] = {"Joyce", "Thurber", "Kirk", "Cameron", "Monroe"};
    	String kevins[] = {"Spacey", "Bacon"};
    	String bruces[] = {"Willis", "The Shark", "Cockburn"};
    	h.put("James", jameses);
    	h.put("Kevin", kevins);
    	h.put("Bruce", bruces);
    	request.setAttributes("givenNames", h);
    %>
    <logic:iterate id="gname" indexId="ind" name="givenNames">
    	<bean:write name="ind" />. <bean:write name="gname" property="key" /><BR>
    	<logic:iterate id="lname" name="gname" property="value" length="4" offset="1">
    		<bean:write name="lname" /><br>
    	</logic:iterate>
    </logic:iterate>
</body>
</html:html>
