<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<f:loadBundle var="msg" basename="demo.Messages" />

<html>
	<head>
		<title> Test h:selectManyListbox in VPE </title>
		<style type="text/css">
			.myStyle0 {background: aqua; }
			.myStyle1 {background: yellow;}
			.myStyle2 {background: green; font-style: italic;}
		</style>
	</head>
	<body>
		<f:view>
		  	<h:selectManyListbox id="sub1" value="#{user.name}"
		  		disabled="false"
				dir="LTR" size="3"
				style="color: red; font-size: large;"
				styleClass="myStyle0"
				enabledClass="myStyle1"	disabledClass="myStyle2" >
	  			<!--  -->  <f:selectItem id="it1_2" itemLabel="Sports" itemValue="2" />
			</h:selectManyListbox>
			</f:view>
	</body>	
</html>  
