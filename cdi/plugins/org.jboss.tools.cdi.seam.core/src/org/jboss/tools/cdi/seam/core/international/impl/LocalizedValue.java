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

import org.jboss.tools.cdi.seam.core.international.ILocalizedValue;
import org.jboss.tools.common.model.XModelObject;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class LocalizedValue implements ILocalizedValue {
	XModelObject object;
	String locale = "";

	@Override
	public String getLocaleName() {
		return locale;
	}

	@Override
	public String getValue() {
		return object.getAttributeValue("value");
	}

	public void setObject(XModelObject object) {
		this.object = object;
		locale = getLocale(object);
	}

	public XModelObject getObject() {
		return object;
	}

	public static String getLocale(XModelObject object) {
		XModelObject p = object;
		while(p != null && p.getFileType() < XModelObject.FILE) p = p.getParent();
		if(p != null) {
			String n = p.getAttributeValue("name");
			int i = n.indexOf('_');
			if(i >= 0) return n.substring(i + 1);
		}
		return "";
	}

}
