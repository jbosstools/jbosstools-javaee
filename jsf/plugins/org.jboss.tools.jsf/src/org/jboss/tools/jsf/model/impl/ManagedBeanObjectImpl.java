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
import org.jboss.tools.common.model.impl.OrderedObjectImpl;

public class ManagedBeanObjectImpl extends OrderedObjectImpl {
	private static final long serialVersionUID = 6027433966850750458L;

	public String name() {
		return "" + getAttributeValue("managed-bean-name");
	}

	public void set(String name, String value) {
		super.set(name, value);
		if("content-kind".equals(name)) {
			validateValue();
		}
	}
	
	public void validateValue() {
		XModelObject o = getChildByPath("Entries");
		XModelObject[] ps = getChildren("JSFManagedProperty");
		String entity = (o == null) ? null : o.getModelEntity().getName();
		String contentKind = get("content-kind");
		if("list-entries".equals(contentKind)) {
			if(!"JSFListEntries".equals(entity)) {
				if(o != null) o.removeFromParent();
				if(ps.length > 0) for (int i = 0; i < ps.length; i++) ps[i].removeFromParent();
				o = getModel().createModelObject("JSFListEntries", null);
				addChild(o);
			}
		} else if("map-entries".equals(contentKind)) {
			if(!"JSFMapEntries".equals(entity)) {
				if(o != null) o.removeFromParent();
				if(ps.length > 0) for (int i = 0; i < ps.length; i++) ps[i].removeFromParent();
				o = getModel().createModelObject("JSFMapEntries", null);
				addChild(o);
			}
		} else {
			if(o != null) o.removeFromParent();
		}		
	}

}
