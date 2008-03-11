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

	private static final String STYLE_PATH = "/panelMenuItem/style.css";

	private static final String PANEL_MENU_ITEM_CLASS = "dr-pmenu-item";

	private static final String NO_SIZE_VALUE = "0";

	private static final String DEFAULT_SIZE_VALUE = "16";

	private static final String PANEL_MENU_NOWARP_CLASS = "dr-pmenu-nowrap";

	private static final String PANEL_MENU_LABLE_CLASS = "dr-pmenu-group-self-label";

	private static final String PANEL_MENU_DIV = "dr-pmenu-top-self-div";

	private static final String PANEL_MENU_ITEM = "panelMenuItem";

	private static final String IMG_POINTS_SRC = "/panelMenuItem/points.gif";

	private static final String IMG_SPACER_SRC = "/panelMenuItem/spacer.gif";

	private static final String EMPTY_DIV_STYLE = "display: none;";

	private static final String DISABLED_CLASS = "dr-pmenu-item-disabled";

	private static final String DISABLED_ITEM_STYLE = "disabledItemStyle";

	private static final String ITEM_CLASS = "itemClass";

	private static final String ICON_ITEM_TOP_POSITION = "iconItemTopPosition";

	private static final String TOP_ITEM_STYLE = "topItemStyle";

	private static final String ICON_TOP_DISABLED_ITEM = "iconTopDisabledItem";

	private static final String DISABLE_ITEM_CLASS = "disableItemClass";

	private static final String ICON_DISABLED_ITEM = "iconDisabledItem";

	private static final String ICON_ITEM_POSITION = "iconItemPosition";

	private static final String ICON_ITEM = "iconItem";

	private static final String ICON_TOP_ITEM = "iconTopItem";

	private static final String TOP_ITEM_CLASS = "topItemClass";

	private static final String ITEM_STYLE = "itemStyle";

	private static final String ICON_DISABLED = "iconDisabled";

	private static final String ICON = "icon";

	private static final String STYLE_CLASS = "styleClass";

	private static final String STYLE = "style";

	private static final String DISABLED = "disabledClass";

	private static final String DISABLED_STYLE = "disabledStyle";

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
			nsIDOMElement parentVisualElement, boolean active) {

		ComponentUtil.setCSSLink(pageContext, STYLE_PATH, PANEL_MENU_ITEM);

		nsIDOMElement parentDiv = visualDocument.createElement("div");
		parentDiv.setAttribute("CLASS", PANEL_MENU_DIV);
		parentVisualElement.appendChild(parentDiv);
		nsIDOMElement div = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);

		parentDiv.appendChild(div);

		if (sourceElement.getParentNode().getNodeName().endsWith(
				":panelMenuGroup")
				|| (sourceElement.getParentNode().getNodeName()
						.endsWith(":panelMenu"))) {
			div.setAttribute("vpeSupport", PANEL_MENU_ITEM);
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
					"element.style");

			String value = sourceElement.getAttribute("label");
			nsIDOMText text = visualDocument.createTextNode(value == null ? ""
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
					":panelMenu")) {

				if (isDisabledItem(sourceElement.getAttribute("disabled"))) {
					setIcon(pageContext, imgPoints, sourceElement,
							sourceParentElement, ICON_TOP_DISABLED_ITEM,
							ICON_DISABLED);
					setItemClassAndStyle(table, sourceParentElement
							.getAttribute(DISABLE_ITEM_CLASS), sourceElement
							.getAttribute(DISABLED), DISABLED_CLASS,
							sourceParentElement
									.getAttribute(DISABLED_ITEM_STYLE),
							sourceElement.getAttribute(DISABLED_STYLE));
				} else {
					setIcon(pageContext, imgPoints, sourceElement,
							sourceParentElement, ICON_TOP_ITEM, ICON);
					setItemClassAndStyle(table, sourceParentElement
							.getAttribute(TOP_ITEM_CLASS), sourceElement
							.getAttribute(STYLE_CLASS), PANEL_MENU_ITEM_CLASS,
							sourceParentElement.getAttribute(TOP_ITEM_STYLE),
							sourceElement.getAttribute(STYLE));
				}
				setIconPosition(sourceParentElement
						.getAttribute(ICON_ITEM_TOP_POSITION), td, tdNowrap,
						imgPoints, imgSpacer2);

			} else {
				if (isDisabledItem(sourceElement.getAttribute("disabled"))) {
					setIcon(pageContext, imgPoints, sourceElement,
							sourceParentElement, ICON_DISABLED_ITEM,
							ICON_DISABLED);
					setItemClassAndStyle(table, sourceParentElement
							.getAttribute(DISABLE_ITEM_CLASS), sourceElement
							.getAttribute(DISABLED), DISABLED_CLASS,
							sourceParentElement
									.getAttribute(DISABLED_ITEM_STYLE),
							sourceElement.getAttribute(DISABLED_STYLE));
				} else {
					setIcon(pageContext, imgPoints, sourceElement,
							sourceParentElement, ICON_ITEM, ICON);
					setItemClassAndStyle(table, sourceParentElement
							.getAttribute(ITEM_CLASS), sourceElement
							.getAttribute(STYLE_CLASS), PANEL_MENU_ITEM_CLASS,
							sourceParentElement.getAttribute(ITEM_STYLE),
							sourceElement.getAttribute(STYLE));
				}
				setIconPosition(sourceParentElement
						.getAttribute(ICON_ITEM_POSITION), td, tdNowrap,
						imgPoints, imgSpacer2);
			}

			List<Node> children = ComponentUtil.getChildren(sourceElement);

			if (!children.isEmpty()) {
				VpeChildrenInfo childInfo = new VpeChildrenInfo(tdLable);
				creationData.addChildrenInfo(childInfo);
				for (Node child : children) {
					if (!(child.getNodeName().endsWith(":panelMenuGroup") || child
							.getNodeName().endsWith(":panelMenu"))) {
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
		element.setAttribute("vspace", NO_SIZE_VALUE);
		element.setAttribute("hspace", NO_SIZE_VALUE);
		element.setAttribute(HtmlComponentUtil.HTML_ATR_HEIGHT,
				DEFAULT_SIZE_VALUE);
	}

	private static void setItemImage(nsIDOMElement place, nsIDOMElement image) {
		place.appendChild(image);
		setDefaultImgAttributes(image);
	}

	private static boolean isDisabledItem(String disabled) {
		if ("true".equals(disabled)) {
			return true;
		}
		return false;
	}

	private static void setIcon(VpePageContext pageContext,
			nsIDOMElement imgPoints, Element sourceElement,
			Element parentElement, String parentIconAttribute,
			String iconAttribute) {
		String icon = sourceElement.getAttribute(iconAttribute);
		String parentIcon = parentElement.getAttribute(parentIconAttribute);
		if (icon == null || icon.length() == 0) {
			if (!(parentIcon == null || parentIcon.length() == 0)) {
				ComponentUtil.setImgFromResources(pageContext, imgPoints,
						parentIcon, IMG_SPACER_SRC);
			} else {
				ComponentUtil.setImg(imgPoints, IMG_POINTS_SRC);
			}
		} else {
			ComponentUtil.setImgFromResources(pageContext, imgPoints, icon,
					IMG_SPACER_SRC);
		}
	}

	private static void setIconPosition(String iconPosition,
			nsIDOMElement right, nsIDOMElement left, nsIDOMElement imgPoints,
			nsIDOMElement imgSpacer2) {
		if (!(iconPosition == null)) {
			if (iconPosition.equals("right")) {
				setItemImage(right, imgPoints);
			} else {
				setItemImage(right, imgSpacer2);
				ComponentUtil.setImg(imgSpacer2, IMG_SPACER_SRC);
				if (iconPosition.equals("left")) {
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
		String resultClass = "";
		if (!(parentClass == null || parentClass.length() == 0)) {
			resultClass += parentClass;
		}
		if (!(itemClass == null || itemClass.length() == 0)) {
			resultClass += itemClass;
		}
		table.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, defaultClass
				+ " " + resultClass);

		String resultStyle = "";
		if (!(parentStyle == null || parentStyle.length() == 0)) {
			resultStyle += parentStyle;
		}
		if (!(itemStyle == null || itemStyle.length() == 0)) {
			resultStyle += itemStyle;
		}
		table.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, resultStyle);
	}
}