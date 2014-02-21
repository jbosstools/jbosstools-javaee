/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.jsf2.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.jboss.tools.jsf.jsf2.model.CompositeComponentConstants;
import org.jboss.tools.jsf.jsf2.model.JSF2ComponentModelManager;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author yzhishko
 * 
 */

@SuppressWarnings("restriction")
public class JSF2ComponentUtil {

	public static Map<String, List<Element>> findCompositeComponents(Node node) {
		Map<String, List<Element>> elementsMap = new HashMap<String, List<Element>>(
				0);
		findChildCompositeComponents(node, elementsMap);
		return elementsMap;
	}

	private static void findChildCompositeComponents(Node node,
			Map<String, List<Element>> elementsMap) {
		if (node instanceof IDOMDocument) {
			node = ((IDOMDocument) node).getDocumentElement();
			findChildCompositeComponents(node, elementsMap);
		} else if (node instanceof ElementImpl) {
			ElementImpl elementImpl = (ElementImpl) node;
			String namespaceURI = elementImpl.getNamespaceURI();
			if (namespaceURI != null
					&& namespaceURI
							.startsWith(CompositeComponentConstants.COMPOSITE_XMLNS)
					&& !namespaceURI.equals(CompositeComponentConstants.COMPOSITE_XMLNS)) {
				List<Element> elements = elementsMap.get(namespaceURI);
				if (elements == null) {
					elements = new ArrayList<Element>(0);
				}
				elements.add(elementImpl);
				elementsMap.put(namespaceURI, elements);
			}
			NodeList children = node.getChildNodes();
			if (children != null) {
				for (int i = 0; i < children.getLength(); i++) {
					findChildCompositeComponents(children.item(i), elementsMap);
				}
			}
		}
	}

	public static IDOMAttr[] findURIContainers(Node scanNode) {
		List<IDOMAttr> attrs = new ArrayList<IDOMAttr>(0);
		findChildURIContainers(scanNode, attrs);
		return attrs.toArray(new IDOMAttr[0]);
	}

	private static void findChildURIContainers(Node node,
			List<IDOMAttr> attrsList) {
		if (node instanceof IDOMDocument) {
			node = ((IDOMDocument) node).getDocumentElement();
			findChildURIContainers(node, attrsList);
		} else if (node instanceof ElementImpl) {
			ElementImpl elementImpl = (ElementImpl) node;
			NamedNodeMap attrsMap = elementImpl.getAttributes();
			if (attrsMap != null && attrsMap.getLength() != 0) {
				for (int i = 0; i < attrsMap.getLength(); i++) {
					IDOMAttr attr = (IDOMAttr) attrsMap.item(i);
					String attrValue = attr.getValue();
					if (attrValue != null
							&& attrValue
									.indexOf(CompositeComponentConstants.COMPOSITE_XMLNS) > -1) {
						String compPath = attrValue.replaceFirst(
								CompositeComponentConstants.COMPOSITE_XMLNS, ""); //$NON-NLS-1$
						if (!"".equals(compPath.trim())) { //$NON-NLS-1$
							if (isCorrectCompositeShemaAttrName(attr.getName())) {
								attrsList.add(attr);
							}
						}
					}
				}
			}
			NodeList children = node.getChildNodes();
			if (children != null) {
				for (int i = 0; i < children.getLength(); i++) {
					findChildURIContainers((IDOMNode) children.item(i),
							attrsList);
				}
			}
		}
	}

