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
package org.jboss.tools.jsf.vpe.facelets.template;

import org.jboss.tools.jsf.vpe.facelets.template.util.Facelets;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.mozilla.interfaces.nsIDOMDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class VpeDecorateTemplate extends org.jboss.tools.vpe.editor.template.VpeDefineContainerTemplate{
	
	public VpeCreationData create(VpePageContext pageContext,
		Node sourceNode, nsIDOMDocument visualDocument) {
	    String fileName = ((Element)sourceNode).getAttribute(Facelets.ATTR_TEMPLATE);
	    return createTemplate(fileName, pageContext, sourceNode, visualDocument);
	}
}
