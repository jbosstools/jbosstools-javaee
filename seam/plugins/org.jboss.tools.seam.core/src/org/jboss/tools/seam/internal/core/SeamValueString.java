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

import org.jboss.tools.seam.core.IValueInfo;
import org.jboss.tools.seam.core.event.Change;
import org.jboss.tools.seam.core.event.ISeamValueString;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamValueString extends SeamObject implements ISeamValueString {
	IValueInfo value;
	String currentValue;
	
	public SeamValueString() {}

	public IValueInfo getValue() {
		return value;
	}
	
	public void setValue(IValueInfo value) {
		this.value = value;
		if(value != null) currentValue = value.getValue();
	}

	public List<Change> merge(SeamObject s) {
		List<Change> changes = super.merge(s);
		SeamValueString v = (SeamValueString)s;
		String v1 = currentValue;
		String v2 = v.value.getValue();
		if(v1 == null || !v1.equals(v2)) {
			changes = Change.addChange(changes, new Change(this, "value", v1, v2));
		}
		setValue(v.value);
		return changes;
	}

}
