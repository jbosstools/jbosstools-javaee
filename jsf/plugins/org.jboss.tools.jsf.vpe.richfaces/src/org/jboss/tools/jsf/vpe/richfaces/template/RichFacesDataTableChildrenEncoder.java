/*******************************************************************************
 * Copyright (c) 2007-2008 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.richfaces.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.RichFacesTemplatesActivator;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeCreatorUtil;
import org.jboss.tools.vpe.editor.template.VpeTemplate;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.SourceDomUtil;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The class {@code RichFacesDataTableChildrenEncoder} encodes children of a {@code rich:*Table}.
 *
 * <BR/>Use this class as follows:
 * <blockquote><pre>
 * RichFacesDataTableChildrenEncoder encoder
 *    = new RichFacesDataTableChildrenEncoder(
 *       creationData, visualDocument, sourceElement, table);
 * encoder.encodeChildren();</pre></blockquote>
 *
 * Method {@link #validateChildren(VpePageContext, Node, nsIDOMDocument, VpeCreationData) validateChildren}
 * MUST be invoked from {@link VpeTemplate#validate(VpePageContext, Node, nsIDOMDocument, VpeCreationData) validate}
 * method of the caller of this class:
 * <blockquote><pre>
 * public void validate(VpePageContext pageContext, Node sourceNode,
 *    nsIDOMDocument visualDocument, VpeCreationData data) {
 *       RichFacesDataTableChildrenEncoder.validateChildren(
 *          pageContext, sourceNode, visualDocument, data);
 *       ...
 * }</pre></blockquote>
 *
 * @author yradtsevich 
 * */
class RichFacesDataTableChildrenEncoder {
	private String firstRowClass = "dr-table-firstrow rich-table-firstrow"; //$NON-NLS-1$
	private String nonFirstRowClass = "dr-table-row rich-table-row"; //$NON-NLS-1$

	/**@param firstRowClass the class of the first row in the table
	 * @param nonFirstRowClass the class of all rows in the table except the first one*/
	public void setRowClasses(final String firstRowClass, final String nonFirstRowClass) {
		this.firstRowClass = firstRowClass;
		this.nonFirstRowClass = nonFirstRowClass;
	}

	/**Non-HTML tag that is used to create temporary containers for {@code rich:subTable} and {@code rich:columnGroup}.*/
	private static final String TAG_SUB_TABLE_OR_COLUMN_GROUP_CONTAINER = "subTableOrColumnGroup-container"; //$NON-NLS-1$
	private final VpeCreationData creationData;
	private final nsIDOMDocument visualDocument;
	private final Element tableSourceElement;
	private final nsIDOMElement tableVisualTag;

	public RichFacesDataTableChildrenEncoder(final VpeCreationData creationData,
			final nsIDOMDocument visualDocument, final Element tableSourceElement,
			final nsIDOMElement tableVisualTag) {
		this.creationData = creationData;
		this.visualDocument = visualDocument;
		this.tableSourceElement = tableSourceElement;
		this.tableVisualTag = tableVisualTag;
	}

	/**
	 * Creates containers in {@code table} for {@code sourceElement}'s children
	 * and adds appropriate objects of {@link VpeChildrenInfo} to {@code creationData}.
	 *
	 * <BR/>It knows about following tags:
	 * {@code rich:column, rich:columns, rich:subTable} and {@code rich:columnGroup}.
	 * <BR/>For any another tag it uses {@link #addElementToTable(Node)} method.
	 * */
	public void encodeChildren() {
		// create an empty childrenInfo. It tells to VpeVisualDomBuilder
		// that it is not necessary to add any child of the sourceElement
		// except ones specified in another vpeChildrenInfo's
		final VpeChildrenInfo childInfo = new VpeChildrenInfo(null);
		creationData.addChildrenInfo(childInfo);

		final List<Node> children = ComponentUtil.getChildren(tableSourceElement);		
		boolean createNewRow = true;
		for (final Node child : children) {
			final String nodeName = child.getNodeName();
			if (nodeName.endsWith(RichFaces.TAG_COLUMN) ||
					nodeName.endsWith(RichFaces.TAG_COLUMNS)) {
				createNewRow |= RichFacesColumnTemplate.isBreakBefore(child);
				addColumnToRow(child, createNewRow);
				createNewRow = false;
			} else if(nodeName.endsWith(RichFaces.TAG_SUB_TABLE)
					|| nodeName.endsWith(RichFaces.TAG_COLUMN_GROUP)) {
				addSubTableOrColumnGroupToTable(child);
				createNewRow = true;
			} else if (!VpeCreatorUtil.isFacet(child)) {
				addElementToTable(child);
				createNewRow = true;
			}
		}
	}

