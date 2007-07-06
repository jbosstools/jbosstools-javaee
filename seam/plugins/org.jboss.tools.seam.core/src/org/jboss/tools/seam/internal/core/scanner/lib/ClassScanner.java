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
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.seam.core.BijectedAttributeType;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.AbstractContextVariable;
import org.jboss.tools.seam.internal.core.BijectedAttribute;
import org.jboss.tools.seam.internal.core.SeamAnnotatedFactory;
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
		component.setType(type);
		component.setClassName(type.getFullyQualifiedName());
		process(cls, component, ds);
		
		ds.getComponents().add(component);
		for (int i = 0; i < ds.getFactories().size(); i++) {
			AbstractContextVariable f = (AbstractContextVariable)ds.getFactories().get(i);
			f.setSourcePath(path);
			f.getId();
		}
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
	
	private void process(Class<?> cls, SeamJavaComponentDeclaration component, LoadedDeclarations ds) {
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
		Method[] ms = null;
		try {
			ms = cls.getMethods();
		} catch (NoClassDefFoundError e) {
			//ignore
		}
		if(ms != null) for (int i = 0; i < ms.length; i++) {
			process(ms[i], component, ds);
		}
	}

	private void process(Method m, SeamJavaComponentDeclaration component, LoadedDeclarations ds) {
		Map<String,Annotation> map = getSeamAnnotations(m.getAnnotations());
		if(map == null || map.isEmpty()) return;
		Annotation a = map.get(FACTORY_ANNOTATION_TYPE);
		if(a != null) {
			processFactory(m, a, component, ds);
		}
		Annotation in = map.get(IN_ANNOTATION_TYPE);
		Annotation out = map.get(OUT_ANNOTATION_TYPE);
		if(in != null || out != null) {
			processBijection(m, in, out, component, ds);
		}
	}
	
	private void processFactory(Method m, Annotation a, SeamJavaComponentDeclaration component, LoadedDeclarations ds) {
		if(a == null) return;
		String name = (String)getValue(a, "value");
		if(name == null || name.length() == 0) {
			name = m.getName();
		}
		SeamAnnotatedFactory factory = new SeamAnnotatedFactory();
		ds.getFactories().add(factory);
		IMethod im = findIMethod(component, m);
		
		factory.setId(im);
		factory.setMethod(im);
		factory.setName(name);
			
		Object scope = getValue(a, "scope");
		if(scope != null) factory.setScopeAsString(scope.toString());
		Object autoCreate = getValue(a, "autoCreate");
		if(autoCreate instanceof Boolean) {
			factory.setAutoCreate((Boolean)autoCreate);
		}
	}
	
	private void processBijection(Member m, Annotation in, Annotation out, SeamJavaComponentDeclaration component, LoadedDeclarations ds) {
		if(in == null && out == null) return;
		BijectedAttribute att = new BijectedAttribute();
		component.getBijectedAttributes().add(att);

		BijectedAttributeType[] types = (in == null) ? new BijectedAttributeType[]{BijectedAttributeType.OUT}
			: (out == null) ? new BijectedAttributeType[]{BijectedAttributeType.IN}
			: new BijectedAttributeType[]{BijectedAttributeType.IN, BijectedAttributeType.OUT};
		att.setTypes(types);

		String name = (String)getValue(in != null ? in : out, "value");
		if(name == null || name.length() == 0) {
			name = m.getName();
		}
		att.setName(name);
		Object scope = getValue(in != null ? in : out, "scope");
		if(scope != null) att.setScopeAsString(scope.toString());

		IMember im = findIMember(component, m);
		att.setMember(im);
		
	}

	private Object getValue(Annotation a, String method) {
		try {
			Method m = a.annotationType().getMethod(method, new Class[0]);
			if(m == null) return null;
			return m.invoke(a, new Object[0]);
		} catch (Throwable e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
		return null;
	}

	private IMember findIMember(SeamJavaComponentDeclaration component, Member m) {
		if(m instanceof Field) return findIField(component, (Field)m);
		if(m instanceof Method) return findIMethod(component, (Method)m);
		return null;
	}

	private IMethod findIMethod(SeamJavaComponentDeclaration component, Method m) {
		IType type = (IType)component.getSourceMember();
		Class<?>[] ps = m.getParameterTypes();
		String[] params = new String[ps == null ? 0 : ps.length];
		for (int i = 0; i < ps.length; i++) params[i] = ps[i].getName();
		return type.getMethod(m.getName(), params);
	}

	private IField findIField(SeamJavaComponentDeclaration component, Field m) {
		IType type = (IType)component.getSourceMember();
		return type.getField(m.getName());
	}

}
