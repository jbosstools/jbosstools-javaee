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

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NamedNodeMapImpl implements NamedNodeMap, NodeList {

	private List<Node> nodes = null;

	public NamedNodeMapImpl() {
		nodes = new ArrayList<Node>();
	}

	public int getLength() {
		if (nodes == null)
			return 0;
		return nodes.size();
	}

	public Node getNamedItem(String name) {
		if (name == null)
			return null;
		if (nodes == null)
			return null;

		int length = nodes.size();
		for (int i = 0; i < length; i++) {
			Node node = nodes.get(i);
			if (node == null)
				continue;
			if (name.equalsIgnoreCase(node.getLocalName()))
				return node;
		}

		return null;
	}

	public Node getNamedItemNS(String uri, String name) {
		if (name == null)
			return null;
		if (nodes == null)
			return null;

		int length = nodes.size();
		for (int i = 0; i < length; i++) {
			Node node = nodes.get(i);
			if (node == null)
				continue;
			String localName = node.getLocalName();
			if (localName == null || !localName.equalsIgnoreCase(name))
				continue;
			String nodeURI = node.getNamespaceURI();
			if (nodeURI == null) {
				if (nodeURI != null)
					continue;
			} else {
				if (nodeURI == null || !nodeURI.equals(nodeURI))
					continue;
			}

			return node;
		}

		return null;
	}

	public Node item(int index) {
		if (nodes == null)
			return null;
		return nodes.get(index);
	}

	public Node removeNamedItem(String name) throws DOMException {

		if (name == null)
			return null;
		if (nodes == null)
			return null;

		int length = nodes.size();
		for (int i = 0; i < length; i++) {
			Node node = nodes.get(i);
			if (node == null)
				continue;
			if (!name.equalsIgnoreCase(node.getLocalName()))
				continue;

			nodes.remove(i);

			return node;
		}

		return null;
	}

	public Node removeNamedItemNS(String uri, String name) throws DOMException {
		if (name == null)
			return null;
		if (nodes == null)
			return null;

		int length = nodes.size();
		for (int i = 0; i < length; i++) {
			Node node = nodes.get(i);
			if (node == null)
				continue;
			String localName = node.getLocalName();
			if (localName == null || !localName.equals(name))
				continue;
			String nodeURI = node.getNamespaceURI();
			if (uri == null) {
				if (nodeURI != null)
					continue;
			} else {
				if (nodeURI == null || !nodeURI.equals(uri))
					continue;
			}

			nodes.remove(i);

			return node;
		}

		return null;
	}

	public Node setNamedItem(Node node) throws DOMException {
		if (node == null)
			return null;

		Node oldNode = removeNamedItem(node.getLocalName());

		nodes.add(node);

		return oldNode;
	}

	public Node setNamedItemNS(Node node) throws DOMException {
		if (node == null)
			return null;

		String name = node.getLocalName();
		String uri = node.getNamespaceURI();
		Node oldNode = removeNamedItemNS(uri, name);
		nodes.add(node);
		return oldNode;
	}
}
