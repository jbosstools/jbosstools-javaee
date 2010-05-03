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

package org.jboss.tools.jsf.web.validation.jsf2.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
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
							.startsWith(JSF2ResourceUtil.JSF2_URI_PREFIX)
					&& !namespaceURI.equals(JSF2ResourceUtil.JSF2_URI_PREFIX)) {
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
									.indexOf(JSF2ResourceUtil.JSF2_URI_PREFIX) > -1) {
						String compPath = attrValue.replaceFirst(
								JSF2ResourceUtil.JSF2_URI_PREFIX, ""); //$NON-NLS-1$
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

}
