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

import java.util.List;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
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

	/** DEFAULT_WIDTH */
	private static final String DEFAULT_WIDTH = "10"; //$NON-NLS-1$
	/** DEFAULT_HEIGHT */
	private static final String DEFAULT_HEIGHT = "10"; //$NON-NLS-1$
	/** ATTR_TRANSPARENT_VALUE */
	private static final String ATTR_TRANSPARENT_VALUE = "transparent"; //$NON-NLS-1$

	/** RICH_PAINT2D_STYLE */
	private static final String RICH_PAINT2D_STYLE = "rich-paint2D"; //$NON-NLS-1$

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
		// convert to Element
		Element sourceElement = (Element) sourceNode;

		nsIDOMElement img = visualDocument.createElement(HTML.TAG_IMG);
		ComponentUtil.setImg(img, IMAGE_NAME);

		// set STYLE attributes
		String attrValue = ComponentUtil.getAttribute(sourceElement, RichFaces.ATTR_STYLE);
		if (attrValue.length() != 0) {
			img.setAttribute(HTML.ATTR_STYLE, attrValue);
		}
		// set CLASS attribute
		attrValue = ComponentUtil.getAttribute(sourceElement, RichFaces.ATTR_STYLE_CLASS);
		String styleClass = RICH_PAINT2D_STYLE;
		if (attrValue.length() != 0) {
			styleClass += Constants.WHITE_SPACE + attrValue;
		}
		img.setAttribute(HTML.ATTR_CLASS, styleClass);
		// set WIDTH attribute
		String width = ComponentUtil.getAttribute(sourceElement, HTML.ATTR_WIDTH, DEFAULT_WIDTH);
		img.setAttribute(HTML.ATTR_WIDTH, width);
		// set HEIGHT attribute
		String height = ComponentUtil.getAttribute(sourceElement, HTML.ATTR_HEIGHT, DEFAULT_HEIGHT);
		img.setAttribute(HTML.ATTR_HEIGHT, height);
		// set BGCOLOR attribute
		String bgColor = ComponentUtil.getAttribute(sourceElement, HTML.ATTR_BGCOLOR, ATTR_TRANSPARENT_VALUE);
		img.setAttribute(HTML.ATTR_BGCOLOR, bgColor);

		/*
         * https://jira.jboss.org/jira/browse/JBIDE-3225
         * Component should render its children.
         */
		VpeCreationData creationData = VisualDomUtil.createTemplateWithTextContainer(
				sourceElement, img, HTML.TAG_SPAN, visualDocument);

		return creationData;
	}
}