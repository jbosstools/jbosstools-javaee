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

import org.jboss.tools.seam.core.event.Change;
import org.jboss.tools.seam.core.event.ISeamValueMapEntry;
import org.jboss.tools.seam.core.event.ISeamValueString;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamValueMapEntry extends SeamObject implements ISeamValueMapEntry {
	SeamValueString key;
	SeamValueString value;
	
	public SeamValueMapEntry() {}

	public ISeamValueString getKey() {
		return key;
	}

	public ISeamValueString getValue() {
		return value;
	}
	
	public void setKey(SeamValueString key) {
		this.key = key;
		adopt(key);
	}

	public void setValue(SeamValueString value) {
		this.value = value;
		adopt(value);
	}

	public List<Change> merge(SeamObject s) {
		List<Change> changes = super.merge(s);
		
		SeamValueMapEntry e = (SeamValueMapEntry)s;
		
		List<Change> keyChanges = key.merge(e.key);
		if(keyChanges != null && keyChanges.size() > 0) {
			Change keyChange = new Change(this, "key", key, key);
			keyChange.addChildren(keyChanges);
			changes = Change.addChange(changes, keyChange);
		}
		
		List<Change> valueChanges = value.merge(e.value);
		if(valueChanges != null && valueChanges.size() > 0) {
			Change valueChange = new Change(this, "value", value, value);
			valueChange.addChildren(valueChanges);
			changes = Change.addChange(changes, valueChange);
		}

		return changes;
	}

}
