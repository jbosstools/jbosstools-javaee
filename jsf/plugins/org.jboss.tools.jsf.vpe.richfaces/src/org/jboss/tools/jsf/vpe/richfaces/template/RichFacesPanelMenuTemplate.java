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

import java.util.HashMap;
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

	private static final String WIDTH_ATTR_PANELMENU = "width";
	private static final String STYLE_ATTR_PANELMENU = "style";
	private static final String STYLECLASS_ATTR_PANELMENU = "styleClass";

	private static final String PANEL_MENU_GROUP_END = ":panelMenuGroup";
	private static final String PANEL_MENU_ITEM_END = ":panelMenuItem";

	private static Map toggleMap = new HashMap();

	// private static final String DISABLED_STYLE_FOR_TABLE = "color:#B1ADA7";

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {

		Element sourceElement = (Element) sourceNode;

		String width = sourceElement.getAttribute(WIDTH_ATTR_PANELMENU);
		String style = sourceElement.getAttribute(STYLE_ATTR_PANELMENU);
		String styleClass = sourceElement
				.getAttribute(STYLECLASS_ATTR_PANELMENU);

		if (width != null) {
			style += "" + "; width:" + width;
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
		int activeId = getActiveId(sourceElement, children);
		int i = 0;

		for (Node child : children) {

			boolean expanded = (i == activeId);

			if (child.getNodeName().endsWith(PANEL_MENU_GROUP_END)) {
				RichFacesPanelMenuGroupTemplate.encode(pageContext,
						vpeCreationData, sourceElement, (Element) child,
						visualDocument, div, expanded, i);
				i++;
			} else if (child.getNodeName().endsWith(PANEL_MENU_ITEM_END)) {
				RichFacesPanelMenuItemTemplate.encode(pageContext,
						vpeCreationData, sourceElement, (Element) child,
						visualDocument, div, expanded);
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
	 * 
	 * @param sourceElement
	 * @param children
	 * @return
	 */
	private int getActiveId(Element sourceElement, List<Node> children) {
		int activeId = -1;
		try {
			activeId = Integer.valueOf((String) toggleMap.get(sourceElement));
		} catch (NumberFormatException nfe) {
			activeId = -1;
		}

		int count = getChildrenCount(children);
		if (count - 1 < activeId) {
			activeId = count - 1;
		}

		return activeId;
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
		toggleMap.put(sourceNode, toggleId);
	}

	public void stopToggling(Node sourceNode) {
		toggleMap.remove(sourceNode);
	}

	public boolean isRecreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}
}