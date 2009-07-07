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

	/** DEFAULT_DIV_SIZE */
	private final static String DEFAULT_WIDTH = "800px"; //$NON-NLS-1$
	private final static String DEFAULT_HEIGHT = "600px"; //$NON-NLS-1$
	private final static String DEFAULT_OVERFLOW = "auto"; //$NON-NLS-1$

	private final static String CLEAR_BUTTON = "Clear"; //$NON-NLS-1$

    /**
     * The Constructor.
     */
    public Ajax4JSFLogTemplate() {
        super();
    }

    /**
	 * Creates a node of the visual tree on the node of the source tree.
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
		// cast to Element
		Element sourceElement = (Element) sourceNode;

		nsIDOMElement divElement = visualDocument.createElement(HTML.TAG_DIV);

		String style = sourceElement.getAttribute(HTML.ATTR_STYLE);
		// set STYLE attributes
		// check 'overflow' attribute
		String parameterValue = VpeStyleUtil.getParameterFromStyleAttribute(style, HTML.STYLE_PARAMETER_OVERFLOW);
		if (parameterValue == null || parameterValue.equals(Constants.EMPTY)) {
			parameterValue = ComponentUtil.getAttribute(sourceElement, HTML.STYLE_PARAMETER_OVERFLOW, DEFAULT_OVERFLOW);
			style = VpeStyleUtil.setParameterInStyle(style, HTML.STYLE_PARAMETER_OVERFLOW, parameterValue);
		}
		// check 'width' attribute
		parameterValue = VpeStyleUtil.getParameterFromStyleAttribute(style, HTML.ATTR_WIDTH);
		if (parameterValue == null || parameterValue.equals(Constants.EMPTY)) {
			parameterValue = ComponentUtil.getAttribute(sourceElement, HTML.ATTR_WIDTH, DEFAULT_WIDTH);
			style = VpeStyleUtil.setParameterInStyle(style, HTML.ATTR_WIDTH, parameterValue);
		}
		// check 'height' attribute
		parameterValue = VpeStyleUtil.getParameterFromStyleAttribute(style, HTML.ATTR_HEIGHT);
		if (parameterValue == null || parameterValue.equals(Constants.EMPTY)) {
			parameterValue = ComponentUtil.getAttribute(sourceElement, HTML.ATTR_HEIGHT, DEFAULT_HEIGHT);
			style = VpeStyleUtil.setParameterInStyle(style, HTML.ATTR_HEIGHT, parameterValue);
		}
		divElement.setAttribute(HTML.ATTR_STYLE, style);

		// set CLASS attribute
		String styleClass = ComponentUtil.getAttribute(sourceElement, RichFaces.ATTR_STYLE_CLASS);
		if (!Constants.EMPTY.equals(styleClass)) {
			divElement.setAttribute(HTML.ATTR_CLASS, styleClass);
		}

		// create 'Clear' button
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