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
package org.jboss.tools.jsf.vpe.jsf.template;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.jboss.tools.jsf.vpe.jsf.template.util.JSF;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.NodesManagingUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Node;

/**
 * Template for h:outputScript .
 * 
 * @author yradtsevich
 */
public class JsfOutputScriptTemplate extends VpeAbstractTemplate {
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		// invisible tag
		return new VpeCreationData(null);
	}

	@Override
	public IRegion getSourceRegionForOpenOn(VpePageContext pageContext,
			Node sourceNode, nsIDOMNode domNode) {
		IRegion region = null;
		Node node = sourceNode.getAttributes().getNamedItem(JSF.ATTR_NAME);
		if (node != null) {
			region = new Region(NodesManagingUtil.getStartOffsetNode(node),
					NodesManagingUtil.getNodeLength(node));
		}
		return region;
	}
}
