 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core;

import java.util.List;
import org.jboss.tools.seam.core.IValueInfo;
import org.jboss.tools.seam.core.event.Change;

/**
 * @author Viacheslav Kabanovich
 */
public class DataModelSelectionAttribute extends BijectedAttribute {
	public static String VALUE = "value";
	String value;
	
	public DataModelSelectionAttribute() {}

	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public void setValue(IValueInfo value) {
		attributes.put(VALUE, value);
		this.value = value == null ? null : value.getValue();
	}

	public List<Change> merge(SeamObject s) {
		List<Change> changes = super.merge(s);
		if(s instanceof DataModelSelectionAttribute) {
			DataModelSelectionAttribute sf = (DataModelSelectionAttribute)s;
			if(!stringsEqual(this.value, sf.value)) {
				changes = Change.addChange(changes, new Change(this, VALUE, this.value, sf.value));
				this.value = sf.value;
			}
		}
		return changes;
	}
	
}
