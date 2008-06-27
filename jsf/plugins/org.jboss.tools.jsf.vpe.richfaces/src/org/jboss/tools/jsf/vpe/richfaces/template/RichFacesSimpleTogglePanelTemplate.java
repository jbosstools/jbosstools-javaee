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

import java.util.ArrayList;
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
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.xpcom.XPCOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesSimpleTogglePanelTemplate extends VpeAbstractTemplate implements VpeToggableTemplate {

	private static Map toggleMap = new HashMap();
	private nsIDOMElement storedHeaderDiv = null;
	
	private static final String COLLAPSED_STYLE ="; display: none;";
	private static final String HEADER_NAME_FACET = "header";
	

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {

		Element sourceElement = (Element)sourceNode;

		nsIDOMElement div = visualDocument.createElement("div");

		VpeCreationData creationData = new VpeCreationData(div);

		ComponentUtil.setCSSLink(pageContext, "simpleTogglePanel/simpleTogglePanel.css", "richFacesSimpleTogglePanel");
		div.setAttribute("class", "dr-stglpnl rich-stglpanel " + ComponentUtil.getAttribute(sourceElement, "styleClass"));
		div.setAttribute("style", "width: " + ComponentUtil.getAttribute(sourceElement, "width") + ";" + ComponentUtil.getAttribute(sourceElement, "style"));

		// Encode Header
		nsIDOMElement headerDiv = visualDocument.createElement("div");
		div.appendChild(headerDiv);
		headerDiv.setAttribute("class", "dr-stglpnl-h rich-stglpanel-header " + ComponentUtil.getAttribute(sourceElement, "headerClass"));
		headerDiv.setAttribute("style", "position : relative; " + ComponentUtil.getHeaderBackgoundImgStyle());

		//http://jira.jboss.com/jira/browse/JBIDE-791
		Element firstElementOfHeaderFacet = ComponentUtil.getFacet(sourceElement, HEADER_NAME_FACET);
		if(firstElementOfHeaderFacet != null) {
			VpeChildrenInfo headerInfo = new VpeChildrenInfo(headerDiv);
			headerInfo.addSourceChild(firstElementOfHeaderFacet);
			creationData.addChildrenInfo(headerInfo);
		} else {
			String label = ComponentUtil.getAttribute(sourceElement, "label");
			headerDiv.appendChild(visualDocument.createTextNode(label));
		}
		/////

		nsIDOMElement switchDiv = visualDocument.createElement("div");
		headerDiv.appendChild(switchDiv);
		switchDiv.setAttribute("style", "position : absolute; top: 0px; right: 5px;");

		String markerName = "openMarker";
		char defaultMarkerCode = 187;
		boolean opened = getActiveState(sourceElement);
		
		headerDiv.setAttribute(VpeVisualDomBuilder.VPE_USER_TOGGLE_ID, (opened ? "false" : "true"));
		storedHeaderDiv = headerDiv;
		
		if(opened) {
			markerName = "closeMarker";
			defaultMarkerCode = 171;
		}
		Element markerFacet = ComponentUtil.getFacet(sourceElement, markerName);
		if(markerFacet==null) {
			switchDiv.appendChild(visualDocument.createTextNode("" + defaultMarkerCode));
		} else {
			VpeChildrenInfo switchInfo = new VpeChildrenInfo(switchDiv);
			switchInfo.addSourceChild(markerFacet);
			creationData.addChildrenInfo(switchInfo);
		}

		// Encode Body
		//if(opened) {
		    nsIDOMElement bodyDiv = visualDocument.createElement("div");
			div.appendChild(bodyDiv);
			bodyDiv.setAttribute("style", "overflow: hidden; height: " + ComponentUtil.getAttribute(sourceElement, "height") + "; width: 100%;");

			nsIDOMElement table = visualDocument.createElement("table");
			bodyDiv.appendChild(table);
			table.setAttribute("cellpadding", "0");
			table.setAttribute("style", "width: 100%");
			nsIDOMElement tr = visualDocument.createElement("tr");
			table.appendChild(tr);
			nsIDOMElement td = visualDocument.createElement("td");
			tr.appendChild(td);
			td.setAttribute("class", "dr-stglpnl-b rich-stglpanel-body " + ComponentUtil.getAttribute(sourceElement, "bodyClass"));

			List<Node> children = ComponentUtil.getChildren(sourceElement, true);
			VpeChildrenInfo bodyInfo = new VpeChildrenInfo(td);
			for (Node child : children) {
				bodyInfo.addSourceChild(child);
			}
			creationData.addChildrenInfo(bodyInfo);
			
			//http://jira.jboss.com/jira/browse/JBIDE-791
			if(!opened) {
				String newStyle = bodyDiv.getAttribute(HtmlComponentUtil.HTML_STYLE_ATTR);
				newStyle += COLLAPSED_STYLE;
				bodyDiv.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, newStyle);
			}
			//-------------------------
		//}
		return creationData;
	}

	/**
	 * Is invoked after construction of all child nodes of the current visual node.
	 * @param pageContext Contains the information on edited page.
	 * @param sourceNode The current node of the source tree.
	 * @param visualDocument The document of the visual tree.
	 * @param data Object <code>VpeCreationData</code>, built by a method <code>create</code>
	 */
	public void validate(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument, VpeCreationData data) {
		super.validate(pageContext, sourceNode, visualDocument, data);
		if (storedHeaderDiv == null) return;
		String value = storedHeaderDiv.getAttribute(VpeVisualDomBuilder.VPE_USER_TOGGLE_ID);
		if ("true".equals(value) || "false".equals(value)) {
			applyAttributeValueOnChildren(VpeVisualDomBuilder.VPE_USER_TOGGLE_ID, value, getChildren(storedHeaderDiv));
			applyAttributeValueOnChildren(VpeVisualDomBuilder.VPE_USER_TOGGLE_LOOKUP_PARENT, "true", getChildren(storedHeaderDiv));
		}
	}

	/**
	 * 	Sets the attribute to element children 
	 * @param attrName attribute name
	 * @param attrValue attribute value
	 * @param children children
	 */
	private void applyAttributeValueOnChildren(String attrName, String attrValue, List<nsIDOMElement> children) {
		if (children == null || attrName == null || attrValue == null) {
			return;
		}
		for (nsIDOMElement child : children) {
			child.setAttribute(attrName, attrValue);
			applyAttributeValueOnChildren(attrName, attrValue, getChildren(child));
		}
	}
	
	/**
	 * Gets element children
	 * @param element the element
	 * @return children
	 */
	private List<nsIDOMElement> getChildren(nsIDOMElement element) {
		List<nsIDOMElement> result = new ArrayList<nsIDOMElement>();
		if (element.hasChildNodes()) {
			nsIDOMNodeList children = element.getChildNodes();
			if (null != children) {
				long len = children.getLength();
				for (int i = 0; i < len; i++) {
					nsIDOMNode item = children.item(i);
					try {
						nsIDOMElement elem = (nsIDOMElement) item
								.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
						result.add(elem);
					} catch (XPCOMException ex) {
						// just ignore this exception
					}
				}
			}
		}
		return result;
	}
	
	private boolean getActiveState(Element sourceElement) {
		String opennedStr;

		opennedStr = (String)toggleMap.get(sourceElement);

		if (opennedStr == null) {
			opennedStr = ComponentUtil.getAttribute(sourceElement, "opened");
		}
		
		if (opennedStr == null || "".equals(opennedStr)) {
			opennedStr = "true";
		}

		return (!"false".equals(opennedStr));
	}

	public void toggle(VpeVisualDomBuilder builder, Node sourceNode, String toggleId) {
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