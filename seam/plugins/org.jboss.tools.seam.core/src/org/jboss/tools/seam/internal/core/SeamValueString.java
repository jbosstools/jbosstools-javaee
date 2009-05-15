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
import java.util.Properties;

import org.jboss.tools.common.model.project.ext.IValueInfo;
import org.jboss.tools.common.model.project.ext.event.Change;
import org.jboss.tools.jst.web.model.project.ext.store.XMLStoreHelper;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.event.ISeamValueString;
import org.w3c.dom.Element;

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

	@Override
	public List<Change> merge(ISeamElement s) {
		List<Change> changes = super.merge(s);
		SeamValueString v = (SeamValueString)s;
		String v1 = currentValue;
		String v2 = v.value.getValue();
		if(v1 == null || !v1.equals(v2)) {
			changes = Change.addChange(changes, new Change(this, "value", v1, v2)); //$NON-NLS-1$
		}
		setValue(v.value);
		return changes;
	}

	public SeamValueString clone() throws CloneNotSupportedException {
		SeamValueString c = (SeamValueString)super.clone();
		return c;
	}

	public String getXMLName() {
		return SeamXMLConstants.TAG_VALUE;
	}
	
	public String getXMLClass() {
		return null;
	}

	public Element toXML(Element parent, Properties context) {
		Element element = super.toXML(parent, context);
		
		if(value != null) {
			XMLStoreHelper.saveValueInfo(element, value, context);
		}

		return element;
	}
	
	public void loadXML(Element element, Properties context) {
		super.loadXML(element, context);
		setValue(XMLStoreHelper.loadValueInfo(element, context));
	}

}
