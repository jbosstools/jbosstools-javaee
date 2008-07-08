<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>



<f:loadBundle var="Message" basename="demo.Messages" />

<html>
<head>
<title>Input User Name Page</title>
<style type="text/css">
h3 {
	background: green;
	color: red;
}

.post-info {
	padding-top: 3px;
	margin-left: 4px;
	color: #00ff00;
}

.post-info a {
	color: #bd4200;
}

</style>
</head>
<body>
	<h3>Hello people</h3>
<f:view>
	<h:form id="greetingForm"
		style="FONT-STYLE: italic; FONT-WEIGHT: bold;">
	<h1><h:outputText value="#{Message.header}" /></h1>

	<p class="post-info">Posted by
		<a href="index.html">erwin</a> | 
		<h:outputLink value="/index.jsp">Home page</h:outputLink> | 
		<h:commandLink value="123456789">abcdef</h:commandLink>
	</p>

	<h:messages style="color: red" />
		<h:outputText value="#{Message.prompt_message}" />
		<h:inputText value="#{user.name}" required="true">
			<f:validateLength maximum="30" minimum="3" />
		</h:inputText>
		
		 <br/>
			
		<h:commandButton action="hello" value="Say Hello!" />
	</h:form>
</f:view>
</body>
</html>