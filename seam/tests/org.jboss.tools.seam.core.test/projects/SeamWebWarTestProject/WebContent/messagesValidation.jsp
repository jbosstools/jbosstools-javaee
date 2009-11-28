<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<f:loadBundle var="Message" basename="Messages"/>

<h:outputText value="#{Message.header}"/>