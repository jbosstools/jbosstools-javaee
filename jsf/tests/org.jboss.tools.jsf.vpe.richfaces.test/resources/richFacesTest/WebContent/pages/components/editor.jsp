<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich" %>

<html>
<head>
<style type="text/css">
	.blue-border {
		color:blue;
	}
</style>
</head>
<body>
<f:view>
<h:form>
	<rich:editor id="richEditor" width="391" height="347" styleClass="blue-border" style="border: 5px dotted;"/>
</h:form>
</f:view>

</body>
</html>
