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

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.seam.core.event.Change;
import org.jboss.tools.seam.core.event.ISeamValueMap;
import org.jboss.tools.seam.core.event.ISeamValueMapEntry;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamValueMap extends SeamObject implements ISeamValueMap {
	List<ISeamValueMapEntry> entries = new ArrayList<ISeamValueMapEntry>();

	public SeamValueMap() {}

	public List<ISeamValueMapEntry> getEntries() {
		return entries;
	}
	
	public void addEntry(SeamValueMapEntry entry) {
		entries.add(entry);
		adopt(entry);
	}
	
	public List<Change> merge(SeamObject s) {
		List<Change> changes = super.merge(s);
		SeamValueMap v = (SeamValueMap)s;
		
		//improve
		if(entries.size() != v.entries.size()) { 
			changes = Change.addChange(changes, new Change(this, "value", entries, v.entries));
		}
		entries = v.entries;
		for (int i = 0; i < entries.size(); i++) {
			adopt((SeamObject)entries.get(i));
		}			

		return changes;
	}

}
