/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.richfaces.template;

import static org.jboss.tools.vpe.xulrunner.util.XPCOM.queryInterface;

import java.util.ArrayList;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.RichFacesTemplatesActivator;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Displays template for scrollableDataTable
 * 
 * @author dsakovich@exadel.com
 * 
 */
public class RichFacesScrollableDataTableTemplate extends VpeAbstractTemplate {

	private static final String COLUMN = ':' + RichFaces.TAG_COLUMN;
	private static final String COLUMNS = ':' + RichFaces.TAG_COLUMNS;
	private static final String DEFAULT_HEIGHT = "500px"; //$NON-NLS-1$
	private static final String DEFAULT_WIDTH = "700px"; //$NON-NLS-1$
	private static final String CSS_STYLE_PATH = "scrollableDataTable/scrollableDataTable.css"; //$NON-NLS-1$
	private static final String COMPONENT_NAME = "richFacesDataTable"; //$NON-NLS-1$

	private static final String CSS_DR_TABLE = "dr-table"; //$NON-NLS-1$
	private static final String CSS_DR_TABLE_HIDDEN = "dr-table-hidden"; //$NON-NLS-1$
	private static final String CSS_RICH_SDT = "rich-sdt"; //$NON-NLS-1$
	private static final String CSS_RICH_SDT_HEADER_CELL = "rich-sdt-header-cell"; //$NON-NLS-1$
	private static final String CSS_RICH_SDT_HEADER_ROW = "rich-sdt-header-row"; //$NON-NLS-1$
	private static final String CSS_RICH_SDT_COLUMN_CELL = "rich-sdt-column-cell"; //$NON-NLS-1$
	private static final String CSS_RICH_SDT_FOOTER_CELL = "rich-sdt-footer-cell"; //$NON-NLS-1$
	private static final String CSS_RICH_SDT_FOOTER_ROW = "rich-sdt-footer-row"; //$NON-NLS-1$
	private static final String CSS_RICH_SDT_HSEP = "rich-sdt-hsep"; //$NON-NLS-1$

	private static final int NUM_ROW = 5;
	private static final String TAG_MAIN_TABLE_WRAPPER = "mainTable-wrapper"; //$NON-NLS-1$

