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
package org.jboss.tools.jsf.vpe.jstl.template;

import org.jboss.tools.jsf.vpe.facelets.template.VpeDecorateTemplate;
import org.jboss.tools.jsf.vpe.jstl.template.util.Jstl;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.mozilla.interfaces.nsIDOMDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class JstlImportTemplate extends VpeDecorateTemplate {

    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument) {
	String fileName = ((Element)sourceNode).getAttribute(Jstl.ATTR_URL);
	VpeCreationData creationData = createTemplate(fileName, pageContext, sourceNode, visualDocument);
	if (null != creationData) {
	    return creationData;
	}
	creationData = createStub(fileName, (Element)sourceNode, visualDocument);
	creationData.setData(null);
	return creationData;
    }

}
