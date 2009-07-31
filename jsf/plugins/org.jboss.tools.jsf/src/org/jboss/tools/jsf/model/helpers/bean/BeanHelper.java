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
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

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
			String n = ms[i].getElementName();
			if(!n.startsWith("get") || n.length() < 4) continue; //$NON-NLS-1$
			n = toPropertyName(n.substring(3));
			if(!map.containsKey(n)) map.put(n, ms[i]);
		}
		Map<String,IJavaElement> smap = getSuperTypeJavaProperties(type);
		if(smap != null) map.putAll(smap);
		return map;
	}
	
	private static String toPropertyName(String rootName) {
		return (rootName.toUpperCase().equals(rootName)) ? rootName : rootName.substring(0, 1).toLowerCase() + rootName.substring(1);
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
			String n = ms[i].getElementName();
			if(n.startsWith("get") && n.length() > 3) { //$NON-NLS-1$
				String gn = toPropertyName(n.substring(3));
				if(gn.equals(property)) return ms[i];
			}
			String t = EclipseJavaUtil.resolveTypeAsString(type, ms[i].getReturnType());
			if(n.startsWith("is") && n.length() > 2 && t != null && (t.equals("boolean") || t.equals("java.lang.Boolean"))) {  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				String gn = toPropertyName(n.substring(2));
				if(gn.equals(property)) return ms[i];
			}
		}
		return null;
	}

	public static IMethod findSetter(IType type, String property) throws JavaModelException {
		IMethod[] ms = type.getMethods();
		for (int i = 0; i < ms.length; i++) {
			String n = ms[i].getElementName();
			if(!n.startsWith("set") || n.length() < 4) continue; //$NON-NLS-1$
			n = toPropertyName(n.substring(3));
			if(n.equals(property)) return ms[i];
		}
		return null;
	}

}
