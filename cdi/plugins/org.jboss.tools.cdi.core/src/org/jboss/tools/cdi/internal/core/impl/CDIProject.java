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
package org.jboss.tools.cdi.internal.core.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IObserverMethod;
import org.jboss.tools.cdi.core.IParametedType;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.BeansXMLDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.ParametedTypeFactory;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.common.text.INodeReference;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CDIProject extends CDIElement implements ICDIProject {
	CDICoreNature n;

	private Map<String, StereotypeElement> stereotypes = new HashMap<String, StereotypeElement>();
	private Map<IPath, StereotypeElement> stereotypesByPath = new HashMap<IPath, StereotypeElement>();
	private Map<String, InterceptorBindingElement> interceptorBindings = new HashMap<String, InterceptorBindingElement>();
	private Map<String, QualifierElement> qualifiers = new HashMap<String, QualifierElement>();
	private Map<String, ScopeElement> scopes = new HashMap<String, ScopeElement>();

	private Set<IBean> allBeans = new HashSet<IBean>();
	private Map<IPath, Set<IBean>> beansByPath = new HashMap<IPath, Set<IBean>>();
	private Map<String, Set<IBean>> beansByName = new HashMap<String, Set<IBean>>();
	private Set<IBean> namedBeans = new HashSet<IBean>();
	private Map<IType, ClassBean> classBeans = new HashMap<IType, ClassBean>();

	BeansXMLData beansXMLData = new BeansXMLData();

	public CDIProject() {}

	public CDICoreNature getNature() {
		return n;
	}

	public void setNature(CDICoreNature n) {
		this.n = n;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getBeans()
	 */
	public IBean[] getBeans() {
		if(allBeans == null || allBeans.isEmpty()) {
			return new IBean[0];
		}
		IBean[] result = new IBean[allBeans.size()];
		synchronized (allBeans) {
			int i=0;
			for (IBean bean : allBeans) {
				result[i++] = bean;
			}
		}
		return result;
	}

	public List<INodeReference> getAlternativeClasses() {
		List<INodeReference> result = new ArrayList<INodeReference>();
		Set<INodeReference> typeAlternatives = beansXMLData.getTypeAlternatives();
		synchronized (typeAlternatives) {
			result.addAll(typeAlternatives);
		}
		return result;
	}

	public List<INodeReference> getAlternativeStereotypes() {
		List<INodeReference> result = new ArrayList<INodeReference>();
		Set<INodeReference> stereotypeAlternatives = beansXMLData.getStereotypeAlternatives();
		synchronized (stereotypeAlternatives) {
			result.addAll(stereotypeAlternatives);
		}
		return result;
	}

	public boolean isClassAlternativeActivated(String fullQualifiedTypeName) {
		return beansXMLData.getTypeAlternativeTypes().contains(fullQualifiedTypeName);
	}

	public List<INodeReference> getAlternatives(String fullQualifiedTypeName) {
		List<INodeReference> result = new ArrayList<INodeReference>();
		Set<INodeReference> typeAlternatives = beansXMLData.getTypeAlternatives();
		synchronized (typeAlternatives) {
			for (INodeReference r: typeAlternatives) {
				if(fullQualifiedTypeName.equals(r.getValue())) result.add(r);
			}
		}
		Set<INodeReference> stereotypeAlternatives = beansXMLData.getStereotypeAlternatives();
		synchronized (stereotypeAlternatives) {
			for (INodeReference r: stereotypeAlternatives) {
				if(fullQualifiedTypeName.equals(r.getValue())) result.add(r);
			}
		}
		return result;
	}

	public IClassBean getBeanClass(IType type) {
		IPath path = type.getPath();
		if(path != null) {
			Set<IBean> bs = null;
			synchronized (beansByPath) {
				bs = beansByPath.get(path);
			}
			if(bs != null) synchronized(bs) {
				for (IBean b: bs) {
					if(b instanceof IClassBean) {
						IClassBean result = (IClassBean)b;
						if(type.getFullyQualifiedName().equals(result.getBeanClass().getFullyQualifiedName())) {
							return result;
						}
					}
				}
			}
		}
		return null;
	}

	public Set<IBean> getBeans(String name,	boolean attemptToResolveAmbiguousNames) {
		Set<IBean> result = new HashSet<IBean>();
		Set<IBean> beans = beansByName.get(name);
		if(beans == null || beans.isEmpty()) {
			return result;
		}
		synchronized (beans) {
			result.addAll(beans);
		}
		if(result.size() == 1 || !attemptToResolveAmbiguousNames) {
			return result;
		}
		Iterator<IBean> it = result.iterator();
		while(it.hasNext()) {
			IBean bean = it.next();
			if(!bean.isAlternative()) it.remove();
		}
		return result;
	}

	public Set<IBean> getBeans(boolean attemptToResolveAmbiguousDependency,
			IType beanType, IAnnotationDeclaration... qualifiers) {
		// TODO
		return null;
	}

	public Set<IBean> getBeans(IInjectionPoint injectionPoints) {
		Set<IBean> result = new HashSet<IBean>();
		IParametedType type = injectionPoints.getType();
		if(type == null) {
			return result;
		}

		Set<IAnnotationDeclaration> qs = injectionPoints.getQualifierDeclarations();
		
		Set<IBean> beans = new HashSet<IBean>();
		synchronized(allBeans) {
			beans.addAll(allBeans);
		}
		for (IBean b: beans) {
			Set<IParametedType> types = b.getLegalTypes();
			if(containsType(types, type)) {
				try {
					Set<IAnnotationDeclaration> qsb = b.getQualifierDeclarations();
					if(areMatchingQualifiers(qsb, qs)) {
						result.add(b);
					}
				} catch (CoreException e) {
					
				}
			}
		}

		return result;
	}

	public static boolean containsType(Set<IParametedType> types, IParametedType type) {
		for (IParametedType t: types) {
			if(t.equals(type)) return true;
		}
		return false;
	}

	public static boolean areMatchingQualifiers(Set<IAnnotationDeclaration> beanQualifiers, Set<IAnnotationDeclaration> injectionQualifiers) throws CoreException {
		if(beanQualifiers == null || beanQualifiers.isEmpty()) {
			if(injectionQualifiers == null || injectionQualifiers.isEmpty()) {
				return true;
			}
		}

		TreeSet<String> injectionKeys = new TreeSet<String>();
		if(injectionQualifiers != null) for (IAnnotationDeclaration d: injectionQualifiers) {
			injectionKeys.add(getQualifierDeclarationKey(d));
		}

		if(injectionKeys.contains(CDIConstants.ANY_QUALIFIER_TYPE_NAME)) {
			return true;
		}
		if(injectionKeys.isEmpty()) {
			injectionKeys.add(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME);
		}

		TreeSet<String> beanKeys = new TreeSet<String>();
		if(beanQualifiers == null || beanQualifiers.isEmpty()) {
			beanKeys.add(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME);
		} else for (IAnnotationDeclaration d: beanQualifiers) {
			beanKeys.add(getQualifierDeclarationKey(d));
		}
		if(beanKeys.size() == 1 && beanKeys.iterator().next().startsWith(CDIConstants.NAMED_QUALIFIER_TYPE_NAME)) {
			beanKeys.add(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME);
		}

		for(String k: injectionKeys) {
			if(!beanKeys.contains(k)) return false;
		}		
		return true;
	}

	static String getQualifierDeclarationKey(IAnnotationDeclaration d) throws CoreException {
		IType type = d.getType();
		IMethod[] ms = type.getMethods();
		StringBuffer result = new StringBuffer();
			result.append(type.getFullyQualifiedName());
		if(ms != null && ms.length > 0) {
			TreeMap<String, String> values = new TreeMap<String, String>();
			IMemberValuePair[] ps = d.getDeclaration().getMemberValuePairs();
			if (ps != null) for (IMemberValuePair p: ps) {
				String n = p.getMemberName();
				Object o = p.getValue();
				if(o != null) {
					values.put(n, o.toString());
				}
			}
			for (IMethod m: ms) {
				String n = m.getElementName();
				if(values.containsKey(n)) continue;
				IMemberValuePair p = m.getDefaultValue();
				n = p.getMemberName();
				Object o = p.getValue();
				if(values.containsKey(n) || o == null) continue;
				values.put(n, o.toString());
			}
			for (String n: values.keySet()) {
				String v = values.get(n);
				result.append(';').append(n).append('=').append(v);
			}
		}		
		return result.toString();
	}

	public Set<IBean> getBeans(IPath path) {
		Set<IBean> result = new HashSet<IBean>();
		Set<IBean> beans = beansByPath.get(path);
		if(beans != null && !beans.isEmpty()) result.addAll(beans);
		return result;
	}

	public List<INodeReference> getDecoratorClasses() {
		List<INodeReference> result = new ArrayList<INodeReference>();
		Set<INodeReference> decorators = beansXMLData.getDecorators();
		synchronized (decorators) {
			result.addAll(decorators);
		}
		return result;
	}

	public List<INodeReference> getDecoratorClasses(String fullQualifiedTypeName) {
		List<INodeReference> result = new ArrayList<INodeReference>();
		Set<INodeReference> decorators = beansXMLData.getDecorators();
		synchronized (decorators) {
			for (INodeReference r: decorators) {
				if(fullQualifiedTypeName.equals(r.getValue())) result.add(r);
			}
		}
		return result;
	}

	public List<INodeReference> getInterceptorClasses() {
		List<INodeReference> result = new ArrayList<INodeReference>();
		Set<INodeReference> interceptors = beansXMLData.getInterceptors();
		synchronized (interceptors) {
			result.addAll(interceptors);
		}
		return result;
	}

	public List<INodeReference> getInterceptorClasses(
			String fullQualifiedTypeName) {
		List<INodeReference> result = new ArrayList<INodeReference>();
		Set<INodeReference> interceptors = beansXMLData.getInterceptors();
		synchronized (interceptors) {
			for (INodeReference r: interceptors) {
				if(fullQualifiedTypeName.equals(r.getValue())) result.add(r);
			}
		}
		return result;
	}

	public Set<IType> getQualifierTypes() {
		Set<IType> result = new HashSet<IType>();
		List<AnnotationDefinition> ds = n.getDefinitions().getAllAnnotations();
		for (AnnotationDefinition d: ds) {
			if(d.getKind() == AnnotationDefinition.QUALIFIER) {
				result.add(d.getType());
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getStereotypes()
	 */
	public IStereotype[] getStereotypes() {
		IStereotype[] result = new IStereotype[stereotypes.size()];
		synchronized (stereotypes) {
			int i=0;
			for (IStereotype s: stereotypes.values()) {
				result[i++] = s;
			}
		}
		return result;
	}

	public boolean isNormalScope(IType annotationType) {
		if(annotationType == null) return false;
		try {
			if(!annotationType.isAnnotation()) return false;
		} catch (CoreException e) {
			return false;
		}
		AnnotationDefinition d = n.getDefinitions().getAnnotation(annotationType);
		List<AnnotationDeclaration> ds = d.getAnnotations();
		for (AnnotationDeclaration a: ds) {
			if(CDIConstants.NORMAL_SCOPE_ANNOTATION_TYPE_NAME.equals(a.getTypeName())) {
				return true;
			}
		}		
		return false;
	}

	public boolean isPassivatingScope(IType annotationType) {
		if(annotationType == null) return false;
		try {
			if(!annotationType.isAnnotation()) return false;
		} catch (CoreException e) {
			return false;
		}
		AnnotationDefinition d = n.getDefinitions().getAnnotation(annotationType);
		List<AnnotationDeclaration> ds = d.getAnnotations();
		for (AnnotationDeclaration a: ds) {
			if(CDIConstants.NORMAL_SCOPE_ANNOTATION_TYPE_NAME.equals(a.getTypeName())) {
				IAnnotation ann = a.getDeclaration();
				try {
					IMemberValuePair[] ps = ann.getMemberValuePairs();
					if(ps != null) for (IMemberValuePair p: ps) {
						if("passivating".equals(p.getMemberName())) {
							Object o = p.getValue();
							return o != null && "true".equalsIgnoreCase(o.toString());
						}
					}
				} catch (JavaModelException e) {
					
				}
				return true;
			}
		}		
		return false;
	}

	public boolean isQualifier(IType annotationType) {
		if(annotationType == null) return false;
		try {
			if(!annotationType.isAnnotation()) return false;
		} catch (CoreException e) {
			return false;
		}
		int k = n.getDefinitions().getAnnotationKind(annotationType);
		
		return k == AnnotationDefinition.QUALIFIER;
	}

	public boolean isScope(IType annotationType) {
		if(annotationType == null) return false;
		try {
			if(!annotationType.isAnnotation()) return false;
		} catch (CoreException e) {
			return false;
		}
		int k = n.getDefinitions().getAnnotationKind(annotationType);
		
		return k == AnnotationDefinition.SCOPE;
	}

	public boolean isStereotype(IType annotationType) {
		if(annotationType == null) return false;
		try {
			if(!annotationType.isAnnotation()) return false;
		} catch (CoreException e) {
			return false;
		}
		int k = n.getDefinitions().getAnnotationKind(annotationType);
		
		return k == AnnotationDefinition.STEREOTYPE;
	}

	public Set<IBean> resolve(Set<IBean> beans) {
		if(beans.size() <= 1) {
			return beans;
		}
		Set<IBean> result = new HashSet<IBean>();
		for (IBean bean: beans) {
			if(bean.isAlternative()) {
				IType type = bean.getBeanClass(); // ?
			}
		}
		// TODO 
		return result;
	}

	public Set<IObserverMethod> resolveObserverMethods(
			IInjectionPoint injectionPoint) {
		// TODO 
		return new HashSet<IObserverMethod>();
	}

	public CDIProject getCDIProject() {
		return this;
	}

	public IResource getResource() {
		return n.getProject();
	}

	public IPath getSourcePath() {
		return n.getProject().getFullPath();
	}

	public StereotypeElement getStereotype(String qualifiedName) {
		return stereotypes.get(qualifiedName);
	}

	public StereotypeElement getStereotype(IPath path) {
		return stereotypesByPath.get(path);
	}

	public InterceptorBindingElement getInterceptorBinding(String qualifiedName) {
		return interceptorBindings.get(qualifiedName);
	}

	public QualifierElement getQualifier(String qualifiedName) {
		return qualifiers.get(qualifiedName);
	}

	public ScopeElement getScope(String qualifiedName) {
		return scopes.get(qualifiedName);
	}

	public void update() {
		rebuildXML();
		rebuildAnnotationTypes();
		rebuildBeans();
	}

	void rebuildAnnotationTypes() {
		stereotypes.clear();
		stereotypesByPath.clear();
		interceptorBindings.clear();
		qualifiers.clear();
		scopes.clear();
		List<AnnotationDefinition> ds = n.getDefinitions().getAllAnnotations();
		for (AnnotationDefinition d: ds) {
			if(d.getKind() == AnnotationDefinition.STEREOTYPE) {
				StereotypeElement s = new StereotypeElement();
				initAnnotationElement(s, d);
				stereotypes.put(d.getQualifiedName(), s);
				if(d.getResource() != null && d.getResource().getFullPath() != null) {
					stereotypesByPath.put(d.getResource().getFullPath(), s);
				}
			} else if(d.getKind() == AnnotationDefinition.INTERCEPTOR_BINDING) {
				InterceptorBindingElement s = new InterceptorBindingElement();
				initAnnotationElement(s, d);
				interceptorBindings.put(d.getQualifiedName(), s);
			} else if(d.getKind() == AnnotationDefinition.QUALIFIER) {
				QualifierElement s = new QualifierElement();
				initAnnotationElement(s, d);
				qualifiers.put(d.getQualifiedName(), s);
			} else if(d.getKind() == AnnotationDefinition.SCOPE) {
				ScopeElement s = new ScopeElement();
				initAnnotationElement(s, d);
				scopes.put(d.getQualifiedName(), s);
			}
		}
	}

	private void initAnnotationElement(CDIAnnotationElement s, AnnotationDefinition d) {
		s.setDefinition(d);
		s.setParent(this);
		IPath r = d.getType().getPath();
		if(r != null) {
			s.setSourcePath(r);
		}
	}

	void rebuildBeans() {
		List<TypeDefinition> typeDefinitions = n.getDefinitions().getTypeDefinitions();
		List<IBean> beans = new ArrayList<IBean>();
		Map<IType, ClassBean> newClassBeans = new HashMap<IType, ClassBean>();

		for (TypeDefinition typeDefinition : typeDefinitions) {
			ClassBean bean = null;
			if(typeDefinition.getInterceptorAnnotation() != null) {
				bean = new InterceptorBean();
			} else if(typeDefinition.getDecoratorAnnotation() != null) {
				bean = new DecoratorBean();
			} else if(typeDefinition.getStatefulAnnotation() != null || typeDefinition.getStatelessAnnotation() != null) {
				bean = new SessionBean();
			} else {
				bean = new ClassBean();
			}
			bean.setParent(this);
			bean.setDefinition(typeDefinition);

			beans.add(bean);
			newClassBeans.put(typeDefinition.getType(), bean);

			Set<IProducer> ps = bean.getProducers();
			for (IProducer producer: ps) {
				beans.add(producer);
			}
		}
	
		for (ClassBean bean: newClassBeans.values()) {
			ParametedType s = bean.getDefinition().getSuperType();
			if(s != null && s.getType() != null) {
				ClassBean superClassBean = newClassBeans.get(s.getType());
				if(superClassBean != null) {
					bean.setSuperClassBean(superClassBean);
				}
			}
		}
	

		synchronized (beansByPath) {
			beansByPath.clear();
		}
		synchronized (beansByName) {
			beansByName.clear();
		}
		synchronized (namedBeans) {
			namedBeans.clear();
		}
		synchronized (allBeans) {
			allBeans.clear();
		}

		classBeans = newClassBeans;
		for (IBean bean: beans) {
			addBean(bean);
		}
		
		System.out.println("Project=" + getNature().getProject());
		System.out.println("Qualifiers=" + qualifiers.size());
		System.out.println("Stereotypes=" + stereotypes.size());
		System.out.println("Scopes=" + scopes.size());
		System.out.println("Named beans=" + beansByName.size());
		System.out.println("Bean paths=" + beansByPath.size());
	}

	void addBean(IBean bean) {
		String name = bean.getName();
		if(name != null && name.length() > 0) {
			Set<IBean> bs = beansByName.get(name);
			if(bs == null) {
				bs = new HashSet<IBean>();
				synchronized (beansByName) {
					beansByName.put(name, bs);				
				}
			}
			synchronized (bs) {
				bs.add(bean);
			}
			synchronized (namedBeans) {
				namedBeans.add(bean);
			}
		}
		IPath path = bean.getSourcePath();
		Set<IBean> bs = beansByPath.get(path);
		if(bs == null) {
			bs = new HashSet<IBean>();
			synchronized (beansByPath) {
				beansByPath.put(path, bs);
			}
		}
		synchronized (bs) {
			bs.add(bean);
		}
		synchronized (allBeans) {
			allBeans.add(bean);
		}
	}

	void rebuildXML() {
		beansXMLData.clean();
		Set<BeansXMLDefinition> beanXMLs = n.getDefinitions().getBeansXMLDefinitions();
		for (BeansXMLDefinition b: beanXMLs) {
			for (INodeReference r: b.getInterceptors()) {
				beansXMLData.addInterceptor(r);
			}
			for (INodeReference r: b.getDecorators()) {
				beansXMLData.addDecorator(r);
			}
			for (INodeReference r: b.getStereotypeAlternatives()) {
				beansXMLData.addStereotypeAlternative(r);
			}
			for (INodeReference r: b.getTypeAlternatives()) {
				beansXMLData.addTypeAlternative(r);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getNamedBeans(boolean)
	 */
	public Set<IBean> getNamedBeans(boolean attemptToResolveAmbiguousNames) {
		//TODO use a cache for named beans with attemptToResolveAmbiguousNames==true
		Set<IBean> result = new HashSet<IBean>();
		synchronized (namedBeans) {
			if(attemptToResolveAmbiguousNames) {
				Set<String> names = new HashSet<String>();
				for (IBean bean : namedBeans) {
					if(names.contains(bean.getName())) {
						continue;
					}
					Set<IBean> beans = getBeans(bean.getName(), attemptToResolveAmbiguousNames);
					if(beans.isEmpty()) {
						result.add(bean);
					} else {
						result.addAll(beans);
						names.add(bean.getName());
					}
				}
			} else {
				result.addAll(namedBeans);
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getBeans(boolean, org.eclipse.jdt.core.IType, org.eclipse.jdt.core.IType[])
	 */
	public Set<IBean> getBeans(boolean attemptToResolveAmbiguousDependency,
			IType beanType, IType... qualifiers) {
		// TODO
		return null;
	}
}