<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich" %>
<f:loadBundle basename="dataTable.columns.messages" var="msg"/>
<html>
	<head>
		<title>Scrollable Data Tabel</title>
		<link rel="stylesheet" href="${request.contextPath}pages/scrollableDataTable/main.css"/>
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
	<body style="#{table.style}">
		<f:view>
			<rich:scrollableDataTable id="table1" value="#{bookList.bookList}" 
			var="book" width="267" height="181" 
			selectedClass="evenRow" rows="3"
			rowClasses="evenRow, oddRow"
			style="#{bookList.stylesForTable}">
			  <f:facet  name="header">
			   		<h:outputText value="#{msg.pageTitle}"/>
			   </f:facet>
			   <f:facet name="footer">
			   		<h:outputText value="#{msg.titleColumnName}"/>
			   </f:facet>
			    	<rich:column sortBy="#{book.price}" id="col2">
					<f:facet name="header">
					<h:outputText value="#{msg.priceColumnName}"/>
					</f:facet>
					<f:facet name="footer">
					<h:outputText value="#{msg.priceColumnName}"/>
					</f:facet>
					<h:outputText value="#{requestbean}"/>
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
			</rich:scrollableDataTable>
			<rich:calendar style="#{bookList.stylesForTable}">
				
			</rich:calendar>
		</f:view>
	</body>	
</html>  
