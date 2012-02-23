/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.jboss.tools.seam.internal.core.scanner.lib;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.env.EnumConstantSignature;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryElementValuePair;
import org.eclipse.jdt.internal.compiler.env.IBinaryField;
import org.eclipse.jdt.internal.compiler.env.IBinaryMethod;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.jboss.tools.common.model.project.ext.IValueInfo;
import org.jboss.tools.common.model.project.ext.impl.ValueInfo;
import org.jboss.tools.seam.core.BeanType;
import org.jboss.tools.seam.core.BijectedAttributeType;
import org.jboss.tools.seam.core.SeamComponentMethodType;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.AbstractContextVariable;
import org.jboss.tools.seam.internal.core.BijectedAttribute;
import org.jboss.tools.seam.internal.core.DataModelSelectionAttribute;
import org.jboss.tools.seam.internal.core.SeamAnnotatedFactory;
import org.jboss.tools.seam.internal.core.SeamComponentMethod;
import org.jboss.tools.seam.internal.core.SeamJavaComponentDeclaration;
import org.jboss.tools.seam.internal.core.SeamMessages;
import org.jboss.tools.seam.internal.core.scanner.LoadedDeclarations;
import org.jboss.tools.seam.internal.core.scanner.Util;
import org.jboss.tools.seam.internal.core.scanner.java.SeamAnnotations;

public class TypeScanner implements SeamAnnotations {

