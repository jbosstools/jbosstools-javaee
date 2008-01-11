<%@ taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>

<rich:inputNumberSlider/>

<rich:inputNumberSlider minValue="-10" maxValue="200" value="40"/>
	
</f:view>
</body>
</html>