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
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
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
	 *	rich:panelMenu attributes
	 */ 
	public static final String DISABLED = "disabled"; //$NON-NLS-1$
	public static final String EXPAND_SINGLE = "expandSingle"; //$NON-NLS-1$
	public static final String WIDTH = "width"; //$NON-NLS-1$
	
	/*
	 *	rich:panelMenu attributes for groups
	 */ 
	public static final String ICON_GROUP_POSITION = "iconGroupPosition"; //$NON-NLS-1$
	public static final String ICON_GROUP_TOP_POSITION = "iconGroupTopPosition"; //$NON-NLS-1$
	public static final String ICON_COLLAPSED_GROUP = "iconCollapsedGroup"; //$NON-NLS-1$
	public static final String ICON_COLLAPSED_TOP_GROUP = "iconCollapsedTopGroup"; //$NON-NLS-1$
	public static final String ICON_EXPANDED_GROUP = "iconExpandedGroup"; //$NON-NLS-1$
	public static final String ICON_EXPANDED_TOP_GROUP = "iconExpandedTopGroup"; //$NON-NLS-1$
	public static final String ICON_DISABLE_GROUP = "iconDisableGroup"; //$NON-NLS-1$
	public static final String ICON_TOP_DISABLE_GROUP = "iconTopDisableGroup"; //$NON-NLS-1$
	
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
	 *	rich:panelMenu style classes
	 */ 
	public static final String STYLE = "style"; //$NON-NLS-1$
	public static final String STYLE_CLASS = "styleClass"; //$NON-NLS-1$
	
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
	public static final String CSS_PANEL_MENU = "rich-panel-menu"; //$NON-NLS-1$

	private static final String PANEL_MENU_GROUP_END = ":panelMenuGroup"; //$NON-NLS-1$
	private static final String PANEL_MENU_ITEM_END = ":panelMenuItem"; //$NON-NLS-1$
	private static final String MARGIN_TOP = "margin-top: 3px; "; //$NON-NLS-1$
	private static final String TOP_MENU_ITEM_ID = ""; //$NON-NLS-1$


	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {

		Element sourceElement = (Element) sourceNode;
		String width = sourceElement.getAttribute(WIDTH);
		String style = sourceElement.getAttribute(STYLE);
		String styleClass = sourceElement.getAttribute(STYLE_CLASS);

		if (width != null) {
			style = "width:" + width + ";" + style; //$NON-NLS-1$ //$NON-NLS-2$
		}

		nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
		VpeCreationData vpeCreationData = new VpeCreationData(div);
		div.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR, "0"); //$NON-NLS-1$
		
		if (style != null) {
			div.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, MARGIN_TOP + style);
		}

		if (styleClass != null) {
			div.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, styleClass);
		}

		List<Node> children = ComponentUtil.getChildren(sourceElement);
		int i = 1;
		List<String> expandedIds = new ArrayList<String>();
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

	public boolean isRecreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}
	
	
}