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

public class RichFacesMenuGroupTemplate extends VpeAbstractTemplate {

	public static final String MENU_GROUP_ID = "MENU-GROUP-ID"; //$NON-NLS-1$
	
	/*
	 * rich:menuGroup constants
	 */
	private final static String COMPONENT_NAME = "menuGroup"; //$NON-NLS-1$
	private final static String STYLE_PATH = "menuGroup/menuGroup.css"; //$NON-NLS-1$
	private static final String SPACER_IMG_PATH = "menuGroup/spacer.gif"; //$NON-NLS-1$
	private static final String ICON_FACET_NAME = "icon"; //$NON-NLS-1$
	private static final String ICON_DISABLED_FACET_NAME = "iconDisabled"; //$NON-NLS-1$
	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final String SPACE = " "; //$NON-NLS-1$

	/*
	 * rich:menuGroup css styles names
	 */
	private static final String CSS_RICH_MENU_GROUP = "rich-menu-group"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_GROUP_LABEL = "rich-menu-group-label"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_GROUP_FOLDER = "rich-menu-group-folder"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_GROUP_HOVER = "rich-menu-group-over"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_LABEL = "rich-menu-item-label"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_ICON = "rich-menu-item-icon"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_FOLDER = "rich-menu-item-folder"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_LABEL_DISABLED = "rich-menu-item-label-disabled"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_ICON_DISABLED = "rich-menu-item-icon-disabled"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_FOLDER_DISABLED = "rich-menu-item-folder-disabled"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_ICON_ENABLED = "rich-menu-item-icon-enabled"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_ICON_SELECTED = "rich-menu-item-icon-selected"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_LIST_BORDER = "rich-menu-list-border"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_LIST_BG = "rich-menu-list-bg"; //$NON-NLS-1$
	private static final String CSS_RICH_LIST_FOLDER_DIV_STYLE = "position: relative; z-index: 100; display: table;"; //$NON-NLS-1$
	private static final String CSS_RICH_LIST_BORDER_DIV_STYLE = "position: relative; z-index: 2; display: table;"; //$NON-NLS-1$
	
	/*
	 * rich:menuGroup attributes names
	 */
	private static final String DIRECTION = "direction"; //$NON-NLS-1$
	private static final String ICON = "icon"; //$NON-NLS-1$
	private static final String ICON_DISABLED = "iconDisabled"; //$NON-NLS-1$
	private static final String ICON_FOLDER = "iconFolder"; //$NON-NLS-1$
	private static final String ICON_FOLDER_DISABLED = "iconFolderDisabled"; //$NON-NLS-1$
	
	/*
	 * rich:menuGroup css styles and classes attributes names
	 */
	private static final String ICON_CLASS = "iconClass"; //$NON-NLS-1$
	private static final String ICON_STYLE = "iconStyle"; //$NON-NLS-1$
	private static final String LABEL_CLASS = "labelClass"; //$NON-NLS-1$
	private static final String SELECT_CLASS = "selectClass"; //$NON-NLS-1$
	private static final String SELECT_STYLE = "selectStyle"; //$NON-NLS-1$
	
	/*
	 * rich:menuGroup attributes
	 */
	private String mg_direction;
	private String mg_disabled;
	private String mg_icon;
	private String mg_iconDisabled;
	private String mg_iconFolder;
	private String mg_iconFolderDisabled;
	private String mg_value;

	/*
	 * rich:menuGroup css styles and classes attributes
	 */
	private String mg_iconClass;
	private String mg_iconStyle;
	private String mg_labelClass;
	private String mg_selectClass;
	private String mg_selectStyle;
	private String mg_style;
	private String mg_styleClass;
	
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		VpeCreationData creationData = null;
		Element sourceElement = (Element)sourceNode;
		ComponentUtil.setCSSLink(pageContext, STYLE_PATH, COMPONENT_NAME);
		readMenuGroupAttributes(sourceElement);
		
		/*
		 * MenuGroup component structure.
		 * In order of  nesting.
		 */
		nsIDOMElement grTopDiv;
		nsIDOMElement grImgSpan;
		nsIDOMElement grImg;
		nsIDOMElement grLabelSpan;
		nsIDOMText grLabelText;
		nsIDOMElement grFolderDiv;
		nsIDOMElement grListBorderDiv;
		nsIDOMElement grListBgDiv;
		
