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

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			Document visualDocument) {
		Element div = visualDocument
			.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		div.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, EMPTY_DIV_STYLE);
		
		return new VpeCreationData(div);
	}
	
	public static VpeCreationData encode(VpePageContext pageContext,VpeCreationData creationData, Element sourceElement, Document visualDocument, Element parentVisualElement, boolean active){
		
		ComponentUtil.setCSSLink(pageContext, STYLE_PATH, PANEL_MENU_ITEM);
		
		Element div = visualDocument
				.createElement(HtmlComponentUtil.HTML_TAG_DIV);
		parentVisualElement.appendChild(div);
		div.setAttribute("vpeSupport", PANEL_MENU_ITEM);
		creationData = new VpeCreationData(div);
		if (sourceElement.getParentNode().getNodeName().endsWith(
				":panelMenuGroup")
				|| (sourceElement.getParentNode().getNodeName()
						.endsWith(":panelMenu"))) {
			Element table = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
			div.appendChild(table);
			table.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, PANEL_MENU_ITEM_CLASS);
			table.setAttribute(HtmlComponentUtil.HTML_CELLPADDING_ATTR, NO_SIZE_VALUE);
			table.setAttribute(HtmlComponentUtil.HTML_CELLSPACING_ATTR, NO_SIZE_VALUE);
			table.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR, NO_SIZE_VALUE);

			Element tr = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_TR);
			table.appendChild(tr);

			Element tdNowrap = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_TD);
			tr.appendChild(tdNowrap);
			tdNowrap.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, PANEL_MENU_NOWARP_CLASS);

			Element imgSpacer1 = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_IMG);
			tdNowrap.appendChild(imgSpacer1);
			imgSpacer1.setAttribute(HtmlComponentUtil.HTML_ATR_WIDTH, DEFAULT_SIZE_VALUE);
			imgSpacer1.setAttribute("vspace", NO_SIZE_VALUE);
			imgSpacer1.setAttribute("hspace", NO_SIZE_VALUE);
			imgSpacer1.setAttribute(HtmlComponentUtil.HTML_ATR_HEIGHT, DEFAULT_SIZE_VALUE);
			ComponentUtil.setImg(imgSpacer1, IMG_SPACER_SRC);

			Element imgPoints = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_IMG);
			tdNowrap.appendChild(imgPoints);
			imgPoints.setAttribute(HtmlComponentUtil.HTML_ATR_WIDTH, DEFAULT_SIZE_VALUE);
			imgPoints.setAttribute("vspace", NO_SIZE_VALUE);
			imgPoints.setAttribute("hspace", NO_SIZE_VALUE);
			imgPoints.setAttribute(HtmlComponentUtil.HTML_ATR_HEIGHT, DEFAULT_SIZE_VALUE);
			ComponentUtil.setImg(imgPoints, IMG_POINTS_SRC);

			Element tdLable = visualDocument
					.createElement(HtmlComponentUtil.HTML_TAG_TD);
			tr.appendChild(tdLable);
			tdLable.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, PANEL_MENU_LABLE_CLASS);
			tdLable.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, "element.style");
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
			imgSpacer2.setAttribute(HtmlComponentUtil.HTML_ATR_WIDTH, DEFAULT_SIZE_VALUE);
			imgSpacer2.setAttribute("vspace", NO_SIZE_VALUE);
			imgSpacer2.setAttribute("hspace", NO_SIZE_VALUE);
			imgSpacer2.setAttribute(HtmlComponentUtil.HTML_ATR_HEIGHT, DEFAULT_SIZE_VALUE);
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
		}
		return creationData;
	}
}
