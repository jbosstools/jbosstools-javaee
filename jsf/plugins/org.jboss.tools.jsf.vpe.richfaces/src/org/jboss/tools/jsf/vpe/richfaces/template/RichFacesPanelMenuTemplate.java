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
import java.util.HashMap;
import java.util.Iterator;
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
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 
 * @author ezheleznyakov@exadel.com
 * 
 */
public class RichFacesPanelMenuTemplate extends VpeAbstractTemplate implements
		VpeToggableTemplate {
	
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

	private static final String PANEL_MENU_GROUP_END = ":panelMenuGroup"; //$NON-NLS-1$
	private static final String PANEL_MENU_ITEM_END = ":panelMenuItem"; //$NON-NLS-1$
	private static final String TRUE = "true"; //$NON-NLS-1$

	private List<String> activeIds = new ArrayList<String>();
	
	private String expandSingle;

	// private static final String DISABLED_STYLE_FOR_TABLE = "color:#B1ADA7";

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {

		Element sourceElement = (Element) sourceNode;

		String width = sourceElement.getAttribute(WIDTH);
		String style = sourceElement.getAttribute(STYLE);
		String styleClass = sourceElement.getAttribute(STYLE_CLASS);
		expandSingle = sourceElement.getAttribute(EXPAND_SINGLE);

		if (width != null) {
			style += "" + "; width:" + width; //$NON-NLS-1$ //$NON-NLS-2$
		}

		nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
		VpeCreationData vpeCreationData = new VpeCreationData(div);

		if (style != null) {
			div.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, style);
		}

		if (styleClass != null) {
			div.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, styleClass);
		}

		List<Node> children = ComponentUtil.getChildren(sourceElement);
		int i = 1;

		for (Node child : children) {
			if (child.getNodeName().endsWith(PANEL_MENU_GROUP_END)) {
				RichFacesPanelMenuGroupTemplate.encode(pageContext,
						vpeCreationData, sourceElement, (Element) child,
						visualDocument, div, getActiveIds(), String.valueOf(i));
				i++;
			} else if (child.getNodeName().endsWith(PANEL_MENU_ITEM_END)) {
				RichFacesPanelMenuItemTemplate.encode(pageContext,
						vpeCreationData, sourceElement, (Element) child,
						visualDocument, div);
			} else {
				nsIDOMElement childDiv = visualDocument
						.createElement(HtmlComponentUtil.HTML_TAG_DIV);
				VpeChildrenInfo childrenInfo = new VpeChildrenInfo(childDiv);
				div.appendChild(childDiv);
				childrenInfo.addSourceChild(child);
				vpeCreationData.addChildrenInfo(childrenInfo);
			}
		}

		return vpeCreationData;
	}

	/**
	 * Gets the active ids.
	 * 
	 * @return the active ids
	 */
	private List<String> getActiveIds() {
		return activeIds;
	}

	/**
	 * 
	 * @param children
	 * @return
	 */
	private int getChildrenCount(List<Node> children) {
		int count = 0;
		for (Node child : children) {
			if (child.getNodeName().endsWith(PANEL_MENU_GROUP_END)) {
				count++;
			}
		}
		return count;
	}

	public void toggle(VpeVisualDomBuilder builder, Node sourceNode,
			String toggleId) {
		
		/*
		 * Expand only one group.
		 */
		if ((null != expandSingle) && (TRUE.equalsIgnoreCase(expandSingle))) {
			if (activeIds.contains(toggleId)) {
				/*
				 * Close group and its children
				 */
				activeIds.remove(toggleId);
				for (Iterator<String> iterator = activeIds.iterator(); iterator.hasNext();) {
					String id = iterator.next();
					if (id.startsWith(toggleId)) {
						iterator.remove();
					}
				}
			} else {
				/*
				 * Expand new group, close others
				 */
				String[] toggleIds = toggleId.split(RichFacesPanelMenuGroupTemplate.GROUP_COUNT_SEPARATOR);
				if ((null != toggleIds) && (toggleIds.length > 0)) {
					for (Iterator<String> iterator = activeIds.iterator(); iterator.hasNext();) {
						String id = iterator.next();
						String[] ids = id.split(RichFacesPanelMenuGroupTemplate.GROUP_COUNT_SEPARATOR);
						if ((null != ids) && (ids.length > 0)) {
							if (ids.length >= toggleIds.length) {
								/*
								 * Remove all ids that are deeper than selected 
								 * and that are on the same level.
								 */
								iterator.remove();
							} else {
								/*
								 * Remove all ids that are not in the selected branch. 
								 */
								for (int i = 0; i < ids.length; i++) {
									if (!ids[i].equalsIgnoreCase(toggleIds[i])) {
										iterator.remove();
									}
								}
							}
						}
					}
				}
				activeIds.add(toggleId);
			}
		} else {
			/*
			 * Expand any number of groups.
			 */
			if (activeIds.contains(toggleId)) {
				activeIds.remove(toggleId);
				for (Iterator<String> iterator = activeIds.iterator(); iterator.hasNext();) {
					String id = iterator.next();
					if (id.startsWith(toggleId)) {
						iterator.remove();
					}
				}
			} else {
				activeIds.add(toggleId);
			}
		}
	}

	public void stopToggling(Node sourceNode) {
		activeIds.clear();
	}

	public boolean isRecreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}
}