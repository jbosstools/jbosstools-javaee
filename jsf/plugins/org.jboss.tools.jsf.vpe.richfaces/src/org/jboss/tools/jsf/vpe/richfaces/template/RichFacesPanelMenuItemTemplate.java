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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

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

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			Document visualDocument) {
		Element div = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		div.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, EMPTY_DIV_STYLE);

		return new VpeCreationData(div);
	}

	public static VpeCreationData encode(VpePageContext pageContext,
			VpeCreationData creationData, Element sourceParentElement,
			Element sourceElement, Document visualDocument,
			Element parentVisualElement, boolean active) {

		ComponentUtil.setCSSLink(pageContext, STYLE_PATH, PANEL_MENU_ITEM);

		Element parentDiv = visualDocument.createElement("div");
		parentDiv.setAttribute("CLASS", PANEL_MENU_DIV);
		parentVisualElement.appendChild(parentDiv);
		Element div = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);

		parentDiv.appendChild(div);

		if (sourceElement.getParentNode().getNodeName().endsWith(
				":panelMenuGroup")
				|| (sourceElement.getParentNode().getNodeName()
						.endsWith(":panelMenu"))) {
			div.setAttribute("vpeSupport", PANEL_MENU_ITEM);
			Element table = visualDocument
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

			Element tr = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_TR);
			table.appendChild(tr);

			Element tdNowrap = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_TD);
			tr.appendChild(tdNowrap);
			tdNowrap.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					PANEL_MENU_NOWARP_CLASS);

			Element tdLable = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_TD);
			tr.appendChild(tdLable);
			tdLable.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					PANEL_MENU_LABLE_CLASS);
			tdLable.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
					"element.style");
			String value = sourceElement.getAttribute("label");
			Text text = visualDocument.createTextNode(value == null ? ""
					: value);

			tdLable.appendChild(text);

			Element td = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_TD);
			tr.appendChild(td);

			Element imgPoints = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_IMG);

			Element imgSpacer1 = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_IMG);

			tdNowrap.appendChild(imgSpacer1);
			setDefaultImgAttributes(imgSpacer1);
			ComponentUtil.setImg(imgSpacer1, IMG_SPACER_SRC);

			Element imgSpacer2 = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_IMG);

			if (sourceElement.getParentNode().getNodeName().endsWith(
					":panelMenu")) {
				String icon = sourceElement.getAttribute("icon");
				if (icon == null || icon.length() == 0) {
					setItemImage(sourceElement, sourceParentElement, td,
							tdNowrap, ICON_ITEM_TOP_POSITION, pageContext,
							visualDocument, sourceParentElement
									.getAttribute(ICON_TOP_ITEM), imgPoints,
							imgSpacer2);
				} else {
					setItemImage(sourceElement, sourceParentElement, td,
							tdNowrap, ICON_ITEM_TOP_POSITION, pageContext,
							visualDocument, icon, imgPoints, imgSpacer2);
				}

				setItemClassAndStyle(sourceElement, sourceParentElement,
						imgPoints, pageContext, table, sourceParentElement
								.getAttribute(TOP_ITEM_CLASS),
						sourceParentElement.getAttribute(TOP_ITEM_STYLE),
						sourceParentElement
								.getAttribute(ICON_TOP_DISABLED_ITEM));

			} else {
				if (sourceElement.getParentNode().getNodeName().endsWith(
						":panelMenuGroup")) {
					String icon = sourceElement.getAttribute("icon");
					if (icon == null || icon.length() == 0) {
						setItemImage(sourceElement, sourceParentElement, td,
								tdNowrap, ICON_ITEM_POSITION, pageContext,
								visualDocument, sourceParentElement
										.getAttribute(ICON_ITEM), imgPoints,
								imgSpacer2);
					} else {
						setItemImage(sourceElement, sourceParentElement, td,
								tdNowrap, ICON_ITEM_POSITION, pageContext,
								visualDocument, icon, imgPoints, imgSpacer2);
					}
				}

				setItemClassAndStyle(sourceElement, sourceParentElement,
						imgPoints, pageContext, table, sourceParentElement
								.getAttribute(ITEM_CLASS), sourceParentElement
								.getAttribute(ITEM_STYLE), sourceParentElement
								.getAttribute(ICON_DISABLED_ITEM));
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
			Element sourceElement, Document visualDocument, Node visualNode,
			Object data, String name, String value) {
		return true;
	}

	private static void setDefaultImgAttributes(Element element) {
		element.setAttribute(HtmlComponentUtil.HTML_ATR_WIDTH,
				DEFAULT_SIZE_VALUE);
		element.setAttribute("vspace", NO_SIZE_VALUE);
		element.setAttribute("hspace", NO_SIZE_VALUE);
		element.setAttribute(HtmlComponentUtil.HTML_ATR_HEIGHT,
				DEFAULT_SIZE_VALUE);
	}

	private static void setImagePosition(Element place, Element image,
			VpePageContext pageContext, String icon) {
		place.appendChild(image);
		setDefaultImgAttributes(image);
		if (icon == null || icon.length() == 0) {
			ComponentUtil.setImg(image, IMG_POINTS_SRC);
		} else {
			ComponentUtil.setImgFromResources(pageContext, image, icon,
					IMG_SPACER_SRC);
		}
	}

	private static void setItemImage(Element sourceElement,
			Element sourceParentElement, Element td, Element tdNowrap,
			String attribute, VpePageContext pageContext,
			Document visualDocument, String image, Element imgPoints,
			Element imgSpacer2) {
		String iconPosition = sourceParentElement.getAttribute(attribute);
		if (!(iconPosition == null)) {
			if (iconPosition.equals("right")) {

				setImagePosition(td, imgPoints, pageContext, image);

			} else {
				td.appendChild(imgSpacer2);
				setDefaultImgAttributes(imgSpacer2);
				ComponentUtil.setImg(imgSpacer2, IMG_SPACER_SRC);
				if (iconPosition.equals("left")) {
					setImagePosition(tdNowrap, imgPoints, pageContext, image);
				}
			}
		} else {
			setImagePosition(tdNowrap, imgPoints, pageContext, image);
			td.appendChild(imgSpacer2);
			setDefaultImgAttributes(imgSpacer2);
			ComponentUtil.setImg(imgSpacer2, IMG_SPACER_SRC);
		}
	}

	private static void setItemClassAndStyle(Element sourceElement,
			Element sourceParentElement, Element imgPoints,
			VpePageContext pageContext, Element table,
			String parentClass, String parentStyle, String icon) {

		if ("true".equals(sourceElement.getAttribute("disabled"))) {

			String iconDisabled = sourceElement.getAttribute("iconDisabled");
			if (iconDisabled == null || iconDisabled.length() == 0) {
				if (!(icon == null || icon.length() == 0)) {
					ComponentUtil.setImgFromResources(pageContext, imgPoints,
							icon, IMG_POINTS_SRC);
				} else {
					ComponentUtil.setImg(imgPoints, IMG_POINTS_SRC);
				}
			} else {
				ComponentUtil.setImgFromResources(pageContext, imgPoints,
						iconDisabled, IMG_SPACER_SRC);
			}

			String resultDisabledClass = "";
			String disabledItemClass = sourceParentElement
					.getAttribute(DISABLE_ITEM_CLASS);
			if (!(disabledItemClass == null || disabledItemClass.length() == 0)) {
				resultDisabledClass += disabledItemClass;
			}
			String disabledClass = sourceElement.getAttribute("disabledClass");
			if (!(disabledClass == null || disabledClass.length() == 0)) {
				resultDisabledClass += disabledClass;
			}
			table.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					DISABLED_CLASS + " " + resultDisabledClass);

			String resultDisabledStyle = "";
			String disabledItemStyle = sourceParentElement
					.getAttribute(DISABLED_ITEM_STYLE);
			if (!(disabledItemStyle == null || disabledItemStyle.length() == 0)) {
				resultDisabledStyle += disabledItemStyle;
			}
			String disabledStyle = sourceElement.getAttribute("disabledStyle");
			if (!(disabledStyle == null || disabledStyle.length() == 0)) {
				resultDisabledStyle += disabledStyle;
			}
			table.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
					resultDisabledStyle);

		} else {
			String resultClass = "";
			String itemClass = parentClass;
			if (!(itemClass == null || itemClass.length() == 0)) {
				resultClass += itemClass;
			}
			String styleClass = sourceElement.getAttribute("styleClass");
			if (!(styleClass == null || styleClass.length() == 0)) {
				resultClass += styleClass;
			}
			table.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
					PANEL_MENU_ITEM_CLASS + " " + resultClass);

			String resultStyle = "";
			String itemStyle = parentStyle;
			if (!(itemStyle == null || itemStyle.length() == 0)) {
				resultStyle += itemStyle;
			}
			String style = sourceElement.getAttribute("style");
			if (!(style == null || style.length() == 0)) {
				resultStyle += style;
			}
			table.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, resultStyle);
		}
	}
}