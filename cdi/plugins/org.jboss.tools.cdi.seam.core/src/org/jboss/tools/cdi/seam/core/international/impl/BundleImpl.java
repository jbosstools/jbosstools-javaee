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

import java.util.HashSet;
import java.util.Set;

import org.jboss.tools.cdi.seam.core.international.IBundle;
import org.jboss.tools.common.model.XModelObject;

public class BundleImpl implements IBundle {
	String name = "";
	Set<XModelObject> objects = new HashSet<XModelObject>();

	public BundleImpl() {}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String s) {
		name = s;
	}

	public Set<String> getPropertyNames() {
		Set<String> result = new HashSet<String>();
		for (XModelObject o: objects) {
			XModelObject[] os = o.getChildren();
			for (XModelObject p: os) {
				result.add(p.getAttributeValue("name"));
			}
		}
		return result;
	}

	public void addObject(XModelObject o) {
		objects.add(o);
	}

}
