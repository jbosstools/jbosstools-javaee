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
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesDropDownMenuTemplate extends VpeAbstractTemplate {
	
	/*
	 * rich:dropDownMenu constants
	 */
	private static final String COMPONENT_NAME = "dropDownMenu"; //$NON-NLS-1$
	private static final String STYLE_PATH = "dropDownMenu/dropDownMenu.css"; //$NON-NLS-1$
	private static final String CHILD_GROUP_NAME = ":menuGroup"; //$NON-NLS-1$
	private static final String CHILD_ITEM_NAME = ":menuItem"; //$NON-NLS-1$
	private static final String LABEL_FACET_NAME = "label"; //$NON-NLS-1$
	private static final String DEFAULT_DDM_TITLE = "ddm"; //$NON-NLS-1$
	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final String SPACE = " "; //$NON-NLS-1$

	/*
	 * Constants for drop down mechanism.
	 */
	private static final String MENU_TOP_ID = "vpe-ddm-menu-title-ul"; //$NON-NLS-1$
	private static final String MENU_TOP_ITEM_ID = "vpe-ddm-menu-title-li"; //$NON-NLS-1$
	private static final String MENU_CHILDREN_LIST_ID = "vpe-ddm-menu-children-ul"; //$NON-NLS-1$
	
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
	private static final String CSS_RICH_DDEMENU_LIST_DIV_STYLE = ""; //$NON-NLS-1$
	private static final String CSS_RICH_DDEMENU_BORDER_DIV_STYLE = ""; //$NON-NLS-1$	
	private static final String CSS_MENU_TOP_DIV = "dr-menu-top-div"; //$NON-NLS-1$	

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

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
	    VpeCreationData creationData = null;
	    Element sourceElement = (Element)sourceNode;
	    
	    ComponentUtil.setCSSLink(pageContext, STYLE_PATH, COMPONENT_NAME);
	    readDropDownMenuAttributes(sourceElement);
	    
		/*
		 * DropDownMenu component structure.
		 * In order of  nesting.
		 */
	    nsIDOMElement ddmMainUL;
	    nsIDOMElement ddmMainLI;
	    nsIDOMElement ddmChildrenUL;
		nsIDOMElement ddmLabelDiv;
		nsIDOMElement ddmTextSpan;
		nsIDOMText ddmLabelText;
		nsIDOMElement ddmListDiv;
		nsIDOMElement ddmListBorderDiv;
		nsIDOMElement ddmListBgDiv;
		
		/*
		 * Creating visual elements
		 */
	    ddmMainUL = visualDocument.createElement(HTML.TAG_UL);
	    ddmMainLI = visualDocument.createElement(HTML.TAG_LI);
	    ddmChildrenUL = visualDocument.createElement(HTML.TAG_UL);
		ddmLabelDiv = visualDocument.createElement(HTML.TAG_DIV);
		ddmTextSpan = visualDocument.createElement(HTML.TAG_SPAN);
		ddmLabelText = visualDocument.createTextNode(EMPTY);
		ddmListDiv = visualDocument.createElement(HTML.TAG_DIV);
		ddmListBorderDiv = visualDocument.createElement(HTML.TAG_DIV);
		ddmListBgDiv = visualDocument.createElement(HTML.TAG_DIV);
		creationData = new VpeCreationData(ddmMainUL);
		
		/*
		 * Nesting elements
		 */
		ddmLabelDiv.appendChild(ddmTextSpan);
//		ddmTextSpan.appendChild(ddmLabelText);
//		ddmLabelDiv.appendChild(ddmListDiv);
		ddmListDiv.appendChild(ddmListBorderDiv);
		ddmListBorderDiv.appendChild(ddmListBgDiv);
		ddmMainUL.appendChild(ddmMainLI);
		ddmMainLI.appendChild(ddmLabelDiv);
		
		/*
		 * Setting attributes for the drop-down mechanism
		 */
	    ddmMainUL.setAttribute(MENU_TOP_ID, EMPTY);
	    ddmMainLI.setAttribute(MENU_TOP_ITEM_ID, EMPTY);
	    ddmChildrenUL.setAttribute(MENU_CHILDREN_LIST_ID, EMPTY);
		
		/*
		 * Setting css classes
		 */
		String labelDivClass = EMPTY;
		String listBorderDivClass = EMPTY;

		labelDivClass += SPACE + CSS_RICH_DDMENU_LABEL + SPACE
				+ CSS_RICH_DDMENU_LABEL_UNSELECT;
		listBorderDivClass += SPACE + CSS_RICH_MENU_LIST_BORDER;
		
		if (ComponentUtil.isNotBlank(ddm_styleClass)) {
			labelDivClass += SPACE + ddm_styleClass;
			listBorderDivClass += SPACE + ddm_styleClass;
		}
		
//		ddmLabelDiv.setAttribute(HTML.ATTR_CLASS, labelDivClass);
		ddmLabelDiv.setAttribute(HTML.ATTR_CLASS, CSS_MENU_TOP_DIV);
		ddmMainLI.setAttribute(HTML.ATTR_CLASS, labelDivClass);
		ddmTextSpan.setAttribute(HTML.ATTR_CLASS, CSS_RICH_LABEL_TEXT_DECOR);
//		ddmListBorderDiv.setAttribute(HTML.ATTR_CLASS, listBorderDivClass);
//		ddmListBgDiv.setAttribute(HTML.ATTR_CLASS, CSS_RICH_MENU_LIST_BG);
		ddmChildrenUL.setAttribute(HTML.ATTR_CLASS, listBorderDivClass + SPACE
				+ CSS_RICH_MENU_LIST_BG);
		/*
		 * Setting css styles
		 */
		String cssListDivStyle = EMPTY;
		String cssListBorderDivStyle = EMPTY;
		String cssLabelDivStyle = EMPTY;
		
		cssListDivStyle += SPACE + CSS_RICH_DDEMENU_LIST_DIV_STYLE;
		cssListBorderDivStyle += SPACE + CSS_RICH_DDEMENU_BORDER_DIV_STYLE;
		
		if (ComponentUtil.isNotBlank(ddm_style)) {
			cssLabelDivStyle += SPACE + ddm_style;
		}
		
//		ddmListDiv.setAttribute(HTML.ATTR_STYLE, cssListDivStyle);
//		ddmListBorderDiv.setAttribute(HTML.ATTR_STYLE, cssListBorderDivStyle);
//		ddmLabelDiv.setAttribute(HTML.ATTR_STYLE, cssLabelDivStyle);
		ddmMainLI.setAttribute(HTML.ATTR_STYLE, cssListDivStyle + SPACE
				+ cssListBorderDivStyle + SPACE + cssLabelDivStyle);
		ddmChildrenUL.setAttribute(HTML.ATTR_STYLE, cssListDivStyle + SPACE
				+ cssListBorderDivStyle + SPACE + cssLabelDivStyle);
		/*
		 * Encoding label value
		 */
		Element labelFacet = ComponentUtil.getFacet(sourceElement, LABEL_FACET_NAME);
		if (null != labelFacet) {
			VpeChildrenInfo childrenInfo = new VpeChildrenInfo(ddmTextSpan);
			childrenInfo.addSourceChild(labelFacet);
			creationData.addChildrenInfo(childrenInfo);
		} else {
			Attr valueAttr = sourceElement.getAttributeNode(HTML.ATTR_VALUE);
	    	String labelValue = (valueAttr != null && valueAttr.getValue() != null)
		    			? valueAttr.getValue()
		    			: DEFAULT_DDM_TITLE;
		    ddmLabelText.setNodeValue(labelValue);
		    ddmTextSpan.appendChild(ddmLabelText);		    
		}

		/*
		 * Adding child nodes:
		 * <rich:menuGroup> and <rich:menuItem> only.
		 */
		List<Node> children = ComponentUtil.getChildren(sourceElement);
		boolean missingChildContainer = true;
		for (Node child : children) {
			if (child.getNodeType() == Node.ELEMENT_NODE
					&& (child.getNodeName().endsWith(CHILD_GROUP_NAME) || child
							.getNodeName().endsWith(CHILD_ITEM_NAME))) {
				if (missingChildContainer) {
					/*
					 * Add children <ul> tag.
					 */
					ddmMainLI.appendChild(ddmChildrenUL);
					missingChildContainer = false;
				}
				VpeChildrenInfo childDivInfo = new VpeChildrenInfo(
						ddmChildrenUL);
				childDivInfo.addSourceChild(child);
				creationData.addChildrenInfo(childDivInfo);
			}
		}
			
		return creationData;
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
			ddm_disabled = sourceElement.getAttribute(HTML.ATTR_DISABLED);
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
			ddm_style = sourceElement.getAttribute(HTML.ATTR_STYLE);
			ddm_styleClass = sourceElement.getAttribute(RichFaces.ATTR_STYLE_CLASS);
		}
	
		@Override
		public boolean recreateAtAttrChange(VpePageContext pageContext,
				Element sourceElement, nsIDOMDocument visualDocument,
				nsIDOMElement visualNode, Object data, String name, String value) {
			return true;
		}
}