/*******************************************************************************
 * Copyright (c) 2007-2011 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.richfaces.template;

import java.util.List;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.VpeElementMapping;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeTemplateSafeWrapper;
import org.jboss.tools.vpe.editor.template.VpeToggableTemplate;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesCollapsibleSubTableTogglerTemplate extends
		VpeAbstractTemplate implements VpeToggableTemplate {
	
	final private static String CSS_TOP_SPAN="rf-csttg";  //$NON-NLS-1$
	final private static String CSS_SPAN_COLLAPSED="rf-csttg-colps";  //$NON-NLS-1$
	final private static String CSS_SPAN_EXPANDED="rf-csttg-exp";  //$NON-NLS-1$
	
	@Override
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		
		nsIDOMElement topSpan = visualDocument.createElement(HTML.TAG_SPAN);
		topSpan.setAttribute(VpeVisualDomBuilder.VPE_USER_TOGGLE_ID, "1"); //$NON-NLS-1$
		topSpan.setAttribute(HTML.ATTR_CLASS, CSS_TOP_SPAN);
		
		nsIDOMElement imgSpan = visualDocument.createElement(HTML.TAG_SPAN);
		imgSpan.setAttribute(VpeVisualDomBuilder.VPE_USER_TOGGLE_LOOKUP_PARENT, "true"); //$NON-NLS-1$
		imgSpan.setAttribute(VpeVisualDomBuilder.VPE_USER_TOGGLE_ID, "2"); //$NON-NLS-1$
		
		nsIDOMElement img = visualDocument.createElement(HTML.TAG_IMG);
		img.setAttribute(VpeVisualDomBuilder.VPE_USER_TOGGLE_LOOKUP_PARENT, "true"); //$NON-NLS-1$
		img.setAttribute(VpeVisualDomBuilder.VPE_USER_TOGGLE_ID, "3"); //$NON-NLS-1$
		
		if (RichFaces.readCollapsedStateFromSourceNode(sourceNode)) {
			imgSpan.setAttribute(HTML.ATTR_CLASS, CSS_SPAN_COLLAPSED);
			ComponentUtil.setImg(img, "/collapsibleSubTableToggler/upIcon.gif"); //$NON-NLS-1$
		} else {
			imgSpan.setAttribute(HTML.ATTR_CLASS, CSS_SPAN_EXPANDED);
			ComponentUtil.setImg(img, "/collapsibleSubTableToggler/downIcon.gif"); //$NON-NLS-1$
		}
		imgSpan.appendChild(img);
		topSpan.appendChild(imgSpan);
		
		VpeCreationData creationData = new VpeCreationData(topSpan);
		return creationData;
	}
	
	@Override
	public void toggle(VpeVisualDomBuilder builder, Node sourceNode,
			String toggleId) {
		Element csttgElement =  (Element) sourceNode;
		String forTable = ""; //$NON-NLS-1$
		if (csttgElement.hasAttribute(HTML.ATTR_FOR)) {
			forTable = csttgElement.getAttribute(HTML.ATTR_FOR);
		}
		if (!"".equalsIgnoreCase(forTable)) { //$NON-NLS-1$
			List<Element> sourceElements = RichFaces.findElementsById(
					(Element) csttgElement.getOwnerDocument()
							.getDocumentElement(), forTable, ":collapsibleSubTable"); //$NON-NLS-1$
			for (Element el : sourceElements) {
				if (builder != null) {
					VpeElementMapping elementMapping = (VpeElementMapping)builder.getDomMapping().getNodeMapping(el);
					if (elementMapping != null) {
						RichFacesCollapsibleSubTableTemplate cstTemplate = (RichFacesCollapsibleSubTableTemplate) ((VpeTemplateSafeWrapper) 
								elementMapping.getTemplate()).getAdapter(RichFacesCollapsibleSubTableTemplate.class);
						if (cstTemplate != null) {
							cstTemplate.toggle(el, toggleId);
							if (RichFaces.readCollapsedStateFromSourceNode(sourceNode)) {
								sourceNode.setUserData(RichFaces.COLLAPSED_STATE, "false", null); //$NON-NLS-1$
							} else {
								sourceNode.setUserData(RichFaces.COLLAPSED_STATE, "true", null); //$NON-NLS-1$
							}
							builder.updateNode(el);
						}
					}
				}
			}
		}
		/*
		 * Update toggler state itself
		 */
		builder.updateNode(sourceNode);
	}

	@Override
	public void stopToggling(Node sourceNode) {
		/*
		 * Do nothing
		 */
	}
}