	/**
	 * Checks if class may be a source of seam components. 
	 * @param f
	 * @return
	 */
	public boolean isLikelyComponentSource(ClassFileReader cls) {
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
	public LoadedDeclarations parse(IType type, ClassFileReader cls, IPath path) {
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
	boolean isSeamAnnotatedClass(ClassFileReader cls) {
		if(cls == null || ((cls.getModifiers() & ClassFileConstants.AccInterface) > 0)) return false;
		IBinaryAnnotation[] as = cls.getAnnotations();
		if(as != null) for (int i = 0; i < as.length; i++) {
			String type = getTypeName(as[i]);
			if(Util.isSeamAnnotationType(type)) {
				return true;
			}
		}
		return false;
	}
	
	public static String getTypeName(IBinaryAnnotation a) {
		if(a.getTypeName() == null) return ""; //$NON-NLS-1$
		String t = new String(a.getTypeName());
		if(t.startsWith("L") && t.endsWith(";")) { //$NON-NLS-1$ //$NON-NLS-2$
			t = t.substring(1, t.length() - 1);
		}
		t = t.replace('/', '.');
		
		return t;
	}

	Map<String,IBinaryAnnotation> getSeamAnnotations(IBinaryAnnotation[] as) {
		if(as == null || as.length == 0) return null;
		Map<String,IBinaryAnnotation> map = null;
		for (int i = 0; i < as.length; i++) {
			String type = getTypeName(as[i]);
			if(Util.isSeamAnnotationType(type)) {
				if(map == null) map = new HashMap<String, IBinaryAnnotation>();
				map.put(type, as[i]);
			}
		}
		return map;
	}

	private void process(ClassFileReader cls, SeamJavaComponentDeclaration component, LoadedDeclarations ds) {
		Map<String, IBinaryAnnotation> map = getSeamAnnotations(cls.getAnnotations());
		if(map != null) {
			IBinaryAnnotation a = map.get(NAME_ANNOTATION_TYPE);
			if(a != null) {
				String name = (String)getValue(a, "value"); //$NON-NLS-1$
				if(name != null) component.setName(name);
			}
			a = map.get(SCOPE_ANNOTATION_TYPE);
			if(a != null) {
				Object scope = getValue(a, "value"); //$NON-NLS-1$
				if(scope != null) component.setScope(scope.toString());
			}
			a = map.get(INSTALL_ANNOTATION_TYPE);
			if(a != null) {
				String precedence = getValue(a, "precedence"); //$NON-NLS-1$
				try {
					int i = Integer.parseInt(precedence);
					component.setPrecedence(i);
				} catch (NumberFormatException e) {
					//ignore
				}
			}
		}
		
		Map<BeanType, IValueInfo> types = new HashMap<BeanType, IValueInfo>();
		for (int i = 0; i < BeanType.values().length; i++) {
			BeanType t = BeanType.values()[i];
			IBinaryAnnotation a = map.get(t.getAnnotationType());
			if(a != null) {
				ValueInfo v = new ValueInfo();
				v.setValue("true"); //$NON-NLS-1$
				types.put(t, v);
			}
		}
		if(!types.isEmpty()) {
			component.setTypes(types);
		}

		
		IBinaryMethod[] ms = null;
		try {
			ms = cls.getMethods();
		} catch (NoClassDefFoundError e) {
			//ignore
		}
		if(ms != null) for (int i = 0; i < ms.length; i++) {
			process(ms[i], component, ds);
		}
		
//		IBinaryField[] fs = null;
//		try {
//			fs = cls.getFields();
//		} catch (NoClassDefFoundError e) {
//			//ignore
//		}
//		if(fs != null) for (int i = 0; i < fs.length; i++) {
//			//TODO
//		}
	}

	private void process(IBinaryMethod m, SeamJavaComponentDeclaration component, LoadedDeclarations ds) {
		Map<String,IBinaryAnnotation> map = getSeamAnnotations(m.getAnnotations());
		if(map == null || map.isEmpty()) return;
		IBinaryAnnotation a = map.get(FACTORY_ANNOTATION_TYPE);
		if(a != null) {
			processFactory(m, a, component, ds);
		}
		processBijection(m, map, component, ds);
		processComponentMethod(m, map, component, ds);
	}
	
	private void processFactory(IBinaryMethod m, IBinaryAnnotation a, SeamJavaComponentDeclaration component, LoadedDeclarations ds) {
		if(a == null) return;
		String name = (String)getValue(a, "value"); //$NON-NLS-1$
		if(name == null || name.length() == 0) {
			name = new String(m.getSelector());
		}
		SeamAnnotatedFactory factory = 
			("org.jboss.seam.international.messages".equals(name) && new String(m.getSelector()).equals("getMessages")) 
			? new SeamMessages()
			: new SeamAnnotatedFactory();
		factory.setParentDeclaration(component);
		factory.setSourcePath(component.getSourcePath());
		ds.getFactories().add(factory);
		IMethod im = findIMethod(component, m);
		
		factory.setId(im);
		factory.setSourceMember(im);
		factory.setName(name);
			
		Object scope = getValue(a, "scope"); //$NON-NLS-1$
		if(scope != null) factory.setScopeAsString(scope.toString());
		Object autoCreate = getValue(a, "autoCreate"); //$NON-NLS-1$
		if(autoCreate instanceof Boolean) {
			factory.setAutoCreate((Boolean)autoCreate);
		}
	}
	
	private void processBijection(IBinaryMethod m, Map<String,IBinaryAnnotation> map, SeamJavaComponentDeclaration component, LoadedDeclarations ds) {
		Map<BijectedAttributeType, IBinaryAnnotation> as = new HashMap<BijectedAttributeType, IBinaryAnnotation>();
		List<BijectedAttributeType> types = new ArrayList<BijectedAttributeType>();
		IBinaryAnnotation main = null;
		for (int i = 0; i < BijectedAttributeType.values().length; i++) {
			IBinaryAnnotation a = map.get(BijectedAttributeType.values()[i].getAnnotationType());
			if(a != null) {
				as.put(BijectedAttributeType.values()[i], a);
				if(main == null) main = a;
				types.add(BijectedAttributeType.values()[i]);
			}
		}
		if(as.isEmpty()) return;
		
		boolean isDataModelSelectionType = !types.get(0).isUsingMemberName();

		BijectedAttribute att = (!isDataModelSelectionType)
			? new BijectedAttribute() : new DataModelSelectionAttribute();
		component.addBijectedAttribute(att);

		att.setTypes(types.toArray(new BijectedAttributeType[0]));

		String name = (String)getValue(main, "value"); //$NON-NLS-1$
		att.setValue(name);
		if(name == null || name.length() == 0 || isDataModelSelectionType) {
			name = new String(m.getSelector());
		}
		att.setName(name);
		Object scope = getValue(main, "scope"); //$NON-NLS-1$
		if(scope != null) att.setScopeAsString(scope.toString());

		IMember im = findIMethod(component, m);
		att.setSourceMember(im);
		
	}

	void processComponentMethod(IBinaryMethod m, Map<String,IBinaryAnnotation> map, SeamJavaComponentDeclaration component, LoadedDeclarations ds) {
		SeamComponentMethod cm = null;
		for (int i = 0; i < SeamComponentMethodType.values().length; i++) {
			SeamComponentMethodType type = SeamComponentMethodType.values()[i];
			IBinaryAnnotation a = map.get(type.getAnnotationType());
			if(a == null) continue;
			if(cm == null) {
				cm = new SeamComponentMethod();
				component.addMethod(cm);
				IMethod im = findIMethod(component, m);
				cm.setSourceMember(im);
				cm.setId(im);
			}
			cm.getTypes().add(type);
		}
	}

	public static String getValue(IBinaryAnnotation a, String method) {
		try {
			IBinaryElementValuePair[] ps = a.getElementValuePairs();
			if(ps != null) for (int i = 0; i < ps.length; i++) {
				if(method.equals(new String(ps[i].getName()))) {
					Object v = ps[i].getValue();
					if(v == null) return null;
					if(v instanceof EnumConstantSignature) {
						EnumConstantSignature cs = (EnumConstantSignature)v;
						char[] cv = cs.getEnumConstantName();
						return cv == null ? null : new String(cv);
					} else if(v instanceof Constant) {
						Constant ic = (Constant)v;
						return ic.stringValue();
					}
					v = v.toString();
					return (String)v;
				}
			}
		} catch (Throwable e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
		return null;
	}

	private IMethod findIMethod(SeamJavaComponentDeclaration component, IBinaryMethod m) {
		String name = new String(m.getSelector());
		IType type = (IType)component.getSourceMember();
		String signature = new String(m.getMethodDescriptor());
		IMethod im = null;
		IMethod[] ms = null;
		try {
			ms = type.getMethods();
		} catch (JavaModelException e) {
			SeamCorePlugin.getDefault().logError(e);
		}
		if(ms != null) for (int i = 0; i < ms.length; i++) {
			if(!ms[i].getElementName().equals(name)) continue;
			//check parameters
			try {
				if(ms[i].getParameterNames().length != m.getArgumentNames().length) continue;
			} catch (JavaModelException e) {
				continue;
			}
			//compare
			return ms[i];
		}
		return null;
	}

	private IField findIField(SeamJavaComponentDeclaration component, Field m) {
		IType type = (IType)component.getSourceMember();
		return type.getField(m.getName());
	}

}