	/**
	 * Creates a node of the visual tree on the node of the source tree. This
	 * visual node should not have the parent node This visual node can have
	 * child nodes.
	 * 
	 * @param pageContext
	 *            Contains the information on edited page.
	 * @param sourceNode
	 *            The current node of the source tree.
	 * @param visualDocument
	 *            The document of the visual tree.
	 * @return The information on the created node of the visual tree.
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {

		Element sourceElement = (Element) sourceNode;

		String width = DEFAULT_WIDTH;	
		if (sourceElement.hasAttribute(HTML.ATTR_WIDTH)) {
			String widthAttrVal = sourceElement.getAttribute(HTML.ATTR_WIDTH);
			width = VpeStyleUtil.addPxIfNecessary(widthAttrVal);
		}
		
		String height = DEFAULT_HEIGHT;
		if (sourceElement.hasAttribute(HTML.ATTR_HEIGHT)) {
			String heightAttrVal = sourceElement.getAttribute(HTML.ATTR_HEIGHT);
			height = VpeStyleUtil.addPxIfNecessary(heightAttrVal);
		}
		
			
		nsIDOMElement div = visualDocument
				.createElement(HTML.TAG_DIV);

		div.setAttribute(HTML.ATTR_CLASS, CSS_DR_TABLE_HIDDEN);

		String divStyle = HTML.ATTR_WIDTH + Constants.COLON
				+ width + Constants.SEMICOLON
				+ HTML.ATTR_HEIGHT + Constants.COLON
				+ height + ";overflow:auto;"; //$NON-NLS-1$
		VpeCreationData creationData = new VpeCreationData(div);

		div.setAttribute(HTML.ATTR_STYLE, divStyle);

		nsIDOMElement mainTable = visualDocument
				.createElement(HTML.TAG_TABLE);

		mainTable.removeAttribute(HTML.ATTR_HEIGHT);

		nsIDOMElement mainTableWrapper = visualDocument.createElement(TAG_MAIN_TABLE_WRAPPER);
		mainTableWrapper.appendChild(mainTable);
		div.appendChild(mainTableWrapper);

		ComponentUtil.setCSSLink(pageContext, CSS_STYLE_PATH, COMPONENT_NAME);
		String tableClass = CSS_DR_TABLE + Constants.WHITE_SPACE + CSS_RICH_SDT;
		if (sourceElement.hasAttribute(RichFaces.ATTR_STYLE_CLASS)) {
			tableClass += Constants.WHITE_SPACE + sourceElement.getAttribute(RichFaces.ATTR_STYLE_CLASS);
		}
		mainTable.setAttribute(HTML.ATTR_CLASS, tableClass);

		// Encode colgroup definition.
		ArrayList<Element> columns = getColumns(sourceElement);
		int columnsLength = getColumnsCount(sourceElement, columns);
		nsIDOMElement colgroup = visualDocument
				.createElement(HTML.TAG_COLGROUP);
		colgroup.setAttribute(HTML.ATTR_SPAN,
				String.valueOf(columnsLength));
		mainTable.appendChild(colgroup);

		// Encode Caption
		encodeCaption(creationData, sourceElement, visualDocument, mainTable);

		// Encode Header
		Element header = ComponentUtil.getFacet(sourceElement, RichFaces.NAME_FACET_HEADER);
		ArrayList<Element> columnsHeaders = ComponentUtil.getColumnsWithFacet(columns, RichFaces.NAME_FACET_HEADER);
		if (header != null || !columnsHeaders.isEmpty()) {
			nsIDOMElement thead = visualDocument
					.createElement(HTML.TAG_THEAD);
			mainTable.appendChild(thead);
			String headerClass = sourceElement.hasAttribute(RichFaces.ATTR_HEADER_CLASS) ? 
					sourceElement.getAttribute(RichFaces.ATTR_HEADER_CLASS) : null;
			if (header != null) {
			    /*
			     * JBIDE-3204 #2:No one style or styleClass should be applyed
			     * for the footer and header of scrollableDataTable as default
			     */
			    encodeTableHeaderOrFooterFacet(pageContext, creationData, thead,
				    columnsLength, visualDocument, header,
				    Constants.EMPTY, Constants.EMPTY, Constants.EMPTY,
				    headerClass, HTML.TAG_TD);
			}
			if (!columnsHeaders.isEmpty()) {
				nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
				thead.appendChild(tr);
				String styleClass = ComponentUtil.encodeStyleClass(null,
						"dr-table-subheader dr-sdt-hr", null, //$NON-NLS-1$
						headerClass);
				if (styleClass != null) {
					tr.setAttribute(HTML.ATTR_CLASS,
							styleClass);
				}
				encodeHeaderOrFooterFacets(creationData, tr, visualDocument,
						columnsHeaders,
						"dr-table-subheadercell rich-table-subheadercell", //$NON-NLS-1$
						headerClass, RichFaces.NAME_FACET_HEADER, HTML.TAG_TD);
			}
		}

		// Encode Footer
		Element footer = ComponentUtil.getFacet(sourceElement, RichFaces.NAME_FACET_FOOTER);
		ArrayList<Element> columnsFooters = ComponentUtil.getColumnsWithFacet(columns, RichFaces.NAME_FACET_FOOTER);
		if (footer != null || !columnsFooters.isEmpty()) {
			nsIDOMElement tfoot = visualDocument
					.createElement(HTML.TAG_TFOOT);
			mainTable.appendChild(tfoot);
			String footerClass = sourceElement.hasAttribute(RichFaces.ATTR_FOOTER_CLASS) ?
					sourceElement.getAttribute(RichFaces.ATTR_FOOTER_CLASS) : null;
			if (!columnsFooters.isEmpty()) {
				nsIDOMElement tr = visualDocument
						.createElement(HTML.TAG_TR);
				tfoot.appendChild(tr);
				String styleClass = ComponentUtil.encodeStyleClass(null,
						"dr-table-subfooter rich-table-subfooter", null, //$NON-NLS-1$
						footerClass);
				if (styleClass != null) {
					tr.setAttribute(HTML.ATTR_CLASS,
							styleClass);
				}
				encodeHeaderOrFooterFacets(creationData, tr, visualDocument,
						columnsFooters,
						"dr-table-subfootercell rich-table-subfootercell", //$NON-NLS-1$
						footerClass, RichFaces.NAME_FACET_FOOTER, HTML.TAG_TD);
			}
			if (footer != null) {
			    /*
			     * JBIDE-3204 #2:No one style or styleClass should be applyed
			     * for the footer and header of scrollableDataTable as default
			     */
			    encodeTableHeaderOrFooterFacet(pageContext, creationData, tfoot,
				    columnsLength, visualDocument, footer,
				    Constants.EMPTY, Constants.EMPTY, Constants.EMPTY,
				    footerClass, HTML.TAG_TD);
			}
		}

		nsIDOMElement tbody = visualDocument
				.createElement(HTML.TAG_TBODY);
		mainTable.appendChild(tbody);
		VisualDomUtil.copyAttributes(sourceNode, tbody);

		for (int i = 0; i < NUM_ROW; i++) {
			new RichFacesDataTableChildrenEncoder(creationData, visualDocument,
					sourceElement, mainTable).encodeChildren();
		}

		return creationData;
	}

	/**
	 * 
	 * @param creationData
	 * @param sourceElement
	 * @param visualDocument
	 * @param table
	 */
	protected void encodeCaption(VpeCreationData creationData,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement table) {
		// Encode caption
		Element captionFromFacet = ComponentUtil.getFacet(sourceElement,
				HTML.TAG_CAPTION);
		if (captionFromFacet != null) {						

			nsIDOMElement caption = visualDocument
					.createElement(HTML.TAG_CAPTION);
			table.appendChild(caption);
			
			String captionClass = "dr-table-caption rich-table-caption";  //$NON-NLS-1$
			if (table.hasAttribute(RichFaces.ATTR_CAPTION_CLASS)) {
				captionClass += Constants.WHITE_SPACE + table.getAttribute(RichFaces.ATTR_CAPTION_CLASS);
			}			
			caption.setAttribute(HTML.ATTR_CLASS, captionClass);
						
			if (table.hasAttribute(RichFaces.ATTR_CAPTION_STYLE)) {
				String captionStyle = table.getAttribute(RichFaces.ATTR_CAPTION_STYLE);
				caption.setAttribute(HTML.ATTR_STYLE, captionStyle);
			}

			VpeChildrenInfo cap = new VpeChildrenInfo(caption);
			cap.addSourceChild(captionFromFacet);
			creationData.addChildrenInfo(cap);
		}

	}

	/**
	 * 
	 * @param creationData
	 * @param parentTr
	 * @param visualDocument
	 * @param headersOrFooters
	 * @param skinCellClass
	 * @param headerClass
	 * @param facetName
	 * @param element
	 */
	public static void encodeHeaderOrFooterFacets(VpeCreationData creationData,
			nsIDOMElement parentTr, nsIDOMDocument visualDocument,
			ArrayList<Element> headersOrFooters, String skinCellClass,
			String headerClass, String facetName, String element) {
		for (Element column : headersOrFooters) {
			String classAttribute = facetName + "Class"; //$NON-NLS-1$
			String columnHeaderClass = column.hasAttribute(classAttribute) ? column.getAttribute(classAttribute) : null;
			nsIDOMElement td = visualDocument.createElement(element);
			parentTr.appendChild(td);
			String styleClass = ComponentUtil.encodeStyleClass(null, skinCellClass,
					headerClass, columnHeaderClass);

			if (!RichFacesColumnTemplate.isVisible(column)) {
				VisualDomUtil.setSubAttribute(td, HTML.ATTR_STYLE,
						HTML.STYLE_PARAMETER_DISPLAY, HTML.STYLE_VALUE_NONE);
			}

			td.setAttribute(HTML.ATTR_CLASS, styleClass);
			td.setAttribute("scop", "col"); //$NON-NLS-1$ //$NON-NLS-2$
			
			if (column.hasAttribute(HTML.ATTR_COLSPAN)) {
				String colspan = column.getAttribute(HTML.ATTR_COLSPAN);
				td.setAttribute(HTML.ATTR_COLSPAN, colspan);
			}
			Element facetBody = ComponentUtil.getFacet(column, facetName);

			VpeChildrenInfo child = new VpeChildrenInfo(td);
			child.addSourceChild(facetBody);
			creationData.addChildrenInfo(child);
		}
	}

	/**
	 * 
	 * @param creationData
	 * @param parentTheadOrTfood
	 * @param columns
	 * @param visualDocument
	 * @param facetBody
	 * @param skinFirstRowClass
	 * @param skinRowClass
	 * @param skinCellClass
	 * @param facetBodyClass
	 * @param element
	 */
	protected void encodeTableHeaderOrFooterFacet(final VpePageContext pageContext, VpeCreationData creationData,
			nsIDOMElement parentTheadOrTfood, int columns,
			nsIDOMDocument visualDocument, Element facetBody,
			String skinFirstRowClass, String skinRowClass,
			String skinCellClass, String facetBodyClass, String element) {
		boolean isColumnGroup = facetBody.getNodeName()
				.endsWith(":columnGroup"); //$NON-NLS-1$
		boolean isSubTable = facetBody.getNodeName().endsWith(":subTable"); //$NON-NLS-1$
		if (isColumnGroup) {
			RichFacesColumnGroupTemplate.DEFAULT_INSTANCE.encodeSubTable(pageContext, creationData,
					facetBody, visualDocument, parentTheadOrTfood);
		} else if (isSubTable) {
			RichFacesSubTableTemplate.DEFAULT_INSTANCE.encodeSubTable(pageContext, creationData,
					facetBody, visualDocument, parentTheadOrTfood);
		} else {
			nsIDOMElement tr = visualDocument
					.createElement(HTML.TAG_TR);
			parentTheadOrTfood.appendChild(tr);

			String styleClass = ComponentUtil.encodeStyleClass(null, skinFirstRowClass,
					facetBodyClass, null);
			if (styleClass != null) {
				tr.setAttribute(HTML.ATTR_CLASS, styleClass);
			}

			nsIDOMElement td = visualDocument.createElement(element);
			tr.appendChild(td);

			styleClass = ComponentUtil.encodeStyleClass(null, skinCellClass, facetBodyClass,
					null);
			if (styleClass != null) {
				td.setAttribute(HTML.ATTR_CLASS, styleClass);
			}

			// the cell spans the entire row
			td.setAttribute(HTML.ATTR_COLSPAN, HTML.VALUE_COLSPAN_ALL);
			
			td.setAttribute(HTML.ATTR_SCOPE,
					HTML.TAG_COLGROUP);

			VpeChildrenInfo child = new VpeChildrenInfo(td);
			child.addSourceChild(facetBody);
			creationData.addChildrenInfo(child);
		}
	}

	/**
	 * 
	 * @param parentSourceElement
	 * @return list of columns
	 */
	public static ArrayList<Element> getColumns(Element parentSourceElement) {
		ArrayList<Element> columns = new ArrayList<Element>();
		NodeList children = parentSourceElement.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String nodeName = child.getNodeName();
			if ((child instanceof Element)
					&& (nodeName.endsWith(COLUMN) || nodeName.endsWith(COLUMNS))) {
				columns.add((Element) child);
			}
		}
		return columns;
	}

	/**
	 * 
	 * @param sourceElement
	 * @param columns
	 * @return
	 */
	protected int getColumnsCount(Element sourceElement,
			ArrayList<Element> columns) {
		int count = 0;
		// check for exact value in component
		try {
			int span = Integer.parseInt(sourceElement.getAttribute("columns")); //$NON-NLS-1$
			count = count > 0 ? span : calculateRowColumns(sourceElement, columns);
		} catch (NumberFormatException e) {
			count = calculateRowColumns(sourceElement, columns);
		}
		return count;
	}

	/*
	 * Calculate max number of columns per row. For rows, recursive calculate
	 * max length.
	 */
	private int calculateRowColumns(Element sourceElement,
			ArrayList<Element> columns) {
		int count = 0;
		int currentLength = 0;
		for (Element column : columns) {
			if (ComponentUtil.isRendered(column)) {
				String nodeName = column.getNodeName();
				if (nodeName.endsWith(":columnGroup")) { //$NON-NLS-1$
					// Store max calculated value of previsous rows.
					if (currentLength > count) {
						count = currentLength;
					}
					// Calculate number of columns in row.
					currentLength = calculateRowColumns(sourceElement,
							getColumns(column));
					// Store max calculated value
					if (currentLength > count) {
						count = currentLength;
					}
					currentLength = 0;
				} else if (nodeName.equals(sourceElement.getPrefix() + COLUMN) ||
						nodeName.equals(sourceElement.getPrefix() + COLUMNS)) {
					// For new row, save length of previsous.
					if (RichFacesColumnTemplate.isBreakBefore(column)) {
						if (currentLength > count) {
							count = currentLength;
						}
						currentLength = 0;
					}
					String colspanStr = column
							.getAttribute(HTML.ATTR_COLSPAN);
					try {
						int colspan = Integer.parseInt(colspanStr);
						currentLength += colspan > 0 ? colspan : 1;
					} catch (NumberFormatException e) {
						currentLength++;
					}
				} else if (nodeName.endsWith(COLUMN)) {
					// UIColumn always have colspan == 1.
					currentLength++;
				}

			}
		}
		if (currentLength > count) {
			count = currentLength;
		}
		return count;
	}

	/**
	 * Checks, whether it is necessary to re-create an element at change of
	 * attribute
	 * 
	 * @param pageContext
	 *            Contains the information on edited page.
	 * @param sourceElement
	 *            The current element of the source tree.
	 * @param visualDocument
	 *            The document of the visual tree.
	 * @param visualNode
	 *            The current node of the visual tree.
	 * @param data
	 *            The arbitrary data, built by a method <code>create</code>
	 * @param name
	 *            Attribute name
	 * @param value
	 *            Attribute value
	 * @return <code>true</code> if it is required to re-create an element at
	 *         a modification of attribute, <code>false</code> otherwise.
	 */
	public boolean recreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#validate(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMDocument, org.jboss.tools.vpe.editor.template.VpeCreationData) */
	@Override
	public void validate(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument, VpeCreationData data) {
		RichFacesDataTableChildrenEncoder.validateChildren(pageContext, sourceNode, visualDocument, data);
		applyStyleClasses(pageContext, sourceNode, visualDocument, data);
	}

	private void applyStyleClasses(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument, VpeCreationData data) {
		
		nsIDOMElement element = (nsIDOMElement) data.getNode();
		final nsIDOMNodeList mainTableWrappers = element.getElementsByTagName(TAG_MAIN_TABLE_WRAPPER);
		
		if (mainTableWrappers == null
				|| mainTableWrappers.getLength() != 1) {
			final RuntimeException e = new RuntimeException("This is probably a bug. There should be exatly one " + TAG_MAIN_TABLE_WRAPPER);//$NON-NLS-1$
			RichFacesTemplatesActivator.getPluginLog().logError(e);
		}
		final nsIDOMNode mainTableWrapper = mainTableWrappers.item(0);			
		final nsIDOMNodeList mainTableWrapperChildren = mainTableWrapper.getChildNodes();

		if (mainTableWrapperChildren == null
				|| mainTableWrapperChildren.getLength() != 1) {
			final RuntimeException e = new RuntimeException("This is probably a bug. " + TAG_MAIN_TABLE_WRAPPER + " should have exactly one child.");//$NON-NLS-1$ //$NON-NLS-2$
			RichFacesTemplatesActivator.getPluginLog().logError(e);
		}
		
		final nsIDOMElement mainTable = queryInterface(mainTableWrapperChildren.item(0), nsIDOMElement.class);
		
		final RichFacesDataTableStyleClassesApplier styleClassesApplier = 
			new RichFacesDataTableStyleClassesApplier(visualDocument, 
					pageContext, sourceNode);
		styleClassesApplier.applyClasses(mainTable);
		
		VisualDomUtil.replaceNodeByItsChildren(mainTableWrapper);
	}

	// @Override
	// public void removeAttribute(VpePageContext pageContext,
	// Element sourceElement, nsIDOMDocument visualDocument,
	// nsIDOMNode visualNode, Object data, String name) {
	// nsIDOMElement visualElement = (nsIDOMElement) visualNode
	// .queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
	// if (name.equalsIgnoreCase(HTML.ATTR_WIDTH)) {
	// String style = visualElement
	// .getAttribute(HtmlComponentUtil.HTML_S				// Append colspan of this column
	// visualElement.removeAttribute(HTML.ATTR_STYLE);
	// style += "; " + HTML.ATTR_WIDTH + " : "
	// + DEFAULT_WIDTH + ";";
	// visualElement
	// .setAttribute(HTML.ATTR_STYLE, style);
	//
	// } else
	//
	// if (name.equalsIgnoreCase(HTML.ATTR_HEIGHT)) {
	// String style = visualElement
	// .getAttribute(HTML.ATTR_STYLE);
	// visualElement.removeAttribute(HTML.ATTR_STYLE);
	// style += "; " + HTML.ATTR_HEIGHT + " : "
	// + DEFAULT_HEIGHT + ";";
	// visualElement
	// .setAttribute(HTML.ATTR_STYLE, style);
	//
	// } else {
	// visualElement.removeAttribute(name);
	// }
	// }
	//
	// @Override
	// public void setAttribute(VpePageContext pageContext, Element
	// sourceElement,
	// nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data,
	// String name, String value) {
	// nsIDOMElement visualElement = (nsIDOMElement) visualNode
	// .queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
	// if (name.equalsIgnoreCase(HTML.ATTR_WIDTH)) {
	// String style = visualElement
	// .getAttribute(HTML.ATTR_STYLE);
	// visualElement.removeAttribute(HTML.ATTR_STYLE);
	// style += "; " + HTML.ATTR_WIDTH + " : " + value
	// + ";";
	// visualElement
	// .setAttribute(HTML.ATTR_STYLE, style);
	//
	// }
	//
	// if (name.equalsIgnoreCase(HTML.ATTR_HEIGHT)) {
	// String style = visualElement
	// .getAttribute(HTML.ATTR_STYLE);
	// visualElement.removeAttribute(HTML.ATTR_STYLE);
	// style += "; " + HTML.ATTR_HEIGHT + " : " + value
	// + ";";
	// visualElement
	// .setAttribute(HTML.ATTR_STYLE, style);
	//
	// }
	// visualElement.setAttribute(name, value);
	// }

}
// html code
// <table style="border: 1px solid;">
// <tr>
// <td>
// <table>
// <tr>
// <td>
// <input type="text"/>ibsert content
// sdfsdfsdf
// </td>
// </tr>
// </table>
// </td>
// <td>
// <div style="overflow: scroll; width: 17px; height: 100%;">
// </div>
// </td>
// </tr>
// <tr>
// <td>
// <div style="overflow: scroll; width: 100%; height: 17px;">
// </div>
// </td>
// </tr>
// <table/>
