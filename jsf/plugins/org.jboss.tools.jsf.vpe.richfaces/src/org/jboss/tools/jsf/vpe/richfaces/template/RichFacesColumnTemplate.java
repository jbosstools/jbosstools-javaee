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
import java.util.Map;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.VpeDomMapping;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.SourceDomUtil;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesColumnTemplate extends VpeAbstractTemplate {
	private static final String HEADER_ICON_STYLE = "vertical-align:middle;"; //$NON-NLS-1$
	private static final String SORTABLE_PATH = "column/sortable.gif"; //$NON-NLS-1$

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument  visualDocument) {
		Element sourceElement = (Element)sourceNode;
		
		boolean visible = isVisible(sourceElement);
		
		nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);

		if (!visible) {
			VisualDomUtil.setSubAttribute(td, HTML.ATTR_STYLE,
					HTML.STYLE_PARAMETER_DISPLAY, HTML.STYLE_VALUE_NONE);
		}

		String columnClass = getColumnClass(sourceElement);
		VisualDomUtil.copyAttributes(sourceNode, td);
		td.setAttribute(HTML.ATTR_CLASS, columnClass);
		final VpeCreationData creationData = new VpeCreationData(td);

		Element headerFacet = SourceDomUtil.getFacetByName(sourceElement,
				RichFaces.NAME_FACET_HEADER);
		Element footerFacet = SourceDomUtil.getFacetByName(sourceElement,
				RichFaces.NAME_FACET_FOOTER);
		Map<String, List<Node>> headerFacetChildren = VisualDomUtil
				.findFacetElements(headerFacet, pageContext);
		Map<String, List<Node>> footerFacetChildren = VisualDomUtil
				.findFacetElements(footerFacet, pageContext);
		boolean headerHtmlElementPresents = headerFacetChildren.get(VisualDomUtil.FACET_HTML_TAGS).size() > 0;
		boolean footerHtmlElementPresents = footerFacetChildren.get(VisualDomUtil.FACET_HTML_TAGS).size() > 0;
		/*
		 * Encode html elements from facets to the column body
		 */
		VpeChildrenInfo tdInfo = new VpeChildrenInfo(td);
		if (headerHtmlElementPresents) {
			for (Node child : headerFacetChildren.get(VisualDomUtil.FACET_HTML_TAGS)) {
				tdInfo.addSourceChild(child);
			}
		}
		if (footerHtmlElementPresents) {
			for (Node child : footerFacetChildren.get(VisualDomUtil.FACET_HTML_TAGS)) {
				tdInfo.addSourceChild(child);
			}
		}
		/*
		 * Encode body
		 */
		List<Node> children = ComponentUtil.getChildren(sourceElement,true);
		for (Node child : children) {
			if (!isFacet(child)) {
				tdInfo.addSourceChild(child);
			}
		}
		creationData.addChildrenInfo(tdInfo);
			
		
		return creationData;
	}

	/**@param child a node
	 * @return <code>true</code>, if the <code>node</code> is <code>rich:facet</code> tag, 
	 * <code>false</code> otherwise*/
	private boolean isFacet(Node child) {
		boolean ret = child.getNodeName().endsWith(RichFaces.TAG_FACET);
		return ret;
	}

	private String getColumnClass(Element sourceElement) {
		String columnClass;
		if(isHeader(sourceElement)) {
			columnClass = "dr-table-headercell rich-table-headercell"; //$NON-NLS-1$
		} else if(isFooter(sourceElement)) {
			columnClass = "dr-table-footercell rich-table-footercell"; //$NON-NLS-1$
		} else {
			columnClass = "dr-table-cell rich-table-cell"; //$NON-NLS-1$
		}
				
		if (sourceElement.hasAttribute(RichFaces.ATTR_STYLE_CLASS)) {
			String styleClass = sourceElement.getAttribute(RichFaces.ATTR_STYLE_CLASS);
			columnClass += " " + styleClass; //$NON-NLS-1$
		}
		return columnClass;
	}

	private boolean isHeader(Element sourceElement) {
		return icludedInFacet(sourceElement, RichFaces.NAME_FACET_HEADER);
	}

	private boolean isFooter(Element sourceElement) {
		return icludedInFacet(sourceElement, RichFaces.NAME_FACET_FOOTER);
	}

	private boolean icludedInFacet(Element sourceElement, String facetName) {
		Node parent = sourceElement.getParentNode();
		if(parent!=null) {
			if(ComponentUtil.isFacet(parent, facetName)) {
				return true;
			} else if (parent.getNodeName().endsWith(RichFaces.TAG_COLUMN_GROUP)) {
				return ComponentUtil.isFacet(parent.getParentNode(), facetName);
			}
		}
		return false;
	}

	@Override
	public boolean recreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#getNodeForUpdate(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMNode, java.lang.Object)
	 */
	@Override
	public Node getNodeForUpdate(VpePageContext pageContext, Node sourceNode,
			nsIDOMNode visualNode, Object data) {
		/* XXX: The implementation is a little tricky, it returns first n-th parent
		 * that has a nodeMapping. */

		final VpeDomMapping domMapping = pageContext.getDomMapping();
		SourceDomUtil.getParentHavingDomMapping(sourceNode, domMapping);
		final Node parent = SourceDomUtil.getParentHavingDomMapping(sourceNode, domMapping);
		
		if (parent != null) {		
			return parent;
		} else {
			return sourceNode;
		}
	}
	
	/** Creates <code>IMG</code> tag if it is specified by <code>&lt;rich:column&gt;</code>
	 * attributes. 
	 * @param pageContext 
	 * 
	 * @param column source <code>&lt;rich:column&gt;</code> element  
	 * @param visualDocument Mozilla's Visual Document
	 * @return <code>IMG</code> tag if it is necessary, <code>null</code> otherwise */
	public static nsIDOMElement getHeaderIcon(VpePageContext pageContext, Element column, nsIDOMDocument visualDocument) {
	    String sortable = ComponentUtil.getAttribute(column, RichFaces.ATTR_SORTABLE);
	    if (RichFaces.VALUE_TRUE.equals(sortable) || column.hasAttribute(RichFaces.ATTR_SORT_BY)) {
			nsIDOMElement img = visualDocument.createElement(HTML.TAG_IMG);			
			if (column.hasAttribute(RichFaces.ATTR_SORT_ICON)) {
				String sortIcon = column.getAttribute(RichFaces.ATTR_SORT_ICON);
				sortIcon = VpeStyleUtil.addFullPathToImgSrc(sortIcon, pageContext, true);
				sortIcon = sortIcon.replace('\\', '/');
	    		img.setAttribute(HTML.ATTR_SRC, sortIcon);            
			} else {
				ComponentUtil.setImg(img, SORTABLE_PATH);
			}
		    img.setAttribute(HTML.ATTR_STYLE, HEADER_ICON_STYLE);
		    return img;
	    } else {
	    	return null;
	    }
	}

	public static boolean isBreakBefore(Node child) {
		String breakBeforeVal = ((Element)child).getAttribute(RichFaces.ATTR_BREAK_BEFORE);
		boolean breakBefore = breakBeforeVal != null && breakBeforeVal.equalsIgnoreCase(RichFaces.VALUE_TRUE);
		return breakBefore;
	}
	
	/**
	 * Returns {@code true} if the {@code column} is visible.
	 *
	 * @param column should be not {@code null}
	 */
	public static boolean isVisible(Element column) {
		return !RichFaces.VALUE_FALSE.equals(column.getAttribute(RichFaces.ATTR_VISIBLE));
	}
}