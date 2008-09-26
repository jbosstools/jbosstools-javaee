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

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.interfaces.nsIDOMText;
import org.mozilla.xpcom.XPCOMException;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesDropDownMenuTemplate extends VpeAbstractTemplate {
	
	/*
	 * rich:dropDownMenu constants
	 */
	private final static String COMPONENT_NAME = "dropDownMenu"; //$NON-NLS-1$
	private final static String STYLE_PATH = "dropDownMenu/dropDownMenu.css"; //$NON-NLS-1$
	private static final String LABEL_FACET_NAME = "label"; //$NON-NLS-1$
	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final String SPACE = " "; //$NON-NLS-1$

	/*
	 * rich:dropDownMenu css styles names
	 */
	private static final String CSS_RICH_DDMENU_LABEL = "rich-ddmenu-label"; //$NON-NLS-1$
	private static final String CSS_RICH_DDMENU_LABEL_UNSELECT = "rich-ddmenu-label-unselect"; //$NON-NLS-1$
	private static final String CSS_RICH_DDMENU_LABEL_SELECT = "rich-ddmenu-label-select"; //$NON-NLS-1$
	private static final String CSS_RICH_DDMENU_LABEL_DISABLED = "rich-ddmenu-label-disabled"; //$NON-NLS-1$
	private static final String CSS_RICH_LABEL_TEXT_DECOR = "rich-label-text-decor"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_LIST_BORDER = "rich-menu-list-border"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_LIST_BG = "rich-menu-list-bg"; //$NON-NLS-1$
	private static final String CSS_RICH_DDEMENU_LIST_DIV_STYLE = "position: relative; z-index: 100; display: table;"; //$NON-NLS-1$
	private static final String CSS_RICH_DDEMENU_BORDER_DIV_STYLE = "position: relative; z-index: 2; display: table;"; //$NON-NLS-1$
	
	/*
	 * rich:dropDownMenu attributes names
	 */
	private static final String DIRECTION = "direction"; //$NON-NLS-1$
	private static final String HORIZONTAL_OFFCET = "horizontalOffset"; //$NON-NLS-1$
	private static final String JOINT_POINT = "jontPoint"; //$NON-NLS-1$
	private static final String POPUP_WIDTH = "popupWidth"; //$NON-NLS-1$
	private static final String VERTICAL_OFFSET = "verticalOffset"; //$NON-NLS-1$
	
	/*
	 * rich:menuGroup css styles and classes attributes names
	 */
	private static final String DISABLED_ITEM_CLASS = "disabledItemClass"; //$NON-NLS-1$
	private static final String DISABLED_ITEM_STYLE = "disabledItemStyle"; //$NON-NLS-1$
	private static final String DISABLED_LABEL_CLASS = "disabledLabelClass"; //$NON-NLS-1$
	private static final String ITEM_CLASS = "itemClass"; //$NON-NLS-1$
	private static final String ITEM_STYLE = "itemStyle"; //$NON-NLS-1$
	private static final String SELECED_LABEL_CLASS = "selectedLabelClass"; //$NON-NLS-1$
	private static final String SELECT_ITEM_CLASS = "selectItemClass"; //$NON-NLS-1$
	
	/*
	 * rich:dropDownMenu attributes
	 */
	private String ddm_direction;
	private String ddm_disabled;
	private String ddm_horizontalOffset;
	private String ddm_jointPoint;
	private String ddm_popupWidth;
	private String ddm_verticalOffset;

	/*
	 * rich:dropDownMenu css styles and classes attributes
	 */
	private String ddm_disabledItemClass;
	private String ddm_disabledItemStyle;
	private String ddm_disabledLabelClass;
	private String ddm_itemClass;
	private String ddm_itemStyle;
	private String ddm_selectedLabelClass;
	private String ddm_selectItemClass;
	private String ddm_style;
	private String ddm_styleClass;

	private nsIDOMElement storedVisualMenu = null;
	private int ddmId = 1;
	
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
	    VpeCreationData creationData = null;
	    Element sourceElement = (Element)sourceNode;
	    
	    Element srcNode = null;
        
        if ((sourceElement.getUserData(VpeVisualDomBuilder.SRC_NODE) != null)
                && (sourceElement.getUserData(VpeVisualDomBuilder.SRC_NODE) instanceof Element)) {
            srcNode = (Element) sourceElement.getUserData(VpeVisualDomBuilder.SRC_NODE);
        }
	    ComponentUtil.setCSSLink(pageContext, STYLE_PATH, COMPONENT_NAME);
	    readDropDownMenuAttributes(sourceElement);
	    
	    
		/*
		 * DropDownMenu component structure.
		 * In order of  nesting.
		 */
		nsIDOMElement ddmLabelDiv;
		nsIDOMElement ddmTextSpan;
		nsIDOMText ddmLabelText;
		nsIDOMElement ddmListDiv;
		nsIDOMElement ddmListBorderDiv;
		nsIDOMElement ddmListBgDiv;
		
		/*
		 * Creating visual elements
		 */
		ddmLabelDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		ddmTextSpan = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_SPAN);
		ddmLabelText = visualDocument.createTextNode(""); //$NON-NLS-1$
		ddmListDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		ddmListBorderDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		ddmListBgDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		creationData = new VpeCreationData(ddmLabelDiv);
		storedVisualMenu = ddmLabelDiv;
		
		/*
		 * Nesting elements
		 */
		ddmLabelDiv.appendChild(ddmTextSpan);
		ddmTextSpan.appendChild(ddmLabelText);
		ddmLabelDiv.appendChild(ddmListDiv);
		ddmListDiv.appendChild(ddmListBorderDiv);
		ddmListBorderDiv.appendChild(ddmListBgDiv);
		
		/*
		 * Setting css classes
		 */
		String labelDivClass = EMPTY;
		String listBorderDivClass = EMPTY;

		labelDivClass += SPACE +  CSS_RICH_DDMENU_LABEL + SPACE + CSS_RICH_DDMENU_LABEL_UNSELECT;
		listBorderDivClass += SPACE + CSS_RICH_MENU_LIST_BORDER;
		
		if (attrPresents(ddm_styleClass)) {
			labelDivClass += SPACE + ddm_styleClass;
			listBorderDivClass += SPACE + ddm_styleClass;
		}
		
		ddmLabelDiv.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, labelDivClass);
		ddmTextSpan.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, CSS_RICH_LABEL_TEXT_DECOR);
		ddmListBorderDiv.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, listBorderDivClass);
		ddmListBgDiv.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, CSS_RICH_MENU_LIST_BG);
		
		/*
		 * Setting css styles
		 */
		String cssListDivStyle = EMPTY;
		String cssListBorderDivStyle = EMPTY;
		String cssLabelDivStyle = EMPTY;
		
		cssListDivStyle += SPACE + CSS_RICH_DDEMENU_LIST_DIV_STYLE;
		cssListBorderDivStyle += SPACE + CSS_RICH_DDEMENU_BORDER_DIV_STYLE;
		
		if (attrPresents(ddm_style)) {
			cssLabelDivStyle += SPACE + ddm_style;
		}
		
		ddmListDiv.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, cssListDivStyle);
		ddmListBorderDiv.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, cssListBorderDivStyle);
		ddmLabelDiv.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, cssLabelDivStyle);
		
		/*
		 * Encoding label value
		 */
		final Element passedElement = (srcNode != null ? srcNode : sourceElement);
		final Element labelFacet = ComponentUtil.getFacet(passedElement, LABEL_FACET_NAME);
		if (null != labelFacet) {
			VpeChildrenInfo childrenInfo = new VpeChildrenInfo(ddmTextSpan);
			childrenInfo.addSourceChild(labelFacet);
			creationData.addChildrenInfo(childrenInfo);
		} else {
			Attr valueAttr = sourceElement.getAttributeNode(HtmlComponentUtil.HTML_VALUE_ATTR);
	    	String labelValue = valueAttr != null && valueAttr.getValue() != null
		    			? valueAttr.getValue()
		    			: EMPTY;
		    ddmLabelText.setNodeValue(labelValue);
		}

		
		/*
		 * Adding child nodes
		 */
		List<Node> children = ComponentUtil.getChildren(passedElement);
		int groupCount = 1;
		for (Node child : children) {
			if (child.getNodeType() == Node.ELEMENT_NODE
					&& child.getNodeName().endsWith(":menuGroup")) { //$NON-NLS-1$
				child.setUserData(RichFacesMenuGroupTemplate.MENU_GROUP_ID,
						String.valueOf(groupCount), null);
				groupCount++;
			}
			nsIDOMElement childDiv = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_DIV);
			ddmListBgDiv.appendChild(childDiv);
			VpeChildrenInfo childDivInfo = new VpeChildrenInfo(childDiv);
			childDivInfo.addSourceChild(child);
			creationData.addChildrenInfo(childDivInfo);
		}
			
		return creationData;
	}

	    @Override
	    public void validate(VpePageContext pageContext, Node sourceNode,
		    nsIDOMDocument visualDocument, VpeCreationData data) {
		super.validate(pageContext, sourceNode, visualDocument, data);
		List<nsIDOMElement> children = getChildren(storedVisualMenu);
		
//		storedVisualMenu.setAttribute(VpeVisualDomBuilder.VPE_USER_MOUSE_OVER_ID, String.valueOf(ddmId));
//		applyAttributeValueOnChildren(VpeVisualDomBuilder.VPE_USER_MOUSE_OVER_ID, String.valueOf(ddmId), children);
//		applyAttributeValueOnChildren(
//				VpeVisualDomBuilder.VPE_USER_MOUSE_OVER_LOOKUP_PARENT,
//				"true", children); //$NON-NLS-1$
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

		/**
		 * Read attributes from the source element.
		 * 
		 * @param sourceNode the source node
		 */
		private void readDropDownMenuAttributes(Element sourceElement) {
			if (null == sourceElement) {
				return;
			}
			
			ddm_direction = sourceElement.getAttribute(DIRECTION);
			ddm_disabled = sourceElement.getAttribute(HtmlComponentUtil.HTML_ATTR_DISABLED);
			ddm_horizontalOffset = sourceElement.getAttribute(HORIZONTAL_OFFCET);
			ddm_jointPoint = sourceElement.getAttribute(JOINT_POINT);
			ddm_popupWidth = sourceElement.getAttribute(POPUP_WIDTH);
			ddm_verticalOffset = sourceElement.getAttribute(VERTICAL_OFFSET);

			ddm_disabledItemClass = sourceElement.getAttribute(DISABLED_ITEM_CLASS);
			ddm_disabledItemStyle = sourceElement.getAttribute(DISABLED_ITEM_STYLE);
			ddm_disabledLabelClass = sourceElement.getAttribute(DISABLED_LABEL_CLASS);
			ddm_itemClass = sourceElement.getAttribute(ITEM_CLASS);
			ddm_itemStyle = sourceElement.getAttribute(ITEM_STYLE);
			ddm_selectedLabelClass = sourceElement.getAttribute(SELECED_LABEL_CLASS);
			ddm_selectItemClass = sourceElement.getAttribute(SELECT_ITEM_CLASS);
			ddm_style = sourceElement.getAttribute(HtmlComponentUtil.HTML_STYLE_ATTR);
			ddm_styleClass = sourceElement.getAttribute(HtmlComponentUtil.HTML_STYLECLASS_ATTR);
		}
	
	    /**
	     * Checks is attribute have some value.
	     * 
	     * @param attr the attribute
	     * 
	     * @return true, if successful
	     */
	    private static boolean attrPresents(String attr) {
			return ((null != attr) && (!EMPTY.equalsIgnoreCase(attr)));
		}
		
		public void onMouseOver(VpeVisualDomBuilder visualDomBuilder, Node sourceNode, String mouseOverId) {
			// TODO Auto-generated method stub
//			visualDomBuilder.updateNode(sourceNode);
		}

		public void stopMouseOver(Node sourceNode) {
			// TODO Auto-generated method stub
			
		}
	    
	    
}