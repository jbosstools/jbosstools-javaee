/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.template.util.model;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeListImpl implements NodeList {

	private List<Node> nodes;

	public NodeListImpl() {
		nodes = new ArrayList<Node>();
	}

	public int getLength() {
		return nodes.size();
	}

	public Node item(int index) {
		return nodes.get(index);
	}

	public Node appendNode(Node node) {

		if (node == null)
			return null;

		nodes.add(node);
		return node;
	}

}
