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

import java.util.List;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesPanelMenuItemTemplate extends VpeAbstractTemplate {

	/*
	 * rich:panelMenuItem attributes
	 */
	private static final String DISABLED = "disabled"; //$NON-NLS-1$
	private static final String ICON = "icon"; //$NON-NLS-1$
	private static final String ICON_DISABLED = "iconDisabled"; //$NON-NLS-1$
	private static final String DISABLED_CLASS = "disabledClass"; //$NON-NLS-1$
	private static final String DISABLED_STYLE = "disabledStyle"; //$NON-NLS-1$
	private static final String STYLE = "style"; //$NON-NLS-1$
	private static final String STYLE_CLASS = "styleClass"; //$NON-NLS-1$

	
	private static final String PANEL_MENU_ITEM_CLASS = "dr-pmenu-item"; //$NON-NLS-1$
	private static final String PANEL_MENU_NOWARP_CLASS = "dr-pmenu-nowrap"; //$NON-NLS-1$
	private static final String PANEL_MENU_LABLE_CLASS = "dr-pmenu-group-self-label"; //$NON-NLS-1$
	private static final String PANEL_MENU_DIV = "dr-pmenu-top-self-div"; //$NON-NLS-1$
	private static final String DISABLED_CLASS_NAME = "dr-pmenu-item-disabled"; //$NON-NLS-1$
	
	private static final String IMG_POINTS_SRC = "/panelMenuItem/points.gif"; //$NON-NLS-1$
	private static final String IMG_SPACER_SRC = "/panelMenuItem/spacer.gif"; //$NON-NLS-1$
	private static final String STYLE_PATH = "/panelMenuItem/style.css"; //$NON-NLS-1$
	
	private static final String SPACE = " "; //$NON-NLS-1$
	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final String TRUE = "true"; //$NON-NLS-1$
	private static final String RIGHT = "right"; //$NON-NLS-1$
	private static final String LEFT = "left"; //$NON-NLS-1$
	private static final String NO_SIZE_VALUE = "0"; //$NON-NLS-1$
	private static final String DEFAULT_SIZE_VALUE = "16"; //$NON-NLS-1$
	
	private static final String PANEL_MENU_ITEM = "panelMenuItem"; //$NON-NLS-1$
	private static final String EMPTY_DIV_STYLE = "display: none;"; //$NON-NLS-1$
	
	/*
	 *	rich:panelMenu attributes for items
	 */ 
	private static String pm_disabled;
	private static String pm_iconItem;
	private static String pm_iconDisabledItem;
	private static String pm_iconItemPosition;
	private static String pm_iconTopItem;
	private static String pm_iconTopDisabledItem;
	private static String pm_iconItemTopPosition;
	
	/*
	 *	rich:panelMenu style classes for items
	 */ 
	private static String pm_disabledItemClass;
	private static String pm_disabledItemStyle;
	private static String pm_topItemClass;
	private static String pm_topItemStyle;
	private static String pm_itemClass;
	private static String pm_itemStyle;
	
	/*
	 * rich:panelMenuItem attributes
	 */
	private static String pmi_disabled;
	private static String pmi_icon;
	private static String pmi_iconDisabled;
	private static String pmi_disabledClass;
	private static String pmi_disabledStyle;
	private static String pmi_style;
	private static String pmi_styleClass;

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		nsIDOMElement div = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		div.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, EMPTY_DIV_STYLE);

		return new VpeCreationData(div);
	}

	public static VpeCreationData encode(VpePageContext pageContext,
			VpeCreationData creationData, Element sourceParentElement,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement parentVisualElement) {

		ComponentUtil.setCSSLink(pageContext, STYLE_PATH, PANEL_MENU_ITEM);

		readPanelMenuAttributes(sourceParentElement);
		readPanelMenuItemAttributes(sourceElement);
		
		nsIDOMElement parentDiv = visualDocument.createElement("div"); //$NON-NLS-1$
		parentDiv.setAttribute("CLASS", PANEL_MENU_DIV); //$NON-NLS-1$
		parentVisualElement.appendChild(parentDiv);
		nsIDOMElement div = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);

		parentDiv.appendChild(div);

		if (sourceElement.getParentNode().getNodeName().endsWith(
				":panelMenuGroup") //$NON-NLS-1$
				|| (sourceElement.getParentNode().getNodeName()
						.endsWith(":panelMenu"))) { //$NON-NLS-1$
			div.setAttribute("vpeSupport", PANEL_MENU_ITEM); //$NON-NLS-1$
			nsIDOMElement table = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
			div.appendChild(table);

			table.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					PANEL_MENU_ITEM_CLASS);
			table.setAttribute(HtmlComponentUtil.HTML_CELLPADDING_ATTR,
					NO_SIZE_VALUE);
			table.setAttribute(HtmlComponentUtil.HTML_CELLSPACING_ATTR,
					NO_SIZE_VALUE);
			table.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR,
					NO_SIZE_VALUE);

			nsIDOMElement tr = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_TR);
			table.appendChild(tr);

			nsIDOMElement tdNowrap = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_TD);
			tr.appendChild(tdNowrap);
			tdNowrap.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					PANEL_MENU_NOWARP_CLASS);

			nsIDOMElement tdLable = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_TD);
			tr.appendChild(tdLable);
			tdLable.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					PANEL_MENU_LABLE_CLASS);
			tdLable.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
					"element.style"); //$NON-NLS-1$

			String value = sourceElement.getAttribute("label"); //$NON-NLS-1$
			nsIDOMText text = visualDocument.createTextNode(value == null ? EMPTY //$NON-NLS-1$
					: value);

			tdLable.appendChild(text);

			nsIDOMElement td = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_TD);
			tr.appendChild(td);

			nsIDOMElement imgPoints = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_IMG);

			nsIDOMElement imgSpacer1 = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_IMG);

			tdNowrap.appendChild(imgSpacer1);
			setDefaultImgAttributes(imgSpacer1);
			ComponentUtil.setImg(imgSpacer1, IMG_SPACER_SRC);

			nsIDOMElement imgSpacer2 = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_IMG);

			if (sourceElement.getParentNode().getNodeName().endsWith(
					":panelMenu")) { //$NON-NLS-1$

				if ((TRUE.equalsIgnoreCase(pmi_disabled))
						|| (TRUE.equalsIgnoreCase(pm_disabled))) {
					setIcon(pageContext, imgPoints, sourceElement,
							sourceParentElement, pm_iconTopDisabledItem,
							pmi_iconDisabled);
					setItemClassAndStyle(table, pm_disabledItemClass,
							pmi_disabledClass, DISABLED_CLASS_NAME,
							pm_disabledItemStyle, pmi_disabledStyle);
				} else {
					setIcon(pageContext, imgPoints, sourceElement,
							sourceParentElement, pm_iconTopItem, pmi_icon);
					setItemClassAndStyle(table, pm_topItemClass,
							pmi_styleClass, PANEL_MENU_ITEM_CLASS,
							pm_topItemStyle, pmi_style);
				}
				setIconPosition(pm_iconItemTopPosition, td, tdNowrap,
						imgPoints, imgSpacer2);

			} else {
				if ((TRUE.equalsIgnoreCase(pmi_disabled))
						|| (TRUE.equalsIgnoreCase(pm_disabled))) {
					setIcon(pageContext, imgPoints, sourceElement,
							sourceParentElement, pm_iconDisabledItem,
							pmi_iconDisabled);
					setItemClassAndStyle(table, pm_disabledItemClass,
							pmi_disabledClass, DISABLED_CLASS_NAME,
							pm_disabledItemStyle, pmi_disabledStyle);
				} else {
					setIcon(pageContext, imgPoints, sourceElement,
							sourceParentElement, pm_iconItem, pmi_icon);
					setItemClassAndStyle(table, pm_itemClass, pmi_styleClass,
							PANEL_MENU_ITEM_CLASS, pm_itemStyle, pmi_style);
				}
				setIconPosition(pm_iconItemPosition, td, tdNowrap, imgPoints,
						imgSpacer2);
			}

			List<Node> children = ComponentUtil.getChildren(sourceElement);

			if (!children.isEmpty()) {
				VpeChildrenInfo childInfo = new VpeChildrenInfo(tdLable);
				creationData.addChildrenInfo(childInfo);
				for (Node child : children) {
					if (!(child.getNodeName().endsWith(":panelMenuGroup") || child //$NON-NLS-1$
							.getNodeName().endsWith(":panelMenu"))) { //$NON-NLS-1$
						childInfo.addSourceChild(child);
					}
				}
			}
		}
		return creationData;
	}

	public boolean isRecreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}

	private static void setDefaultImgAttributes(nsIDOMElement element) {
		element.setAttribute(HtmlComponentUtil.HTML_ATR_WIDTH,
				DEFAULT_SIZE_VALUE);
		element.setAttribute("vspace", NO_SIZE_VALUE); //$NON-NLS-1$
		element.setAttribute("hspace", NO_SIZE_VALUE); //$NON-NLS-1$
		element.setAttribute(HtmlComponentUtil.HTML_ATR_HEIGHT,
				DEFAULT_SIZE_VALUE);
	}

	private static void setItemImage(nsIDOMElement place, nsIDOMElement image) {
		place.appendChild(image);
		setDefaultImgAttributes(image);
	}

	private static void setIcon(VpePageContext pageContext,
			nsIDOMElement imgPoints, Element sourceElement,
			Element parentElement, String parentIconPath,
			String iconPath) {
		if (iconPath == null || iconPath.length() == 0) {
			if (!(parentIconPath == null || parentIconPath.length() == 0)) {
				ComponentUtil.setImgFromResources(pageContext, imgPoints,
						parentIconPath, IMG_SPACER_SRC);
			} else {
				ComponentUtil.setImg(imgPoints, IMG_POINTS_SRC);
			}
		} else {
			ComponentUtil.setImgFromResources(pageContext, imgPoints, iconPath,
					IMG_SPACER_SRC);
		}
	}

	private static void setIconPosition(String iconPosition,
			nsIDOMElement right, nsIDOMElement left, nsIDOMElement imgPoints,
			nsIDOMElement imgSpacer2) {
		if (!(iconPosition == null)) {
			if (iconPosition.equals(RIGHT)) { 
				setItemImage(right, imgPoints);
			} else {
				setItemImage(right, imgSpacer2);
				ComponentUtil.setImg(imgSpacer2, IMG_SPACER_SRC);
				if (iconPosition.equals(LEFT)) {
					setItemImage(left, imgPoints);
				}
			}
		} else {
			setItemImage(left, imgPoints);
			setItemImage(right, imgSpacer2);
			ComponentUtil.setImg(imgSpacer2, IMG_SPACER_SRC);
		}
	}

	private static void setItemClassAndStyle(nsIDOMElement table,
			String parentClass, String itemClass, String defaultClass,
			String parentStyle, String itemStyle) {
		
		String resultClass = EMPTY; 
		if (!(defaultClass == null || defaultClass.length() == 0)) {
			resultClass += defaultClass;
		}
		if (!(parentClass == null || parentClass.length() == 0)) {
			resultClass += SPACE + parentClass;
		}
		if (!(itemClass == null || itemClass.length() == 0)) {
			resultClass += SPACE + itemClass;
		}
		table.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, resultClass);

		String resultStyle = EMPTY;
		if (!(parentStyle == null || parentStyle.length() == 0)) {
			resultStyle += parentStyle;
		}
		if (!(itemStyle == null || itemStyle.length() == 0)) {
			resultStyle += SPACE + itemStyle;
		}
		table.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, resultStyle);
	}
	
	/**
	 * Read attributes from the source element.
	 * 
	 * @param sourceNode the source node
	 */
	private static void readPanelMenuAttributes(Element sourceParentElement) {
		
		if (null == sourceParentElement) {
			return;
		}
		
		/*
		 *	rich:panelMenu attributes for items
		 */ 
		pm_disabled = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.DISABLED);
		pm_iconItem = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.ICON_ITEM);
		pm_iconDisabledItem = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.ICON_DISABLED_ITEM);
		pm_iconItemPosition = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.ICON_ITEM_POSITION);
		pm_iconTopItem = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.ICON_TOP_ITEM);
		pm_iconTopDisabledItem = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.ICON_TOP_DISABLED_ITEM);
		pm_iconItemTopPosition = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.ICON_ITEM_TOP_POSITION);
		
		/*
		 *	rich:panelMenu style classes for items
		 */ 
		pm_disabledItemClass = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.DISABLED_ITEM_CLASS);
		pm_disabledItemStyle = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.DISABLED_ITEM_STYLE);
		pm_topItemClass = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.TOP_ITEM_CLASS);
		pm_topItemStyle = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.TOP_ITEM_STYLE);
		pm_itemClass = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.ITEM_CLASS);
		pm_itemStyle = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.ITEM_STYLE);
	
	}
	
	/**
	 * Read attributes from the source element.
	 * 
	 * @param sourceNode the source node
	 */
	private static void readPanelMenuItemAttributes(Element sourceElement) {
		
		if (null == sourceElement) {
			return;
		}
		
		/*
		 * pich:panelMenuItem attributes
		 */
		pmi_disabled = sourceElement.getAttribute(DISABLED);
		pmi_icon = sourceElement.getAttribute(ICON);
		pmi_iconDisabled = sourceElement.getAttribute(ICON_DISABLED);
		pmi_disabledClass = sourceElement.getAttribute(DISABLED_CLASS);
		pmi_disabledStyle = sourceElement.getAttribute(DISABLED_STYLE);
		pmi_style = sourceElement.getAttribute(STYLE);
		pmi_styleClass = sourceElement.getAttribute(STYLE_CLASS);
		
	}
}