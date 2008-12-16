<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<f:loadBundle basename="dataFilterSlider.messages" var="msg"/>
<html>
	<head>
		<title></title>
		<link rel="stylesheet" href="/pages/dataTableAndColumns/main.css"/>
		  <style type="text/css">

            body{
                font: normal 11px tahoma, sans-serif;
            }

            .column{
                width:75px;
                font: normal 11px tahoma, sans-serif;
                text-align:center;
            }

            .column-index{
                width:75px;
                font: normal 11px tahoma, sans-serif;
                text-align:left;
            }

            .list-row3{
                background-color:#ececec;
            }

            .list-row1{
                background-color:#f1f6fd;
            }

            .list-row2{
                background-color:#fff;
            }

            .list-header{
                font: bold 11px tahoma, sans-serif;
                text-align:center;
            }

            .list-table1{
                border:1px solid #bed6f8;
            }

            .list-table2{
                border:1px solid #bed6f8;
            }

            
            
        </style>	
	</head>
	<body>
		<f:view>
	<h:form>
<!-- rich:columns realization -->
			   <rich:dataTable id="trust" value="#{bookList.bookList}" var="book" border="10" bgcolor="red" style="#{table.style}">
			   <f:facet  name="header">
			   		<h:outputText value="#{msg.pageTitle}"/>
			   </f:facet>
			   <f:facet name="footer">
			   		<h:outputText value="#{msg.titleColumnName}"/>
			   </f:facet>
			   <rich:subTable value="#{bookList.bookList}" var="book">
			    	<rich:column sortBy="#{book.price}" id="col2" style="#{table.style}">
					<f:facet name="header">
					<h:outputText value="#{msg.priceColumnName}"/>
					</f:facet>
					<f:facet name="footer">
					<h:outputText value="#{msg.priceColumnName}"/>
					</f:facet>
					<h:outputText value="#{book.price}"/>
				</rich:column>
				<rich:column id="col3" sortBy="#{book.numOfCopies}" visible="true">
					<f:facet name="footer">
					<h:outputText value="#{msg.titleColumnName}"/>
					</f:facet>
					<f:facet name="header">
					<h:outputText value="#{msg.titleColumnName}"/>
					</f:facet>
					<h:outputText value="#{book.numOfCopies}"/>
				</rich:column>
				
			   </rich:subTable>
			   
			</rich:dataTable>
<!-- end realisation -->
			<rich:dataTable id="table1" value="#{bookList.bookList}" var="book" style="#{bookList.stylesForTable}"
							columnClasses="evenRow, oddRow" sortMode="multi" >
					<f:facet name="header">
				 <h:outputText value="#{msg.pageTitle}"/>
				</f:facet>
				<f:facet name="footer">
				 <h:outputText value="#{msg.priceColumnName}"/>
				</f:facet>
				<rich:column sortBy="#{book.price}" id="col2">
					<f:facet name="header">
					<h:outputText value="#{msg.priceColumnName}"/>
					</f:facet>
					<f:facet name="footer">
					<h:outputText value="#{msg.priceColumnName}"/>
					</f:facet>
					<h:outputText value="#{book.price}"/>
				</rich:column>
				<rich:column id="col3" sortBy="#{book.numOfCopies}" visible="false">
					<f:facet name="footer">
					<h:outputText value="#{msg.titleColumnName}"/>
					</f:facet>
					<f:facet name="header">
					<h:outputText value="#{msg.titleColumnName}"/>
					</f:facet>
					<h:outputText value="#{book.numOfCopies}"/>
				</rich:column>
				<rich:column sortBy="#{book.price}" id="col4">
					<f:facet name="header">
					<h:outputText value="#{msg.priceColumnName}"/>
					</f:facet>
					<f:facet name="footer">
					<h:outputText value="#{msg.priceColumnName}"/>
					</f:facet>
					<h:outputText value="#{book.price}"/>
				</rich:column>
				<rich:column id="col5" sortBy="#{book.numOfCopies}" visible="false">
					<f:facet name="footer">
					<h:outputText value="#{msg.titleColumnName}"/>
					</f:facet>
					<f:facet name="header">
					<h:outputText value="#{msg.titleColumnName}"/>
					</f:facet>
					<h:outputText value="#{book.numOfCopies}"/>
				</rich:column>
     		</rich:dataTable>
			</h:form>
     		<h:form>
			   <rich:dataTable id="trust" value="#{bookList.bookList}" var="col" border="10" bgcolor="red" style="float:left;height : 146px; width : 150px;">
			   <f:facet  name="header">
			   		<h:outputText value="#{msg.pageTitle}"/>
			   </f:facet>
			   <f:facet name="footer">
			   		<h:outputText value="#{msg.titleColumnName}"/>
			   </f:facet>
			   	   
			   </rich:dataTable>
		</h:form>
<h:form>
<rich:dataTable value="#{bookList.bookList}" var="cap" rows="5" footerClass="evenRow" headerClass="oddRow" style="#{table.style}">
 <f:facet name="header">
 	   <rich:columnGroup styleClass="btn">
            <rich:column rowspan="2">
                <h:outputText value="State Flag"/>
            </rich:column>
            <rich:column colspan="3">
                <h:outputText value="State Info"/>
            </rich:column>
            <rich:column  breakBefore="true">
                <h:outputText value="State Name"/>
            </rich:column>
            <rich:column>
                <h:outputText value="State Capital"/>
            </rich:column>
             <rich:column>
                <h:outputText value="Time Zone"/>
            </rich:column>
        </rich:columnGroup>
    </f:facet>
    <f:facet name="footer"><h:outputText value="This is footer part" /> </f:facet>
   <rich:column colspan="4">
    	 <f:facet name="header">
		<h:outputText value="Flags"/>
		</f:facet>
		<f:facet name="footer">
		<h:outputText value="Countries"/>
		</f:facet>
		<h:outputText value="Begin" />		
    </rich:column>
    <rich:columnGroup style="" columnClasses="">
	    <rich:column>
	    <f:facet name="footer"><h:outputText value="Unvisible footer" /> </f:facet>
	    <f:facet name="header"><h:outputText value="Unvisible header" /> </f:facet>
	        <h:outputText value="#{cap.name}"/>
	    </rich:column>
        <rich:column>
            <h:outputText value="#{cap.price}"/>
        </rich:column>
        <rich:column>
            <h:outputText value="#{cap.numOfCopies}"/>
        </rich:column>
        <rich:column>
            <h:outputText value="#{cap.numOfCopies}"/>
        </rich:column>
    </rich:columnGroup> 
</rich:dataTable>
</h:form>

<h:form>
 <h:dataTable border="2" columnClasses="evenRow, oddRow" value="#{bookList.bookList}" var="book">
 	
 		<h:column footerClass="oddRow" headerClass="btn">
 		<f:facet name="header"><h:outputText value="Header"/> </f:facet>
 		<f:facet name="footer"><h:outputText value="Footer"/> </f:facet>
 			<h:outputText value="#{book.name}"/>
 		</h:column>
 		<h:column>
 			<h:outputText value="#{book.price}"/>
 		</h:column>
 		<h:column>
 			<h:outputText value="#{book.numOfCopies}" />
 		</h:column>
       </h:dataTable>
       <rich:calendar buttonIcon="/pages/dataTableAndColumns/images/img12.gif"></rich:calendar>
</h:form>
       </f:view>
       <div>
       ${bookList.message}
       </div> 
		</body>	
</html>