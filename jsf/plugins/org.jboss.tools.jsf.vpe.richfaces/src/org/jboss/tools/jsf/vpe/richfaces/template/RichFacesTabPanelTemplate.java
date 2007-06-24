/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.vpe.richfaces.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeToggableTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesTabPanelTemplate extends VpeAbstractTemplate implements VpeToggableTemplate {

	private static Map toggleMap = new HashMap();

	@Override
	public boolean isRecreateAtAttrChange(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
		return true;
	}

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, Document visualDocument) {

		Element sourceElement = (Element)sourceNode;

		Element table = visualDocument.createElement("table");

		VpeCreationData creationData = new VpeCreationData(table);

		ComponentUtil.setCSSLink(pageContext, "tabPanel/tabPanel.css", "richFacesTabPanel");
		table.setAttribute("class", "rich-tabpanel " + ComponentUtil.getAttribute(sourceElement, "styleClass"));
		table.setAttribute("border", "0");
		table.setAttribute("cellpadding", "0");
		table.setAttribute("cellspacing", "0");
		table.setAttribute("style", getStyle(sourceElement));
		
		Element tbody = visualDocument.createElement("tbody");
		table.appendChild(tbody);
		Element tr = visualDocument.createElement("tr");
		tbody.appendChild(tr);
		Element td = visualDocument.createElement("td");
		tr.appendChild(td);
		td.setAttribute("align", getHeaderAlignment(sourceElement));

		Element inerTable = visualDocument.createElement("table");
		td.appendChild(inerTable);
		inerTable.setAttribute("border", "0");
		inerTable.setAttribute("cellpadding", "0");
		inerTable.setAttribute("cellspacing", "0");

		// Encode header
		Element inerTr = visualDocument.createElement("tr");
		inerTable.appendChild(inerTr);
		Element inerTd = visualDocument.createElement("td");
		inerTr.appendChild(inerTd);
		Element img = visualDocument.createElement("img");
		inerTd.appendChild(img);
		ComponentUtil.setImg(img, "common/spacer.gif");
		img.setAttribute("width", "2");
		img.setAttribute("height", "1");
		img.setAttribute("border", "0");

		List<Node> children = ComponentUtil.getChildren(sourceElement);
		int activeId = getActiveId(sourceElement, children);
		int i = 0;
		for (Node child : children) {
			boolean active = (i == activeId);
			
			if(child.getNodeName().endsWith(":tab")) {
				RichFacesTabTemplate.encodeHeader((Element)child, visualDocument, inerTr, active, ComponentUtil.getAttribute(sourceElement, "activeTabClass"), ComponentUtil.getAttribute(sourceElement, "inactiveTabClass"), ComponentUtil.getAttribute(sourceElement, "disabledTabClass"), String.valueOf(i));
				i++;
				// Add <td><img src="#{spacer}" height="1" alt="" border="0" style="#{this:encodeHeaderSpacing(context, component)}"/></td>
				Element spaceTd = visualDocument.createElement("td");
				inerTr.appendChild(spaceTd);
				Element spaceImg = visualDocument.createElement("img");
				spaceTd.appendChild(spaceImg);
				ComponentUtil.setImg(spaceImg, "common/spacer.gif");
				spaceImg.setAttribute("height", "1");
				spaceImg.setAttribute("border", "0");
				String headerSpacing = sourceElement.getAttribute("headerSpacing");
				if(headerSpacing==null) {
					headerSpacing = "1";
				}
				spaceImg.setAttribute("style", "width: " + headerSpacing + "px");
			}
		}

		inerTd = visualDocument.createElement("td");
		inerTr.appendChild(inerTd);
		img = visualDocument.createElement("img");
		inerTd.appendChild(img);
		ComponentUtil.setImg(img, "common/spacer.gif");
		img.setAttribute("width", "1");
		img.setAttribute("height", "1");
		img.setAttribute("border", "0");

		// Encode first child tab
		inerTr = visualDocument.createElement("tr");
		tbody.appendChild(inerTr);
		children = ComponentUtil.getChildren(sourceElement);
		i = 0;
		for (Node child : children) {
			boolean active = (i == activeId);
			if(child.getNodeName().endsWith(":tab")) {
				i++;
				if (active) {
					RichFacesTabTemplate.encodeBody(creationData, (Element)child, visualDocument, inerTr, true, ComponentUtil.getAttribute(sourceElement, "tabClass"), ComponentUtil.getAttribute(sourceElement, "activeTabClass"), ComponentUtil.getAttribute(sourceElement, "inactiveTabClass"), ComponentUtil.getAttribute(sourceElement, "disabledTabClass"), ComponentUtil.getAttribute(sourceElement, "contentClass"), ComponentUtil.getAttribute(sourceElement, "contentStyle"));
					break;
				}
			}
		}

		return creationData;
	}

	private int getActiveId(Element sourceElement, List<Node> children) {
		int activeId = -1;
		try { 
			activeId = Integer.valueOf((String)toggleMap.get(sourceElement));
		} catch (NumberFormatException nfe) {
			activeId = -1;
		}

		if (activeId == -1) {
			activeId = getTabId(children, sourceElement.getAttribute("selectedTab"));
		}
		
		if (activeId == -1) 
			activeId = 0;
			
		int count = getChildrenCount(children);
		if (count - 1 < activeId) {
			activeId = count - 1;
		}
		
		return activeId;
	}
	
	private int getChildrenCount(List<Node> children) {
		int count = 0;
		for (Node child : children) {
			if (child.getNodeName().endsWith(":tab")) {
				count++;
			}
		}
		return count;
	}

	private int getTabId(List<Node> children, String tabName) {
		if (tabName == null) return -1;
		int count = 0;
		for (Node child : children) {
			if (child.getNodeName().endsWith(":tab")) {
				if (!(child instanceof Element))
					continue;
				
				String name = ((Element)child).getAttribute("name");
				if (tabName.equals(name))
					return count;
				
				count++;
			}
		}
		return -1;
	}

	private String getStyle(Element sourceElement) {
		String widthAttrValue = sourceElement.getAttribute("width");
		String heightAttrValue = sourceElement.getAttribute("height");
		String styleAttrValue = sourceElement.getAttribute("style");
		String style = styleAttrValue != null ? styleAttrValue : "";

		if (!ComponentUtil.parameterPresent(styleAttrValue, "width")) {
			String width = (widthAttrValue != null && widthAttrValue.length() > 0) ? widthAttrValue : "100%";
			style = ComponentUtil.addParameter(style, "width:" + width);
		}

		if (!ComponentUtil.parameterPresent(styleAttrValue, "height")) {
			String height = (heightAttrValue != null && heightAttrValue.length() > 0) ? heightAttrValue : "";
			if (height.length() > 0) {
				style =ComponentUtil.addParameter(style, "height:" + height);
			}
		}
		return style;
	}

	private String getHeaderAlignment(Element sourceElement) {
		String headerAlignment = sourceElement.getAttribute("headerAlignment");
		if(headerAlignment==null) {
			headerAlignment = "left"; 
		}
		return headerAlignment;
	}

	public void toggle(VpeVisualDomBuilder builder, Node sourceNode, String toggleId) {
		toggleMap.put(sourceNode, toggleId);
	}

	public void stopToggling(Node sourceNode) {
		toggleMap.remove(sourceNode);
	}
}