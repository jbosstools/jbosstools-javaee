/*******************************************************************************
 * Copyright (c) 2007-2008 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.vpe.ajax4jsf.template;

import org.jboss.tools.jsf.vpe.richfaces.template.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Template for the <a4j:log> component.
 *
 * @author Igor Zhukov
 */
public class Ajax4JSFLogTemplate extends VpeAbstractTemplate {
	
	private static final String CLEAR_BUTTON = "Clear"; //$NON-NLS-1$

    public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
		Element sourceElement = (Element) sourceNode;
		nsIDOMElement divElement = visualDocument.createElement(HTML.TAG_DIV);
		String style = sourceElement.getAttribute(HTML.ATTR_STYLE);
		/*
		 * Set OVERFLOW to the STYLE 
		 */
		if (sourceElement.hasAttribute(HTML.STYLE_PARAMETER_OVERFLOW)) {
			style = VpeStyleUtil.setParameterInStyle(style, 
					HTML.STYLE_PARAMETER_OVERFLOW, 
					sourceElement.getAttribute(HTML.STYLE_PARAMETER_OVERFLOW));
		}
		/*
		 * Set WIDTH to the STYLE 
		 */
		int size;
		if (sourceElement.hasAttribute(HTML.ATTR_WIDTH)) {
			size = VpeStyleUtil.cssSizeToInt(sourceElement.getAttribute(HTML.ATTR_WIDTH));
			if (size != -1) {
				style = VpeStyleUtil.setSizeInStyle(style, HTML.ATTR_WIDTH, size);
			}
		}
		/*
		 * Set HEIGHT to the STYLE 
		 */
		if (sourceElement.hasAttribute(HTML.ATTR_HEIGHT)) {
			size = VpeStyleUtil.cssSizeToInt(sourceElement.getAttribute(HTML.ATTR_HEIGHT));
			if (size != -1) {
				style = VpeStyleUtil.setSizeInStyle(style, HTML.ATTR_HEIGHT, size);
			}
		}
		/*
		 * Set STYLE to the DIV 
		 */
		divElement.setAttribute(HTML.ATTR_STYLE, style);

		/*
		 * Set CLASS attribute to the DIV 
		 */
		if (sourceElement.hasAttribute(RichFaces.ATTR_STYLE_CLASS)) {
			divElement.setAttribute(HTML.ATTR_CLASS, 
					sourceElement.getAttribute(RichFaces.ATTR_STYLE_CLASS));
		}

		/*
		 * Create 'Clear' button
		 */
        nsIDOMElement clearButton = visualDocument.createElement(HTML.TAG_BUTTON);
        clearButton.appendChild(visualDocument.createTextNode(CLEAR_BUTTON));
        clearButton.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TYPE_BUTTON);

        divElement.appendChild(clearButton);
        
        /*
         * https://jira.jboss.org/jira/browse/JBIDE-3708
         * Component should render its children.
         */
        VpeCreationData creationData = VisualDomUtil.createTemplateWithTextContainer(
				sourceElement, divElement, HTML.TAG_DIV, visualDocument);
        
        return creationData;
    }
}