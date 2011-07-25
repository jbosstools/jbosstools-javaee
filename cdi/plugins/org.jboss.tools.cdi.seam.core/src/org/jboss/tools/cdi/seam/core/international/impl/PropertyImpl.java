/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.seam.core.international.impl;

import java.util.HashMap;
import java.util.Map;

import org.jboss.tools.cdi.seam.core.international.IBundle;
import org.jboss.tools.cdi.seam.core.international.ILocalizedValue;
import org.jboss.tools.cdi.seam.core.international.IProperty;
import org.jboss.tools.common.model.XModelObject;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class PropertyImpl implements IProperty {
	String name;
	BundleImpl bundle;
	Map<String, LocalizedValue> values = new HashMap<String, LocalizedValue>();

	@Override
	public IBundle getBundle() {
		return bundle;
	}

	public void setBundle(BundleImpl bundle) {
		this.bundle = bundle;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public ILocalizedValue getValue() {
		ILocalizedValue result = values.get("");
		if(result == null && !values.isEmpty()) {
			result = values.values().iterator().next();
		}
		return result;
	}
	
	@Override
	public ILocalizedValue getValue(String locale) {
		return values.get(locale);
	}

	public void addObject(XModelObject o) {
		LocalizedValue value = new LocalizedValue();
		value.setObject(o);
		values.put(value.getLocaleName(), value);
	}

}
