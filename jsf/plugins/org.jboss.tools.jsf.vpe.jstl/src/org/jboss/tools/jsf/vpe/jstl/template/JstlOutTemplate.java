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
package org.jboss.tools.jsf.vpe.jstl.template;

import org.jboss.tools.jsf.vpe.jsf.template.AbstractOutputJsfTemplate;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Class for creating Out content
 *
 * @author Igor Zhukov
 */
public class JstlOutTemplate extends AbstractOutputJsfTemplate {

    /**
     * Create html instead c:out component.
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
        // convert to Element
        Element sourceElement = (Element) sourceNode;
        // create span element
        nsIDOMElement span = VisualDomUtil.createBorderlessContainer(visualDocument);

        VpeCreationData creationData = new VpeCreationData(span);

		processOutputAttribute(pageContext, visualDocument, sourceElement, span, creationData);

        return creationData;
    }
}