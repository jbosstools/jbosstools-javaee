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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jboss.tools.common.xml.XMLUtilities;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.event.Change;
import org.jboss.tools.seam.core.event.ISeamValueMap;
import org.jboss.tools.seam.core.event.ISeamValueMapEntry;
import org.w3c.dom.Element;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamValueMap extends SeamObject implements ISeamValueMap {
	List<ISeamValueMapEntry> entries = new ArrayList<ISeamValueMapEntry>();

	public SeamValueMap() {}

	public List<ISeamValueMapEntry> getEntries() {
		return entries;
	}
	
	public void addEntry(ISeamValueMapEntry entry) {
		entries.add(entry);
		adopt(entry);
	}
	
	@Override
	public List<Change> merge(ISeamElement s) {
		List<Change> changes = super.merge(s);
		SeamValueMap v = (SeamValueMap)s;
		
		//improve
		if(entries.size() != v.entries.size()) { 
			changes = Change.addChange(changes, new Change(this, "value", entries, v.entries)); //$NON-NLS-1$
		}
		entries = v.entries;
		for (int i = 0; i < entries.size(); i++) {
			adopt((SeamObject)entries.get(i));
		}			

		return changes;
	}

	public SeamValueMap clone() throws CloneNotSupportedException {
		SeamValueMap c = (SeamValueMap)super.clone();
		c.entries = new ArrayList<ISeamValueMapEntry>();
		for (ISeamValueMapEntry v : entries) {
			c.addEntry(v.clone());
		}
		return c;
	}

	public String getXMLName() {
		return SeamXMLConstants.TAG_VALUE;
	}
	
	public String getXMLClass() {
		return SeamXMLConstants.CLS_MAP;
	}

	public Element toXML(Element parent, Properties context) {
		Element element = super.toXML(parent, context);
		
		for (ISeamValueMapEntry entry: entries) {
			SeamObject o = (SeamObject)entry;
			o.toXML(element, context);
		}

		return element;
	}
	
	public void loadXML(Element element, Properties context) {
		super.loadXML(element, context);
		Element[] cs = XMLUtilities.getChildren(element, SeamValueMapEntry.TAG_ENTRY);
		for (int i = 0; i < cs.length; i++) {
			SeamValueMapEntry entry = new SeamValueMapEntry();
			entry.loadXML(cs[i], context);
			addEntry(entry);
		}
	}

}