		/*
		 * Creating visual elements
		 */
		grTopDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		grImgSpan = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_SPAN);
		grImg = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);
		grLabelSpan = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_SPAN);
		grLabelText = visualDocument.createTextNode(EMPTY);
		grFolderDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		grListBorderDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		grListBgDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		creationData = new VpeCreationData(grTopDiv);
		
		/*
		 * Nesting elements
		 */
		grTopDiv.appendChild(grImgSpan);
		grTopDiv.appendChild(grLabelSpan);
		grLabelSpan.appendChild(grLabelText);
		grTopDiv.appendChild(grFolderDiv);
		grFolderDiv.appendChild(grListBorderDiv);
		grListBorderDiv.appendChild(grListBgDiv);
		
		/*
		 * Setting css classes
		 */
		
		String topDivClass = EMPTY;
		String imgSpanClass = EMPTY;
		String labelSpanClass = EMPTY;
		String folderDivClass = EMPTY;
		
		topDivClass += SPACE + CSS_RICH_MENU_GROUP;
		imgSpanClass += SPACE + CSS_RICH_MENU_ITEM_ICON_ENABLED;
		labelSpanClass += SPACE + CSS_RICH_MENU_ITEM_LABEL + SPACE + CSS_RICH_MENU_GROUP_LABEL;
		folderDivClass += SPACE + CSS_RICH_MENU_ITEM_FOLDER + SPACE + CSS_RICH_MENU_GROUP_FOLDER;
		
		if (attrPresents(mg_styleClass)) {
			topDivClass += SPACE + mg_styleClass;
		}
		if (attrPresents(mg_iconClass)) {
			imgSpanClass += SPACE + mg_iconClass;
			folderDivClass += SPACE + mg_iconClass;
		}
		if (attrPresents(mg_labelClass)) {
			labelSpanClass += SPACE + mg_labelClass;
		}
		
		grTopDiv.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, topDivClass);
		grImgSpan.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, imgSpanClass);
		grLabelSpan.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, labelSpanClass);
		grFolderDiv.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, folderDivClass);
		grListBorderDiv.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, CSS_RICH_MENU_LIST_BORDER);
		grListBgDiv.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, CSS_RICH_MENU_LIST_BG);
		
		/*
		 * Setting css styles
		 */
		String topDivStyle = EMPTY;
		
		if (attrPresents(mg_style)) {
			topDivStyle += SPACE + mg_style;
		}
		
		grTopDiv.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, topDivStyle);
		grFolderDiv.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, CSS_RICH_LIST_FOLDER_DIV_STYLE);
		grListBorderDiv.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, CSS_RICH_LIST_BORDER_DIV_STYLE);
		
		/*
		 * Encode label value
		 */
		Attr valueAttr = sourceElement.getAttributeNode(HtmlComponentUtil.HTML_VALUE_ATTR);
    	String labelValue = valueAttr != null
				&& valueAttr.getValue() != null ? valueAttr.getValue() : EMPTY;
		grLabelText.setNodeValue(labelValue);
		
		/*
		 * Encode icon facets
		 */
		Element iconFacet = ComponentUtil.getFacet(sourceElement, ICON_FACET_NAME);
		Element iconDisabledFacet = ComponentUtil.getFacet(sourceElement, ICON_DISABLED_FACET_NAME);
		if (null != iconFacet) {
			VpeChildrenInfo childInfo = new VpeChildrenInfo(grImgSpan);
			childInfo.addSourceChild(iconFacet);
			creationData.addChildrenInfo(childInfo);
		} else {
			String iconPath = sourceElement.getAttribute(ICON);
			if (attrPresents(iconPath)) {
				/*
				 * Add path to specified image
				 */
				String imgFullPath = VpeStyleUtil.addFullPathToImgSrc(iconPath, pageContext, true);
				grImg.setAttribute(HtmlComponentUtil.HTML_ATR_SRC, imgFullPath);
			} else {
				/*
				 * Create spacer image
				 */
				ComponentUtil.setImg(grImg, SPACER_IMG_PATH);
			}
			/*
			 * Add image to span
			 */
			grImgSpan.appendChild(grImg);
			
		}
		
		String menuGroupId = (String) sourceNode.getUserData(MENU_GROUP_ID);
		/*
		 * Adding child nodes
		 */
		List<Node> children = ComponentUtil.getChildren(sourceElement);
		for (Node child : children) {
			nsIDOMElement childDiv = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_DIV);
			grListBgDiv.appendChild(childDiv);
			VpeChildrenInfo childDivInfo = new VpeChildrenInfo(childDiv);
			childDivInfo.addSourceChild(child);
			creationData.addChildrenInfo(childDivInfo);
		}

		return creationData;
	}

	/**
	 * Read attributes from the source element.
	 * 
	 * @param sourceNode the source node
	 */
	private void readMenuGroupAttributes(Element sourceElement) {
		if (null == sourceElement) {
			return;
		}
		mg_direction = sourceElement.getAttribute(DIRECTION);
		mg_disabled = sourceElement.getAttribute(HtmlComponentUtil.HTML_ATTR_DISABLED);
		mg_icon = sourceElement.getAttribute(ICON);
		mg_iconDisabled = sourceElement.getAttribute(ICON_DISABLED);
		mg_iconFolder = sourceElement.getAttribute(ICON_FOLDER);
		mg_iconFolderDisabled = sourceElement.getAttribute(ICON_FOLDER_DISABLED);
		mg_value = sourceElement.getAttribute(HtmlComponentUtil.HTML_VALUE_ATTR);

		mg_iconClass = sourceElement.getAttribute(ICON_CLASS);
		mg_iconStyle = sourceElement.getAttribute(ICON_STYLE);
		mg_labelClass = sourceElement.getAttribute(LABEL_CLASS);
		mg_selectClass = sourceElement.getAttribute(SELECT_CLASS);
		mg_selectStyle = sourceElement.getAttribute(SELECT_STYLE);
		mg_style = sourceElement.getAttribute(HtmlComponentUtil.HTML_STYLE_ATTR);
		mg_styleClass = sourceElement.getAttribute(HtmlComponentUtil.HTML_STYLECLASS_ATTR);
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
//			visualDomBuilder.updateNode(sourceNode);
		}

		public void stopMouseOver(Node sourceNode) {
			// TODO Auto-generated method stub
			
		}
	    
}
