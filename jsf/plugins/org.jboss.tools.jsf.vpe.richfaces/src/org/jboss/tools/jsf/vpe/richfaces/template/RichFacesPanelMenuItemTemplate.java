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
	private static final String LABEL = "label"; //$NON-NLS-1$
	private static final String ICON = "icon"; //$NON-NLS-1$
	private static final String ICON_DISABLED = "iconDisabled"; //$NON-NLS-1$
	private static final String DISABLED_CLASS = "disabledClass"; //$NON-NLS-1$
	private static final String DISABLED_STYLE = "disabledStyle"; //$NON-NLS-1$
	private static final String STYLE = "style"; //$NON-NLS-1$
	private static final String STYLE_CLASS = "styleClass"; //$NON-NLS-1$

	/*
	 *	rich:panelMenuItem css styles
	 */ 
	public static final String CSS_TOP_ITEM = "rich-pmenu-top-item"; //$NON-NLS-1$
	public static final String CSS_TOP_ITEM_ICON = "rich-pmenu-top-item-icon"; //$NON-NLS-1$
	public static final String CSS_TOP_ITEM_LABEL = "rich-pmenu-top-item-label"; //$NON-NLS-1$
	public static final String CSS_ITEM = "rich-pmenu-item"; //$NON-NLS-1$
	public static final String CSS_ITEM_ICON = "rich-pmenu-item-icon"; //$NON-NLS-1$
	public static final String CSS_ITEM_LABEL = "rich-pmenu-item-label"; //$NON-NLS-1$
	public static final String CSS_ITEM_SELECTED = "rich-pmenu-item-selected"; //$NON-NLS-1$
	public static final String CSS_DISABLED_ELEMENT = "rich-pmenu-disabled-element"; //$NON-NLS-1$
	
	/*
	 *	rich:panelMenuItem default css styles
	 */ 
	private static final String DR_NOWARP_CLASS = "dr-pmenu-nowrap"; //$NON-NLS-1$
	private static final String DR_TOP_DIV = "dr-pmenu-top-self-div"; //$NON-NLS-1$
	
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
	
	private static final String COMPONENT_NAME = "panelMenuItem"; //$NON-NLS-1$
	private static final String PANEL_MENU_END_TAG = ":panelMenu"; //$NON-NLS-1$
	private static final String PANEL_MENU_GROUP_END_TAG = ":panelMenuGroup"; //$NON-NLS-1$
	private static final String EMPTY_DIV_STYLE = "display: none;"; //$NON-NLS-1$
	private static final String MARGIN_TOP = "margin-top: 3px; "; //$NON-NLS-1$
	
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

		ComponentUtil.setCSSLink(pageContext, STYLE_PATH, COMPONENT_NAME);

		readPanelMenuAttributes(sourceParentElement);
		readPanelMenuItemAttributes(sourceElement);
		
		nsIDOMElement parentDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		parentDiv.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, DR_TOP_DIV);
		parentDiv.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR, "0"); //$NON-NLS-1$
		parentDiv.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, MARGIN_TOP);
		parentVisualElement.appendChild(parentDiv);
		nsIDOMElement div = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		parentDiv.appendChild(div);
		
		
		boolean childOfPanelMenu = sourceElement.getParentNode().getNodeName().endsWith(
				PANEL_MENU_END_TAG);
		boolean childOfPanelMenuGroup = sourceElement.getParentNode().getNodeName().endsWith(
				PANEL_MENU_GROUP_END_TAG);
		
		if (childOfPanelMenu || childOfPanelMenuGroup) {
			div.setAttribute("vpeSupport", COMPONENT_NAME); //$NON-NLS-1$
			nsIDOMElement table = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
			div.appendChild(table);

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
			

			nsIDOMElement tdLabel = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_TD);
			tr.appendChild(tdLabel);

			String value = sourceElement.getAttribute(LABEL);
			nsIDOMText text = visualDocument.createTextNode(value == null ? EMPTY
					: value);

			tdLabel.appendChild(text);

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

			setItemClassAndStyle(childOfPanelMenu, table, tr, imgPoints, tdLabel, imgSpacer1);
			setIcon(childOfPanelMenu, pageContext, imgPoints);
			setIconPosition(pm_iconItemTopPosition, td, tdNowrap, imgPoints, imgSpacer2);
			
			List<Node> children = ComponentUtil.getChildren(sourceElement);

			if (!children.isEmpty()) {
				VpeChildrenInfo childInfo = new VpeChildrenInfo(tdLabel);
				creationData.addChildrenInfo(childInfo);
				for (Node child : children) {
					if (!(child.getNodeName().endsWith(PANEL_MENU_GROUP_END_TAG) || child 
							.getNodeName().endsWith(PANEL_MENU_END_TAG))) {
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
	
	/**
	 * Gets the specified icon from panelMenu or panelMenuGroupItem
	 * 
	 * @param pm_icon the panelMenu icon attribute
	 * @param pmi_icon the panelMenuGroupItem icon attribute
	 * 
	 * @return the specified icon
	 */
	private static String[] getSpecifiedIcon(String pm_icon, String pmi_icon) {
		String[] imgPath = {EMPTY, EMPTY};
		if (attrPresents(pmi_icon)) {
			/*
			 * Icon was set in the panelMenuItem attribute.
			 */
			imgPath[0] = pmi_icon;
			if (RichFacesPanelMenuGroupTemplate.DEFAULT_ICON_MAP.containsKey(pmi_icon)) {
				imgPath[1] = RichFacesPanelMenuGroupTemplate.DEFAULT_ICON_MAP.get(pmi_icon);
			}
		} else if (attrPresents(pm_iconTopDisabledItem)) {
			/*
			 * Icon was set in the panelMenu attribute.
			 */
			imgPath[0] = pm_icon;
			if (RichFacesPanelMenuGroupTemplate.DEFAULT_ICON_MAP.containsKey(pm_icon)) {
				imgPath[1] = RichFacesPanelMenuGroupTemplate.DEFAULT_ICON_MAP.get(pm_icon);
			}
		}
		return imgPath;
	}
	
	/**
	 * Sets the src attribute value for the image.
	 * 
	 * @param childOfPanelMenu if the item is the child of panelMenu
	 * @param pageContext the page context
	 * @param img the image
	 */
	private static void setIcon(boolean childOfPanelMenu, VpePageContext pageContext, nsIDOMElement img) {
		String[] imgPath = {EMPTY, EMPTY};
		boolean disabled = (TRUE.equalsIgnoreCase(pmi_disabled))
			|| (TRUE.equalsIgnoreCase(pm_disabled));
		
		if (childOfPanelMenu) {
			if (disabled) {
				imgPath = getSpecifiedIcon(pm_iconTopDisabledItem, pmi_iconDisabled);
			} else {
				imgPath = getSpecifiedIcon(pm_iconTopItem, pmi_icon);
			}
		} else {
			if (disabled) {
				imgPath = getSpecifiedIcon(pm_iconDisabledItem, pmi_iconDisabled);
			} else {
				imgPath = getSpecifiedIcon(pm_iconItem, pmi_icon);
			}
		}
		
		if (EMPTY.equalsIgnoreCase(imgPath[0])) {
			/*
			 * Icon wasn't set. Set default image.
			 */
			ComponentUtil.setImg(img, IMG_POINTS_SRC);
		} else {
			/*
			 * Set specified icon.
			 */
			if (RichFacesPanelMenuGroupTemplate.DEFAULT_ICON_MAP.containsKey(imgPath[0])) {
				ComponentUtil.setImg(img, imgPath[1]);
			} else {
				// TODO ADD SPACER OR IMG PATH
				img.setAttribute(HtmlComponentUtil.HTML_ATR_SRC, HtmlComponentUtil.FILE_PROTOCOL+imgPath[1]);
//				ComponentUtil.setImgFromResources(pageContext, imgPoints, imgPath[1],
//						IMG_SPACER_SRC);
			}
		}
	}

	
	/**
	 * Sets the item class and style for the item.
	 * 
	 * @param childOfPanelMenu if the item is the child of panelMenu
	 * @param table the table
	 * @param tr the table row
	 * @param img1 the first table row column image
	 * @param td2 the second table row column
	 * @param img3 the third table row column image
	 */
	private static void setItemClassAndStyle(boolean childOfPanelMenu,
			nsIDOMElement table,
			nsIDOMElement tr,
			nsIDOMElement img1,
			nsIDOMElement td2,
			nsIDOMElement img3) {
		
		String tableStyle = MARGIN_TOP;
		String tableClass = EMPTY;
		String trClass = EMPTY;
		String img1Class = EMPTY;
		String td2Class = EMPTY;
		String img3Class = EMPTY;
		
		if (attrPresents(pmi_styleClass)) {
			tableClass += SPACE + pmi_styleClass;
		}
		if (attrPresents(pmi_style)) {
			tableStyle += SPACE + pmi_style;
		}
		
		tableClass = CSS_ITEM;
		
		if ((TRUE.equalsIgnoreCase(pmi_disabled))
				|| (TRUE.equalsIgnoreCase(pm_disabled))) {
			tableClass += SPACE + CSS_DISABLED_ELEMENT;
			if (attrPresents(pm_disabledItemClass)) {
				tableClass += SPACE + pm_disabledItemClass;
			}
			if (attrPresents(pm_disabledItemStyle)) {
				tableStyle += SPACE + pm_disabledItemStyle;
			}
			if (attrPresents(pmi_disabledClass)) {
				tableClass += SPACE + pmi_disabledClass;
			}
			if (attrPresents(pmi_disabledStyle)) {
				tableStyle += SPACE + pmi_disabledStyle;
			}
			
		} else {
			img1Class = DR_NOWARP_CLASS + SPACE + CSS_ITEM_ICON;
			td2Class = CSS_ITEM_LABEL;
			img3Class = CSS_ITEM_ICON;
			if (attrPresents(pm_itemClass)) {
				tableClass += SPACE + pm_itemClass;
			}
			if (attrPresents(pm_itemStyle)) {
				tableStyle += SPACE + pm_itemStyle;
			}
			if (childOfPanelMenu) {
				tableClass += SPACE + CSS_TOP_ITEM;
				img1Class += SPACE + CSS_TOP_ITEM_ICON; 
				td2Class += SPACE + CSS_TOP_ITEM_LABEL; 
				img3Class += SPACE + CSS_TOP_ITEM_ICON; 
				if (attrPresents(pm_topItemClass)) {
					tableClass += SPACE + pm_topItemClass;
				}
				if (attrPresents(pm_topItemStyle)) {
					tableStyle += SPACE + pm_topItemStyle;
				}
			}
		}
		
		table.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, tableStyle);
		
		table.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, tableClass);
		tr.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, trClass);
		img3.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, img1Class);
		td2.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, td2Class);
		img3.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, img3Class);
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
		 * rich:panelMenuItem attributes
		 */
		pmi_disabled = sourceElement.getAttribute(DISABLED);
		pmi_icon = sourceElement.getAttribute(ICON);
		pmi_iconDisabled = sourceElement.getAttribute(ICON_DISABLED);
		pmi_disabledClass = sourceElement.getAttribute(DISABLED_CLASS);
		pmi_disabledStyle = sourceElement.getAttribute(DISABLED_STYLE);
		pmi_style = sourceElement.getAttribute(STYLE);
		pmi_styleClass = sourceElement.getAttribute(STYLE_CLASS);
		
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
}