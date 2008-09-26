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

import java.util.List;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesMenuItemTemplate extends VpeAbstractTemplate {

	/*
	 * rich:menuItem constants
	 */
	private final static String COMPONENT_NAME = "menuItem"; //$NON-NLS-1$
	private final static String STYLE_PATH = "menuItem/menuItem.css"; //$NON-NLS-1$
	private static final String SPACER_IMG_PATH = "menuItem/spacer.gif"; //$NON-NLS-1$
	private static final String ICON_FACET_NAME = "icon"; //$NON-NLS-1$
	private static final String ICON_DISABLED_FACET_NAME = "iconDisabled"; //$NON-NLS-1$
	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final String SPACE = " "; //$NON-NLS-1$
	
	/*
	 * rich:menuItem css styles names
	 */
	private static final String CSS_RICH_MENU_ITEM = "rich-menu-item"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_LABEL = "rich-menu-item-label"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_ICON = "rich-menu-item-icon"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_DISABLED = "rich-menu-item-disabled"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_ENABLED = "rich-menu-item-enabled"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_HOVER = "rich-menu-item-hover"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_LABEL_DISBLED = "rich-menu-item-label-disabled"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_ICON_DISABLED = "rich-menu-item-icon-disabled"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_LABEL_ENABLED = "rich-menu-item-label-enabled"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_ICON_ENABLED = "rich-menu-item-icon-enabled"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_LABEL_SELECTED = "rich-menu-item-label-selected"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_ICON_SELECTED = "rich-menu-item-icon-selected"; //$NON-NLS-1$
	
	/*
	 * rich:menuItem attributes names
	 */
	private static final String ICON = "icon"; //$NON-NLS-1$

	/*
	 * rich:menuItem css styles and classes attributes names
	 */
	private static final String ICON_CLASS = "iconClass"; //$NON-NLS-1$
	private static final String ICON_DISABLED = "iconDisabled"; //$NON-NLS-1$
	private static final String ICON_STYLE = "iconStyle"; //$NON-NLS-1$
	private static final String LABEL_CLASS = "labelClass"; //$NON-NLS-1$
	private static final String SELECT_STYLE = "selectStyle"; //$NON-NLS-1$
	private static final String SELECT_CLASS = "selectClass"; //$NON-NLS-1$
	
	/*
	 * rich:menuItem attributes 
	 */
	private String mi_disabled;
	private String mi_icon;
	private String mi_value;
	
	/*
	 * rich:menuItem css styles and classes attributes
	 */
	private String mi_iconClass;
	private String mi_iconDisabled;
	private String mi_iconStyle;
	private String mi_labelClass;
	private String mi_selectClass;
	private String mi_selectStyle;
	private String mi_style;
	private String mi_styleClass;
	
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		VpeCreationData creationData = null;
		Element sourceElement = (Element)sourceNode;
		ComponentUtil.setCSSLink(pageContext, STYLE_PATH, COMPONENT_NAME);
		readMenuItemAttributes(sourceElement);
		
		/*
		 * MenuItem component structure.
		 * In order of  nesting.
		 */
		nsIDOMElement itemTopDiv;
		nsIDOMElement itemIconImgSpan;
		nsIDOMElement itemIconImg;
		nsIDOMElement itemLabelSpan;
		nsIDOMText itemLabelText;
		
		/*
		 * Creating visual elements
		 */
		itemTopDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		itemIconImgSpan = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_SPAN);
		itemIconImg = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);
		itemLabelSpan = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_SPAN);
		itemLabelText = visualDocument.createTextNode(""); //$NON-NLS-1$
		creationData = new VpeCreationData(itemTopDiv);
		
		/*
		 * Nesting elements
		 */
		itemTopDiv.appendChild(itemIconImgSpan);
		itemTopDiv.appendChild(itemLabelSpan);
		itemLabelSpan.appendChild(itemLabelText);
		
		/*
		 * Setting css classes
		 */
		String topDivClass = EMPTY;
		String iconImgSpanClass = EMPTY;
		String labelSpanClass = EMPTY;
		
		topDivClass += SPACE + CSS_RICH_MENU_ITEM;
		iconImgSpanClass += SPACE + CSS_RICH_MENU_ITEM_ICON;
		labelSpanClass += SPACE + CSS_RICH_MENU_ITEM_LABEL;
		
		if (attrPresents(mi_styleClass)) {
			topDivClass += SPACE + mi_styleClass;
		}
		if (attrPresents(mi_iconClass)) {
			iconImgSpanClass += SPACE + mi_iconClass;
		}
		if (attrPresents(mi_labelClass)) {
			labelSpanClass += SPACE + mi_labelClass;
		}
		
		itemTopDiv.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, topDivClass);
		itemIconImgSpan.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, iconImgSpanClass);
		itemLabelSpan.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, labelSpanClass);

		/*
		 * Setting css styles
		 */
		String topDivStyle = EMPTY;
		
		if (attrPresents(mi_style)) {
			topDivStyle += SPACE + mi_style;
		}
		
		itemTopDiv.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, topDivStyle);
		
		/*
		 * Encode label and icon value
		 */
		Attr valueAttr = sourceElement.getAttributeNode(HtmlComponentUtil.HTML_VALUE_ATTR);
    	String labelValue = valueAttr != null
				&& valueAttr.getValue() != null ? valueAttr.getValue() : ""; //$NON-NLS-1$
		itemLabelText.setNodeValue(labelValue);
		
		/*
		 * Encode icon facets
		 */
		Element iconFacet = ComponentUtil.getFacet(sourceElement, ICON_FACET_NAME);
		Element iconDisabledFacet = ComponentUtil.getFacet(sourceElement, ICON_DISABLED_FACET_NAME);
		if (null != iconFacet) {
			VpeChildrenInfo childInfo = new VpeChildrenInfo(itemIconImgSpan);
			childInfo.addSourceChild(iconFacet);
			creationData.addChildrenInfo(childInfo);
		} else {
			String iconPath = sourceElement.getAttribute(ICON);
			if (attrPresents(iconPath)) {
				/*
				 * Add path to specified image
				 */
				String imgFullPath = VpeStyleUtil.addFullPathToImgSrc(iconPath, pageContext, true);
				itemIconImg.setAttribute(HtmlComponentUtil.HTML_ATR_SRC, imgFullPath);
			} else {
				/*
				 * Create spacer image
				 */
				ComponentUtil.setImg(itemIconImg, SPACER_IMG_PATH);
			}
			/*
			 * Add image to span
			 */
			itemIconImgSpan.appendChild(itemIconImg);

		}
		
		/*
		 * Adding child nodes
		 */
		List<Node> children = ComponentUtil.getChildren(sourceElement);
		for (Node child : children) {
			VpeChildrenInfo childInfo = new VpeChildrenInfo(itemLabelSpan);
			childInfo.addSourceChild(child);
			creationData.addChildrenInfo(childInfo);
		}
		
		return creationData;
	}
	
	
	/**
	 * Read attributes from the source element.
	 * 
	 * @param sourceNode the source node
	 */
	private void readMenuItemAttributes(Element sourceElement) {
		if (null == sourceElement) {
			return;
		}
		mi_disabled = sourceElement.getAttribute(HtmlComponentUtil.HTML_ATTR_DISABLED);
		mi_icon = sourceElement.getAttribute(ICON);
		mi_value = sourceElement.getAttribute(HtmlComponentUtil.HTML_VALUE_ATTR);
		
		mi_iconClass = sourceElement.getAttribute(ICON_CLASS);
		mi_iconDisabled = sourceElement.getAttribute(ICON_DISABLED);
		mi_iconStyle = sourceElement.getAttribute(ICON_STYLE);
		mi_labelClass = sourceElement.getAttribute(LABEL_CLASS);
		mi_selectClass = sourceElement.getAttribute(SELECT_CLASS);
		mi_selectStyle = sourceElement.getAttribute(SELECT_STYLE);
		mi_style = sourceElement.getAttribute(HtmlComponentUtil.HTML_STYLE_ATTR);
		mi_styleClass = sourceElement.getAttribute(HtmlComponentUtil.HTML_STYLECLASS_ATTR);
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
    
	@Override
	public boolean isRecreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}
	
	public void onMouseOver(VpeVisualDomBuilder visualDomBuilder, Node sourceNode, String mouseOverId) {
		// TODO Auto-generated method stub
//		visualDomBuilder.updateNode(sourceNode);
	}

	public void stopMouseOver(Node sourceNode) {
		// TODO Auto-generated method stub
		
	}
}