	/**
	 * Makes necessary changes in the table's body after all children of the table have been encoded.
	 */
	public static void validateChildren(final VpePageContext pageContext, final Node sourceNode,
			final nsIDOMDocument visualDocument, final VpeCreationData creationData) {
		final nsIDOMNode visualNode = creationData.getNode();
		fixSubTables(visualNode);
	}

	/**
	 * Creates a container for {@code subTableOrColumnGroupNode} in {@code table}
	 * and adds an appropriate object of {@link VpeChildrenInfo} to {@code creationData}.
	 * <BR/>The container is the tag {@link #TAG_SUB_TABLE_OR_COLUMN_GROUP_CONTAINER}.
	 */
	private nsIDOMElement addSubTableOrColumnGroupToTable(final Node subTableOrColumnGroupNode) {
		final nsIDOMElement subTableOrColumnGroupContainer = visualDocument
				.createElement(TAG_SUB_TABLE_OR_COLUMN_GROUP_CONTAINER);
		tableVisualTag.appendChild(subTableOrColumnGroupContainer);
		final VpeChildrenInfo childInfo = new VpeChildrenInfo(subTableOrColumnGroupContainer);
		childInfo.addSourceChild(subTableOrColumnGroupNode);
		creationData.addChildrenInfo(childInfo);

		return subTableOrColumnGroupContainer;
	}

	private nsIDOMElement currentRow = null;
	private VpeChildrenInfo currentRowChildrenInfo = null;
	private int rowNumber = 0;
	/**
	 * Creates a container for {@code columnNode} in {@code table}
	 * and adds an appropriate object of {@link VpeChildrenInfo} to {@code creationData}.
	 * <BR/>If the parameter {@code createNewRow} is {@code true} then it  creates the
	 * container in a new row.
	 * */
	private nsIDOMElement addColumnToRow(final Node columnNode, final boolean createNewRow) {
		if ( createNewRow || (currentRow == null) ) {
			currentRow = visualDocument.createElement(HTML.TAG_TR);
			tableVisualTag.appendChild(currentRow);
			currentRowChildrenInfo = new VpeChildrenInfo(currentRow);
			creationData.addChildrenInfo(currentRowChildrenInfo);
			rowNumber++;
			if (rowNumber == 1) {
				currentRow.setAttribute(HTML.ATTR_CLASS, firstRowClass);
			} else {
				currentRow.setAttribute(HTML.ATTR_CLASS, nonFirstRowClass);
			}
		}

		currentRowChildrenInfo.addSourceChild(columnNode);
		return currentRow;
	}

	/**
	 * Creates a row container for {@code node} in {@code table}
	 * and adds an appropriate object of {@link VpeChildrenInfo} to {@code creationData}.
	 * <BR/>The container spans the entire row.
	 * */
	private void addElementToTable(final Node node) {
		final nsIDOMElement tr = this.visualDocument.createElement(HTML.TAG_TR);
		tableVisualTag.appendChild(tr);
		final nsIDOMElement td = this.visualDocument.createElement(HTML.TAG_TD);

		td.setAttribute(HTML.ATTR_COLSPAN, HTML.VALUE_COLSPAN_ALL);
		tr.appendChild(td);
		final VpeChildrenInfo childInfo = new VpeChildrenInfo(td);
		childInfo.addSourceChild(node);
		creationData.addChildrenInfo(childInfo);
	}

	/**
	 * Replaces all occurencies of {@link #TAG_SUB_TABLE_OR_COLUMN_GROUP_CONTAINER} tag in
	 * the {@code visualNode} by the tag's child.
	 * @see #addSubTableOrColumnGroupToTable(Node)
	 */
	private static void fixSubTables(final nsIDOMNode visualNode) {
		final nsIDOMElement element = (nsIDOMElement) visualNode;
		final nsIDOMNodeList subTableContainers = element.getElementsByTagName(TAG_SUB_TABLE_OR_COLUMN_GROUP_CONTAINER);
		final long length = subTableContainers.getLength();
		for (int i = 0; i < length; i++) {
			final nsIDOMNode subTableContainer = subTableContainers.item(0);
			final nsIDOMNodeList subTableContainerChildren = subTableContainer.getChildNodes();

			if (subTableContainerChildren == null
					|| subTableContainerChildren.getLength() != 1) {
				final RuntimeException e = new RuntimeException("This is probably a bug. subTable-container should have one inner tag.");//$NON-NLS-1$
				RichFacesTemplatesActivator.getPluginLog().logError(e);
			}

			VisualDomUtil.replaceNodeByItsChildren(subTableContainer);
		}
	}	

