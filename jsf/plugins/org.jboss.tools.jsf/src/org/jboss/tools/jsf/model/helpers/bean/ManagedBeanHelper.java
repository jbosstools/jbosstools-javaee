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
package org.jboss.tools.jsf.model.helpers.bean;

import org.eclipse.jdt.core.*;
import org.jboss.tools.common.model.*;

public class ManagedBeanHelper {
	
	public static IType getType(XModelObject bean) {
		if(bean == null) return null;
		String typename = bean.getAttributeValue("managed-bean-class"); //$NON-NLS-1$
		if(typename == null || typename.length() == 0) return null;
		IJavaProject jp = BeanHelper.getJavaProject(bean);
		try {
			return jp == null ? null : jp.findType(typename);
		} catch (JavaModelException e) {
			//ignore
			return null;
		}
	}
	
	public static IMember getMember(XModelObject property) {
		if(property == null) return null;
		String propertyName = property.getAttributeValue("property-name"); //$NON-NLS-1$
		if(propertyName == null || propertyName.length() == 0) return null;
		IType type = getType(property.getParent());
		if(type == null) return null;
		IField f = type.getField(propertyName);
		if(f != null && f.exists()) return f;
		String getter = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1); //$NON-NLS-1$
		IMethod m = type.getMethod(getter, new String[0]);
		if(m != null && m.exists()) return m;
		return null;
	}

}
