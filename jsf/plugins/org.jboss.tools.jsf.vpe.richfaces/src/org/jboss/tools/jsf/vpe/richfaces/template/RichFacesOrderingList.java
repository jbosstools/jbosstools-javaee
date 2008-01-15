/**
 * 
 */
package org.jboss.tools.jsf.vpe.richfaces.template;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.RichFacesTemplatesActivator;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author dmaliarevich
 * 
 */
public class RichFacesOrderingList extends VpeAbstractTemplate {

	final static String DEFAULT_HEIGHT = "200px";
	final static String DEFAULT_WIDTH = "300px";
	final static String HEADER = "header";
	final static String HEADER_CLASS = "headerClass";
	final static String FOOTER = "footer";
	final static String FOOTER_CLASS = "footerClass";
	final static String CAPTION_CLASS = "captionClass";
	final static String CAPTION_STYLE = "captionStyle";
	final static String SPACE = " ";

	private static String STYLE_FOR_CAPTOION_LABEL = "white-space: normal; word-wrap: break-word; font-weight: bold; ";
	private static String STYLE_FOR_LOW_SCROLL = "overflow: scroll; width: 100%; height: 17px;";
	private static String STYLE_FOR_RIGHT_SCROLL = "overflow: scroll; width: 17px; height: 100%;";

	private static int NUM_ROW = 1;

	private static final String TOP_CONTROL_IMG = "orderingList/top.gif";
	private static final String UP_CONTROL_IMG = "orderingList/up.gif";
	private static final String DOWN_CONTROL_IMG = "orderingList/down.gif";
	private static final String BOTTOM_CONTROL_IMG = "orderingList/bottom.gif";

	private static final String BUTTON_BG = "orderingList/button_bg.gif";
	private static final String HEADER_CELL_BG = "orderingList/table_header_cell_bg.gif";

	private static final String LIST_WIDTH = "listWidth";
	private static final String LIST_HEIGHT = "listHeight";

	private static final String TOP_CONTROL_LABEL = "topControlLabel";
	private static final String UP_CONTROL_LABEL = "upControlLabel";
	private static final String DOWN_CONTROL_LABEL = "downControlLabel";
	private static final String BOTTOM_CONTROL_LABEL = "bottomControlLabel";

	private static final String TOP_CONTROL_LABEL_DEFAULT = "First";
	private static final String UP_CONTROL_LABEL_DEFAULT = "Up";
	private static final String DOWN_CONTROL_LABEL_DEFAULT = "Down";
	private static final String BOTTOM_CONTROL_LABEL_DEFAULT = "Last";

