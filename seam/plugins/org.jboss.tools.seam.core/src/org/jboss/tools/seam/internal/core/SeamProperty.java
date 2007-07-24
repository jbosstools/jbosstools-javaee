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

import java.util.List;

import org.jboss.tools.seam.core.ISeamProperty;
import org.jboss.tools.seam.core.event.Change;
import org.jboss.tools.seam.core.event.ISeamValue;

public class SeamProperty extends AbstractSeamDeclaration implements ISeamProperty {
	protected ISeamValue value;

	public SeamProperty() {}

	public ISeamValue getValue() {
		return value;
	}
	
	public void setValue(ISeamValue value) {
		this.value = value;
		if(value != null) adopt((SeamObject)value);
	}

	public List<Change> merge(SeamObject s) {
		List<Change> changes = super.merge(s);
		
		SeamProperty d = (SeamProperty)s;

		if(!stringsEqual(name, d.name)) {
			changes = Change.addChange(changes, new Change(this, "name", name, d.name));
			name = d.name;
		}
		
		if(value == null) {
			if(d.value != null) {
				setValue(d.value);
				changes = Change.addChange(changes, new Change(this, "value", null, value));
			}
		} else if(d.value == null) {
			if(value != null) {
				changes = Change.addChange(changes, new Change(this, "value", value, null));
			}
			value = null;			
		} else {		
			List<Change> cs = ((SeamObject)value).merge((SeamObject)d.value);
			if(cs != null && cs.size() > 0) {
				Change c = new Change(this, "value", value, value);
				c.addChildren(cs);
				changes = Change.addChange(changes, c);
			}
		}
		
		return changes;
	}

	boolean stringsEqual(String s1, String s2) {
		return s1 == null ? s2 == null : s1.equals(s2);
	}
	
}
