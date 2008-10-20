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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jboss.tools.seam.core.BijectedAttributeType;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.IValueInfo;
import org.jboss.tools.seam.core.event.Change;
import org.w3c.dom.Element;

/**
 * @author Viacheslav Kabanovich
 */
public class BijectedAttribute extends SeamJavaContextVariable implements IBijectedAttribute {
	protected BijectedAttributeType[] types = null;
	
	public BijectedAttribute() {		
	}

	public BijectedAttributeType[] getTypes() {
		return types;
	}
	
	public boolean isOfType(BijectedAttributeType type) {
		if(types == null) return false;
		for (int i = 0; i < types.length; i++) {
			if(types[i] == type) return true;
		}
		return false;
	}
	
	public boolean isContextVariable() {
		if(types == null || types.length == 0) return false;
		for (int i = 0; i < types.length; i++) {
			if(types[i].isOut()) return true;
		}
		return false;
	}

	public void setTypes(BijectedAttributeType[] types) {
		this.types = types;
	}
	
	public String getValue() {
		return getName();
	}
	
	public void setValue(String value) {}
	
	public void setValue(IValueInfo value) {}

	@Override
	public List<Change> merge(ISeamElement s) {
		List<Change> changes = super.merge(s);
		AbstractContextVariable f = (AbstractContextVariable)s;
		
		if(f instanceof BijectedAttribute) {
			BijectedAttribute sf = (BijectedAttribute)f;
			if(!typesAreEqual(types, sf.types)) {
				changes = Change.addChange(changes, new Change(this, "types", types, sf.types)); //$NON-NLS-1$
				this.types = sf.types;
			}
		}
		
		return changes;
	}
	
	boolean typesAreEqual(BijectedAttributeType[] types1, BijectedAttributeType[] types2) {
		if(types1 == null || types2 == null) return types2 == types1;
		if(types1.length != types2.length) return false;
		for (int i = 0; i < types1.length; i++) {
			if(types1[i] != types2[i]) return false;
		}
		return true;
		
	}

	public BijectedAttribute clone() throws CloneNotSupportedException {
		BijectedAttribute c = (BijectedAttribute)super.clone();
		return c;
	}

	public String getXMLName() {
		return SeamXMLConstants.TAG_BIJECTED_ATTRIBUTE;
	}

	public Element toXML(Element parent, Properties context) {
		Element element = super.toXML(parent, context);
		
		if(types != null) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < types.length; i++) sb.append(types[i].toString()).append(';');
			element.setAttribute("attr-types", sb.toString());
		}

		return element;
	}
	
	public void loadXML(Element element, Properties context) {
		super.loadXML(element, context);
		
		if(element.hasAttribute("attr-types")) {
			String v = element.getAttribute("attr-types");
			if(v != null && v.length() > 0) {
				String[] cs = v.split(";");
				List<BijectedAttributeType> list = new ArrayList<BijectedAttributeType>();
				for (int i = 0; i < cs.length; i++) {
					list.add(BijectedAttributeType.valueOf(cs[i]));
				}
				types = list.toArray(new BijectedAttributeType[0]);
			}
		}
	}

}
