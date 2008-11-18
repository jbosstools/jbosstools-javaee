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

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.RichFacesTemplatesActivator;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
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
	final static String DEFAULT_HEIGHT = "500px";
	final static String DEFAULT_WIDTH = "700px";
	final static String HEADER = "header";
	final static String HEADER_CLASS = "headerClass";
	final static String FOOTER = "footer";
	final static String FOOTER_CLASS = "footerClass";
	final static String CAPTION_CLASS = "captionClass";
	final static String CAPTION_STYLE = "captionStyle";
	final static String SPACE = " ";

	private static String STYLE_FOR_LOW_SCROLL = "overflow: scroll; width: 100%; height: 17px;";
	private static String STYLE_FOR_RIGHT_SCROLL = "overflow: scroll; width: 17px; height: 100%;";

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

		String width = sourceElement
				.getAttribute(HtmlComponentUtil.HTML_WIDTH_ATTR);
		String height = sourceElement
				.getAttribute(HtmlComponentUtil.HTML_HEIGHT_ATTR);

		// -----------CommonTable
		nsIDOMElement tableCommon = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TABLE);

		VpeCreationData creationData = new VpeCreationData(tableCommon);

		nsIDOMElement tr1 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TR);

		nsIDOMElement tr2 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TR);

		tableCommon.appendChild(tr1);
		tableCommon.appendChild(tr2);

		// ---------tr2
		nsIDOMElement tr2_TD = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		tr2.appendChild(tr2_TD);

		nsIDOMElement tr2_td_DIV = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		tr2_td_DIV.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
				STYLE_FOR_LOW_SCROLL);
		tr2_TD.appendChild(tr2_td_DIV);

		// --------------------------------------------

		// ---------------------tr1------------------------
		nsIDOMElement tr1_TD1 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		tr1.appendChild(tr1_TD1);

		nsIDOMElement tr1_TD2 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		tr1.appendChild(tr1_TD2);

		nsIDOMElement tr1_td2_DIV = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		tr1_td2_DIV.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
				STYLE_FOR_RIGHT_SCROLL);
		tr1_TD2.appendChild(tr1_td2_DIV);

		// -------------------------------------------------------
		nsIDOMElement div = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		tr1_TD1.appendChild(div);
		div.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-table-hidden");

		String divStyle = HtmlComponentUtil.HTML_WIDTH_ATTR + " : "
				+ (width == null ? DEFAULT_WIDTH : width) + ";"
				+ HtmlComponentUtil.HTML_HEIGHT_ATTR + " : "
				+ (height == null ? DEFAULT_HEIGHT : height) + ";";

		div.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, divStyle);

		nsIDOMElement mainTable = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
		ComponentUtil.copyAttributes(sourceNode, mainTable);
		mainTable.removeAttribute(HtmlComponentUtil.HTML_ATR_HEIGHT);

		nsIDOMElement mainTableWrapper = visualDocument.createElement(TAG_MAIN_TABLE_WRAPPER);
		mainTableWrapper.appendChild(mainTable);
		div.appendChild(mainTableWrapper);

		ComponentUtil.setCSSLink(pageContext,
				"scrollableDataTable/scrollableDataTable.css",
				"richFacesDataTable");
		String tableClass = sourceElement
				.getAttribute(HtmlComponentUtil.HTML_STYLECLASS_ATTR);
		mainTable
				.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
						"dr-table rich-table "
								+ (tableClass == null ? "" : tableClass));

		// Encode colgroup definition.
		ArrayList<Element> columns = getColumns(sourceElement);
		int columnsLength = getColumnsCount(sourceElement, columns);
		nsIDOMElement colgroup = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_COLGROUP);
		colgroup.setAttribute(HtmlComponentUtil.HTML_TAG_SPAN, String
				.valueOf(columnsLength));
		mainTable.appendChild(colgroup);

		// Encode Caption
		encodeCaption(creationData, sourceElement, visualDocument, mainTable);

		// Encode Header
		Element header = ComponentUtil.getFacet(sourceElement, HEADER);
		ArrayList<Element> columnsHeaders = getColumnsWithFacet(columns, HEADER);
		if (header != null || !columnsHeaders.isEmpty()) {
			nsIDOMElement thead = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_THEAD);
			mainTable.appendChild(thead);
			String headerClass = (String) sourceElement
					.getAttribute(HEADER_CLASS);
			if (header != null) {
				encodeTableHeaderOrFooterFacet(pageContext, creationData, thead,
						columnsLength, visualDocument, header,
						"dr-table-header rich-table-header",
						"dr-table-header-continue rich-table-header-continue",
						"dr-table-headercell rich-table-headercell",
						headerClass, HtmlComponentUtil.HTML_TAG_TD);
			}
			if (!columnsHeaders.isEmpty()) {
				nsIDOMElement tr = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);
				thead.appendChild(tr);
				String styleClass = encodeStyleClass(null,
						"dr-table-subheader rich-table-subheader", null,
						headerClass);
				if (styleClass != null) {
					tr.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
							styleClass);
				}
				encodeHeaderOrFooterFacets(creationData, tr, visualDocument,
						columnsHeaders,
						"dr-table-subheadercell rich-table-subheadercell",
						headerClass, HEADER, HtmlComponentUtil.HTML_TAG_TD);
			}
		}

		// Encode Footer
		Element footer = ComponentUtil.getFacet(sourceElement, FOOTER);
		ArrayList<Element> columnsFooters = getColumnsWithFacet(columns, FOOTER);
		if (footer != null || !columnsFooters.isEmpty()) {
			nsIDOMElement tfoot = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_TFOOT);
			mainTable.appendChild(tfoot);
			String footerClass = (String) sourceElement
					.getAttribute(FOOTER_CLASS);
			if (!columnsFooters.isEmpty()) {
				nsIDOMElement tr = visualDocument
						.createElement(HtmlComponentUtil.HTML_TAG_TR);
				tfoot.appendChild(tr);
				String styleClass = encodeStyleClass(null,
						"dr-table-subfooter rich-table-subfooter", null,
						footerClass);
				if (styleClass != null) {
					tr.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
							styleClass);
				}
				encodeHeaderOrFooterFacets(creationData, tr, visualDocument,
						columnsFooters,
						"dr-table-subfootercell rich-table-subfootercell",
						footerClass, FOOTER, HtmlComponentUtil.HTML_TAG_TD);
			}
			if (footer != null) {
				encodeTableHeaderOrFooterFacet(pageContext, creationData, tfoot,
						columnsLength, visualDocument, footer,
						"dr-table-footer rich-table-footer",
						"dr-table-footer-continue rich-table-footer-continue",
						"dr-table-footercell rich-table-footercell",
						footerClass, HtmlComponentUtil.HTML_TAG_TD);
			}
		}

		nsIDOMElement tbody = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TBODY);
		mainTable.appendChild(tbody);

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
				HtmlComponentUtil.HTML_TAG_CAPTION);
		if (captionFromFacet != null) {
			String captionClass = (String) table.getAttribute(CAPTION_CLASS);
			String captionStyle = (String) table.getAttribute(CAPTION_STYLE);

			nsIDOMElement caption = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_CAPTION);
			table.appendChild(caption);
			if (captionClass != null && captionClass.length() > 0) {
				captionClass = "dr-table-caption rich-table-caption "
						+ captionClass;
			} else {
				captionClass = "dr-table-caption rich-table-caption";
			}
			caption.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					captionClass);
			if (captionStyle != null && captionStyle.length() > 0) {
				caption.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
						captionStyle);
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
			String classAttribute = facetName + "Class";
			String columnHeaderClass = column.getAttribute(classAttribute);
			nsIDOMElement td = visualDocument.createElement(element);
			parentTr.appendChild(td);
			String styleClass = encodeStyleClass(null, skinCellClass,
					headerClass, columnHeaderClass);

			if (!RichFacesColumnTemplate.isVisible(column)) {
				VisualDomUtil.setSubAttribute(td, HTML.ATTR_STYLE,
						HTML.STYLE_PARAMETER_DISPLAY, HTML.STYLE_VALUE_NONE);
			}

			td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, styleClass);
			td.setAttribute("scop", "col");
			String colspan = column
					.getAttribute(HtmlComponentUtil.HTML_TABLE_COLSPAN);
			if (colspan != null && colspan.length() > 0) {
				td.setAttribute(HtmlComponentUtil.HTML_TABLE_COLSPAN, colspan);
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
				.endsWith(":columnGroup");
		boolean isSubTable = facetBody.getNodeName().endsWith(":subTable");
		if (isColumnGroup) {
			RichFacesColumnGroupTemplate.DEFAULT_INSTANCE.encode(pageContext, creationData,
					facetBody, visualDocument, parentTheadOrTfood);
		} else if (isSubTable) {
			RichFacesSubTableTemplate.DEFAULT_INSTANCE.encode(pageContext, creationData,
					facetBody, visualDocument, parentTheadOrTfood);
		} else {
			nsIDOMElement tr = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_TR);
			parentTheadOrTfood.appendChild(tr);

			String styleClass = encodeStyleClass(null, skinFirstRowClass,
					facetBodyClass, null);
			if (styleClass != null) {
				tr.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, styleClass);
			}
			String style = ComponentUtil.getHeaderBackgoundImgStyle();
			tr.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, style);

			nsIDOMElement td = visualDocument.createElement(element);
			tr.appendChild(td);

			styleClass = encodeStyleClass(null, skinCellClass, facetBodyClass,
					null);
			if (styleClass != null) {
				td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, styleClass);
			}

			// the cell spans the entire row
			td.setAttribute(HTML.ATTR_COLSPAN, HTML.VALUE_COLSPAN_ALL);
			
			td.setAttribute(HtmlComponentUtil.HTML_SCOPE_ATTR,
					HtmlComponentUtil.HTML_TAG_COLGROUP);

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
	 * @param columns
	 * @param facetName
	 * @return list of columns with facet
	 */
	public static ArrayList<Element> getColumnsWithFacet(
			ArrayList<Element> columns, String facetName) {
		ArrayList<Element> columnsWithFacet = new ArrayList<Element>();
		for (Element column : columns) {
			Element body = ComponentUtil.getFacet(column, facetName);
			if (body != null) {
				columnsWithFacet.add(column);
			}
		}
		return columnsWithFacet;
	}

	/**
	 * 
	 * @param parentPredefined
	 * @param predefined
	 * @param parent
	 * @param custom
	 * @return
	 */
	public static String encodeStyleClass(Object parentPredefined,
			Object predefined, Object parent, Object custom) {
		StringBuffer styleClass = new StringBuffer();
		// Construct predefined classes
		if (null != parentPredefined) {
			styleClass.append(parentPredefined).append(SPACE);
		} else if (null != predefined) {
			styleClass.append(predefined).append(SPACE);
		}
		// Append class from parent component.
		if (null != parent) {
			styleClass.append(parent).append(SPACE);
		}
		if (null != custom) {
			styleClass.append(custom);
		}
		if (styleClass.length() > 0) {
			return styleClass.toString();
		}
		return null;
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
			int span = Integer.parseInt(sourceElement.getAttribute("columns"));
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
				if (nodeName.endsWith(":columnGroup")) {
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
					String breakBeforeStr = column.getAttribute("breakBefore");
					// For new row, save length of previsous.
					if (Boolean.getBoolean(breakBeforeStr)) {
						if (currentLength > count) {
							count = currentLength;
						}
						currentLength = 0;
					}
					String colspanStr = column
							.getAttribute(HtmlComponentUtil.HTML_TABLE_COLSPAN);
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
	public boolean isRecreateAtAttrChange(VpePageContext pageContext,
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
		
		final nsIDOMElement mainTable = (nsIDOMElement) mainTableWrapperChildren.item(0)
			.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		
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
	// if (name.equalsIgnoreCase(HtmlComponentUtil.HTML_WIDTH_ATTR)) {
	// String style = visualElement
	// .getAttribute(HtmlComponentUtil.HTML_S				// Append colspan of this column
	// visualElement.removeAttribute(HtmlComponentUtil.HTML_STYLE_ATTR);
	// style += "; " + HtmlComponentUtil.HTML_WIDTH_ATTR + " : "
	// + DEFAULT_WIDTH + ";";
	// visualElement
	// .setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, style);
	//
	// } else
	//
	// if (name.equalsIgnoreCase(HtmlComponentUtil.HTML_HEIGHT_ATTR)) {
	// String style = visualElement
	// .getAttribute(HtmlComponentUtil.HTML_STYLE_ATTR);
	// visualElement.removeAttribute(HtmlComponentUtil.HTML_STYLE_ATTR);
	// style += "; " + HtmlComponentUtil.HTML_HEIGHT_ATTR + " : "
	// + DEFAULT_HEIGHT + ";";
	// visualElement
	// .setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, style);
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
	// if (name.equalsIgnoreCase(HtmlComponentUtil.HTML_WIDTH_ATTR)) {
	// String style = visualElement
	// .getAttribute(HtmlComponentUtil.HTML_STYLE_ATTR);
	// visualElement.removeAttribute(HtmlComponentUtil.HTML_STYLE_ATTR);
	// style += "; " + HtmlComponentUtil.HTML_WIDTH_ATTR + " : " + value
	// + ";";
	// visualElement
	// .setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, style);
	//
	// }
	//
	// if (name.equalsIgnoreCase(HtmlComponentUtil.HTML_HEIGHT_ATTR)) {
	// String style = visualElement
	// .getAttribute(HtmlComponentUtil.HTML_STYLE_ATTR);
	// visualElement.removeAttribute(HtmlComponentUtil.HTML_STYLE_ATTR);
	// style += "; " + HtmlComponentUtil.HTML_HEIGHT_ATTR + " : " + value
	// + ";";
	// visualElement
	// .setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, style);
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
