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

import java.util.List;
import java.util.Map;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.ResourceUtil;
import org.jboss.tools.vpe.editor.util.SourceDomUtil;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesTabTemplate extends VpeAbstractTemplate {

	public static final String TAB_HEADER_ATTR = "tabheaderattr"; //$NON-NLS-1$
	public static final String YES = "yes"; //$NON-NLS-1$
	public static final String DISABLED_ELEMENT_STYLE = "none"; //$NON-NLS-1$
	public static final String TAB_BODY_ATTR = "tabbodyattr"; //$NON-NLS-1$
	
	private final static String SPACER_FILE_PATH = "common/spacer.gif"; //$NON-NLS-1$
	private final static String BORDER_FILE_PATH = "tabPanel/border.gif"; //$NON-NLS-1$
	
	private final static String VPE_USER_TOGGLE_ID = "vpe-user-toggle-id"; //$NON-NLS-1$

	private static final String DISABLED = "disabled"; //$NON-NLS-1$
	private static final String LABEL_WIDTH = "labelWidth"; //$NON-NLS-1$
	
	private static final String CSS_HEADER = "rich-tab-header"; //$NON-NLS-1$
	private static final String CSS_LABEL = "rich-tab-label"; //$NON-NLS-1$
	private static final String CSS_ACTIVE = "rich-tab-active"; //$NON-NLS-1$
	private static final String CSS_INACTIVE = "rich-tab-inactive"; //$NON-NLS-1$
	private static final String CSS_DISABLED = "rich-tab-disabled"; //$NON-NLS-1$
	
	private static final String ZERO = "0"; //$NON-NLS-1$
	private static final String ONE = "1"; //$NON-NLS-1$
	private static final String TEN = "10"; //$NON-NLS-1$
	private static final String SPACE = " "; //$NON-NLS-1$
	private static final String EMPTY = ""; //$NON-NLS-1$
	
	private static final String HUNDRED_PERCENTS = "100%"; //$NON-NLS-1$
	private static final String HEIGHT_STYLE_NAME = "height: "; //$NON-NLS-1$
	private static final String WIDTH_STYLE_NAME = "width: "; //$NON-NLS-1$
	private static final String STYLE_SEMICOLUMN = "; "; //$NON-NLS-1$
	private static final String PX = "px"; //$NON-NLS-1$
	
	private static final String BODY_TABLE_STYLE = "position: relative; z-index: 1;"; //$NON-NLS-1$
	private static final String HEADER_TABLE_STYLE = "height : 100%; position : relative; z-index : 2;"; //$NON-NLS-1$
	private static final String HEADER_TD_STYLE = "height: 100%; vertical-align: bottom;"; //$NON-NLS-1$
	private static final String TRUE = "true"; //$NON-NLS-1$
	
	/**
	 * Encode body of tab
	 * @param creationData
	 * @param sourceElement
	 * @param visualDocument
	 * @param parentTr
	 * @param active
	 * @param contentClass
	 * @param contentStyle
	 * @return the tab body
	 */
	public static VpeCreationData encodeBody(
			VpePageContext pageContext,
			VpeCreationData creationData, 
			Element sourceElement, 
			nsIDOMDocument visualDocument, 
			nsIDOMElement parentTr, 
			boolean active,
			String contentClass,
			String contentStyle) {
	    	nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);

		if(creationData==null) {
			creationData = new VpeCreationData(td);
		} else {
			parentTr.appendChild(td);
		}
		if(!active) {
			return creationData;
		}
		td.setAttribute(HTML.ATTR_HEIGHT, HUNDRED_PERCENTS);

		nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
		td.appendChild(table);
		table.setAttribute(HTML.ATTR_BORDER, ZERO);
		table.setAttribute(HTML.ATTR_CELLPADDING, TEN);
		table.setAttribute(HTML.ATTR_CELLSPACING, ZERO);
		table.setAttribute(HTML.ATTR_WIDTH, HUNDRED_PERCENTS);
		table.setAttribute(HTML.ATTR_CLASS,RichFacesTabPanelTemplate.CSS_CONTENT_POSITION);
		table.setAttribute(HTML.ATTR_STYLE, BODY_TABLE_STYLE);

		nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
		table.appendChild(tr);
		td = visualDocument.createElement(HTML.TAG_TD);
		tr.appendChild(td);
		td.setAttribute(HTML.ATTR_CLASS, 
				ComponentUtil.getAttribute(sourceElement, RichFaces.ATTR_STYLE_CLASS)
				+ SPACE + contentClass);
		td.setAttribute(HTML.ATTR_STYLE, 
				ComponentUtil.getAttribute(sourceElement, HTML.ATTR_STYLE)
				+ STYLE_SEMICOLUMN +  contentStyle);
		
		Map<String, List<Node>> labelFacetChildren = null;
		Element labelFacet = SourceDomUtil.getFacetByName(pageContext,
				sourceElement, RichFaces.NAME_FACET_LABEL);
		if (null != labelFacet) {
			labelFacetChildren = VisualDomUtil.findFacetElements(labelFacet, pageContext);
		}
		/*
		 * https://jira.jboss.org/jira/browse/JBIDE-3373
		 * If there are some odd HTML elements from facet
		 * add them to the panel body first.
		 */
		boolean labelHtmlElementsPresents = ((labelFacetChildren != null) && (labelFacetChildren
				.get(VisualDomUtil.FACET_HTML_TAGS).size() > 0));
		VpeChildrenInfo bodyInfo = new VpeChildrenInfo(td);
		if (labelHtmlElementsPresents) {
				for (Node node : labelFacetChildren.get(VisualDomUtil.FACET_HTML_TAGS)) {
					bodyInfo.addSourceChild(node);
				}
		}
		
		/*
		 * Add the rest tab's content
		 */
		List<Node> children = ComponentUtil.getChildren(sourceElement, true);
		for (Node child : children) {
			bodyInfo.addSourceChild(child);
		}
		creationData.addChildrenInfo(bodyInfo);
		return creationData;
	}

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
		nsIDOMElement table = visualDocument.createElement(HTML.TAG_DIV); 
		VpeCreationData creationData = new VpeCreationData(table);
		nsIDOMElement headerTable = visualDocument.createElement(HTML.TAG_TABLE);
		headerTable.setAttribute(HTML.ATTR_BORDER, ZERO);
		headerTable.setAttribute(HTML.ATTR_CELLPADDING, ZERO);
		headerTable.setAttribute(HTML.ATTR_CELLSPACING, ZERO);
		headerTable.setAttribute(TAB_HEADER_ATTR, YES);
		headerTable.setAttribute(HTML.ATTR_STYLE,
				HTML.STYLE_PARAMETER_DISPLAY
						+ ":" + DISABLED_ELEMENT_STYLE + STYLE_SEMICOLUMN); //$NON-NLS-1$
		headerTable.appendChild(encodeHeader(pageContext, creationData,
				(Element) sourceNode, visualDocument, table, false, EMPTY,
				EMPTY, EMPTY, EMPTY, EMPTY));
		nsIDOMElement bodyTable = visualDocument.createElement(HTML.TAG_TABLE);
		bodyTable.setAttribute(HTML.ATTR_BORDER, ZERO);
		bodyTable.setAttribute(HTML.ATTR_CELLPADDING, ZERO);
		bodyTable.setAttribute(HTML.ATTR_CELLSPACING, ZERO);
		bodyTable.setAttribute(TAB_BODY_ATTR, YES); 
		table.appendChild(headerTable);
		table.appendChild(bodyTable);
		encodeBody(pageContext, creationData, (Element)sourceNode, visualDocument, bodyTable, true, EMPTY, EMPTY);
		return creationData;

	}

	/**
	 * Encode Header of tab
	 * @param sourceElement
	 * @param visualDocument
	 * @param parentTr
	 * @param active
	 * @param activeTabClass
	 * @param inactiveTabClass
	 * @param disabledTabClass
	 */
	public static nsIDOMElement encodeHeader(
			VpePageContext pageContext,
			VpeCreationData creationData,
			Element sourceElement, 
			nsIDOMDocument visualDocument,
			nsIDOMElement parentTr,
			boolean active,
			String headerClass,
			String activeTabClass,
			String inactiveTabClass,
			String disabledTabClass, 
			String toggleId) {
		nsIDOMElement headerTd = visualDocument.createElement(HTML.TAG_TD);
		parentTr.appendChild(headerTd);
		headerTd.setAttribute(HTML.ATTR_STYLE, HEADER_TD_STYLE);
		String styleClass = RichFacesTabPanelTemplate.CSS_CELL_DISABLED
			+	SPACE + CSS_DISABLED;
		if(!TRUE.equalsIgnoreCase(sourceElement.getAttribute(DISABLED))) {
			if(active) {
				styleClass = RichFacesTabPanelTemplate.CSS_CELL_ACTIVE;
			} else {
				styleClass = RichFacesTabPanelTemplate.CSS_CELL_INACTIVE;
			}
		}
		headerTd.setAttribute(HTML.ATTR_CLASS, styleClass);
		headerTd.setAttribute(VPE_USER_TOGGLE_ID, toggleId);

		nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
		headerTd.appendChild(table);
		table.setAttribute(HTML.ATTR_BORDER, ZERO);
		table.setAttribute(HTML.ATTR_CELLPADDING, ZERO);
		table.setAttribute(HTML.ATTR_CELLSPACING, ZERO);
		table.setAttribute(HTML.ATTR_STYLE, HEADER_TABLE_STYLE);
		table.setAttribute(VPE_USER_TOGGLE_ID, toggleId);

		nsIDOMElement mainTr = visualDocument.createElement(HTML.TAG_TR);
		table.appendChild(mainTr);
		encodeSpacer(mainTr, visualDocument);

		nsIDOMElement mainTd = visualDocument.createElement(HTML.TAG_TD);
		mainTr.appendChild(mainTd);
		mainTd.setAttribute(VPE_USER_TOGGLE_ID, toggleId);

		table = visualDocument.createElement(HTML.TAG_TABLE);
		mainTd.appendChild(table);
		
		String labelWidth = ComponentUtil.getAttribute(sourceElement, LABEL_WIDTH);
		String tableStyle = HEIGHT_STYLE_NAME + HUNDRED_PERCENTS + STYLE_SEMICOLUMN;
		if ((null != labelWidth) && (!EMPTY.equalsIgnoreCase(labelWidth))) {
			int val = -1;
			try {
				val = Integer.parseInt(labelWidth);
			} catch (NumberFormatException e) {
				/*
				 * Ignore
				 */
			}
			if (val > 0) {
				labelWidth = val + PX;
			}
		} else {
			labelWidth = HUNDRED_PERCENTS;
		}
		tableStyle += WIDTH_STYLE_NAME + labelWidth + STYLE_SEMICOLUMN;
		table.setAttribute(HTML.ATTR_STYLE, tableStyle);
		table.setAttribute(HTML.ATTR_BORDER, ZERO);
		table.setAttribute(HTML.ATTR_CELLPADDING, ZERO);
		table.setAttribute(HTML.ATTR_CELLSPACING, ZERO);
		table.setAttribute(VPE_USER_TOGGLE_ID, toggleId);

		nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
		table.appendChild(tr);
		mainTd = visualDocument.createElement(HTML.TAG_TD);
		tr.appendChild(mainTd);

		styleClass = CSS_HEADER
			+ SPACE + CSS_LABEL
			+ SPACE + CSS_DISABLED
			+ SPACE + disabledTabClass;

		if(!TRUE.equalsIgnoreCase(sourceElement.getAttribute(DISABLED))) {
			if(active) {
				styleClass = CSS_HEADER
					+ SPACE + CSS_LABEL
					+ SPACE + CSS_ACTIVE
					+ SPACE + activeTabClass;
			} else {
				styleClass = CSS_HEADER
					+ SPACE + CSS_LABEL
					+ SPACE + CSS_INACTIVE
					+ SPACE + inactiveTabClass;
			}
		}
		String tabStyleClass = ComponentUtil.getAttribute(sourceElement, RichFaces.ATTR_STYLE_CLASS);
		styleClass += SPACE + headerClass + SPACE + tabStyleClass;
		mainTd.setAttribute(HTML.ATTR_CLASS, styleClass);
		
		mainTd.setAttribute(VPE_USER_TOGGLE_ID, toggleId);
		
		/*
		 * https://jira.jboss.org/jira/browse/JBIDE-3373
		 * Encode the Label Facet
		 * Find elements from the f:facet 
		 */
		Element labelFacet = SourceDomUtil.getFacetByName(pageContext,
				sourceElement, RichFaces.NAME_FACET_LABEL);
		if (null != labelFacet) {
			/*
			 * By adding attribute VPE-FACET to this visual node 
			 * we force JsfFacet to be rendered inside it
			 * without creating an additional and superfluous visual tag.
			 */
			mainTd.setAttribute(VpeVisualDomBuilder.VPE_FACET, RichFaces.NAME_FACET_LABEL);
			VpeChildrenInfo labelInfo = new VpeChildrenInfo(mainTd);
			labelInfo.addSourceChild(labelFacet);
			creationData.addChildrenInfo(labelInfo);
		} else if (sourceElement.hasAttribute(RichFaces.ATTR_LABEL)) {
			Attr labelAttr = sourceElement.getAttributeNode(RichFaces.ATTR_LABEL);
			if (null != labelAttr) {
				String bundleValue = ResourceUtil.getBundleValue(pageContext, labelAttr.getValue());
				mainTd.appendChild(visualDocument.createTextNode(bundleValue));
			}
		} else {
			char space = 160;
			mainTd.appendChild(visualDocument.createTextNode(EMPTY + space));
		}
		encodeSpacer(mainTr, visualDocument);
		return headerTd;
	}

	/*
	 * Add <td class="dr-tbpnl-tbbrdr rich-tabhdr-side-border"><img src="#{spacer}" width="1" height="1" alt="" border="0" /></td>
	 */
	private static void encodeSpacer(nsIDOMElement parentTr, nsIDOMDocument visualDocument) {
	    	nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
		parentTr.appendChild(td);
		td.setAttribute(HTML.ATTR_CLASS, 
				 RichFacesTabPanelTemplate.CSS_SIDE_CELL
				 + SPACE + 
				 RichFacesTabPanelTemplate.CSS_SIDE_BORDER);
		String style = ComponentUtil.getBackgoundImgStyle(BORDER_FILE_PATH); 
		td.setAttribute(HTML.ATTR_STYLE, style);
		nsIDOMElement img = visualDocument.createElement(HTML.TAG_IMG);
		td.appendChild(img);
		ComponentUtil.setImg(img, SPACER_FILE_PATH);
		img.setAttribute(HTML.ATTR_WIDTH, ONE);
		img.setAttribute(HTML.ATTR_HEIGHT, ONE);
		img.setAttribute(HTML.ATTR_BORDER, ZERO);

	}

}