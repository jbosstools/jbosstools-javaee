<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
 <head>
  <title>JSF Test Page</title>
 </head>
 <body>
   <f:view>
     <h1>
      <h:outputText value="#{mySearchableBean.sFoo2()}"/>
      <h:outputText value="#{sFoo}"/>
      <h:outputText value="#{sFoo1}"/>
      <h:outputText value="#{sFoo + mySearchableBean.sFoo2()}"/>
     </h1>
   </f:view>
 </body>
</html> 