	/**
	 * Encoded facets from columns to the table.
	 * 
	 * @param pageContext VpePageContext
	 * @param columnsWithFacets list of the source elements for columns 
	 * @param facetName {@code "header"} or {@code "footer"} or other
	 * @param visualParentTR visal element to put {@code "<tr>"} to
	 * @param visualElementForTD string name for facet cell (usually {@code <td>})
	 * @param cellClass css class for the facet's cell
	 * @param headerClass user defined css class for column's header
	 */
	public void encodeColumnsFacets(VpePageContext pageContext,
			ArrayList<Element> columnsWithFacets, String facetName,
			nsIDOMElement visualParentTR, String visualElementForTD,
			String cellClass, String headerClass) {

		for (Element column : columnsWithFacets) {
			Element facet = SourceDomUtil.getFacetByName(pageContext,column, facetName);
			/*
			 * If facet is null unwanted cells might be added.
			 * Thus do not add TD for such facets.
			 */
			if (null != facet) {
				String classAttribute = facetName + "Class"; //$NON-NLS-1$

				String columnHeaderClass = column.hasAttribute(classAttribute) ? column.getAttribute(classAttribute) : null;
				nsIDOMElement td = visualDocument.createElement(visualElementForTD);
				visualParentTR.appendChild(td);
				String styleClass = ComponentUtil.encodeStyleClass(null, cellClass, headerClass, columnHeaderClass);
				if (!RichFacesColumnTemplate.isVisible(column)) {
					VisualDomUtil.setSubAttribute(td, HTML.ATTR_STYLE,
							HTML.STYLE_PARAMETER_DISPLAY, HTML.STYLE_VALUE_NONE);
				}
				td.setAttribute(HTML.ATTR_CLASS, styleClass);
				td.setAttribute(HTML.ATTR_SCOPE, HTML.TAG_COL);		    	
				if(column.hasAttribute(HTML.ATTR_COLSPAN)) {
					String colspan = column.getAttribute(HTML.ATTR_COLSPAN);
					td.setAttribute(HTML.ATTR_COLSPAN, colspan);
				}
				if (RichFaces.NAME_FACET_HEADER.equals(facetName)) {
					nsIDOMElement icon = RichFacesColumnTemplate.getHeaderIcon(pageContext, column, visualDocument);
					if (icon != null) {
						td.appendChild(icon);
					}
				}
				/*
				 * Add facet source here
				 */
				VpeChildrenInfo childrenInfo = new VpeChildrenInfo(td);
				childrenInfo.addSourceChild(facet);
				creationData.addChildrenInfo(childrenInfo);
			}
		}
	}

	/**
	 * Adds the header or footer facets to the table
	 * 
	 * @param pageContext VpePageContext
	 * @param visualTagForFacet {@code <thead>} or {@code <tfoot>} or any similar
	 * @param innerFacetSourceNode single source elemen``t to be rendered inside facet
	 * @param visualElementForTD string name for facet cell (usually {@code <td>})
	 * @param facetTHeadClass css class for {@code <thead>} or {@code <tfoot>} or any similar
	 * @param firstRowClass css class for the first facet row
	 * @param rowClass css class for the facet's row
	 * @param cellClass css class for the facet's cell
	 */
	public void encodeTableFacets(VpePageContext pageContext, 
			nsIDOMElement visualTagForFacet, Element innerFacetSourceNode, 
			String visualElementForTD, String facetTHeadClass, 
			String firstRowClass, String rowClass, String cellClass) {

		if (null == innerFacetSourceNode) {
			RichFacesTemplatesActivator.getDefault().logError("Source element to be rendered inside facet is 'null' !"); //$NON-NLS-1$
		}

		boolean isColumnGroup = innerFacetSourceNode.getNodeName().endsWith(RichFaces.TAG_COLUMN_GROUP);
		boolean isSubTable = innerFacetSourceNode.getNodeName().endsWith(RichFaces.TAG_SUB_TABLE);
		if(isColumnGroup) {
			RichFacesColumnGroupTemplate.DEFAULT_INSTANCE.encodeSubTable(pageContext, creationData, innerFacetSourceNode, visualDocument, visualTagForFacet);
		} else if(isSubTable) {
			RichFacesSubTableTemplate.DEFAULT_INSTANCE.encodeSubTable(pageContext, creationData, innerFacetSourceNode, visualDocument, visualTagForFacet);
		} else {
			nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
			visualTagForFacet.appendChild(tr);
			// TODO facetTHeadClass should be applied only once
			// TODO rowClass is never applied
			String styleClass = ComponentUtil.encodeStyleClass(null, firstRowClass, facetTHeadClass, null);
			if(styleClass!=null) {
				tr.setAttribute(HTML.ATTR_CLASS, styleClass);
			}
			String style = ComponentUtil.getHeaderBackgoundImgStyle();
			tr.setAttribute(HTML.ATTR_STYLE, style);

			nsIDOMElement td = visualDocument.createElement(visualElementForTD);
			tr.appendChild(td);
			// TODO facetTHeadClass should be applied only once
			styleClass = ComponentUtil.encodeStyleClass(null, cellClass, facetTHeadClass, null);
			if(styleClass!=null) {
				td.setAttribute(HTML.ATTR_CLASS, styleClass);
			}
			// the cell spans the entire row
			td.setAttribute(HTML.ATTR_COLSPAN, HTML.VALUE_COLSPAN_ALL);
			td.setAttribute(HTML.ATTR_SCOPE, HTML.TAG_COLGROUP);
			VpeChildrenInfo child = new VpeChildrenInfo(td);
			child.addSourceChild(innerFacetSourceNode);
			creationData.addChildrenInfo(child);
		}
	}

