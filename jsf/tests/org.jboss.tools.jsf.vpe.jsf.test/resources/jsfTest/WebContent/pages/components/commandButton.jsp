<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<DIV id="div1">
		<h:commandButton value="commandButton1" />
		<h:commandButton value="commandButton2" type="button" />
		<h:commandButton value="commandButton3" type="reset" />
		<h:commandButton value="commandButton4" type="submit" />
		<h:commandButton value="commandButton5" image="" />
		<h:commandButton value="commandButton6" type="" />
</DIV>

<DIV id="div2">
<h:commandButton value="cb1"> </h:commandButton>
<h:commandButton value="cb2" type="button"> </h:commandButton>
<h:commandButton value="cb3" type="reset"> </h:commandButton>
<h:commandButton value="cb4" type="submit"> </h:commandButton>
<h:commandButton value="cb5" image=""> </h:commandButton>
<h:commandButton value="cb6" type=""> </h:commandButton>
</DIV>

<DIV id="div3">
<h:commandButton value="cb1"> with some text </h:commandButton>
<h:commandButton value="cb2" type="button"> with some text </h:commandButton>
<h:commandButton value="cb3" type="reset"> with some text </h:commandButton>
<h:commandButton value="cb4" type="submit"> with some text </h:commandButton>
<h:commandButton value="cb5" image=""> with some text </h:commandButton>
<h:commandButton value="cb6" type=""> with some text </h:commandButton>
</DIV>

</f:view>
</body>
</html>