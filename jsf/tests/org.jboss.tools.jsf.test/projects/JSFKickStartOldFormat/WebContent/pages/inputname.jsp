<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<f:loadBundle basename="demo.bundle.Messages" var="Message"/>

<HTML>
    <HEAD> <title>Input Name Page</title> </HEAD>
    <body bgcolor="white">
	<f:view>
		<h1><h:outputText value="#{Message.inputname_header}"/></h1>
		<h:messages style="color: red"/>
    	<h:form id="helloForm">
    		
    		<h:outputText value="#{Message.prompt}"/>
    		<h:inputText id="userName" value="#{nameBean.userName}" required="true">
	    		<f:validateLength minimum="2" maximum="20"/>
    		</h:inputText>
	 	<h:commandButton id="submit" action="greeting" value="Say Hello" />
    	</h:form>
   		<h:outputText value="#{second['age']}"/>
   		<h:outputText value="#{second['age1']}"/>
	</f:view>
    </body>
</HTML>  
