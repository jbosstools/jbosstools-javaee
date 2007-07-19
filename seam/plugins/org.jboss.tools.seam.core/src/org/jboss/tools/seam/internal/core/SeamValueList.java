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
import org.jboss.tools.seam.core.event.ISeamValueList;
import org.jboss.tools.seam.core.event.ISeamValueString;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamValueList extends SeamObject implements ISeamValueList {
	List<ISeamValueString> values = new ArrayList<ISeamValueString>();
	
	public SeamValueList() {}

	public List<ISeamValueString> getValues() {
		return values;
	}
	
	public void addValue(SeamValueString value) {
		values.add(value);
		adopt(value);
	}
	
	public List<Change> merge(SeamObject s) {
		List<Change> changes = super.merge(s);
		SeamValueList v = (SeamValueList)s;
		
		//improve
		if(values.size() != v.values.size()) {
			changes = Change.addChange(changes, new Change(this, "value", values, v.values));
		}
		values = v.values;
		for (int i = 0; i < values.size(); i++) {
			adopt((SeamObject)values.get(i));
		}

		return changes;
	}

}