	/**
	 * Encodes the whole table's header and columns' header
	 * 
	 * @param pageContext VpePageContext
	 * @param visualParentForFacetTHead visual node to put facet's {@code <thead>} or {@code <tfoot>} or similar
	 * @param visualTagForFacetTHead what tag will be rendered: {@code <thead>} or {@code <tfoot>} or other
	 * @param visualElementForTD string name for facet cell (usually {@code <td>})
	 * @param facetName {@code "header"} or {@code "footer"} or other
	 * @param customFacetClass user's class for columns' headers/footer
	 * @param facetTHeadClass user's class for table's headers/footer
	 * @param firstRowClass css class for the first row in table's headers/footer
	 * @param rowClass css class for non-first row in table's headers/footer
	 * @param cellClass css class for the facet's cell
	 */
	public void encodeTableHeader(VpePageContext pageContext,
			nsIDOMElement visualParentForFacetTHead, 
			String visualTagForFacetTHead, String visualElementForTD, 
			String facetName, String customFacetClass, String facetTHeadClass, 
			String firstRowClass, String rowClass, String cellClass) {

		Element facetSourceElement = SourceDomUtil.getFacetByName(pageContext,
				tableSourceElement, facetName);

		ArrayList<Element> columns = RichFaces.getColumns(tableSourceElement);
		int columnsLength = RichFaces.getColumnsCount(tableSourceElement, columns);

		Map<String, List<Node>> facetChildren = VisualDomUtil.findFacetElements(facetSourceElement, pageContext);
		boolean headerJsfElementPresents = facetChildren.get(VisualDomUtil.FACET_JSF_TAG).size() > 0;
		boolean hasColumnWithFacets = RichFaces.hasColumnWithFacet(columns, facetName);
		if(headerJsfElementPresents || hasColumnWithFacets) {
			nsIDOMElement createdVisualTagForFacetTHead = null;
			if ((null == visualTagForFacetTHead) || "".equalsIgnoreCase(visualTagForFacetTHead)) { //$NON-NLS-1$
				/*
				 * For subtables mostly:
				 * Header won't be placed into separate thead or tfoot tag.
				 * Then put it to the parent's visual node.
				 */
				createdVisualTagForFacetTHead = visualParentForFacetTHead;
			} else {
				createdVisualTagForFacetTHead = visualDocument.createElement(
						visualTagForFacetTHead); //thead or tfoot
				visualParentForFacetTHead.appendChild(createdVisualTagForFacetTHead);
			}
			String facetClass = null; 
			if (tableSourceElement.hasAttribute(facetName + "Class")) { //$NON-NLS-1$
				facetClass = tableSourceElement.getAttribute(facetName + "Class"); //$NON-NLS-1$
			}
			/*
			 * Encode facet for the whole table 	
			 */
			if(headerJsfElementPresents) {
				Element node = (Element) facetChildren.get(VisualDomUtil.FACET_JSF_TAG).get(0);
				encodeTableFacets(pageContext,
						createdVisualTagForFacetTHead, node, visualElementForTD, 
						facetTHeadClass, firstRowClass, rowClass, cellClass);
			}
			/*
			 * Encode facets for columns
			 */
			if(hasColumnWithFacets) {
				nsIDOMElement visualParentTR = visualDocument.createElement(HTML.TAG_TR);
				createdVisualTagForFacetTHead.appendChild(visualParentTR);
				String styleClass = ComponentUtil.encodeStyleClass(null, customFacetClass, null, facetClass);
				if(styleClass!=null) {
					visualParentTR.setAttribute(HTML.ATTR_CLASS, styleClass);
				}
				encodeColumnsFacets(pageContext,
						columns, facetName, visualParentTR, visualElementForTD, 
						cellClass, facetClass);
			}
		}
	}