	private static boolean isCorrectCompositeShemaAttrName(String attrName) {
		if (attrName == null) {
			return false;
		}
		if ("xmlns".equals(attrName.trim()) || "uri".equals(attrName.trim())) { //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		}
		if (attrName.indexOf(':') < 0) {
			return false;
		}
		attrName = attrName.substring(0, attrName.indexOf(':')).trim();
		if ("xmlns".equals(attrName)) { //$NON-NLS-1$
			return true;
		}
		return false;
	}

	public static Map<IFile, List<IDOMNode>> findCompositeComponentsWithURI(
			IResource resource, String URI) throws CoreException {
		Map<IFile, List<IDOMNode>> nodeMap = new HashMap<IFile, List<IDOMNode>>();
		findCompositeComponentsWithURI(resource, nodeMap, URI);
		return nodeMap;
	}

	private static void findCompositeComponentsWithURI(IResource resource,
			Map<IFile, List<IDOMNode>> nodeMap, String URI)
			throws CoreException {
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			IDOMDocument document = JSF2ComponentModelManager
					.getReadableDOMDocument(file);
			Map<String, List<Element>> map = findCompositeComponents(document);
			Set<Entry<String, List<Element>>> entries = map.entrySet();
			List<IDOMNode> nodes = new ArrayList<IDOMNode>();
			for (Iterator<Entry<String, List<Element>>> iterator = entries
					.iterator(); iterator.hasNext();) {
				Entry<String, List<Element>> entry = (Entry<String, List<Element>>) iterator
						.next();
				if (URI.equals(entry.getKey())) {
					for (Element element : entry.getValue()) {
						if (element instanceof IDOMNode) {
							nodes.add((IDOMNode) element);
						}
					}
				}
			}
			if (!nodes.isEmpty()) {
				nodeMap.put(file, nodes);
			}
		} else if (resource instanceof IProject) {
			IResource[] children = ((IProject) resource).members();
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					findCompositeComponentsWithURI(children[i], nodeMap, URI);
				}
			}
		} else if (resource instanceof IFolder) {
			IResource[] children = ((IFolder) resource).members();
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					findCompositeComponentsWithURI(children[i], nodeMap, URI);
				}
			}
		}
	}
	
	public static IDOMElement findCompositeImpl(Node node){
		IDOMElement[] compositeImpl = new IDOMElement[1];
		findCompositeImpl(node, compositeImpl);
		return compositeImpl[0];
	}
	
	private static void findCompositeImpl(Node node,
			IDOMElement[] interfaceElement) {
		if (node instanceof IDOMDocument) {
			IDOMDocument document = (IDOMDocument) node;
			findCompositeImpl(document.getDocumentElement(),
					interfaceElement);
		}
		if (node instanceof ElementImpl) {
			ElementImpl impl = (ElementImpl) node;
			String nameSpace = impl.getNamespaceURI();
			if (CompositeComponentConstants.COMPOSITE_XMLNS.equals(nameSpace)) {
				String nodeName = impl.getLocalName();
				if ("implementation".equals(nodeName)) { //$NON-NLS-1$
					interfaceElement[0] = impl;
					return;
				}
			} else {
				NodeList nodeList = node.getChildNodes();
				if (nodeList != null) {
					for (int i = 0; i < nodeList.getLength(); i++) {
						findCompositeImpl(nodeList.item(i),
								interfaceElement);
					}
				}
			}
		}
	}
	
	public static IDOMAttr[] extractAttrsWithValue(IDOMElement elToExtract, String value){
		List<IDOMAttr> attrs = new ArrayList<IDOMAttr>();
		extractAttrsWithValue(elToExtract, value, attrs);
		return attrs.toArray(new IDOMAttr[0]);
	}
	
	private static void extractAttrsWithValue(IDOMElement elToExtract, String value, List<IDOMAttr> attrs){
		NamedNodeMap namedNodeMap = elToExtract.getAttributes();
		if (namedNodeMap != null) {
			for (int i = 0; i < namedNodeMap.getLength(); i++) {
				IDOMAttr attr = (IDOMAttr) namedNodeMap.item(i);
				if (value.equals(attr.getValue().trim())) {
					attrs.add(attr);
				}
			}
		}
		NodeList children = elToExtract.getChildNodes();
		if (children != null) {
			for (int i = 0; i < children.getLength(); i++) {
				Node node = children.item(i);
				if (node instanceof IDOMElement) {
					extractAttrsWithValue((IDOMElement) node, value, attrs);
				}
			}
		}
	}
	
}
