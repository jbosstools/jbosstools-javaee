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
package org.jboss.tools.seam.internal.core;

import java.util.List;

import org.jboss.tools.seam.core.ISeamProperty;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.IValueInfo;
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

	public int getLength() {
		IValueInfo info = attributes.get(ISeamXmlComponentDeclaration.NAME);
		if(info != null) return info.getLength();
		return 0;
	}

	public int getStartPosition() {
		IValueInfo info = attributes.get(ISeamXmlComponentDeclaration.NAME);
		if(info != null) return info.getStartPosition();
		return 0;
	}

	public List<Change> merge(SeamObject s) {
		List<Change> changes = super.merge(s);
		
		SeamProperty d = (SeamProperty)s;

		if(!stringsEqual(name, d.name)) {
			changes = Change.addChange(changes, new Change(this, "name", name, d.name)); //$NON-NLS-1$
			name = d.name;
		}
		
		if(value == null) {
			if(d.value != null) {
				setValue(d.value);
				changes = Change.addChange(changes, new Change(this, "value", null, value)); //$NON-NLS-1$
			}
		} else if(d.value == null) {
			if(value != null) {
				changes = Change.addChange(changes, new Change(this, "value", value, null)); //$NON-NLS-1$
			}
			value = null;			
		} else {		
			List<Change> cs = ((SeamObject)value).merge((SeamObject)d.value);
			if(cs != null && cs.size() > 0) {
				Change c = new Change(this, "value", value, value); //$NON-NLS-1$
				c.addChildren(cs);
				changes = Change.addChange(changes, c);
			}
		}
		
		return changes;
	}

	boolean stringsEqual(String s1, String s2) {
		return s1 == null ? s2 == null : s1.equals(s2);
	}
	
	public SeamProperty clone() throws CloneNotSupportedException {
		SeamProperty c = (SeamProperty)super.clone();
		c.value = value == null ? null : value.clone();
		return c;
	}

}
