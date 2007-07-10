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
package org.jboss.tools.seam.internal.core;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jboss.tools.seam.core.ISeamProperty;
import org.jboss.tools.seam.core.event.Change;

public class SeamProperty extends SeamObject implements ISeamProperty {
	protected String name;
	protected Object value;
	protected int startPosition = -1;
	protected int length = -1;

	public SeamProperty() {}

	public SeamProperty(String name) {
		this.name = name;
	}

	public SeamProperty(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public SeamProperty(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public int getStartPosition() {
		return startPosition;
	}
	
	public void setStartPosition(int v) {
		startPosition = v;
	}

	public int getLength() {
		return length;
	}
	
	public void setLength(int v) {
		length = v;
	}

	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}

	public List<Change> merge(SeamObject s) {
		List<Change> changes = super.merge(s);
		
		SeamProperty d = (SeamProperty)s;

		startPosition = d.startPosition;
		length = d.length;

		if(!stringsEqual(name, d.name)) {
			changes = Change.addChange(changes, new Change(this, "name", name, d.name));
			name = d.name;
		}
		if(!valuesEqual(value, d.value)) {
			changes = Change.addChange(changes, new Change(this, "value", value, d.value));
			value = d.value;
		}		
		
		return changes;
	}

	boolean stringsEqual(String s1, String s2) {
		return s1 == null ? s2 == null : s1.equals(s2);
	}
	
	boolean valuesEqual(Object v1, Object v2) {
		if(v1 == null) return v2 == null;
		if(v2 == null) return v1 == null;
		if(v1 == v2) return true;
		if(v1 instanceof List && v2 instanceof List) {
			List<?> l1 = (List<?>)v1;
			List<?> l2 = (List<?>)v2;
			if(l1.size() != l2.size()) return false;
			for (int i = 0; i < l1.size(); i++) {
				if(!valuesEqual(l1.get(i), l2.get(i))) return false;
			}
			return true;
		} else if(v1 instanceof Map && v2 instanceof Map) {
			Map<?,?> m1 = (Map<?,?>)v1;
			Map<?,?> m2 = (Map<?,?>)v2;
			if(m1.size() != m2.size()) return false;
			Iterator<?> it = m1.keySet().iterator();
			while(it.hasNext()) {
				Object key = it.next();
				Object o1 = m1.get(key);
				Object o2 = m2.get(key);
				if(o2 == null) return false;
				if(!valuesEqual(o1, o2)) return false;
			}
			return true;
		}
		if(v1.equals(v2)) return true;
		
		return false;
	}

}