	/**
	 * Encodes the whole table's footer and columns' footer
	 * 
	 * @param pageContext VpePageContext
	 * @param visualParentForFacetTHead visual node to put facet's {@code <thead>} or {@code <tfoot>} or similar
	 * @param visualTagForFacetTHead what tag will be rendered: {@code <thead>} or {@code <tfoot>} or other
	 * @param visualElementForTD string name for facet cell (usually {@code <td>})
	 * @param facetName {@code "header"} or {@code "footer"} or other
	 * @param customFacetClass user's class for columns' headers/footer
	 * @param facetTHeadClass user's class for table's headers/footer
	 * @param firstRowClass css class for the first row in table's headers/footer
	 * @param rowClass css class for non-first row in table's headers/footer
	 * @param cellClass css class for the facet's cell
	 */
	public void encodeTableFooter(VpePageContext pageContext,
			nsIDOMElement visualParentForFacetTHead, 
			String visualTagForFacetTHead, String visualElementForTD, 
			String facetName, String customFacetClass, String facetTHeadClass, 
			String firstRowClass, String rowClass, String cellClass) {

		Element facetSourceElement = SourceDomUtil.getFacetByName(pageContext,
				tableSourceElement, facetName);

		ArrayList<Element> columns = RichFaces.getColumns(tableSourceElement);
		int columnsLength = RichFaces.getColumnsCount(tableSourceElement, columns);

		Map<String, List<Node>> facetChildren = VisualDomUtil.findFacetElements(facetSourceElement, pageContext);
		boolean headerJsfElementPresents = facetChildren.get(VisualDomUtil.FACET_JSF_TAG).size() > 0;
		boolean hasColumnWithFacets = RichFaces.hasColumnWithFacet(columns, facetName);
		if(headerJsfElementPresents || hasColumnWithFacets) {
			nsIDOMElement createdVisualTagForFacetTHead = null;
			if ((null == visualTagForFacetTHead) || "".equalsIgnoreCase(visualTagForFacetTHead)) { //$NON-NLS-1$
				/*
				 * For subtables mostly:
				 * Header won't be placed into separate thead or tfoot tag.
				 * Then put it to the parent's visual node.
				 */
				createdVisualTagForFacetTHead = visualParentForFacetTHead;
			} else {
				createdVisualTagForFacetTHead = visualDocument.createElement(
						visualTagForFacetTHead); //thead or tfoot
				visualParentForFacetTHead.appendChild(createdVisualTagForFacetTHead);
			}
			String facetClass = null; 
			if (tableSourceElement.hasAttribute(facetName + "Class")) { //$NON-NLS-1$
				facetClass = tableSourceElement.getAttribute(facetName + "Class"); //$NON-NLS-1$
			} 
			/*
			 * Encode facets for columns first
			 */
			if(hasColumnWithFacets) {
				nsIDOMElement visualParentTR = visualDocument.createElement(HTML.TAG_TR);
				createdVisualTagForFacetTHead.appendChild(visualParentTR);
				String styleClass = ComponentUtil.encodeStyleClass(null, customFacetClass, null, facetClass);
				if(styleClass!=null) {
					visualParentTR.setAttribute(HTML.ATTR_CLASS, styleClass);
				}
				encodeColumnsFacets(pageContext, 
						columns, facetName, visualParentTR, visualElementForTD, 
						cellClass, facetClass);
			}
			/*
			 * Encode facet for the whole table 	
			 */
			if(headerJsfElementPresents) {
				Element node = (Element) facetChildren.get(VisualDomUtil.FACET_JSF_TAG).get(0);
				encodeTableFacets(pageContext,
						createdVisualTagForFacetTHead, node, visualElementForTD, 
						facetTHeadClass, firstRowClass, rowClass, cellClass);
			}
		}
	}
}
