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
package org.jboss.tools.jsf.model.impl;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.impl.RegularObjectImpl;

public class ManagedPropertyObjectImpl extends RegularObjectImpl {
	private static final long serialVersionUID = 5886395372205322093L;

	public String name() {
		return "" + getAttributeValue("property-name");
	}
	
	public void set(String name, String value) {
		super.set(name, value);
		if("value-kind".equals(name)) {
			validateValue();
		}
	}
	
	public String getAttributeValue(String name) {
		if("value".equals(name)) {
			String vk = get("value-kind");
			if("value".equals(vk)) return super.getAttributeValue(name);
			return "[" + vk + "]";
		}
		return super.getAttributeValue(name);
	}
	
	public void validateValue() {
		XModelObject o = getChildByPath("Entries");
		String entity = (o == null) ? null : o.getModelEntity().getName();
		String valueKind = get("value-kind");
		if("list-entries".equals(valueKind)) {
			if(!"JSFListEntries".equals(entity)) {
				if(o != null) o.removeFromParent();
				o = getModel().createModelObject("JSFListEntries", null);
				addChild(o);
			}
		} else if("map-entries".equals(valueKind)) {
			if(!"JSFMapEntries".equals(entity)) {
				if(o != null) o.removeFromParent();
				o = getModel().createModelObject("JSFMapEntries", null);
				addChild(o);
			}
		} else {
			if(o != null) o.removeFromParent();
		}		
	}

	public boolean isNotSingleValue() {
		return !"value".equals(getAttributeValue("value-kind"));
	}
	
	public boolean isAttributeEditable(String name) {
		if(!super.isAttributeEditable(name)) return false;
		if("value".equals(name)) {
			return !isNotSingleValue();
		}
		return true;
	}

}
