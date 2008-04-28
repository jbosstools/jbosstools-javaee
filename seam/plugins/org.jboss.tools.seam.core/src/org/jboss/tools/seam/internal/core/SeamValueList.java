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
import org.jboss.tools.seam.core.event.ISeamValueList;
import org.jboss.tools.seam.core.event.ISeamValueString;
import org.w3c.dom.Element;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamValueList extends SeamObject implements ISeamValueList {
	List<ISeamValueString> values = new ArrayList<ISeamValueString>();
	
	public SeamValueList() {}

	public List<ISeamValueString> getValues() {
		return values;
	}
	
	public void addValue(ISeamValueString value) {
		values.add(value);
		adopt(value);
	}
	
	@Override
	public List<Change> merge(ISeamElement s) {
		List<Change> changes = super.merge(s);
		SeamValueList v = (SeamValueList)s;
		
		//improve
		if(values.size() != v.values.size()) {
			changes = Change.addChange(changes, new Change(this, "value", values, v.values)); //$NON-NLS-1$
		}
		values = v.values;
		for (int i = 0; i < values.size(); i++) {
			adopt((SeamObject)values.get(i));
		}

		return changes;
	}

	public SeamValueList clone() throws CloneNotSupportedException {
		SeamValueList c = (SeamValueList)super.clone();
		c.values = new ArrayList<ISeamValueString>();
		for (ISeamValueString v : values) {
			c.addValue(v.clone());
		}
		return c;
	}

	public String getXMLName() {
		return SeamXMLConstants.TAG_VALUE;
	}
	
	public String getXMLClass() {
		return SeamXMLConstants.CLS_LIST;
	}

	public Element toXML(Element parent, Properties context) {
		Element element = super.toXML(parent, context);
		
		for (ISeamValueString entry: values) {
			SeamObject o = (SeamObject)entry;
			o.toXML(element, context);
		}

		return element;
	}
	
	public void loadXML(Element element, Properties context) {
		super.loadXML(element, context);
		Element[] cs = XMLUtilities.getChildren(element, SeamXMLConstants.TAG_VALUE);
		for (int i = 0; i < cs.length; i++) {
			SeamValueString entry = new SeamValueString();
			entry.loadXML(cs[i], context);
			addValue(entry);
		}
	}

}
