/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IDecorator;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInterceptor;
import org.jboss.tools.cdi.core.IInterceptorBinding;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.common.java.IParametedType;

public class CDICache implements Cloneable {
	
	static Collection<IBean> EMPTY = Collections.emptyList();

	private Map<String, StereotypeElement> stereotypes = new HashMap<String, StereotypeElement>();
	private Map<IPath, StereotypeElement> stereotypesByPath = new HashMap<IPath, StereotypeElement>();
	private Map<String, InterceptorBindingElement> interceptorBindings = new HashMap<String, InterceptorBindingElement>();
	private Map<IPath, InterceptorBindingElement> interceptorBindingsByPath = new HashMap<IPath, InterceptorBindingElement>();
	private Map<String, QualifierElement> qualifiers = new HashMap<String, QualifierElement>();
	private Map<IPath, QualifierElement> qualifiersByPath = new HashMap<IPath, QualifierElement>();
	private Map<String, ScopeElement> scopes = new HashMap<String, ScopeElement>();
	private Map<IPath, ScopeElement> scopesByPath = new HashMap<IPath, ScopeElement>();

	private Set<IBean> allBeans = new HashSet<IBean>();
	private Set<IBean> declaredBeans = new HashSet<IBean>();
	private Map<IPath, List<IBean>> beansByPath = new HashMap<IPath, List<IBean>>();
	private Map<String, Set<IBean>> beansByName = new HashMap<String, Set<IBean>>();
	private List<Set<IBean>> beansByTypes = new ArrayList<Set<IBean>>();
	private Set<IBean> namedBeans = new HashSet<IBean>();
	protected Map<IType, IClassBean> classBeans = new HashMap<IType, IClassBean>();
	private Set<IBean> alternatives = new HashSet<IBean>();
	private Set<IDecorator> decorators = new HashSet<IDecorator>();
	private Set<IInterceptor> interceptors = new HashSet<IInterceptor>();

	protected Set<IType> allTypes = new HashSet<IType>();
	protected Map<TypeDefinition, ClassBean> definitionToClassbeans = new HashMap<TypeDefinition, ClassBean>();

	private Map<String, Set<IInjectionPoint>> injectionPointsByType = new HashMap<String, Set<IInjectionPoint>>();

	private int beansByTypeSize;
	private int objectIndex;

	public CDICache() {
		setBeansByTypeSize(21);
	}

	public synchronized void setBeansByTypeSize(int beansByTypeSize) {
		List<Set<IBean>> beansByTypes = new ArrayList<Set<IBean>>();
		for (int i = 0; i < beansByTypeSize; i++) beansByTypes.add(new HashSet<IBean>());
		this.beansByTypes = beansByTypes;
		this.beansByTypeSize = beansByTypeSize;
		objectIndex = Math.abs("java.lang.Object".hashCode()) % beansByTypeSize;
	}

	
	private int toTypeIndex(IType type) {
		return Math.abs(type.getFullyQualifiedName().hashCode()) % beansByTypeSize;
	}

	public synchronized IBean[] getBeans() {
		return allBeans.toArray(new IBean[allBeans.size()]);
	}

	public Collection<IBean> getAllBeans() {
		return allBeans;
	}

	public synchronized Collection<IBean> getDeclaredBeans() {
		return new ArrayList<IBean>(declaredBeans);
	}

	public synchronized IBean[] getBeansByLegalType(IParametedType type) {
		if(type.getType() == null) return new IBean[0];
		int index = toTypeIndex(type.getType());
		Collection<IBean> bs = index == objectIndex ? allBeans : beansByTypes.get(index);
		return bs.toArray(new IBean[bs.size()]);
	}

	public synchronized IQualifier[] getQualifiers() {
		return qualifiers.values().toArray(new IQualifier[qualifiers.size()]);
	}

	public synchronized IStereotype[] getStereotypes() {
		return stereotypes.values().toArray(new IStereotype[stereotypes.size()]);
	}

	public synchronized IBean[] getAlternatives() {
		return alternatives.toArray(new IBean[alternatives.size()]);
	}

	public synchronized IDecorator[] getDecorators() {
		return decorators.toArray(new IDecorator[decorators.size()]);
	}

	public synchronized IInterceptor[] getInterceptors() {
		return interceptors.toArray(new IInterceptor[interceptors.size()]);
	}

	public synchronized StereotypeElement getStereotype(IPath path) {
		return stereotypesByPath.get(path);
	}

