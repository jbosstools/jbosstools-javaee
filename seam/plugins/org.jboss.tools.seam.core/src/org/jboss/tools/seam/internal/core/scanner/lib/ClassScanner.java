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
package org.jboss.tools.seam.internal.core.scanner.lib;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.seam.internal.core.SeamJavaComponentDeclaration;
import org.jboss.tools.seam.internal.core.scanner.LoadedDeclarations;
import org.jboss.tools.seam.internal.core.scanner.java.SeamAnnotations;

/**
 * Loads seam components from Class object.
 *  
 * @author Viacheslav Kabanovich
 */
public class ClassScanner implements SeamAnnotations {
	
	/**
	 * Checks if class may be a source of seam components. 
	 * @param f
	 * @return
	 */
	public boolean isLikelyComponentSource(Class<?> cls) {
		return cls != null && isSeamAnnotatedClass(cls);
	}
	
	/**
	 * Loads seam components from class.
	 * Returns object that contains loaded components or null;
	 * @param type
	 * @param cls
	 * @param path
	 * @return
	 */
	public LoadedDeclarations parse(IType type, Class<?> cls, IPath path) {
		if(!isLikelyComponentSource(cls)) return null;
		LoadedDeclarations ds = new LoadedDeclarations();
		
		SeamJavaComponentDeclaration component = new SeamJavaComponentDeclaration();
		component.setSourcePath(path);
		component.setId(type);
		component.setClassName(type.getFullyQualifiedName());
		process(cls, component);
		
		ds.getComponents().add(component);
		return ds;		
	}
	
	/**
	 * Check if class has at least one seam annotation.
	 * @param cls
	 * @return
	 */
	boolean isSeamAnnotatedClass(Class<?> cls) {
		if(cls == null || cls.isInterface()) return false;
		Annotation[] as = cls.getAnnotations();
		for (int i = 0; i < as.length; i++) {
			Class<?> acls = as[i].annotationType();
			if(acls.getName().startsWith(SEAM_ANNOTATION_TYPE_PREFIX)) {
				return true;
			}
		}
		return false;
	}
	
	Map<String,Annotation> getSeamAnnotations(Annotation[] as) {
		if(as == null || as.length == 0) return null;
		Map<String,Annotation> map = null;
		for (int i = 0; i < as.length; i++) {
			Class<?> acls = as[i].annotationType();
			if(acls.getName().startsWith(SEAM_ANNOTATION_TYPE_PREFIX)) {
				if(map == null) map = new HashMap<String, Annotation>();
				map.put(acls.getName(), as[i]);
			}
		}
		return map;
	}
	
	private void process(Class<?> cls, SeamJavaComponentDeclaration component) {
		Map<String, Annotation> map = getSeamAnnotations(cls.getAnnotations());
		if(map != null) {
			Annotation a = map.get(NAME_ANNOTATION_TYPE);
			if(a != null) {
				String name = (String)getValue(a, "value");
				if(name != null) component.setName(name);
			}
			a = map.get(SCOPE_ANNOTATION_TYPE);
			if(a != null) {
				Object scope = getValue(a, "value");
				if(scope != null) component.setScope(scope.toString());
			}
			a = map.get(INSTALL_ANNOTATION_TYPE);
			if(a != null) {
				Object precedence = getValue(a, "precedence");
				if(precedence instanceof Integer) component.setPrecedence((Integer)precedence);
			}
		}
		Method[] ms = cls.getMethods();
		for (int i = 0; i < ms.length; i++) {
			map = getSeamAnnotations(ms[i].getAnnotations());
			if(map == null || map.isEmpty()) continue;
			
			
		}
	}
	
	private Object getValue(Annotation a, String method) {
		try {
			Method m = a.annotationType().getMethod(method, new Class[0]);
			if(m == null) return null;
			return m.invoke(a, new Object[0]);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

}
