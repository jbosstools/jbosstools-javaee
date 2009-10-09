/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jsf.jsf2.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.tools.common.meta.XMapping;
import org.jboss.tools.common.meta.XModelMetaData;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.NamespaceMapping;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class CompositeComponentNamespaces {
	private static Map<String,CompositeComponentNamespaces> map = new HashMap<String, CompositeComponentNamespaces>();
	
	public static CompositeComponentNamespaces getInstance(XModelMetaData meta, String version) {
		CompositeComponentNamespaces instance = map.get(version);
		if(instance == null) {
			instance = new CompositeComponentNamespaces(meta, version);
		}
		return instance;
	}
	
	String versionSuffix = null;
	
	private Map<String, String> uriToNamespace = new HashMap<String, String>();
	
	private CompositeComponentNamespaces(XModelMetaData meta, String versionSuffix) {
		XMapping m = meta.getMapping("JSF2CompositeNamespaces"); //$NON-NLS-1$
		if(m == null) return;
		this.versionSuffix = versionSuffix;
		String[] keys = m.getKeys();
		for (int i = 0; i < keys.length; i++) {
			String v = m.getValue(keys[i]);
			uriToNamespace.put(v, keys[i]);
		}
	}
	
	static String XMLNS_PREFIX = "xmlns:";	 //$NON-NLS-1$

	public NamespaceMapping getNamespaceMapping(Element element) {
		NamespaceMapping mapping = new NamespaceMapping();
		NamedNodeMap as = element.getAttributes();
		for (int i = 0; i < as.getLength(); i++) {
			Node n = as.item(i);
			String name = n.getNodeName();
			if(name.startsWith(XMLNS_PREFIX)) {
				String actualNamespace = name.substring(XMLNS_PREFIX.length());
				String uri = n.getNodeValue();
				String defaultNamespace = getDefaultNamespace(uri);
				if(defaultNamespace == null) {
					defaultNamespace = name.substring(XMLNS_PREFIX.length());
				}
				mapping.addNamespace(defaultNamespace, actualNamespace, uri);
			}
		}
		return mapping;
	}
	
	public String getDefaultNamespace(String uri) {
		return uriToNamespace.get(uri);
	}
	
	public void validateNamespaces(XModelObject object, Element element) {
		NamespaceMapping namespaceMapping = NamespaceMapping.load(object);
		if(namespaceMapping == null) namespaceMapping = new NamespaceMapping();
//		StringBuffer loc = new StringBuffer();
//		loc.append(object.getAttributeValue("xsi:schemaLocation")); //$NON-NLS-1$
		XModelObject[] cs = object.getChildren();

		Set<String> ns = new HashSet<String>();
		
		for (int i = 0; i < cs.length; i++) {
			String n = cs[i].getModelEntity().getXMLSubPath();
			int k = n.indexOf(':');
			if(k < 0) continue;
			String defaultNamespace = n.substring(0, k);
			validateNamespace(element, namespaceMapping, defaultNamespace, ns);
//			String schema = getSchema(defaultNamespace);
//			if(loc.indexOf(uri) < 0) {
//				loc.append(' ').append(uri).append(' ').append(schema);
//			}
			
		}

		XModelObject im = object.getChildByPath("Implementation");
		if(im != null) {
			XModelObject[] cs2 = im.getChildren();
			for (int i = 0; i < cs2.length; i++) {
				String n = cs2[i].getAttributeValue("tag");
				int k = n.indexOf(':');
				if(k < 0) continue;
				String defaultNamespace = n.substring(0, k);
				validateNamespace(element, namespaceMapping, defaultNamespace, ns);
			}
		}
		
		if(!ns.contains("composite")) {
			validateNamespace(element, namespaceMapping, "composite", ns);
		}
//		object.setAttributeValue("xsi:schemaLocation", loc.toString()); //$NON-NLS-1$
	}

	static void validateNamespace(Element element, NamespaceMapping namespaceMapping, String defaultNamespace, Set<String> ns) {
		if(ns.contains(defaultNamespace)) return;
		ns.add(defaultNamespace);
		String uri = namespaceMapping.getURIForDefaultNamespace(defaultNamespace);
		if(uri == null) return;
		String actualNamespace = namespaceMapping.getActualNamespace(defaultNamespace);
		if(actualNamespace == null) actualNamespace = defaultNamespace;
		element.setAttribute(XMLNS_PREFIX + actualNamespace, uri);
	}
}
