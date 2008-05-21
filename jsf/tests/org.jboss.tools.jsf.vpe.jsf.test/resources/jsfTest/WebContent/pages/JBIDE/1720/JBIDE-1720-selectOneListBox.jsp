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
			<h:selectOneListbox id="sub2" value="#{user.name}" 
				disabled="true" size="3"
				disabledClass="myStyle" enabledClass="myStyle1"
				style="font-size: large;" styleClass="myStyle2"	
				readonly="false" >
			  <f:selectItem id="it1" itemLabel="News" itemValue="1" />
			  <f:selectItem id="it2" itemLabel="Sports" itemValue="2" />
			  <f:selectItem id="it3" itemLabel="Music" itemValue="3" />
			  <f:selectItem id="it4" itemLabel="Java" itemValue="4" />
			  <f:selectItem id="it5" itemLabel="Web" itemValue="5" />
			</h:selectOneListbox>
		  </h:form>
		</f:view>
	</body>	
</html>  
