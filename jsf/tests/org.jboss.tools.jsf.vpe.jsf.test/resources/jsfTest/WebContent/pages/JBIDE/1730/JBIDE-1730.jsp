<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<f:loadBundle var="mgs" basename="demo.Messages" />

<html>
	<head>
		<title> Test h:pannelGrid and h:pannelGroup in VPE </title>
		<style type="text/css">
			.myStyle0 {background: aqua;}
			.myStyle1 {background: yellow;}
			.myStyle2 {background: lime;}
		</style>
	</head>
	<body>
		<f:view>
		  <h:form>
		  	<h:panelGrid
				id="gridId1"
				dir="RTL"
				columns="2" border="5"  width="250"
				rules="all" frame="above"
				cellpadding="4" cellspacing="6" 
				bgcolor="silver"
				style="" 
				styleClass=""
				captionStyle="color : red;" captionClass="myStyle0"
				columnClasses="" rowClasses="" 
				headerClass="" footerClass="" >
			  <f:facet name="caption">
			  	<h:outputText value="Caption"/>
			  </f:facet>
			  <f:facet name="header">
			  	<h:panelGroup 
			  		id="groupId1_1"
			  		layout="block"
			  		style=""
			  		styleClass="" >
			      	<h:outputText value="Header"/>
			      	<br/>
			      	<h:graphicImage value="/pictures/pic1.jpg" />
			    </h:panelGroup>
			  </f:facet>
			  <h:panelGroup
			  		id="groupId1_2"
			    	layout="block"
			    	style=""
			    	styleClass="" >
			  	<h:outputLabel for="username1" value="name" />
			  </h:panelGroup>
			  <h:panelGroup
			  		id="groupId1_3"
			    	layout="block"
			    	style=""
			    	styleClass="" >
			  	<h:inputText id="username1" value="#{user.name}" />
			  </h:panelGroup>
			  <h:outputLabel for="surname1" value="surname" />
			  <h:inputText id="surname1" value="#{user.surname}" />
			  <f:facet name="footer">
			    <h:panelGroup 
			    	id="groupId1_4"
			    	layout="block"
			    	style=""
			    	styleClass="" >
			      <h:outputText value="Footer"/>
			      <br/>
			      <h:graphicImage value="/pictures/pic1.jpg"/>
			    </h:panelGroup>
			  </f:facet>
			</h:panelGrid>
		  </h:form>
		</f:view>
	</body>	
</html>  
