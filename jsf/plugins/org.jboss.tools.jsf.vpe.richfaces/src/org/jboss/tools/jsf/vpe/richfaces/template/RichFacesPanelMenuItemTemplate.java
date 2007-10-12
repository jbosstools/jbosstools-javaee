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

	private static final String PANEL_MENU_ITEM = "panelMenuItem";

	private static final String IMG_POINTS_SRC = "/panelMenuItem/points.gif";

	private static final String IMG_SPACER_SRC = "/panelMenuItem/spacer.gif";

	private static final String EMPTY_DIV_STYLE = "display: none;";

	private static final String DISABLED_STYLE = "color:#B1ADA7;";

	private static final String DISABLED_CLASS = "dr-pmenu-item-disabled";

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			Document visualDocument) {
		Element div = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		div.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, EMPTY_DIV_STYLE);

		return new VpeCreationData(div);
	}

	public static VpeCreationData encode(VpePageContext pageContext,
			VpeCreationData creationData, Element sourceElement,
			Document visualDocument, Element parentVisualElement, boolean active) {

		ComponentUtil.setCSSLink(pageContext, STYLE_PATH, PANEL_MENU_ITEM);

		Element div = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		parentVisualElement.appendChild(div);
		
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

			Element imgSpacer1 = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_IMG);
			tdNowrap.appendChild(imgSpacer1);
			setDefaultImgAttributes(imgSpacer1);
			ComponentUtil.setImg(imgSpacer1, IMG_SPACER_SRC);

			Element imgPoints = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_IMG);
			tdNowrap.appendChild(imgPoints);
			
			String icon = sourceElement.getAttribute("icon");
			//String iconStyle = sourceElement.getAttribute("iconStyle");
			//String iconClass = sourceElement.getAttribute("iconClass");

			if (icon == null || icon.length() == 0) {
				ComponentUtil.setImg(imgPoints, IMG_POINTS_SRC);
			} else {
				ComponentUtil.setImgFromResources(pageContext, imgPoints, icon,
						IMG_POINTS_SRC);
			}
			
			/*if ((iconStyle == null) || (iconStyle.length() == 0)) {
				setDefaultImgAttributes(imgPoints);
			} else {
				imgPoints.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
						iconStyle);
			}
			
			if ((iconClass == null) || (iconClass.length() == 0)) {
				setDefaultImgAttributes(imgPoints);
			} else {
				imgPoints.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
						iconClass);
			}*/

			Element tdLable = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_TD);
			tr.appendChild(tdLable);
			tdLable.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, PANEL_MENU_LABLE_CLASS);
			tdLable.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
					"element.style");
			String value = sourceElement.getAttribute("label");
			Text text = visualDocument.createTextNode(value == null ? ""
					: value);
			tdLable.appendChild(text);

			Element td = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_TD);
			tr.appendChild(td);

			Element imgSpacer2 = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_IMG);
			td.appendChild(imgSpacer2);
			setDefaultImgAttributes(imgSpacer2);
			ComponentUtil.setImg(imgSpacer2, IMG_SPACER_SRC);

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
			
			String styleClass = sourceElement.getAttribute("styleClass");
			if (!(styleClass == null || styleClass.length() == 0)) {
				div.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, styleClass);
			}
			else {
				styleClass="";
			}

			if ("true".equals(sourceElement.getAttribute("disabled"))) {
				String disabledStyle = sourceElement
						.getAttribute("disabledStyle");
				String disabledClass = sourceElement
						.getAttribute("disabledClass");
				String iconDisabled = sourceElement
						.getAttribute("iconDisabled");
				if (iconDisabled == null || iconDisabled.length() == 0) {
					ComponentUtil.setImg(imgPoints, IMG_POINTS_SRC);
				} else {
					ComponentUtil.setImgFromResources(pageContext, imgPoints,
							iconDisabled, IMG_SPACER_SRC);
				}

				if ((disabledStyle == null) || (disabledStyle.length() == 0)) {
					table.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
							DISABLED_STYLE);
				} else {
					table.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
							disabledStyle);
				}
				if ((disabledClass == null) || (disabledClass.length() == 0)) {
					div.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,styleClass+" "+ DISABLED_CLASS);
				} else {
					div.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,
							styleClass + " " + disabledClass);
				}
			}
		}
		return creationData;
	}
	
	public boolean isRecreateAtAttrChange(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
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
}