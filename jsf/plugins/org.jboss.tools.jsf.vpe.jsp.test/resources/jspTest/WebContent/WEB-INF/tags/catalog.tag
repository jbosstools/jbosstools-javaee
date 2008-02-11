<jsp:directive.taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:directive.taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:directive.attribute name="bookDB" required="true" type="com.sun.bookstore3.database.BookDB" %>
<jsp:directive.attribute name="color" required="true" />
<jsp:directive.attribute name="normalPrice" fragment="true" />
<jsp:directive.attribute name="onSale" fragment="true" />
<jsp:directive.variable name-given="price" />
<jsp:directive.variable name-given="salePrice" />

<center>
<table summary="layout">
<c:forEach var="book" begin="0" items="${bookDB.books}">
  <tr>
  <c:set var="bookId" value="${book.bookId}" />
  <td bgcolor="${color}"> 
      <c:url var="url" value="/bookdetails" >
        <c:param name="bookId" value="${bookId}" />
      </c:url>
      <a href="${url}"><strong>${book.title}&nbsp;</strong></a></td> 
  <td bgcolor="${color}" rowspan=2>
    
  <c:set var="salePrice" value="${book.price * .85}" />
  <c:set var="price" value="${book.price}" />
    <c:choose>
      <c:when test="${book.onSale}" >
        <jsp:invoke fragment="onSale" />
      </c:when>
      <c:otherwise>
        <jsp:invoke fragment="normalPrice" />
      </c:otherwise>
    </c:choose>
  &nbsp;</td> 
  <td bgcolor="${color}" rowspan=2> 
  <c:url var="url" value="/bookcatalog" >
    <c:param name="Add" value="${bookId}" />
  </c:url> 
  <p><strong><a href="${url}">&nbsp;<fmt:message key="CartAdd"/>&nbsp;</a></td></tr> 
  <tr> 
  <td bgcolor="#ffffff"> 
  &nbsp;&nbsp;<fmt:message key="By"/> <em>${book.firstName}&nbsp;${book.surname}</em></td></tr>
</c:forEach>
</table>
</center>


