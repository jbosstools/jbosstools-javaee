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

import org.jboss.tools.seam.core.ISeamXmlFactory;
import org.jboss.tools.seam.core.IValueInfo;
import org.jboss.tools.seam.core.event.Change;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamXmlFactory extends AbstractContextVariable implements ISeamXmlFactory {
	String method = null;
	String value = null;

	public String getMethod() {
		return method;
	}

	public String getValue() {
		return value;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setMethod(IValueInfo value) {
		attributes.put("method", value);
		setMethod(value == null ? null : value.getValue());
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public void setValue(IValueInfo value) {
		attributes.put("value", value);
		setValue(value == null ? null : value.getValue());
	}

	public List<Change> merge(SeamObject s) {
		List<Change> changes = super.merge(s);
		SeamXmlFactory xf = (SeamXmlFactory)s;

		if(!stringsEqual(value, xf.value)) {
			changes = Change.addChange(changes, new Change(this, "value", value, xf.value));
			value = xf.value;
		}
		if(!stringsEqual(method, xf.method)) {
			changes = Change.addChange(changes, new Change(this, "method", method, xf.method));
			method = xf.method;
		}
	
		return changes;
	}
}
