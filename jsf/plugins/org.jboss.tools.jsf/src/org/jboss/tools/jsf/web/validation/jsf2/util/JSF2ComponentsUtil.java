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
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author yzhishko
 *
 */

@SuppressWarnings("restriction")
public class JSF2ComponentsUtil {

	public static Map<String, List<Element>> findJSF2CompositeComponents(
			IDOMDocument document) {
		Map<String, List<Element>> elementsMap = new HashMap<String, List<Element>>(
				0);
		findJSF2ChildCompositeComponents(document, elementsMap);
		return elementsMap;
	}

	private static void findJSF2ChildCompositeComponents(Node node,
			Map<String, List<Element>> elementsMap) {
		if (node instanceof IDOMDocument) {
			node = ((IDOMDocument) node).getDocumentElement();
			findJSF2ChildCompositeComponents(node, elementsMap);
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
					findJSF2ChildCompositeComponents(children.item(i), elementsMap);
				}
			}
		}
	}

}
