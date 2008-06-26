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
package org.jboss.tools.jsf.vpe.seam;

import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ComponentUtil {

	/**
	 * Copies all attributes from source node to visual node.
	 * 
	 * @param sourceNode
	 * @param visualNode
	 */
	public static void copyAttributes(Node sourceNode,
			nsIDOMElement visualElement) {
		NamedNodeMap namedNodeMap = sourceNode.getAttributes();
		for (int i = 0; i < namedNodeMap.getLength(); i++) {
			Node attribute = namedNodeMap.item(i);
			visualElement.setAttribute(attribute.getNodeName(), attribute
					.getNodeValue());
		}
	}

}
