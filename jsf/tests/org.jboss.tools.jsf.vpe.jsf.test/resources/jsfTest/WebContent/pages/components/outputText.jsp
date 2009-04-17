<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
<style type="text/css">
	span {
		color: yellow;
		background-color: green;
	}
</style>
</head>
<body>

<f:view>
	<div id="outputText0">
		<h:outputText value="outputText0"/>
	</div>
	<h:outputText value="outputText1" id="outputText1" />
	<h:outputText value="outputText2" id="outputText2" escape="true" />
	<h:outputText value="outputText3" id="outputText3" escape="false" />
	<div id="outputTextGroup">
		<h:outputText value="outputText4 " id="outputText"/>
		<h:outputText value="outputText5 " dir="LTR"/>
		<h:outputText value="outputText6 " lang="java"/>
		<h:outputText value="outputText7 " style="style"/>
		<h:outputText value="outputText8 " styleClass="styleClass"/>
		<h:outputText value="outputText9 " title="title"/>
	</div>
</f:view>
</body>
</html>