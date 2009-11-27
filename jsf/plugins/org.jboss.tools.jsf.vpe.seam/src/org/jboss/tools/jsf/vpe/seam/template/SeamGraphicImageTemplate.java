/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.seam.template;

import java.util.List;

import org.jboss.tools.jsf.vpe.jsf.template.util.ComponentUtil;
import org.jboss.tools.jsf.vpe.seam.template.util.SeamUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Class for s:graphicImage template.
 * 
 * @author dmaliarevich
 */
public class SeamGraphicImageTemplate extends VpeAbstractTemplate {

    /*
     * s:transformImageSize tag name.
     */
    private final String TRANSFORM_IMAGE_SIZE_NAME = ":transformImageSize"; //$NON-NLS-1$
    
    public SeamGraphicImageTemplate() {
	super();
    }

    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument) {
	Element sourceElement = (Element) sourceNode;
	nsIDOMElement img = visualDocument.createElement(HTML.TAG_IMG);
	
	/*
	 * Indicates that source node has width or height attributes.
	 */
	boolean hasWidth = false;
	boolean hasHeight = false;
	
	/*
	 * Reading source attributes and setting them to the visual node.
	 */
	if (sourceElement.hasAttribute(HTML.ATTR_ALT)) {
	     img.setAttribute(HTML.ATTR_ALT, sourceElement.getAttribute(HTML.ATTR_ALT));
	}
	if (sourceElement.hasAttribute(HTML.ATTR_DIR)) {
	    img.setAttribute(HTML.ATTR_DIR, sourceElement.getAttribute(HTML.ATTR_DIR));
	}
	if (sourceElement.hasAttribute(HTML.ATTR_WIDTH)) {
	    img.setAttribute(HTML.ATTR_WIDTH, sourceElement.getAttribute(HTML.ATTR_WIDTH));
	    hasWidth = true;
	}
	if (sourceElement.hasAttribute(HTML.ATTR_HEIGHT)) {
	    img.setAttribute(HTML.ATTR_HEIGHT, sourceElement.getAttribute(HTML.ATTR_HEIGHT));
	    hasHeight = true;
	}
	if (sourceElement.hasAttribute(HTML.ATTR_STYLE)) {
	    img.setAttribute(HTML.ATTR_STYLE, sourceElement.getAttribute(HTML.ATTR_STYLE));
	}
	if (sourceElement.hasAttribute(SeamUtil.ATTR_STYLE_CLASS)) {
	    img.setAttribute(HTML.ATTR_CLASS, sourceElement.getAttribute(SeamUtil.ATTR_STYLE_CLASS));
	}
	if (sourceElement.hasAttribute(HTML.ATTR_VALUE)) {
	    img.setAttribute(HTML.ATTR_SRC, VpeStyleUtil.addFullPathToImgSrc(
		    sourceElement.getAttribute(HTML.ATTR_VALUE), pageContext,
		    true));
	} else if (sourceElement.hasAttribute(SeamUtil.ATTR_URL)) {
	    img.setAttribute(HTML.ATTR_SRC, VpeStyleUtil.addFullPathToImgSrc(
		    sourceElement.getAttribute(SeamUtil.ATTR_URL), pageContext,
		    true));
	}
	
	/*
	 * Looking for any seam transformation tag to apply the transformation to the image.
	 */
	List<Node> children = ComponentUtil.getChildren(sourceElement);
	/*
	 * If s:graphicImage has width and height attributes
	 * skip any size transformation.
	 */
	if (!(hasHeight || hasWidth)) {
	    for (Node node : children) {
		if (node.getNodeName().endsWith(TRANSFORM_IMAGE_SIZE_NAME)) {
		    Element transform = (Element) node;
		    if (transform.hasAttribute(HTML.ATTR_WIDTH)) {
			img.setAttribute(HTML.ATTR_WIDTH, transform
				.getAttribute(HTML.ATTR_WIDTH));
		    }
		    if (transform.hasAttribute(HTML.ATTR_HEIGHT)) {
			img.setAttribute(HTML.ATTR_HEIGHT, transform
				.getAttribute(HTML.ATTR_HEIGHT));
		    }
		    /*
		     * Apply only the first transform element.
		     */
		    break;
		}
	    }
	}
	return new VpeCreationData(img);
    }

}
