/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.richfaces.template;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 
 * @author ezheleznyakov@exadel.com
 * 
 */
public class RichFacesPanelMenuTemplate extends VpeAbstractTemplate {
	
    	/*
     	* Path to default css style sheet.
     	*/
    	private static final String CSS_STYLE_PATH = "/panelMenu/style.css"; //$NON-NLS-1$

    	/*
     	* Component name
     	*/
    	private static final String COMPONENT_NAME = "panelMenu"; //$NON-NLS-1$

	/*
	 *	rich:panelMenu attributes
	 */ 
	public static final String EXPAND_SINGLE = "expandSingle"; //$NON-NLS-1$
	
	/*
	 *	rich:panelMenu attributes for groups
	 */ 
	public static final String ICON_GROUP_POSITION = "iconGroupPosition"; //$NON-NLS-1$
	public static final String ICON_GROUP_TOP_POSITION = "iconGroupTopPosition"; //$NON-NLS-1$
	public static final String ICON_COLLAPSED_GROUP = "iconCollapsedGroup"; //$NON-NLS-1$
	public static final String ICON_COLLAPSED_TOP_GROUP = "iconCollapsedTopGroup"; //$NON-NLS-1$
	public static final String ICON_EXPANDED_GROUP = "iconExpandedGroup"; //$NON-NLS-1$
	public static final String ICON_EXPANDED_TOP_GROUP = "iconExpandedTopGroup"; //$NON-NLS-1$
	public static final String ICON_DISABLED_GROUP = "iconDisabledGroup"; //$NON-NLS-1$
	public static final String ICON_TOP_DISABLED_GROUP = "iconTopDisabledGroup"; //$NON-NLS-1$
	
	/*
	 *	rich:panelMenu attributes for items
	 */ 
	public static final String ICON_ITEM = "iconItem"; //$NON-NLS-1$
	public static final String ICON_DISABLED_ITEM = "iconDisabledItem"; //$NON-NLS-1$
	public static final String ICON_ITEM_POSITION = "iconItemPosition"; //$NON-NLS-1$
	public static final String ICON_TOP_ITEM = "iconTopItem"; //$NON-NLS-1$
	public static final String ICON_TOP_DISABLED_ITEM = "iconTopDisabledItem"; //$NON-NLS-1$
	public static final String ICON_ITEM_TOP_POSITION = "iconItemTopPosition"; //$NON-NLS-1$
	
	/*
	 *	rich:panelMenu style classes for groups
	 */ 
	public static final String DISABLED_GROUP_CLASS = "disabledGroupClass"; //$NON-NLS-1$
	public static final String DISABLED_GROUP_STYLE = "disabledGroupStyle"; //$NON-NLS-1$
	public static final String TOP_GROUP_CLASS = "topGroupClass"; //$NON-NLS-1$
	public static final String TOP_GROUP_STYLE = "topGroupStyle"; //$NON-NLS-1$
	public static final String GROUP_CLASS = "groupClass"; //$NON-NLS-1$
	public static final String GROUP_STYLE = "groupStyle"; //$NON-NLS-1$
	
	/*
	 *	rich:panelMenu style classes for items
	 */ 
	public static final String DISABLED_ITEM_CLASS = "disabledItemClass"; //$NON-NLS-1$
	public static final String DISABLED_ITEM_STYLE = "disabledItemStyle"; //$NON-NLS-1$
	public static final String TOP_ITEM_CLASS = "topItemClass"; //$NON-NLS-1$
	public static final String TOP_ITEM_STYLE = "topItemStyle"; //$NON-NLS-1$
	public static final String ITEM_CLASS = "itemClass"; //$NON-NLS-1$
	public static final String ITEM_STYLE = "itemStyle"; //$NON-NLS-1$
	
	/*
	 *	rich:panelMenu css styles
	 */ 
	public static final String CSS_PMENU = "rich-pmenu"; //$NON-NLS-1$

	private static final String PANEL_MENU_GROUP_END = ":panelMenuGroup"; //$NON-NLS-1$
	private static final String PANEL_MENU_ITEM_END = ":panelMenuItem"; //$NON-NLS-1$
	private static final String MARGIN_TOP = "margin-top: 3px; "; //$NON-NLS-1$
	private static final String TOP_MENU_ITEM_ID = ""; //$NON-NLS-1$


	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {

		ComponentUtil.setCSSLink(pageContext, CSS_STYLE_PATH, COMPONENT_NAME);
	    
		Element sourceElement = (Element) sourceNode;
		
		String style = Constants.EMPTY;
		String styleClass = CSS_PMENU;
		
		nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
		VpeCreationData vpeCreationData = new VpeCreationData(div);
		div.setAttribute(HTML.ATTR_BORDER, Constants.ZERO_STRING);

		if (sourceElement.hasAttribute(HTML.ATTR_WIDTH)) {
			String widthAttr = sourceElement.getAttribute(HTML.ATTR_WIDTH);
			style += "width:" + widthAttr + "; "; //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (sourceElement.hasAttribute(RichFaces.ATTR_STYLE)) {
			String styleAttr = sourceElement.getAttribute(RichFaces.ATTR_STYLE);
			style += styleAttr;
		}
		div.setAttribute(HTML.ATTR_STYLE, style);

		if (sourceElement.hasAttribute(RichFaces.ATTR_STYLE_CLASS)) {
			String styleClassAttr = sourceElement.getAttribute(RichFaces.ATTR_STYLE_CLASS);
			styleClass += Constants.WHITE_SPACE + styleClassAttr;
		}
		div.setAttribute(HTML.ATTR_CLASS, styleClass);
		
		List<Node> children = ComponentUtil.getChildren(sourceElement);
		int i = 1;
		List<String> expandedIds = (List<String>) sourceNode
			.getUserData(RichFacesPanelMenuGroupTemplate.VPE_EXPANDED_TOGGLE_IDS);
		if (null == expandedIds) {
		    expandedIds = new ArrayList<String>();
		}
		for (Node child : children) {
			if (child.getNodeName().endsWith(PANEL_MENU_GROUP_END)) {
				child.setUserData(VpeVisualDomBuilder.VPE_USER_TOGGLE_ID, String.valueOf(i), null);
				child.setUserData(RichFacesPanelMenuGroupTemplate.VPE_EXPANDED_TOGGLE_IDS, expandedIds, null);
				i++;
			}
			if (child.getNodeName().endsWith(PANEL_MENU_ITEM_END)) {
				child.setUserData(RichFacesPanelMenuItemTemplate.VPE_PANEL_MENU_ITEM_ID, TOP_MENU_ITEM_ID, null);
			}
			
			VpeChildrenInfo childrenInfo = new VpeChildrenInfo(div);
			childrenInfo.addSourceChild(child);
			vpeCreationData.addChildrenInfo(childrenInfo);
		}
		return vpeCreationData;
	}

	public boolean recreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}
	
	
}