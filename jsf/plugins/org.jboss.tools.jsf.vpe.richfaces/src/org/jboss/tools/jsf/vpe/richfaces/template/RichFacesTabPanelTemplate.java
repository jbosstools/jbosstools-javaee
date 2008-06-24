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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeToggableTemplate;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.xpcom.XPCOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesTabPanelTemplate extends VpeAbstractTemplate implements VpeToggableTemplate {

	public static final String CSS_PANEL = "rich-tabpanel"; //$NON-NLS-1$
	public static final String CSS_CONTENT = "rich-tabpanel-content"; //$NON-NLS-1$
	public static final String CSS_CONTENT_POSITION = "rich-tabpanel-content-position"; //$NON-NLS-1$
	public static final String CSS_SIDE_BORDER = "rich-tabhdr-side-border"; //$NON-NLS-1$
	public static final String CSS_SIDE_CELL = "rich-tabhdr-side-cell"; //$NON-NLS-1$
	public static final String CSS_CELL_ACTIVE = "rich-tabhdr-cell-active"; //$NON-NLS-1$
	public static final String CSS_CELL_INACTIVE = "rich-tabhdr-cell-inactive"; //$NON-NLS-1$
	public static final String CSS_CELL_DISABLED = "rich-tabhdr-cell-disabled"; //$NON-NLS-1$
	
	private static final String RICH_FACES_TAB_PANEL = "richFacesTabPanel"; //$NON-NLS-1$
	private static final String CSS_FILE_PATH = "tabPanel/tabPanel.css"; //$NON-NLS-1$
	private static final String SPACER_FILE_PATH = "common/spacer.gif"; //$NON-NLS-1$
	private static final String INCLUDE_TAG = ":include"; //$NON-NLS-1$
	private static final String YES = "yes"; //$NON-NLS-1$

	private final String HEADER_ALINGMENT = "headerAlignment"; //$NON-NLS-1$
	private final String HEADER_SPACING = "headerSpacing"; //$NON-NLS-1$
	private final String SELECTED_TAB = "selectedTab"; //$NON-NLS-1$
	private final String DIR = "dir"; //$NON-NLS-1$
	private final String DIR_RTL = "RTL"; //$NON-NLS-1$
	private final String DIR_LTR = "LTR"; //$NON-NLS-1$
	
	private final String HEADER_CLASS = "headerClass"; //$NON-NLS-1$
	private final String CONTENT_CLASS = "contentClass"; //$NON-NLS-1$
	private final String CONTENT_STYLE = "contentStyle"; //$NON-NLS-1$
	private final String TAB_CLASS = "tabClass"; //$NON-NLS-1$
	private final String ACTIVE_TAB_CLASS = "activeTabClass"; //$NON-NLS-1$
	private final String INACTIVE_TAB_CLASS = "inactiveTabClass"; //$NON-NLS-1$
	private final String DISABLED_TAB_CLASS = "disabledTabClass"; //$NON-NLS-1$
	
	private final String ZERO = "0"; //$NON-NLS-1$
	private final String ONE = "1"; //$NON-NLS-1$
	private final String TWO = "2"; //$NON-NLS-1$
	private final String SPACE = " "; //$NON-NLS-1$
	private final String EMPTY = ""; //$NON-NLS-1$
	
	private final String TAB = ":tab"; //$NON-NLS-1$
	private final String NAME = "name"; //$NON-NLS-1$
	
	private List<nsIDOMElement> storedTabHeaders = new ArrayList<nsIDOMElement>();
	private static Map toggleMap = new HashMap();

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {

		Element sourceElement = (Element)sourceNode;

		nsIDOMElement table = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
		
		VpeCreationData creationData = new VpeCreationData(table);
		ComponentUtil.setCSSLink(pageContext, CSS_FILE_PATH, RICH_FACES_TAB_PANEL);
		setDirAttr(table, sourceElement);
		table.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, 
				ComponentUtil.getAttribute(sourceElement,	HtmlComponentUtil.HTML_STYLECLASS_ATTR)
				+ SPACE + CSS_PANEL);
		table.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR, ZERO);
		table.setAttribute(HtmlComponentUtil.HTML_CELLPADDING_ATTR, ZERO);
		table.setAttribute(HtmlComponentUtil.HTML_CELLSPACING_ATTR, ZERO);
		table.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, getStyle(sourceElement));
		
		nsIDOMElement tbody = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TBODY);
		table.appendChild(tbody);
		nsIDOMElement tr = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);
		tbody.appendChild(tr);
		nsIDOMElement td = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
		tr.appendChild(td);
		td.setAttribute(HtmlComponentUtil.HTML_ALIGN_ATTR, getHeaderAlignment(sourceElement));
		td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, ComponentUtil
				.getAttribute(sourceElement, HEADER_CLASS));
		nsIDOMElement inerTable = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
		td.appendChild(inerTable);
		inerTable.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR, ZERO);
		inerTable.setAttribute(HtmlComponentUtil.HTML_CELLPADDING_ATTR, ZERO);
		inerTable.setAttribute(HtmlComponentUtil.HTML_CELLSPACING_ATTR, ZERO);

		// Encode header
		nsIDOMElement inerTr = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);
		inerTable.appendChild(inerTr);
		nsIDOMElement inerTd = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
		inerTr.appendChild(inerTd);
		nsIDOMElement img = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);
		inerTd.appendChild(img);
		ComponentUtil.setImg(img, SPACER_FILE_PATH);
		img.setAttribute(HtmlComponentUtil.HTML_WIDTH_ATTR, TWO);
		img.setAttribute(HtmlComponentUtil.HTML_HEIGHT_ATTR, ONE);
		img.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR, ZERO);

		String headerSpacing = sourceElement.getAttribute(HEADER_SPACING);
		List<Node> children = ComponentUtil.getChildren(sourceElement);
		int activeId = getActiveId(sourceElement, children);
		int i = 0;
		for (Node child : children) {
			boolean active = (i == activeId);

			if (child.getNodeName().endsWith(INCLUDE_TAG)) {
				VpeChildrenInfo vpeChildrenInfo = new VpeChildrenInfo(inerTr);
				creationData.addChildrenInfo(vpeChildrenInfo);
				vpeChildrenInfo.addSourceChild(child);
			}
			
			if(child.getNodeName().endsWith(TAB)) {
				/*
				 * Adds spacer before first tab
				 */
				if (i == 0) {
					addSpacer(visualDocument, inerTr, headerSpacing);
				}
				
				nsIDOMElement headerTd = RichFacesTabTemplate.encodeHeader(
						pageContext,
						creationData,
						(Element) child,
						visualDocument, inerTr, active,
								ComponentUtil.getAttribute(sourceElement,	
									TAB_CLASS),
						ComponentUtil.getAttribute(sourceElement, 
								ACTIVE_TAB_CLASS)
								+ SPACE + CSS_CELL_ACTIVE,
						ComponentUtil.getAttribute(sourceElement,
								INACTIVE_TAB_CLASS)
								+ SPACE + CSS_CELL_INACTIVE,
						ComponentUtil.getAttribute(sourceElement,
								DISABLED_TAB_CLASS)
								+ SPACE + CSS_CELL_DISABLED, 
								String.valueOf(i));
				i++;
				addSpacer(visualDocument, inerTr, headerSpacing);
				storedTabHeaders.add(headerTd);
			}
		}

		inerTd = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
		inerTr.appendChild(inerTd);
		img = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);
		inerTd.appendChild(img);
		ComponentUtil.setImg(img, SPACER_FILE_PATH);
		img.setAttribute(HtmlComponentUtil.HTML_WIDTH_ATTR, ONE);
		img.setAttribute(HtmlComponentUtil.HTML_HEIGHT_ATTR, ONE);
		img.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR, ZERO);

		// Encode first child tab
		inerTr = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);
		tbody.appendChild(inerTr);
		children = ComponentUtil.getChildren(sourceElement);
		i = 0;
		for (Node child : children) {
			boolean active = (i == activeId);
			if(child.getNodeName().endsWith(TAB)) {
				i++;
				if (active) {
					RichFacesTabTemplate.encodeBody(creationData,
							(Element) child, visualDocument, inerTr, true,
							ComponentUtil.getAttribute(sourceElement,
									CONTENT_CLASS)
									+ SPACE + CSS_PANEL
									+ SPACE + CSS_CONTENT
									+ SPACE + CSS_CONTENT_POSITION,
							ComponentUtil.getAttribute(sourceElement,
									CONTENT_STYLE));
					break;
				}
			}
		}

		return creationData;
	}
	
	/**
	 * Adds the spacer.
	 * Add <td><img src="#{spacer}" height="1" alt="" border="0" style="#{this:encodeHeaderSpacing(context, component)}"/></td>
	 * 
	 * @param visualDocument the visual document
	 * @param parentTr the parent tr
	 * @param headerSpacing the header spacing
	 */
	private void addSpacer(nsIDOMDocument visualDocument, nsIDOMElement parentTr, String headerSpacing) {
		nsIDOMElement spaceTd = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
		parentTr.appendChild(spaceTd);
		nsIDOMElement spaceImg = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);
		spaceTd.appendChild(spaceImg);
		ComponentUtil.setImg(spaceImg, SPACER_FILE_PATH);
		spaceImg.setAttribute(HtmlComponentUtil.HTML_HEIGHT_ATTR, ONE);
		spaceImg.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR, ZERO);
		if(headerSpacing==null) {
			headerSpacing = ONE;
		}
		spaceImg.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, "width: " + headerSpacing + "px"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private int getActiveId(Element sourceElement, List<Node> children) {
		int activeId = -1;
		try { 
			activeId = Integer.valueOf((String)toggleMap.get(sourceElement));
		} catch (NumberFormatException nfe) {
			activeId = -1;
		}

		if (activeId == -1) {
			activeId = getTabId(children, sourceElement.getAttribute(SELECTED_TAB));
		}
		
		if (activeId == -1) 
			activeId = 0;
			
		int count = getChildrenCount(children);
		if (count - 1 < activeId) {
			activeId = count - 1;
		}
		
		return activeId;
	}
	
	private int getChildrenCount(List<Node> children) {
		int count = 0;
		for (Node child : children) {
			if (child.getNodeName().endsWith(TAB)) {
				count++;
			}
		}
		return count;
	}

	private int getTabId(List<Node> children, String tabName) {
		if (tabName == null) return -1;
		int count = 0;
		for (Node child : children) {
			if (child.getNodeName().endsWith(TAB)) {
				if (!(child instanceof Element))
					continue;
				
				String name = ((Element)child).getAttribute(NAME);
				if (tabName.equals(name))
					return count;
				
				count++;
			}
		}
		return -1;
	}

	private String getStyle(Element sourceElement) {
	     
		String widthAttrValue = sourceElement.getAttribute(HtmlComponentUtil.HTML_WIDTH_ATTR);
		String heightAttrValue = sourceElement.getAttribute(HtmlComponentUtil.HTML_HEIGHT_ATTR);
		String styleAttrValue = sourceElement.getAttribute(HtmlComponentUtil.HTML_STYLE_ATTR);
		String style = styleAttrValue != null ? styleAttrValue : EMPTY;

		if (!ComponentUtil.parameterPresent(styleAttrValue, HtmlComponentUtil.HTML_WIDTH_ATTR)) {
			String width = (widthAttrValue != null && widthAttrValue.length() > 0) ? widthAttrValue : "100%"; //$NON-NLS-1$
			style = ComponentUtil.addParameter(style, "width:" + width); //$NON-NLS-1$
		}

		if (!ComponentUtil.parameterPresent(styleAttrValue, HtmlComponentUtil.HTML_HEIGHT_ATTR)) {
			String height = (heightAttrValue != null && heightAttrValue.length() > 0) ? heightAttrValue : EMPTY;
			if (height.length() > 0) {
				style =ComponentUtil.addParameter(style, "height:" + height); //$NON-NLS-1$
			}
		}
	  
		return style;
	}
	
	
	/**
	 * Sets the dir attribute to the element.
	 * 
	 * @param element the element
	 * @param dir the dir value
	 */
	private void setDirAttr(nsIDOMElement element, Element sourceElement) {
		String dir = ComponentUtil.getAttribute(sourceElement, DIR);
		if ((null != dir) && (!EMPTY.equals(dir))){
			if ((DIR_LTR.equalsIgnoreCase(dir)) || (DIR_RTL.equalsIgnoreCase(dir))) {
				element.setAttribute(DIR, dir);
			}
		}
	}

	private String getHeaderAlignment(Element sourceElement) {
		String headerAlignment = sourceElement.getAttribute(HEADER_ALINGMENT);
		if(headerAlignment==null) {
			headerAlignment = HtmlComponentUtil.HTML_ALIGN_LEFT_VALUE; 
		}
		return headerAlignment;
	}

	public void toggle(VpeVisualDomBuilder builder, Node sourceNode, String toggleId) {
		toggleMap.put(sourceNode, toggleId);
	}

	public void stopToggling(Node sourceNode) {
		toggleMap.remove(sourceNode);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#isRecreateAtAttrChange(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Element, org.mozilla.interfaces.nsIDOMDocument, org.mozilla.interfaces.nsIDOMElement, java.lang.Object, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isRecreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}
	
	/**
	 * Is invoked after construction of all child nodes of the current visual node.
	 * @param pageContext Contains the information on edited page.
	 * @param sourceNode The current node of the source tree.
	 * @param visualDocument The document of the visual tree.
	 * @param data Object <code>VpeCreationData</code>, built by a method <code>create</code>
	 */
	public void validate(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument, VpeCreationData data) {
		
		List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();
		ComponentUtil.findAllElementsByName(data.getNode(), elements, HtmlComponentUtil.HTML_TAG_TABLE);
		for (nsIDOMNode node : elements) {
			try {
			nsIDOMElement element = (nsIDOMElement) node.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
			if (ComponentUtil.getAttribute(element, RichFacesTabTemplate.TAB_HEADER_ATTR).equalsIgnoreCase(YES)) {
				element.removeAttribute(HtmlComponentUtil.HTML_STYLE_ATTR);
			}
			if (ComponentUtil.getAttribute(element, RichFacesTabTemplate.TAB_BODY_ATTR).equalsIgnoreCase(YES)) {
				element.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, HtmlComponentUtil.CSS_DISPLAY+" : "+RichFacesTabTemplate.DISABLED_ELEMENT_STYLE+";");  //$NON-NLS-1$//$NON-NLS-2$
			}
			} catch (XPCOMException exeption) {
				// Ignore
			}
		}
		
		super.validate(pageContext, sourceNode, visualDocument, data);
		if ((storedTabHeaders == null) || (storedTabHeaders.size() < 1)){
			return;
		}
		
		for (nsIDOMElement tab : storedTabHeaders) {
			String value = tab.getAttribute(VpeVisualDomBuilder.VPE_USER_TOGGLE_ID);
			applyAttributeValueOnChildren(VpeVisualDomBuilder.VPE_USER_TOGGLE_ID, value, getChildren(tab));
			applyAttributeValueOnChildren(
					VpeVisualDomBuilder.VPE_USER_TOGGLE_LOOKUP_PARENT,
					"true", getChildren(tab)); //$NON-NLS-1$
		}
		
	}
	
	/**
	 * 	Sets the attribute to element children 
	 * @param attrName attribute name
	 * @param attrValue attribute value
	 * @param children children
	 */
	private void applyAttributeValueOnChildren(String attrName, String attrValue, List<nsIDOMElement> children) {
		if (children == null || attrName == null || attrValue == null) {
			return;
		}
		for (nsIDOMElement child : children) {
			child.setAttribute(attrName, attrValue);
			applyAttributeValueOnChildren(attrName, attrValue, getChildren(child));
		}
	}
	
	/**
	 * Gets element children
	 * @param element the element
	 * @return children
	 */
	private List<nsIDOMElement> getChildren(nsIDOMElement element) {
		List<nsIDOMElement> result = new ArrayList<nsIDOMElement>();
		if (element.hasChildNodes()) {
			nsIDOMNodeList children = element.getChildNodes();
			if (null != children) {
				long len = children.getLength();
				for (int i = 0; i < len; i++) {
					nsIDOMNode item = children.item(i);
					try {
						nsIDOMElement elem = (nsIDOMElement) item
								.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
						result.add(elem);
					} catch (XPCOMException ex) {
						// just ignore this exception
					}
				}
			}
		}
		return result;
	}
	
}