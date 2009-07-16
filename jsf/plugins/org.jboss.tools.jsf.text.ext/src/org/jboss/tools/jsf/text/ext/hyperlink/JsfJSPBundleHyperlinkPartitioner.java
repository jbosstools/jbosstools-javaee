/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.jsf.text.ext.hyperlink;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.jboss.tools.common.text.ext.util.Utils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * 
 * @author Jeremy
 *
 */
public class JsfJSPBundleHyperlinkPartitioner extends JSPBundleHyperlinkPartitioner {
	
	private static final String JSF_CORE_URI = "http://java.sun.com/jsf/core";
	
	protected String[] getLoadBundleTagPrefixes(IDocument document, int offset) {
		return getLoadBundleTagPrefixes(document, new Region(offset, 0));
	}
	
	static String[] getLoadBundleTagPrefixes(IDocument document, IRegion region) {
		Map<String, Set<String>> namespaces = getNameSpaces(document, region.getOffset());
		
		if (namespaces == null) 
			return null;
		
		Set<String> prefixes = namespaces.get(JSF_CORE_URI);
		if (prefixes == null) 
			return null;
			
		return prefixes.toArray(new String[prefixes.size()]);
	}
	
	/**
	 * Method collects the namespaces used in the document
	 * 
	 * @param document
	 * @param offset
	 * @return
	 */
	static public Map<String, Set<String>> getNameSpaces(IDocument document, int offset) {
		IStructuredModel sModel = StructuredModelManager
									.getModelManager()
									.getExistingModelForRead(document);
			
		try {
			if (sModel == null)
				return null;

			Document xmlDocument = (sModel instanceof IDOMModel) ? ((IDOMModel) sModel)
					.getDocument()
					: null;

			if (xmlDocument == null)
				return null;

			Map<String, Set<String>> namespaces = new HashMap<String,Set<String>>();
			Node n = Utils.findNodeForOffset(xmlDocument, offset);
			while (n != null) {
				if (!(n instanceof Element)) {
					if (n instanceof Attr) {
						n = ((Attr) n).getOwnerElement();
					} else {
						n = n.getParentNode();
					}
					continue;
				}

				NamedNodeMap attrs = n.getAttributes();
				for (int j = 0; attrs != null && j < attrs.getLength(); j++) {
					Attr a = (Attr) attrs.item(j);
					String name = a.getName();
					if (name.startsWith("xmlns:")) {
						final String prefix = name.substring("xmlns:".length());
						final String uri = a.getValue();
						if (prefix != null && prefix.trim().length() > 0 &&
								uri != null && uri.trim().length() > 0) {

							Set<String> prefixes = namespaces.get(uri.trim());
							if (prefixes == null) {
								prefixes = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
								namespaces.put(uri.trim(), prefixes);
							}
							prefixes.add(prefix.trim());
						}
					}
				}

				n = n.getParentNode();
			}

			return namespaces;
		} finally {
			if (sModel != null) {
				sModel.releaseFromRead();
			}
		}
	}
}
