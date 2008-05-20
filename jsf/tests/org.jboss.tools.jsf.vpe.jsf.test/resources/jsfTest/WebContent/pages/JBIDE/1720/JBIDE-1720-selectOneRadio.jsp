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
		  <h:selectOneRadio id="sub1" value="#{user.selects}" 
				layout="pageDirection" border="10" dir="ltr"
				disabled="true"
				disabledClass="myStyle1" enabledClass="myStyle"
				readonly="true" style="color: red; background: pink;" styleClass="myStyle2"  >
			  <f:selectItem id="item1" itemLabel="<b>News</b>" itemValue="1" 
			  	escape="false" />
			  <f:selectItem id="item2" itemLabel="Sports" itemValue="2" />
			  <f:selectItem id="item3" itemLabel="Music" itemValue="3" />
			  <f:selectItem id="item4" itemLabel="Java" itemValue="4" />
			  <f:selectItem id="item5" itemLabel="Web" itemValue="5" />
			</h:selectOneRadio>
		</f:view>
	</body>	
</html>  
