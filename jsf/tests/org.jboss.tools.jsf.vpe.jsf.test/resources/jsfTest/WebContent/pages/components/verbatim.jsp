<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
<head>
</head>
<body>

<f:view>
	<h1><h:outputText value="verbatim" /></h1>
	<f:verbatim>
		<table width="50%">
			<tr>
				<td>1</td>
				<td>2</td>
			</tr>
			<tr>
				<td>3</td>
				<td>4</td>
			</tr>
		</table>
	</f:verbatim>
</f:view>
</body>
</html>