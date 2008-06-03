<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
	<head>
		<title></title>
		<style type="text/css">
			.myStyle {background: aqua;}
			.myStyle1 {background: yellow;}
			.myStyle2 {background: green; font-style: italic;}
		</style>
	</head>
	<body>
		<f:view>
		   <h:form>
			<h:selectOneMenu id="subs2" value="#{user.name}" 
				dir="rtl" disabled="false"
				disabledClass="myStyle" enabledClass="myStyle1"
				style="font-size: large;" styleClass="myStyle2"
				readonly="false" > 
			  <f:selectItem id="ite1" itemLabel="News" itemValue="1" />
			  <f:selectItem id="ite2" itemLabel="Sports" itemValue="2" />
			  <f:selectItem id="ite3" itemLabel="Music" itemValue="3" />
			  <f:selectItem id="ite4" itemLabel="Java" itemValue="4" />
			  <f:selectItem id="ite5" itemLabel="Web" itemValue="5" />
			</h:selectOneMenu>
		  </h:form>
		</f:view>
	</body>	
</html>  
