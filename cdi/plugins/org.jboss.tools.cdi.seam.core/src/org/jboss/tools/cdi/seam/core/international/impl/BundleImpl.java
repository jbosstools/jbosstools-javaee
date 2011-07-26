/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.seam.core.international.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jboss.tools.cdi.seam.core.international.IBundle;
import org.jboss.tools.cdi.seam.core.international.IProperty;
import org.jboss.tools.common.model.XModelObject;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BundleImpl implements IBundle {
	String name = "";
	Map<String, XModelObject> objects = new TreeMap<String, XModelObject>();
	Map<String, PropertyImpl> properties = null;

	public BundleImpl() {}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String s) {
		name = s;
	}

	@Override
	public Set<String> getPropertyNames() {
		initProperties();
		return properties.keySet();
	}

	public void initProperties() {
		if(properties == null) {
			Map<String, PropertyImpl> ps = new HashMap<String, PropertyImpl>();
			for (XModelObject o: objects.values()) {
				XModelObject[] os = o.getChildren();
				for (XModelObject p: os) {
					String name = p.getAttributeValue("name");
					PropertyImpl pi = _getProperty(name, ps);
					pi.addObject(p);
				}
			}
			properties = ps;
		}
	}

	public void addObject(XModelObject o) {
		objects.put(LocalizedValue.getLocale(o), o);
	}

	public IProperty getProperty(String name) {
		initProperties();
		return properties.get(name);
	}

	private PropertyImpl _getProperty(String name, Map<String, PropertyImpl> properties) {
		PropertyImpl p = properties.get(name);
		if(p == null) {
			p = new PropertyImpl();
			p.setBundle(this);
			p.setName(name);
			properties.put(name, p);
		}
		return p;
	}

	/*
	 * Returns file objects sorted by locales.
	 */
	public Map<String, XModelObject> getObjects() {
		return objects;
	}

}
