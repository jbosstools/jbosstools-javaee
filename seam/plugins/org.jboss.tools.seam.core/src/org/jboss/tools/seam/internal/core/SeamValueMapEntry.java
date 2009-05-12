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

import org.jboss.tools.common.xml.XMLUtilities;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.event.Change;
import org.jboss.tools.seam.core.event.ISeamValueMapEntry;
import org.jboss.tools.seam.core.event.ISeamValueString;
import org.w3c.dom.Element;

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

	@Override
	public List<Change> merge(ISeamElement s) {
		List<Change> changes = super.merge(s);
		
		SeamValueMapEntry e = (SeamValueMapEntry)s;
		
		List<Change> keyChanges = key.merge(e.key);
		if(keyChanges != null && !keyChanges.isEmpty()) {
			Change keyChange = new Change(this, "key", key, key); //$NON-NLS-1$
			keyChange.addChildren(keyChanges);
			changes = Change.addChange(changes, keyChange);
		}
		
		List<Change> valueChanges = value.merge(e.value);
		if(valueChanges != null && !valueChanges.isEmpty()) {
			Change valueChange = new Change(this, "value", value, value); //$NON-NLS-1$
			valueChange.addChildren(valueChanges);
			changes = Change.addChange(changes, valueChange);
		}

		return changes;
	}

	public SeamValueMapEntry clone() throws CloneNotSupportedException {
		SeamValueMapEntry c = (SeamValueMapEntry)super.clone();
		c.key = key == null ? null : key.clone();
		c.value = value == null ? null : value.clone();
		return c;
	}

	public String getXMLName() {
		return TAG_ENTRY;
	}
	
	public String getXMLClass() {
		return null;
	}
	
	static String TAG_ENTRY = "entry";
	static String TAG_KEY = "key";
	static String TAG_VALUE = "value";

	public Element toXML(Element parent, Properties context) {
		Element element = super.toXML(parent, context);
		if(key != null) {
			Element e_key = XMLUtilities.createElement(element, TAG_KEY);
			key.toXML(e_key, context);
		}
		if(value != null) {
			value.toXML(element, context);
		}

		return element;
	}
	
	public void loadXML(Element element, Properties context) {
		super.loadXML(element, context);
		Element c = XMLUtilities.getUniqueChild(element, SeamXMLConstants.TAG_VALUE);
		if(c != null) {
			value = new SeamValueString();
			value.loadXML(c, context);
			adopt(value);
		}
		c = XMLUtilities.getUniqueChild(element, TAG_KEY);
		if(c != null) {
			c = XMLUtilities.getUniqueChild(c, SeamXMLConstants.TAG_VALUE);
			if(c != null) {
				key = new SeamValueString();
				key.toXML(c, context);
				adopt(key);
			}
		}
	}

}
