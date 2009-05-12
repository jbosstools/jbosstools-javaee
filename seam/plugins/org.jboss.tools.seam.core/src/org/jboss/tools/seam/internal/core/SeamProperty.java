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

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.xml.XMLUtilities;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamProperty;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.IValueInfo;
import org.jboss.tools.seam.core.event.Change;
import org.jboss.tools.seam.core.event.ISeamValue;
import org.w3c.dom.Element;

public class SeamProperty extends AbstractSeamDeclaration implements ISeamProperty {
	protected ISeamValue value;

	public SeamProperty() {}

	public ISeamValue getValue() {
		return value;
	}
	
	public void setValue(ISeamValue value) {
		this.value = value;
		if(value != null) adopt((SeamObject)value);
	}

	public int getLength() {
		IValueInfo info = attributes.get(ISeamXmlComponentDeclaration.NAME);
		if(info != null) return info.getLength();
		return 0;
	}

	public int getStartPosition() {
		IValueInfo info = attributes.get(ISeamXmlComponentDeclaration.NAME);
		if(info != null) return info.getStartPosition();
		return 0;
	}

	@Override
	public List<Change> merge(ISeamElement s) {
		List<Change> changes = super.merge(s);
		
		SeamProperty d = (SeamProperty)s;

		if(!stringsEqual(name, d.name)) {
			changes = Change.addChange(changes, new Change(this, "name", name, d.name)); //$NON-NLS-1$
			name = d.name;
		}
		
		if(value == null) {
			if(d.value != null) {
				setValue(d.value);
				changes = Change.addChange(changes, new Change(this, SeamXMLConstants.ATTR_VALUE, null, value));
			}
		} else if(d.value == null) {
			if(value != null) {
				changes = Change.addChange(changes, new Change(this, SeamXMLConstants.ATTR_VALUE, value, null));
			}
			value = null;
		} else if(!value.getClass().getName().equals(d.value.getClass().getName())) {
			Object old = value;
			setValue(d.value);
			changes = Change.addChange(changes, new Change(this, SeamXMLConstants.ATTR_VALUE, old, value));
		} else {	
			List<Change> cs = ((SeamObject)value).merge((SeamObject)d.value);
			if(cs != null && !cs.isEmpty()) {
				Change c = new Change(this, SeamXMLConstants.ATTR_VALUE, value, value);
				c.addChildren(cs);
				changes = Change.addChange(changes, c);
			}
		}
		
		return changes;
	}

	boolean stringsEqual(String s1, String s2) {
		return s1 == null ? s2 == null : s1.equals(s2);
	}
	
	public SeamProperty clone() throws CloneNotSupportedException {
		SeamProperty c = (SeamProperty)super.clone();
		c.value = value == null ? null : value.clone();
		return c;
	}

	public String getXMLName() {
		return SeamXMLConstants.TAG_PROPERTY;
	}
	
	public Element toXML(Element parent, Properties context) {
		Element element = super.toXML(parent, context);

		XModelObject old = pushModelObject(context);

		if(value instanceof SeamObject) {
			SeamObject o = (SeamObject)value;
			o.toXML(element, context);
		}
		
		popModelObject(context, old);

		return element;
	}
	
	public void loadXML(Element element, Properties context) {
		super.loadXML(element, context);

		XModelObject old = pushModelObject(context);

		Element c = XMLUtilities.getUniqueChild(element, SeamXMLConstants.TAG_VALUE);
		if(c != null) {
			SeamObject v = null;
			String cls = c.getAttribute(SeamXMLConstants.ATTR_CLASS);
			if(SeamXMLConstants.CLS_MAP.equals(cls)) {
				v = new SeamValueMap();
			} else if(SeamXMLConstants.CLS_LIST.equals(cls)) {
				v = new SeamValueList();
			} else {
				v = new SeamValueString();
			}
			if(v != null) {
				v.loadXML(c, context);
				setValue((ISeamValue)v);
			}			
		}

		popModelObject(context, old);
	}

}
