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
import java.util.List;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesSubTableTemplate extends VpeAbstractTemplate {

	public static RichFacesSubTableTemplate DEFAULT_INSTANCE = new RichFacesSubTableTemplate();

	public RichFacesSubTableTemplate() {
		super();
	}

	@Override
	public boolean isRecreateAtAttrChange(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
		return true;
	}

	/**
	 * Encode columnGroup
	 * @param creationData
	 * @param columnGroupSourceElement
	 * @param visualDocument
	 * @param parentVisualNode
	 * @return
	 */
	public VpeCreationData encode(VpeCreationData creationData, Element sourceElement, Document visualDocument, Element parentVisualNode) {
		if(creationData!=null) {
			// Encode header
			encodeHeader(creationData, sourceElement, visualDocument, parentVisualNode);
		}

		Element tr = visualDocument.createElement("tr");
		ComponentUtil.copyAttributes(sourceElement, tr);

		boolean header = false;
		boolean footer = false;
		if(isHeader(sourceElement)) {
			tr.setAttribute("class", getHeaderClass());
			String style = getHeaderBackgoundImgStyle();
			if(style!=null) {
				tr.setAttribute("style", style);
			}
			header = true;
		} else if(isFooter(sourceElement)) {
			tr.setAttribute("class", getFooterClass());
			footer = true;
		} else {
			tr.setAttribute("class", getCellClass());
		}

		if(creationData==null) {
			// Method was called from create()
			creationData = new VpeCreationData(tr);			
		} else {
			// Method was called from dataTable
			parentVisualNode.appendChild(tr);
		}

		// Create mapping to Encode body
		VpeChildrenInfo trInfo = new VpeChildrenInfo(tr);
		creationData.addChildrenInfo(trInfo);
		List<Node> children = ComponentUtil.getChildren(sourceElement);
		for (Node child : children) {
			if(child.getNodeName().endsWith(":column")) {
				String breakBefore = ((Element)child).getAttribute("breakBefore");
				if(breakBefore!=null && breakBefore.equalsIgnoreCase("true")) {
					// Start new TR
					tr = visualDocument.createElement("tr");
					if(header) {
						tr.setAttribute("class", getHeaderContinueClass());
					} else if(footer) {
						tr.setAttribute("class", getFooterContinueClass());
					} else {
						tr.setAttribute("class", getCellClass());
					}
					ComponentUtil.copyAttributes(sourceElement, tr);
					if(parentVisualNode!=null) {
						parentVisualNode.appendChild(tr);
					}
					trInfo = new VpeChildrenInfo(tr);
					creationData.addChildrenInfo(trInfo);
				}
			}
			trInfo.addSourceChild(child);
		}

		if(parentVisualNode!=null) {
			// Encode footer
			encodeFooter(creationData, sourceElement, visualDocument, parentVisualNode);
		}

		return creationData;
	}

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, Document visualDocument) {
		Element sourceElement = (Element)sourceNode;

		VpeCreationData creationData = encode(null, sourceElement, visualDocument, null);
		return creationData;
	}

	protected void encodeHeader(VpeCreationData creationData, Element sourceElement, Document visualDocument, Element parentVisualNode) {
		encodeHeaderOrFooter(creationData, sourceElement, visualDocument, parentVisualNode, "header", "dr-subtable-header rich-subtable-header", "dr-subtable-headercell rich-subtable-headercell");
	}

	protected void encodeFooter(VpeCreationData creationData, Element sourceElement, Document visualDocument, Element parentVisualNode) {
		encodeHeaderOrFooter(creationData, sourceElement, visualDocument, parentVisualNode, "footer", "dr-subtable-footer rich-subtable-footer", "dr-subtable-footercell rich-subtable-footercell");
	}

	protected void encodeHeaderOrFooter(VpeCreationData creationData, Element sourceElement, Document visualDocument, Element parentVisualNode, String facetName, String trClass, String tdClass) {
		ArrayList<Element> columns = RichFacesDataTableTemplate.getColumns(sourceElement);
		ArrayList<Element> columnsHeaders = RichFacesDataTableTemplate.getColumnsWithFacet(columns, facetName);
		if(!columnsHeaders.isEmpty()) {
			Element tr = visualDocument.createElement("tr");
			parentVisualNode.appendChild(tr);
			String styleClass = trClass;
			if(styleClass!=null) {
				tr.setAttribute("class", styleClass);
			}
			RichFacesDataTableTemplate.encodeHeaderOrFooterFacets(creationData, tr, visualDocument, columnsHeaders,
					tdClass,
					null, facetName, "td");
		}

	}

	private boolean isHeader(Element sourceElement) {
		return icludedInFacet(sourceElement, "header");
	}

	private boolean isFooter(Element sourceElement) {
		return icludedInFacet(sourceElement, "footer");
	}

	private boolean icludedInFacet(Element sourceElement, String facetName) {
		Node parent = sourceElement.getParentNode();
		return parent!=null && ComponentUtil.isFacet(parent, facetName);
	}

	protected String getHeaderClass() {
		return "dr-subtable-header rich-subtable-header";
	}

	protected String getHeaderContinueClass() {
		return "dr-subtable-header-continue rich-subtable-header-continue";
	}

	protected String getFooterClass() {
		return "dr-subtable-footer rich-subtable-footer";
	}

	protected String getFooterContinueClass() {
		return "dr-subtable-footer-continue rich-subtable-footer-continue";
	}

	protected String getCellClass() {
		return "dr-subtable-cell rich-subtable-cell";
	}

	protected String getHeaderBackgoundImgStyle() {
		return null;
	}

	@Override
	public void removeAttribute(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name) {
		((Element)visualNode).removeAttribute(name);
	}

	@Override
	public void setAttribute(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
		((Element)visualNode).setAttribute(name, value);
	}
}