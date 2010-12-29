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

import java.util.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.*;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.util.BeanUtil;

public class BeanHelper {
	
	public static Map<String,IJavaElement> getJavaProperties(IType type) throws JavaModelException {
		Map<String,IJavaElement> map = new TreeMap<String,IJavaElement>();
		if(type == null || !type.exists()) return map;
		IField[] fs = type.getFields();
		for (int i = 0; i < fs.length; i++) {
			map.put(fs[i].getElementName(), fs[i]);
		}
		IMethod[] ms = type.getMethods();
		for (int i = 0; i < ms.length; i++) {
			if(!BeanUtil.isGetter(ms[i])) continue;
			String n = BeanUtil.getPropertyName(ms[i].getElementName());
			if(n != null && !map.containsKey(n)) map.put(n, ms[i]);
		}
		Map<String,IJavaElement> smap = getSuperTypeJavaProperties(type);
		if(smap != null) map.putAll(smap);
		return map;
	}
	
	static Map<String,IJavaElement> getSuperTypeJavaProperties(IType type) throws JavaModelException {
		String scn = type.getSuperclassName();
		if(scn == null || scn.length() == 0 || scn.equals("java.lang.Object")) return null; //$NON-NLS-1$
		String[][] rs = type.resolveType(scn);
		if(rs == null || rs.length == 0) return null;
		String st = (rs[0][0].length() == 0) ? rs[0][1] : rs[0][0] + "." + rs[0][1]; //$NON-NLS-1$
		IJavaProject p = type.getJavaProject();
		IType stype = null;
		try {
			if(p != null) stype = p.findType(st);
		} catch (JavaModelException e) {
			//ignore
		}
		return (stype == null) ? null : getJavaProperties(stype);
	}

	public static IJavaProject getJavaProject(XModelObject context) {
		if(context == null) return null;
		IResource r = EclipseResourceUtil.getResource(context);
		if(r == null) return null;
		IProject project = r.getProject();
		return EclipseResourceUtil.getJavaProject(project);
	}
	
	public static IMethod findGetter(IType type, String property) throws JavaModelException {
		IMethod[] ms = type.getMethods();
		for (int i = 0; i < ms.length; i++) {
			if(!BeanUtil.isGetter(ms[i])) continue;
			String n = BeanUtil.getPropertyName(ms[i].getElementName());
			if(n != null && n.equals(property)) return ms[i];
		}
		return null;
	}

	public static IMethod findSetter(IType type, String property) throws JavaModelException {
		IMethod[] ms = type.getMethods();
		for (int i = 0; i < ms.length; i++) {
			if(!BeanUtil.isSetter(ms[i])) continue;
			String n = BeanUtil.getPropertyName(ms[i].getElementName());
			if(n != null && n.equals(property)) return ms[i];
		}
		return null;
	}

}
