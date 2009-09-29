<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<html>
    <body>

	<f:view>
   	<h:form id="greetingForm">
   			<h1>Test for  JBIDE-4954</h1>
	 	<h:graphicImage value="#{resource['']}" />
  	</h:form>
	</f:view>
    </body>
</html>