	public StereotypeElement getStereotype(String qualifiedName) {
		return stereotypes.get(qualifiedName.replace('$', '.'));
	}

	public synchronized IInterceptorBinding[] getInterceptorBindings() {
		return interceptorBindings.values().toArray(new IInterceptorBinding[interceptorBindings.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getInterceptorBinding(java.lang.String)
	 */
	public InterceptorBindingElement getInterceptorBinding(String qualifiedName) {
		return interceptorBindings.get(qualifiedName.replace('$', '.'));
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getInterceptorBinding(org.eclipse.core.runtime.IPath)
	 */
	public IInterceptorBinding getInterceptorBinding(IPath path) {
		return interceptorBindingsByPath.get(path);
	}

	public QualifierElement getQualifier(String qualifiedName) {
		return qualifiers.get(qualifiedName.replace('$', '.'));
	}

	public QualifierElement getQualifier(IPath path) {
		return qualifiersByPath.get(path);
	}

	public synchronized Set<String> getScopeNames() {
		Set<String> result = new HashSet<String>();
		result.addAll(scopes.keySet());
		return result;
	}

	public ScopeElement getScope(String qualifiedName) {
		return scopes.get(qualifiedName.replace('$', '.'));
	}

	public IScope getScope(IPath path) {
		return scopesByPath.get(path);
	}

	public CDICache getModifiedCopy(IFile file, Collection<IBean> beans) {
		CDICache p = null;
		try {
			p = (CDICache)clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		p.allBeans = new HashSet<IBean>();
		synchronized(this) {
			p.allBeans.addAll(allBeans);
		}
		Collection<IBean> oldBeans = getBeans(file.getFullPath());
		p.allBeans.removeAll(oldBeans);
		p.allBeans.addAll(beans);

		p.beansByTypes = new ArrayList<Set<IBean>>();
		for (int i = 0; i < beansByTypeSize; i++) {
			Set<IBean> bs = new HashSet<IBean>(beansByTypes.get(i));
			bs.removeAll(oldBeans);
			bs.addAll(beans);
			p.beansByTypes.add(bs);
		}
		
		Set<IBean> oldNamedBeans = null;
		for (IBean b: oldBeans) {
			if(b.getName() != null) {
				if(oldNamedBeans == null) oldNamedBeans = new HashSet<IBean>();
				oldNamedBeans.add(b);
			}
		}
		Set<IBean> newNamedBeans = null;
		for (IBean b: beans) {
			if(b.getName() != null) {
				if(newNamedBeans == null) newNamedBeans = new HashSet<IBean>();
				newNamedBeans.add(b);
			}
		}
		if(newNamedBeans != null || oldNamedBeans != null) {
			p.namedBeans = new HashSet<IBean>();
			p.beansByName = new HashMap<String, Set<IBean>>();
			synchronized(this) {
				p.namedBeans.addAll(namedBeans);
				if(oldNamedBeans != null) p.namedBeans.removeAll(oldNamedBeans);
				if(newNamedBeans != null) p.namedBeans.addAll(newNamedBeans);
				for (String n: beansByName.keySet()) {
					Set<IBean> bs = new HashSet<IBean>(beansByName.get(n));
					p.beansByName.put(n, bs);
				}
				if(oldNamedBeans != null) {
					for (IBean b: oldNamedBeans) {
						String n = b.getName();
						Set<IBean> bs = p.beansByName.get(n);
						if(bs != null && bs.contains(b)) {
							bs.remove(b);
						}
					}
				}
				if(newNamedBeans != null) {
					for (IBean b: newNamedBeans) {
						String n = b.getName();
						Set<IBean> bs = p.beansByName.get(n);
						if(bs == null) {
							bs = new HashSet<IBean>();
							p.beansByName.put(n, bs);
						}
						bs.add(b);
					}
				}
			}
		}
		
		return p;
	}

	public synchronized Collection<IBean> getBeans(IPath path) {
		return (beansByPath.containsKey(path)) ? new ArrayList<IBean>(beansByPath.get(path)) : EMPTY;
	}
	
	public Set<IInjectionPoint> getInjections(String fullyQualifiedTypeName) {
		Set<IInjectionPoint> result = injectionPointsByType.get(fullyQualifiedTypeName);
		if(result == null) result = Collections.emptySet();		
		return result;
	}

	public Collection<IBean> getNamedBeans() {
		return namedBeans;
	}

	public boolean containsType(IType t) {
		return allTypes.contains(t);
	}

	public Collection<IType> getAllTypes() {
		return allTypes;
	}

	public Collection<IBean> getBeans(String name) {
		return beansByName.containsKey(name) ? beansByName.get(name) : EMPTY;
	}

	public synchronized void addBean(IBean bean, boolean isDeclaredByThisProject) {
		String name = bean.getName();
		IPath path = bean.getSourcePath();
		List<IBean> bs = beansByPath.get(path);
		if(bs == null) {
			bs = new ArrayList<IBean>();
			beansByPath.put(path, bs);
		}
		bs.add(bean);
		buildInjectionPoinsByType(bean);
		boolean isAbstract = (bean instanceof ClassBean) && !((ClassBean)bean).getDefinition().hasBeanConstructor();
		if(isAbstract) {
			return;
		}
		if(name != null && name.length() > 0) {
			Set<IBean> bsn = beansByName.get(name);
			if(bsn == null) {
				bsn = new HashSet<IBean>();
				beansByName.put(name, bsn);				
			}
			bsn.add(bean);
			namedBeans.add(bean);
		}
		if(bean.isAlternative()) {
			alternatives.add(bean);
		}
		if(bean instanceof IDecorator) {
			decorators.add((IDecorator)bean);
		}
		if(bean instanceof IInterceptor) {
			interceptors.add((IInterceptor)bean);
		}
		if(bean instanceof IClassBean) {
			IClassBean c = (IClassBean)bean;
			IType t = c.getBeanClass();
			if(t != null && !classBeans.containsKey(t)) {
				classBeans.put(t, c);
			}
		}
		allBeans.add(bean);
		if(isDeclaredByThisProject) {
			declaredBeans.add(bean);
		}
		for (IParametedType t: bean.getLegalTypes()) {
			if(t.getType() != null && t.getType().exists()) {
				int index = toTypeIndex(t.getType());
				if(index != objectIndex) {
					beansByTypes.get(index).add(bean);
				}
			}
		}
	}

	synchronized void buildInjectionPoinsByType(IBean b) {
		Collection<IInjectionPoint> ps = b.getInjectionPoints();
		for (IInjectionPoint p: ps) {
			IParametedType t = p.getType();
			if(t == null || t.getType() == null) continue;
			String n = t.getType().getFullyQualifiedName();
			Set<IInjectionPoint> s = injectionPointsByType.get(n);
			if(s == null) {
				s = new HashSet<IInjectionPoint>();
				injectionPointsByType.put(n, s);
			}
			s.add(p);
		}
	}

	public synchronized void clean() {
		beansByPath.clear();
		beansByName.clear();
		namedBeans.clear();
		alternatives.clear();
		decorators.clear();
		interceptors.clear();
		allBeans.clear();
		declaredBeans.clear();
		injectionPointsByType.clear();
		
		for (int i = 0; i < beansByTypeSize; i++) beansByTypes.get(i).clear();
	}

	public synchronized void cleanAnnotations() {
		stereotypes.clear();
		stereotypesByPath.clear();
		interceptorBindings.clear();
		qualifiers.clear();
		qualifiersByPath.clear();
		interceptorBindingsByPath.clear();
		scopes.clear();
		scopesByPath.clear();
	}

	public void add(StereotypeElement s) {
		stereotypes.put(s.getDefinition().getQualifiedName().replace('$', '.'), s);
		if(s.getDefinition().getResource() != null && s.getDefinition().getResource().getFullPath() != null) {
			stereotypesByPath.put(s.getDefinition().getResource().getFullPath(), s);
		}
	}

	public void add(InterceptorBindingElement s) {
		interceptorBindings.put(s.getDefinition().getQualifiedName().replace('$', '.'), s);
		if(s.getDefinition().getResource() != null && s.getDefinition().getResource().getFullPath() != null) {
			interceptorBindingsByPath.put(s.getDefinition().getResource().getFullPath(), s);
		}
	}

	public void add(QualifierElement s) {
		qualifiers.put(s.getDefinition().getQualifiedName().replace('$', '.'), s);
		if(s.getDefinition().getResource() != null && s.getDefinition().getResource().getFullPath() != null) {
			qualifiersByPath.put(s.getDefinition().getResource().getFullPath(), s);
		}
	}

	public void add(ScopeElement s) {
		scopes.put(s.getDefinition().getQualifiedName().replace('$', '.'), s);
		if(s.getDefinition().getResource() != null && s.getDefinition().getResource().getFullPath() != null) {
			scopesByPath.put(s.getDefinition().getResource().getFullPath(), s);
		}
	}
}