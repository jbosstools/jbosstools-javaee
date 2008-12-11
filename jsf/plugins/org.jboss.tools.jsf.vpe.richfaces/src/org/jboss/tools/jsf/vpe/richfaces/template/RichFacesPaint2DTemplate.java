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

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Class for creating Paint2D content
 *
 * @author Max Areshkau
 */
public class RichFacesPaint2DTemplate extends VpeAbstractTemplate {

	/** IMAGE_NAME */
	private static final String IMAGE_NAME = "/paint2D/paint2D.gif"; //$NON-NLS-1$

	/** PAINT2D_CSS_FILE */
	private static final String PAINT2D_CSS_FILE = "/paint2D/paint2D.css"; //$NON-NLS-1$

	/**
	 * Create html instead rich:faces component.
	 *
	 * @param pageContext
	 *            Contains the information on edited page.
	 * @param sourceNode
	 *            The current node of the source tree.
	 * @param visualDocument
	 *            The document of the visual tree.
	 * @return The information on the created node of the visual tree.
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
		ComponentUtil.setCSSLink(pageContext, PAINT2D_CSS_FILE, "paint2d"); //$NON-NLS-1$

		nsIDOMElement img = visualDocument.createElement(HTML.TAG_IMG);
		ComponentUtil.setImg(img, IMAGE_NAME);
		String attrValue = ((Element) sourceNode).getAttribute(RichFaces.ATTR_STYLE_CLASS);
		if (attrValue != null && attrValue.length() != 0) {
			img.setAttribute(HTML.ATTR_CLASS, attrValue);
		} else if (((Element) sourceNode).getAttribute(HTML.ATTR_WIDTH) == null
				&& ((Element) sourceNode).getAttribute(HTML.ATTR_HEIGHT) == null) {
			img.setAttribute(HTML.ATTR_CLASS, "imgStyleClass"); //$NON-NLS-1$
		}
		VisualDomUtil.copyAttributes(sourceNode, img);
		VpeCreationData creationData = new VpeCreationData(img);

		return creationData;
	}
}