	private static final String CAPTION_LABEL = "captionLabel";
	private static final String CONTROLS_VERTICAL_ALIGN = "controlsVerticalAlign";
	private static final String SHOW_BUTTON_LABELS = "showButtonLabels";
	private static final String FAST_ORDER_CONTROL_VISIBLE = "fastOrderControlsVisible";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.tools.vpe.editor.template.VpeTemplate#create(org.jboss.tools.vpe.editor.context.VpePageContext,
	 *      org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMDocument)
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {

		Element sourceElement = (Element) sourceNode;

		String listWidth = sourceElement.getAttribute(LIST_WIDTH);
		String listHeight = sourceElement.getAttribute(LIST_HEIGHT);

		String topControlLabel = sourceElement.getAttribute(TOP_CONTROL_LABEL);
		String upControlLabel = sourceElement.getAttribute(UP_CONTROL_LABEL);
		String downControlLabel = sourceElement
				.getAttribute(DOWN_CONTROL_LABEL);
		String bottomControlLabel = sourceElement
				.getAttribute(BOTTOM_CONTROL_LABEL);

		String showButtonLabelsStr = sourceElement
				.getAttribute(SHOW_BUTTON_LABELS);
		String fastOrderControlsVisibleStr = sourceElement
				.getAttribute(FAST_ORDER_CONTROL_VISIBLE);
		boolean showButtonLabels = ComponentUtil
				.string2boolean(showButtonLabelsStr);
		boolean fastOrderControlsVisible = ComponentUtil
				.string2boolean(fastOrderControlsVisibleStr);

		String controlsVerticalAlign = sourceElement
				.getAttribute(CONTROLS_VERTICAL_ALIGN);
		String captionLabel = sourceElement.getAttribute(CAPTION_LABEL);

		// --------------------- COMMON TABLE ------------------------
		nsIDOMElement tableCommon = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TABLE);

		VpeCreationData creationData = new VpeCreationData(tableCommon);

		nsIDOMElement row1 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TR);
		nsIDOMElement row2 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TR);

		tableCommon.appendChild(row1);
		tableCommon.appendChild(row2);

		// ---------------------row1------------------------
		nsIDOMElement row1_TD1 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		row1.appendChild(row1_TD1);

		nsIDOMElement row1_TD1_DIV = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		row1_TD1_DIV.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
				STYLE_FOR_CAPTOION_LABEL + "width: "
						+ (listWidth == null ? DEFAULT_WIDTH : listWidth)
						+ "px");
		row1_TD1_DIV.appendChild(visualDocument.createTextNode(captionLabel));
		row1_TD1.appendChild(row1_TD1_DIV);

		// ---------------------row2 ---- with list table and buttons------------------------
		nsIDOMElement row2_TD1 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		row2.appendChild(row2_TD1);

		nsIDOMElement row2_TD2 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		row2.appendChild(row2_TD2);

		// ---------------------buttonsTable------------------------
		nsIDOMElement buttonsDiv = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		buttonsDiv.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "rich-ordering-list-button-layout");
		
		nsIDOMElement btnUpDiv = createButtonDiv(visualDocument,
				(null == upControlLabel ? UP_CONTROL_LABEL_DEFAULT
						: upControlLabel), UP_CONTROL_IMG, new Boolean(
						showButtonLabels).booleanValue());
		nsIDOMElement btnDownDiv = createButtonDiv(visualDocument,
				(null == upControlLabel ? DOWN_CONTROL_LABEL_DEFAULT
						: upControlLabel), DOWN_CONTROL_IMG, new Boolean(
						showButtonLabels).booleanValue());

		if (fastOrderControlsVisible) {
			nsIDOMElement btnTopDiv = createButtonDiv(visualDocument,
					(null == upControlLabel ? TOP_CONTROL_LABEL_DEFAULT
							: upControlLabel), TOP_CONTROL_IMG, new Boolean(
									showButtonLabels).booleanValue());
			nsIDOMElement btnBottomDiv = createButtonDiv(visualDocument,
					(null == upControlLabel ? BOTTOM_CONTROL_LABEL_DEFAULT
							: upControlLabel), BOTTOM_CONTROL_IMG, new Boolean(
									showButtonLabels).booleanValue());
			
			buttonsDiv.appendChild(btnTopDiv);
			buttonsDiv.appendChild(btnBottomDiv);
		}

		buttonsDiv.appendChild(btnUpDiv);
		buttonsDiv.appendChild(btnDownDiv);
		
		row2_TD2.setAttribute(HtmlComponentUtil.HTML_ALIGN_ATTR, "center");
		row2_TD2.setAttribute(HtmlComponentUtil.HTML_ATTR_VALIGN, ("center"
				.equalsIgnoreCase(controlsVerticalAlign) ? "middle"
				: controlsVerticalAlign));
		row2_TD2.appendChild(buttonsDiv);

		// --------------------------------------------

		// ---------------------listTable------------------------
		nsIDOMElement listTable = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
		nsIDOMElement tr1 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TR);
		nsIDOMElement tr2 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TR);

		listTable.appendChild(tr1);
		listTable.appendChild(tr2);

		row2_TD1.appendChild(listTable);

		// ---------------------tr1------------------------
		nsIDOMElement tr1_TD1 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		tr1.appendChild(tr1_TD1);

		nsIDOMElement tr1_TD2 = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		tr1.appendChild(tr1_TD2);

		nsIDOMElement tr1_TD2_DIV = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		tr1_TD2_DIV.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
				STYLE_FOR_RIGHT_SCROLL);
		tr1_TD2.appendChild(tr1_TD2_DIV);

		// -------------------------------------------------------

		// ---------------------tr2------------------------
		nsIDOMElement tr2_TD = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TD);
		tr2.appendChild(tr2_TD);

		nsIDOMElement tr2_TD_DIV = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		tr2_TD_DIV.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
				STYLE_FOR_LOW_SCROLL);
		tr2_TD.appendChild(tr2_TD_DIV);

		// --------------------------------------------

		nsIDOMElement div = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		tr1_TD1.appendChild(div);
		div.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-table-hidden");

		String divStyle = HtmlComponentUtil.HTML_WIDTH_ATTR + " : "
				+ (listWidth == null ? DEFAULT_WIDTH : listWidth) + ";"
				+ HtmlComponentUtil.HTML_HEIGHT_ATTR + " : "
				+ (listHeight == null ? DEFAULT_HEIGHT : listHeight) + ";";

		div.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, divStyle);

		nsIDOMElement table = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
		ComponentUtil.copyAttributes(sourceNode, table);
		table.removeAttribute(HtmlComponentUtil.HTML_ATR_HEIGHT);
		table.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, "width: 100%;");
		div.appendChild(table);

		ComponentUtil.setCSSLink(pageContext, "orderingList/orderingList.css",
				"richFacesOrderingList");
		String tableClass = sourceElement
				.getAttribute(HtmlComponentUtil.HTML_STYLECLASS_ATTR);
		table
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
		table.appendChild(colgroup);

		// Encode Caption
		encodeCaption(creationData, sourceElement, visualDocument, table);

		// Encode Header
		Element header = ComponentUtil.getFacet(sourceElement, HEADER);
		ArrayList<Element> columnsHeaders = getColumnsWithFacet(columns, HEADER);
		if (header != null || !columnsHeaders.isEmpty()) {
			nsIDOMElement thead = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_THEAD);
			table.appendChild(thead);
			String headerClass = (String) sourceElement
					.getAttribute(HEADER_CLASS);
			if (header != null) {
				encodeTableHeaderFacet(creationData, thead, columnsLength,
						visualDocument, header,
						"dr-table-header rich-table-header",
						"dr-table-header-continue rich-table-header-continue",
						"dr-table-headercell rich-table-headercell",
						headerClass, HtmlComponentUtil.HTML_TAG_TD);
			}
			if (!columnsHeaders.isEmpty()) {
				nsIDOMElement tr = visualDocument
						.createElement(HtmlComponentUtil.HTML_TAG_TR);
				thead.appendChild(tr);
				
				String styleClass = encodeStyleClass(null,
						"dr-table-subheader rich-table-subheader", null,
						headerClass);
				if (styleClass != null) {
					tr.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
							styleClass);
				}
				encodeHeaderFacets(creationData, tr, visualDocument,
						columnsHeaders,
						"rich-ordering-list-table-header-cell",
						headerClass, HEADER, HtmlComponentUtil.HTML_TAG_TD);
			}
		}

		nsIDOMElement tbody = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_TBODY);
		table.appendChild(tbody);

		// Create mapping to Encode body
		for (int i = 0; i < NUM_ROW; i++) {
			List<Node> children = ComponentUtil.getChildren(sourceElement);
			boolean firstRow = true;
			nsIDOMElement tr = null;
			VpeChildrenInfo trInfo = null;
			for (Node child : children) {
				if (child.getNodeName().endsWith(":column")) {
					String breakBefore = ((Element) child)
							.getAttribute("breakBefore");
					if (breakBefore != null
							&& breakBefore.equalsIgnoreCase("true")) {
						tr = null;
					}
					if (tr == null) {
						tr = visualDocument
								.createElement(HtmlComponentUtil.HTML_TAG_TR);
						if (firstRow) {
							tr.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
									"dr-table-firstrow rich-table-firstrow");
							firstRow = false;
						} else {
							tr.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
									"dr-table-row rich-table-row");
						}
						trInfo = new VpeChildrenInfo(tr);
						tbody.appendChild(tr);
						creationData.addChildrenInfo(trInfo);
					}
					trInfo.addSourceChild(child);
				} else if (child.getNodeName().endsWith(":columnGroup")) {
					RichFacesColumnGroupTemplate.DEFAULT_INSTANCE.encode(
							creationData, (Element) child, visualDocument,
							tbody);
					tr = null;
				} else if (child.getNodeName().endsWith(":subTable")) {
					RichFacesSubTableTemplate.DEFAULT_INSTANCE.encode(
							creationData, (Element) child, visualDocument,
							tbody);
					tr = null;
				} else {
					VpeChildrenInfo childInfo = new VpeChildrenInfo(tbody);
					childInfo.addSourceChild(child);
					creationData.addChildrenInfo(childInfo);
					tr = null;
				}
			}
		}

		return creationData;
	}

	/**
	 * Creates control button with image and label.
	 * 
	 * @param visualDocument
	 *            visual document
	 * @param btnName
	 *            the button label
	 * @param imgName
	 *            path to the image
	 * @param showButtonLabels
	 *            show button label flag
	 * 
	 * @return the button
	 */
	private nsIDOMElement createButtonDiv(nsIDOMDocument visualDocument,
			String btnName, String imgName, boolean showButtonLabels) {
		
		nsIDOMElement div1 = visualDocument
			.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		nsIDOMElement div2 = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		nsIDOMElement a = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_A);
		nsIDOMElement div3 = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		nsIDOMElement img = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_IMG);
		
		div1.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "rich-ordering-control");
		div2.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "rich-ordering-list-button");
		
		a.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "rich-ordering-list-button-selection");
		div3.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "rich-ordering-list-button-content");
		
		String  resourceFolder = RichFacesTemplatesActivator.getPluginResourcePath();
		img.setAttribute("src", "file://" + resourceFolder + imgName);
		
		String divStyle = "background-image: url(file://" + resourceFolder + BUTTON_BG + ");";
		div2.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, divStyle);

		div1.appendChild(div2);
		div2.appendChild(a);
		a.appendChild(div3);
		
		div3.appendChild(img);
		if (showButtonLabels) {
			div3.appendChild(visualDocument.createTextNode(btnName));
		}
		
		return div1;
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
	public static void encodeHeaderFacets(VpeCreationData creationData,
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
			td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, styleClass);
			
			nsIDOMElement div1 = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_DIV);
			String resourceFolder = RichFacesTemplatesActivator
					.getPluginResourcePath();
			String div1Style = "background-image: url(file://" + resourceFolder
					+ HEADER_CELL_BG + ");";
			div1.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					"rich-ordering-list-table-header-cell");
			div1.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, div1Style);
			td.appendChild(div1);

			td.setAttribute("scop", "col");
			String colspan = column
					.getAttribute(HtmlComponentUtil.HTML_TABLE_COLSPAN);
			if (colspan != null && colspan.length() > 0) {
				td.setAttribute(HtmlComponentUtil.HTML_TABLE_COLSPAN, colspan);
			}
			Element facetBody = ComponentUtil.getFacet(column, facetName);

			VpeChildrenInfo child = new VpeChildrenInfo(div1);
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
	protected void encodeTableHeaderFacet(VpeCreationData creationData,
			nsIDOMElement parentTheadOrTfood, int columns,
			nsIDOMDocument visualDocument, Element facetBody,
			String skinFirstRowClass, String skinRowClass,
			String skinCellClass, String facetBodyClass, String element) {
		boolean isColumnGroup = facetBody.getNodeName()
				.endsWith(":columnGroup");
		boolean isSubTable = facetBody.getNodeName().endsWith(":subTable");
		if (isColumnGroup) {
			RichFacesColumnGroupTemplate.DEFAULT_INSTANCE.encode(creationData,
					facetBody, visualDocument, parentTheadOrTfood);
		} else if (isSubTable) {
			RichFacesSubTableTemplate.DEFAULT_INSTANCE.encode(creationData,
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

			if (columns > 0) {
				td.setAttribute(HtmlComponentUtil.HTML_TABLE_COLSPAN, String
						.valueOf(columns));
			}
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
			if ((child instanceof Element)
					&& child.getNodeName().endsWith(":column")) {
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
		Integer span = null;
		try {
			span = Integer.valueOf(sourceElement.getAttribute("columns"));
		} catch (Exception e) {
			// Ignore bad attribute
		}
		if (null != span && span.intValue() != Integer.MIN_VALUE) {
			count = span.intValue();
		} else {
			// calculate max html columns count for all columns/rows children.
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
				if (column.getNodeName().endsWith(":columnGroup")) {
					// Store max calculated value of previous rows.
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
				} else if (column.getNodeName().equals(
						sourceElement.getPrefix() + ":column")) {
					String breakBeforeStr = column.getAttribute("breakBefore");
					boolean breakBefore = false;
					if (breakBeforeStr != null) {
						try {
							breakBefore = Boolean.getBoolean(breakBeforeStr);
						} catch (Exception e) {
							// Ignore bad attribute
						}
					}
					// For new row, save length of previous.
					if (breakBefore) {
						if (currentLength > count) {
							count = currentLength;
						}
						currentLength = 0;
					}
					String colspanStr = column
							.getAttribute(HtmlComponentUtil.HTML_TABLE_COLSPAN);
					Integer colspan = null;
					try {
						colspan = Integer.valueOf(colspanStr);
					} catch (Exception e) {
						// Ignore
					}
					// Append colspan of this column
					if (null != colspan
							&& colspan.intValue() != Integer.MIN_VALUE) {
						currentLength += colspan.intValue();
					} else {
						currentLength++;
					}
				} else if (column.getNodeName().endsWith(":column")) {
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
}
