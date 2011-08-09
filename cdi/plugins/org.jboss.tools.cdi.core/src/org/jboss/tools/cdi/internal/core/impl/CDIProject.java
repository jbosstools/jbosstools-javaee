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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanMember;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.IBuiltInBean;
import org.jboss.tools.cdi.core.ICDIAnnotation;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IDecorator;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IInterceptor;
import org.jboss.tools.cdi.core.IInterceptorBinding;
import org.jboss.tools.cdi.core.IObserverMethod;
import org.jboss.tools.cdi.core.IParameter;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.IProducerMethod;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.core.extension.feature.IAmbiguousBeanResolverFeature;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.BeansXMLDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.DefinitionContext;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.cdi.internal.core.scanner.ImplementationCollector;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.java.ParametedType;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
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
	private Map<IPath, InterceptorBindingElement> interceptorBindingsByPath = new HashMap<IPath, InterceptorBindingElement>();
	private Map<String, QualifierElement> qualifiers = new HashMap<String, QualifierElement>();
	private Map<IPath, QualifierElement> qualifiersByPath = new HashMap<IPath, QualifierElement>();
	private Map<String, ScopeElement> scopes = new HashMap<String, ScopeElement>();
	private Map<IPath, ScopeElement> scopesByPath = new HashMap<IPath, ScopeElement>();

	private Set<IBean> allBeans = new HashSet<IBean>();
	private Map<IPath, Set<IBean>> beansByPath = new HashMap<IPath, Set<IBean>>();
	private Map<String, Set<IBean>> beansByName = new HashMap<String, Set<IBean>>();
	private Set<IBean> namedBeans = new HashSet<IBean>();
	private Map<IType, IClassBean> classBeans = new HashMap<IType, IClassBean>();
	private Set<IBean> alternatives = new HashSet<IBean>();
	private Set<IDecorator> decorators = new HashSet<IDecorator>();
	private Set<IInterceptor> interceptors = new HashSet<IInterceptor>();

	private Map<String, Set<IInjectionPoint>> injectionPointsByType = new HashMap<String, Set<IInjectionPoint>>();

	BeansXMLData allBeansXMLData = new BeansXMLData();
	BeansXMLData projectBeansXMLData = new BeansXMLData();

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
		if(allBeans.isEmpty()) {
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
		Set<INodeReference> typeAlternatives = allBeansXMLData.getTypeAlternatives();
		synchronized (typeAlternatives) {
			result.addAll(typeAlternatives);
		}
		return result;
	}

	public List<INodeReference> getAlternativeStereotypes() {
		List<INodeReference> result = new ArrayList<INodeReference>();
		Set<INodeReference> stereotypeAlternatives = allBeansXMLData.getStereotypeAlternatives();
		synchronized (stereotypeAlternatives) {
			result.addAll(stereotypeAlternatives);
		}
		return result;
	}

	/**
	 * Selected in at least one bean archive
	 * 
	 * @param fullQualifiedTypeName
	 * @return
	 */
	public boolean isClassAlternativeActivated(String fullQualifiedTypeName) {
		return allBeansXMLData.getTypeAlternativeTypes().contains(fullQualifiedTypeName);
	}

	public List<INodeReference> getAlternatives(String fullQualifiedTypeName) {
		List<INodeReference> result = new ArrayList<INodeReference>();
		Set<INodeReference> typeAlternatives = allBeansXMLData.getTypeAlternatives();
		synchronized (typeAlternatives) {
			for (INodeReference r: typeAlternatives) {
				if(fullQualifiedTypeName.equals(r.getValue())) result.add(r);
			}
		}
		Set<INodeReference> stereotypeAlternatives = allBeansXMLData.getStereotypeAlternatives();
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
			if(bs != null) { 
				synchronized(bs) {
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
		return getResolvedBeans(result, attemptToResolveAmbiguousNames);
	}

	public Set<IBean> getResolvedBeans(Set<IBean> result, boolean attemptToResolveAmbiguousness) {
		if(result.size() > 1) {
			Iterator<IBean> it = result.iterator();
			while(it.hasNext()) {
				IBean b = it.next();
				if(b instanceof IBuiltInBean) {
					it.remove();
				}
			}
		}
		if(result.size() < 1 || !attemptToResolveAmbiguousness) {
			return result;
		}
		
		boolean containsAlternatives = false;
		Iterator<IBean> it = result.iterator();
		Set<IBean> disabled = null;
		while(it.hasNext()) {
			IBean b = it.next();
			if(!b.isEnabled() || b instanceof IDecorator || b instanceof IInterceptor) {
				it.remove();
				continue;
			}
			if(b.isAlternative()) {
				if(b.isSelectedAlternative()) {
					containsAlternatives = true;
				} else {
					it.remove();
				}
			}
			IBean bean = b.getSpecializedBean();
			if(bean!=null && b.isEnabled()) {
				if(disabled==null) {
					disabled = new HashSet<IBean>();
				}
				disabled.add(bean);
			}
		}

		if(disabled!=null) {
			result.removeAll(disabled);
		}

		if(containsAlternatives) {
			it = result.iterator();
			while(it.hasNext()) {
				IBean bean = it.next();
				if(bean.isAlternative()) continue;
				if(bean instanceof IProducer && bean instanceof IBeanMember) {
					IBeanMember p = (IBeanMember)bean;
					if(p.getClassBean() != null && p.getClassBean().isAlternative()) continue;
				}
				it.remove();
			}
		}
		
		if(result.size() > 1) {
			Set<IAmbiguousBeanResolverFeature> extensions = getExtensionManager().getAmbiguousBeanResolverFeatures();
			for (IAmbiguousBeanResolverFeature e: extensions) {
				result = e.getResolvedBeans(result);
				if(result.size() < 2) return result;
			}
		}

		return result;
	}

	public Set<IBean> getBeans(boolean attemptToResolveAmbiguousDependency,
			IParametedType beanType, IQualifierDeclaration... qualifiers) {
		Set<IBean> result = new HashSet<IBean>();
		IParametedType type = beanType;
		if(type == null) {
			return result;
		}

		Set<IQualifierDeclaration> qs = new HashSet<IQualifierDeclaration>();
		if(qualifiers != null) for (IQualifierDeclaration d: qualifiers) qs.add(d);
		
		Set<IBean> beans = new HashSet<IBean>();
		synchronized(allBeans) {
			beans.addAll(allBeans);
		}
		for (IBean b: beans) {
			Set<IParametedType> types = b.getLegalTypes();
			if(containsType(types, type)) {
				try {
					Set<IQualifierDeclaration> qsb = b.getQualifierDeclarations(true);
					if(areMatchingQualifiers(qsb, qs)) {
						result.add(b);
					}
				} catch (CoreException e) {
					CDICorePlugin.getDefault().logError(e);
				}
			}
		}

		return getResolvedBeans(result, attemptToResolveAmbiguousDependency);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getBeans(boolean, org.jboss.tools.cdi.core.IInjectionPoint)
	 */
	public Set<IBean> getBeans(boolean attemptToResolveAmbiguousDependency, IInjectionPoint injectionPoint) {
		if(injectionPoint.getDeclaringProject() != this) {
			return injectionPoint.getDeclaringProject().getBeans(attemptToResolveAmbiguousDependency, injectionPoint);
		}
		Set<IBean> result = new HashSet<IBean>();
		IParametedType type = injectionPoint.getType();
		if(type == null) {
			return result;
		}
	
		if(type.getType() != null && CDIConstants.EVENT_TYPE_NAME.equals(type.getType().getFullyQualifiedName())) {
			List<? extends IParametedType> ps = type.getParameters();
			if(ps.size() == 1) {
				EventBean eventBean = new EventBean(type, injectionPoint);
				eventBean.setParent(this);
				eventBean.setSourcePath(injectionPoint.getSourcePath());
				result.add(eventBean);
				return result;
			}
		}
		
		if(type.getType() != null && CDIConstants.INSTANCE_TYPE_NAME.equals(type.getType().getFullyQualifiedName())) {
			List<? extends IParametedType> ps = type.getParameters();
			if(ps.size() == 1) {
				type = ps.get(0);
			}
		}
		
		boolean isNew = false;

		Set<IQualifierDeclaration> qs = injectionPoint.getQualifierDeclarations();
		for (IQualifierDeclaration d: qs) {
			if(CDIConstants.NEW_QUALIFIER_TYPE_NAME.equals(d.getTypeName())) {
				isNew = true;
				break;
			}				
		}
	
		Set<IBean> beans = new HashSet<IBean>();
		synchronized(allBeans) {
			beans.addAll(allBeans);
		}
		boolean delegateInjectionPoint = injectionPoint.isDelegate();

		String injectionPointName = injectionPoint.getBeanName();

		for (IBean b: beans) {
			if(b instanceof ClassBean && !(b instanceof IBuiltInBean)) {
				IType bType = b.getBeanClass();
				try {
					if(bType != null && Flags.isAbstract(bType.getFlags())) {
						continue;
					}
				} catch (JavaModelException e) {
					CDICorePlugin.getDefault().logError(e);
				}
			}
			if(isNew) {
				//TODO improve
				IType bType = b.getBeanClass();
				if(bType != null && type.getType() != null 
						&& bType.getFullyQualifiedName().equals(type.getType().getFullyQualifiedName())) {
					result.add(b);
				}
				continue;
			}
			Set<IParametedType> types = b.getLegalTypes();
			if(containsType(types, type)) {
				try {
					if(delegateInjectionPoint && b == injectionPoint.getClassBean()) {
						continue;
					}
					if(injectionPointName != null && !injectionPointName.equals(b.getName())) {
						//
						continue;
					}
					Set<IQualifierDeclaration> qsb = b.getQualifierDeclarations(true);
					if(areMatchingQualifiers(qsb, qs)) {
						result.add(b);
					}
				} catch (CoreException e) {
					CDICorePlugin.getDefault().logError(e);
				}
			}
		}

		return getResolvedBeans(result, attemptToResolveAmbiguousDependency);
	}

	public static boolean containsType(Set<IParametedType> types, IParametedType type) {
		if(type == null) {
			return false;
		}
		IType jType = type.getType();
		if(jType == null) return false;
		for (IParametedType t: types) {
			IType jType1 = t.getType();
			if(jType1 == null || !jType.getFullyQualifiedName().equals(jType1.getFullyQualifiedName())) continue;
			if(!((ParametedType)t).getArrayPrefix().equals(((ParametedType)type).getArrayPrefix())) continue;
			if(((ParametedType)t).isAssignableTo((ParametedType)type, false)) {
				return true;
			}
			
		}
		return false;
	}

	public static boolean areMatchingQualifiers(Collection<IQualifierDeclaration> beanQualifiers, Collection<IQualifierDeclaration> injectionQualifiers) throws CoreException {
		if(beanQualifiers.isEmpty()) {
			if(injectionQualifiers.isEmpty()) {
				return true;
			}
		}

		TreeSet<String> injectionKeys = new TreeSet<String>();
		for (IQualifierDeclaration d: injectionQualifiers) {
			injectionKeys.add(getAnnotationDeclarationKey(d));
		}

		if(injectionKeys.contains(CDIConstants.ANY_QUALIFIER_TYPE_NAME)) {
			return true;
		}
		if(injectionKeys.isEmpty()) {
			injectionKeys.add(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME);
		}

		TreeSet<String> beanKeys = new TreeSet<String>();
		if(beanQualifiers.isEmpty()) {
			beanKeys.add(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME);
		} else for (IQualifierDeclaration d: beanQualifiers) {
			beanKeys.add(getAnnotationDeclarationKey(d));
		}
		if(beanKeys.size() == 1 && beanKeys.iterator().next().startsWith(CDIConstants.NAMED_QUALIFIER_TYPE_NAME)) {
			beanKeys.add(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME);
		}

		for(String k: injectionKeys) {
			if(!beanKeys.contains(k)) return false;
		}
		return true;
	}

	/**
	 * Simplified implementation that does not compare members.
	 * @param beanQualifiers
	 * @param injectionQualifiers
	 * @return
	 * @throws CoreException
	 */
	public static boolean areMatchingQualifiers(Set<IQualifierDeclaration> beanQualifiers, IType... injectionQualifiers) throws CoreException {
		if(!beanQualifiers.isEmpty() || injectionQualifiers.length != 0) {

			TreeSet<String> injectionKeys = new TreeSet<String>();
			for (IType d: injectionQualifiers) {
				injectionKeys.add(d.getFullyQualifiedName());
			}
	
			if(!injectionKeys.contains(CDIConstants.ANY_QUALIFIER_TYPE_NAME)) {
				if(injectionKeys.isEmpty()) {
					injectionKeys.add(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME);
				}
		
				TreeSet<String> beanKeys = new TreeSet<String>();
				if(beanQualifiers.isEmpty()) {
					beanKeys.add(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME);
				} else {
					for (IAnnotationDeclaration d: beanQualifiers) {
						beanKeys.add(d.getTypeName());
					}
				}
				if(beanKeys.size() == 1 && beanKeys.iterator().next().startsWith(CDIConstants.NAMED_QUALIFIER_TYPE_NAME)) {
					beanKeys.add(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME);
				}
		
				for(String k: injectionKeys) {
					if(!beanKeys.contains(k)) { 
						return false;
					}
				}
			}
		}
		return true;
	}

	public static boolean areMatchingEventQualifiers(Set<IQualifierDeclaration> eventQualifiers, Set<IQualifierDeclaration> paramQualifiers) throws CoreException {
		if(!eventQualifiers.isEmpty() || paramQualifiers.isEmpty()) {

			TreeSet<String> paramKeys = new TreeSet<String>();
			for (IQualifierDeclaration d: paramQualifiers) {
				paramKeys.add(getAnnotationDeclarationKey(d));
			}
	
			TreeSet<String> eventKeys = new TreeSet<String>();
			for (IAnnotationDeclaration d: eventQualifiers) {
				eventKeys.add(getAnnotationDeclarationKey(d));
			}
	
			if(!eventKeys.contains(CDIConstants.ANY_QUALIFIER_TYPE_NAME)) {
				eventKeys.add(CDIConstants.ANY_QUALIFIER_TYPE_NAME);
			}
	
			for(String k: paramKeys) {
				if(!eventKeys.contains(k)) return false;
			}
		}
		return true;
	}

	public static String getAnnotationDeclarationKey(IAnnotationDeclaration d) throws CoreException {
		ICDIAnnotation annotation = (ICDIAnnotation)d.getAnnotation();
		Set<IMethod> nb = annotation == null ? new HashSet<IMethod>() : annotation.getNonBindingMethods();
		return getAnnotationDeclarationKey(d, nb);
	}

	private static String getAnnotationDeclarationKey(IAnnotationDeclaration d, Set<IMethod> ignoredMembers) throws CoreException {
		Set<IMethod> nb = ignoredMembers == null ? new HashSet<IMethod>() : ignoredMembers;
		IType type = d.getType();
		StringBuffer result = new StringBuffer();
		result.append(type.getFullyQualifiedName());
		if(CDIConstants.NAMED_QUALIFIER_TYPE_NAME.equals(type.getFullyQualifiedName())) {
			//Declared name is excluded from comparison; names should be compared by invoking getName() method.
			return result.toString();
		}
		IMethod[] ms = type.getMethods();
		if(ms.length > 0) {
			TreeMap<String, String> values = new TreeMap<String, String>();
			IMemberValuePair[] ps = d.getMemberValuePairs();
			if (ps != null) for (IMemberValuePair p: ps) {
				String n = p.getMemberName();
				Object o = p.getValue();
				int k = p.getValueKind();
				if(k == IMemberValuePair.K_QUALIFIED_NAME || k == IMemberValuePair.K_SIMPLE_NAME) {
					String s = o.toString();
					int dot = s.lastIndexOf('.');
					//We reduce value to simple name. That makes it not precise
					//and there must be a test that display limit of this approach.
					if(dot >= 0) {
						String s1 = s.substring(dot + 1);
						if(!"class".equals(s1)) {
							o = s1;
						}
					}
				}
				values.put(n, o.toString());

			}
			for (IMethod m: ms) {
				String n = m.getElementName();
				if(nb.contains(m)) {
					values.remove(n);
				} else if(!values.containsKey(n)) {
					IMemberValuePair p = m.getDefaultValue();
					if (p != null) {
						n = p.getMemberName();
						Object o = p.getValue();
						if(!values.containsKey(n)) {
							values.put(n, o.toString());
						}
					}
				}
			}
			for (String n: values.keySet()) {
				result.append(';').append(n).append('=').append(values.get(n));
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
		Set<INodeReference> decorators = allBeansXMLData.getDecorators();
		synchronized (decorators) {
			result.addAll(decorators);
		}
		return result;
	}

	public List<INodeReference> getDecoratorClasses(String fullQualifiedTypeName) {
		List<INodeReference> result = new ArrayList<INodeReference>();
		Set<INodeReference> decorators = allBeansXMLData.getDecorators();
		synchronized (decorators) {
			for (INodeReference r: decorators) {
				if(fullQualifiedTypeName.equals(r.getValue())) result.add(r);
			}
		}
		return result;
	}

	public List<INodeReference> getInterceptorClasses() {
		List<INodeReference> result = new ArrayList<INodeReference>();
		Set<INodeReference> interceptors = allBeansXMLData.getInterceptors();
		synchronized (interceptors) {
			result.addAll(interceptors);
		}
		return result;
	}

	public List<INodeReference> getInterceptorClasses(
			String fullQualifiedTypeName) {
		List<INodeReference> result = new ArrayList<INodeReference>();
		Set<INodeReference> interceptors = allBeansXMLData.getInterceptors();
		synchronized (interceptors) {
			for (INodeReference r: interceptors) {
				if(fullQualifiedTypeName.equals(r.getValue())) result.add(r);
			}
		}
		return result;
	}

	/**
	 * True if type qualifiedName is selected in this module, false otherwise.
	 * 
	 * @param qualifiedName
	 * @return true if type qualifiedName is selected in this module
	 */
	public boolean isTypeAlternative(String qualifiedName) {
		return projectBeansXMLData.getTypeAlternativeTypes().contains(qualifiedName);
	}

	/**
	 * True if stereotype qualifiedName is selected in this module, false otherwise.
	 * 
	 * @param qualifiedName
	 * @return true if stereotype qualifiedName is selected in this module
	 */
	public boolean isStereotypeAlternative(String qualifiedName) {
		return projectBeansXMLData.getStereotypeAlternativeTypes().contains(qualifiedName);
	}

	public Set<IType> getQualifierTypes() {
		Set<IType> result = new HashSet<IType>();
		List<AnnotationDefinition> ds = n.getDefinitions().getAllAnnotations();
		for (AnnotationDefinition d: ds) {
			if((d.getKind() & AnnotationDefinition.QUALIFIER) > 0) {
				result.add(d.getType());
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getQualifiers()
	 */
	public IQualifier[] getQualifiers() {
		IQualifier[] result = new IQualifier[qualifiers.size()];
		synchronized (qualifiers) {
			int i=0;
			for (IQualifier q: qualifiers.values()) {
				result[i++] = q;
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

	/* (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getAlternatives()
	 */
	public IBean[] getAlternatives() {
		IBean[] result = new IBean[alternatives.size()];
		synchronized (alternatives) {
			int i=0;
			for (IBean bean: alternatives) {
				result[i++] = bean;
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getDecorators()
	 */
	public IDecorator[] getDecorators() {
		IDecorator[] result = new IDecorator[decorators.size()];
		synchronized (decorators) {
			int i=0;
			for (IDecorator bean: decorators) {
				result[i++] = bean;
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getInterceptors()
	 */
	public IInterceptor[] getInterceptors() {
		IInterceptor[] result = new IInterceptor[interceptors.size()];
		synchronized (interceptors) {
			int i=0;
			for (IInterceptor bean: interceptors) {
				result[i++] = bean;
			}
		}
		return result;
	}

	public boolean isNormalScope(IType annotationType) {
		try {
			if(annotationType.isAnnotation()) {
				AnnotationDefinition d = n.getDefinitions().getAnnotation(annotationType);
				List<IAnnotationDeclaration> ds = d.getAnnotations();
				for (IAnnotationDeclaration a: ds) {
					if(a instanceof AnnotationDeclaration) {
						AnnotationDeclaration aa = (AnnotationDeclaration)a;
						if(CDIConstants.NORMAL_SCOPE_ANNOTATION_TYPE_NAME.equals(aa.getTypeName())) {
							return true;
						}
					}
				}				
			}
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return false;
	}

	public boolean isPassivatingScope(IType annotationType) {
		try {
			if(annotationType.isAnnotation())  {
				AnnotationDefinition d = n.getDefinitions().getAnnotation(annotationType);
				List<IAnnotationDeclaration> ds = d.getAnnotations();
				for (IAnnotationDeclaration a: ds) {
					if(a instanceof AnnotationDeclaration) {
						AnnotationDeclaration aa = (AnnotationDeclaration)a;
						if(CDIConstants.NORMAL_SCOPE_ANNOTATION_TYPE_NAME.equals(aa.getTypeName())) {
							Object o = a.getMemberValue("passivating");
							return o != null && "true".equalsIgnoreCase(o.toString());
						}
					}
				}
			}
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return false;
	}

	public boolean isQualifier(IType annotationType) {
		boolean result = false;
		try {
			if(annotationType.isAnnotation()) {
				int k = n.getDefinitions().getAnnotationKind(annotationType);
				result = k > 0 && (k & AnnotationDefinition.QUALIFIER) > 0;
			}
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return result;
	}

	public boolean isScope(IType annotationType) {
		boolean result = false;
		try {
			if(annotationType.isAnnotation()) {
				int k = n.getDefinitions().getAnnotationKind(annotationType);
				result = k > 0 && (k & AnnotationDefinition.SCOPE) > 0;
			}
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return result;
	}

	public boolean isStereotype(IType annotationType) {
		boolean result = false;
		try {
			if(annotationType.isAnnotation()) {
				int k = n.getDefinitions().getAnnotationKind(annotationType);
				result= k > 0 && (k & AnnotationDefinition.STEREOTYPE) > 0;
			}
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return result;
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

	public Set<IObserverMethod> resolveObserverMethods(IInjectionPoint injectionPoint) {
		Set<IObserverMethod> result = new HashSet<IObserverMethod>();

		IParametedType eventType = getEventType(injectionPoint);
		
		if(eventType != null) {
			for (IBean ib: allBeans) {
				if(!(ib instanceof IClassBean)) continue;
				IClassBean b = (IClassBean)ib;
				Set<IObserverMethod> ms = b.getObserverMethods();
				for (IObserverMethod m: ms) {
					IObserverMethod om = (IObserverMethod)m;
					Set<IParameter> params = om.getObservedParameters();
					if(!params.isEmpty()) {
						IParameter param = params.iterator().next();
						IParametedType paramType = param.getType();
						if(((ParametedType)eventType).isAssignableTo((ParametedType)paramType, true)
								&& areMatchingEventQualifiers(param, injectionPoint)) {
							result.add(om);
						}
					}
				}			
			}
		}
		return result;
	}

	/**
	 * Returns type parameter of type javax.enterprise.event.Event<T>
	 * In all other cases returns null.
	 * 
	 * @param t
	 * @return
	 */
	private IParametedType getEventType(IInjectionPoint p) {
		IParametedType t = p.getType();
		if(t == null || t.getType() == null || !CDIConstants.EVENT_TYPE_NAME.equals(t.getType().getFullyQualifiedName())) {
			return null;
		}
		List<? extends IParametedType> ps = t.getParameters();
		return ps.isEmpty() ? null : ps.get(0);
	}

	private boolean areMatchingEventQualifiers(IParameter observerParam, IInjectionPoint event) {
		try {
			return areMatchingEventQualifiers(event.getQualifierDeclarations(), observerParam.getQualifierDeclarations());
		} catch (CoreException err) {
			CDICorePlugin.getDefault().logError(err);
		}
		return false;
	}

	public Set<IInjectionPoint> findObservedEvents(IParameter observedEventParameter) {
		Map<IField, IInjectionPoint> result = new HashMap<IField, IInjectionPoint>();

		if(observedEventParameter.getBeanMethod() instanceof IObserverMethod) {
			IParametedType paramType = observedEventParameter.getType();
			for (IBean ib: allBeans) {
				if(!(ib instanceof IClassBean)) {
					continue;
				}
				IClassBean b = (IClassBean)ib;
				Set<IInjectionPoint> ps = b.getInjectionPoints();
				for (IInjectionPoint p: ps) {
					if(p instanceof IInjectionPointField) {
						IParametedType eventType = getEventType(p);
						if(eventType != null && ((ParametedType)eventType).isAssignableTo((ParametedType)paramType, true)) {
							if(areMatchingEventQualifiers(observedEventParameter, p)) {
								 result.put(((IInjectionPointField)p).getField(), p);
							 }
						}
					}
				}
			}			
		}
		
		return new HashSet<IInjectionPoint>(result.values());
	}

	public Set<IBeanMethod> resolveDisposers(IProducerMethod producer) {
		Set<IBeanMethod> result = new HashSet<IBeanMethod>();
		IClassBean cb = producer.getClassBean();
		if(cb != null) {

			Set<IParametedType> types = producer.getLegalTypes();
			Set<IQualifierDeclaration> qs = producer.getQualifierDeclarations(true);
	
			Set<IBeanMethod> ds = cb.getDisposers();
			for (IBeanMethod m: ds) {
				List<IParameter> ps = m.getParameters();
				IParameter match = null;
				for (IParameter p: ps) {
					if(!p.isAnnotationPresent(CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME)) continue;
					IParametedType type = p.getType();
					if(!containsType(types, type)) continue;
					try {
						if(areMatchingQualifiers(qs, p.getQualifierDeclarations())) {
							match = p;
							break;
						}
					} catch (CoreException e) {
						CDICorePlugin.getDefault().logError(e);
					}
				}
				if(match != null) {
					result.add(m);
				}
			}
		}
		return result;
	}

	@Override
	public CDIProject getCDIProject() {
		return this;
	}

	@Override
	public ICDIProject getDeclaringProject() {
		return this;
	}

	@Override
	public IResource getResource() {
		return n.getProject();
	}

	@Override
	public IPath getSourcePath() {
		return n.getProject().getFullPath();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getStereotype(java.lang.String)
	 */
	public StereotypeElement getStereotype(String qualifiedName) {
		return stereotypes.get(qualifiedName);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getStereotype(org.eclipse.core.runtime.IPath)
	 */
	public StereotypeElement getStereotype(IPath path) {
		return stereotypesByPath.get(path);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getStereotype(org.eclipse.jdt.core.IType)
	 */
	public StereotypeElement getStereotype(IType type) {
		IPath path = type.getPath();
		return stereotypesByPath.get(path);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getInterceptorBindings()
	 */
	public IInterceptorBinding[] getInterceptorBindings() {
		IInterceptorBinding[] result = new IInterceptorBinding[interceptorBindings.size()];
		synchronized (interceptorBindings) {
			int i=0;
			for (IInterceptorBinding s: interceptorBindings.values()) {
				result[i++] = s;
			}
		}
		return result;
	
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getInterceptorBinding(java.lang.String)
	 */
	public InterceptorBindingElement getInterceptorBinding(String qualifiedName) {
		return interceptorBindings.get(qualifiedName);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getInterceptorBinding(org.eclipse.core.runtime.IPath)
	 */
	public IInterceptorBinding getInterceptorBinding(IPath path) {
		return interceptorBindingsByPath.get(path);
	}

	public QualifierElement getQualifier(String qualifiedName) {
		return qualifiers.get(qualifiedName);
	}

	public QualifierElement getQualifier(IPath path) {
		return qualifiersByPath.get(path);
	}

	public Set<String> getScopeNames() {
		Set<String> result = new HashSet<String>();
		result.addAll(scopes.keySet());
		return result;
	}

	public ScopeElement getScope(String qualifiedName) {
		return scopes.get(qualifiedName);
	}

	public IScope getScope(IPath path) {
		return scopesByPath.get(path);
	}

	public void update() {
		rebuildXML();
		rebuildAnnotationTypes();
		rebuildBeans();

		Set<IBuildParticipantFeature> buildParticipants = n.getExtensionManager().getBuildParticipantFeatures();
		for (IBuildParticipantFeature p: buildParticipants) p.buildBeans(this);
				
		Set<CDICoreNature> ds = n.getCDIProjects(true);
		for (CDICoreNature c: ds) {
			Set<IBuildParticipantFeature> bs = c.getExtensionManager().getBuildParticipantFeatures();
			for (IBuildParticipantFeature bp: bs) {
				bp.buildBeans(this);
			}
		}
		
		CDICoreNature[] ps = n.getDependentProjects().toArray(new CDICoreNature[0]);
		for (CDICoreNature p: ps) {
			if(p.getProject() != null && p.getProject().isAccessible() && p.getDelegate() != null) {
				p.getDelegate().update();
			}
		}		
	}

	void rebuildAnnotationTypes() {
		stereotypes.clear();
		stereotypesByPath.clear();
		interceptorBindings.clear();
		qualifiers.clear();
		qualifiersByPath.clear();
		interceptorBindingsByPath.clear();
		scopes.clear();
		scopesByPath.clear();
		List<AnnotationDefinition> ds = n.getAllAnnotations();
		for (AnnotationDefinition d: ds) {
			if((d.getKind() & AnnotationDefinition.STEREOTYPE) > 0) {
				StereotypeElement s = new StereotypeElement();
				initAnnotationElement(s, d);
				stereotypes.put(d.getQualifiedName(), s);
				if(d.getResource() != null && d.getResource().getFullPath() != null) {
					stereotypesByPath.put(d.getResource().getFullPath(), s);
				}
			}
			if((d.getKind() & AnnotationDefinition.INTERCEPTOR_BINDING) > 0) {
				InterceptorBindingElement s = new InterceptorBindingElement();
				initAnnotationElement(s, d);
				interceptorBindings.put(d.getQualifiedName(), s);
				if(d.getResource() != null && d.getResource().getFullPath() != null) {
					interceptorBindingsByPath.put(d.getResource().getFullPath(), s);
				}
			}
			if((d.getKind() & AnnotationDefinition.QUALIFIER) > 0) {
				QualifierElement s = new QualifierElement();
				initAnnotationElement(s, d);
				qualifiers.put(d.getQualifiedName(), s);
				if(d.getResource() != null && d.getResource().getFullPath() != null) {
					qualifiersByPath.put(d.getResource().getFullPath(), s);
				}
			}
			if((d.getKind() & AnnotationDefinition.SCOPE) > 0) {
				ScopeElement s = new ScopeElement();
				initAnnotationElement(s, d);
				scopes.put(d.getQualifiedName(), s);
				if(d.getResource() != null && d.getResource().getFullPath() != null) {
					scopesByPath.put(d.getResource().getFullPath(), s);
				}
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
		List<TypeDefinition> typeDefinitions = n.getAllTypeDefinitions();
		List<IBean> beans = new ArrayList<IBean>();
		Map<IType, IClassBean> newClassBeans = new HashMap<IType, IClassBean>();
	
		ImplementationCollector ic = new ImplementationCollector(typeDefinitions);

		for (TypeDefinition typeDefinition : typeDefinitions) {
			if(typeDefinition.isVetoed()) {
				continue;
			}
			ClassBean bean = null;
			if(typeDefinition.getInterceptorAnnotation() != null || ic.isInterceptor(typeDefinition.getType())) {
				bean = new InterceptorBean();
			} else if(typeDefinition.getDecoratorAnnotation() != null || ic.isDecorator(typeDefinition.getType())) {
				bean = new DecoratorBean();
			} else if(typeDefinition.getStatefulAnnotation() != null || typeDefinition.getStatelessAnnotation() != null || typeDefinition.getSingletonAnnotation() != null) {
				bean = new SessionBean();
			} else {
				bean = new ClassBean();
			}
			bean.setParent(this);
			bean.setDefinition(typeDefinition);

			if(typeDefinition.hasBeanConstructor()) {
				beans.add(bean);
				newClassBeans.put(typeDefinition.getType(), bean);
			}

			Set<IProducer> ps = bean.getProducers();
			for (IProducer producer: ps) {
				beans.add(producer);
			}
		}
	
		for (String builtin: BuiltInBeanFactory.BUILT_IN) {
			IType type = n.getType(builtin);
			if(type != null && !newClassBeans.containsKey(type)) {
				TypeDefinition t = new TypeDefinition();
				t.setType(type, n.getDefinitions(), TypeDefinition.FLAG_NO_ANNOTATIONS);
				ClassBean bean = BuiltInBeanFactory.newClassBean(this, t);
				newClassBeans.put(t.getType(), bean);
				beans.add(bean);
			}
		}
	
		for (IClassBean bean: newClassBeans.values()) {
			IParametedType s = ((ClassBean)bean).getSuperType();
			if(s != null && s.getType() != null) {
				IClassBean superClassBean = newClassBeans.get(s.getType());
				if(bean instanceof ClassBean) {
					((ClassBean)bean).setSuperClassBean(superClassBean);
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
		synchronized (alternatives) {
			alternatives.clear();
		}
		synchronized (decorators) {
			decorators.clear();
		}
		synchronized (interceptors) {
			interceptors.clear();
		}
		synchronized (allBeans) {
			allBeans.clear();
		}

		classBeans = newClassBeans;
		for (IBean bean: beans) {
			addBean(bean);
		}
	
		buildInjectionPoinsByType();

	}

	public void addBean(IBean bean) {
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
		if(bean.isAlternative()) {
			synchronized (alternatives) {
				alternatives.add(bean);
			}
		}
		if(bean instanceof IDecorator) {
			synchronized (decorators) {
				decorators.add((IDecorator)bean);
			}
		}
		if(bean instanceof IInterceptor) {
			synchronized (interceptors) {
				interceptors.add((IInterceptor)bean);
			}
		}
		if(bean instanceof IClassBean) {
			IClassBean c = (IClassBean)bean;
			IType t = c.getBeanClass();
			if(t != null && !classBeans.containsKey(t)) {
				classBeans.put(t, c);
			}
		}
		synchronized (allBeans) {
			allBeans.add(bean);
		}
	}

	void buildInjectionPoinsByType() {
		injectionPointsByType.clear();
		
		for (IBean b: allBeans) {
			Set<IInjectionPoint> ps = b.getInjectionPoints();
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
		
	}

	void rebuildXML() {
		allBeansXMLData.clean();
		projectBeansXMLData.clean();
		Set<BeansXMLDefinition> beanXMLs = n.getAllBeanXMLDefinitions();
		for (BeansXMLDefinition b: beanXMLs) {
			IPath p = b.getPath();
			boolean t = (!p.lastSegment().endsWith(".jar") && p.segment(0).equals(getNature().getProject().getName()));
			for (INodeReference r: b.getInterceptors()) {
				allBeansXMLData.addInterceptor(r);
				if(t) projectBeansXMLData.addInterceptor(r);
			}
			for (INodeReference r: b.getDecorators()) {
				allBeansXMLData.addDecorator(r);
				if(t) projectBeansXMLData.addDecorator(r);
			}
			for (INodeReference r: b.getStereotypeAlternatives()) {
				allBeansXMLData.addStereotypeAlternative(r);
				if(t) projectBeansXMLData.addStereotypeAlternative(r);
			}
			for (INodeReference r: b.getTypeAlternatives()) {
				allBeansXMLData.addTypeAlternative(r);
				if(t) projectBeansXMLData.addTypeAlternative(r);
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
					if(!names.contains(bean.getName())) {
						Set<IBean> beans = getBeans(bean.getName(), attemptToResolveAmbiguousNames);
						if(beans.isEmpty()) {
							result.add(bean);
						} else {
							result.addAll(beans);
							names.add(bean.getName());
						}
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
			IParametedType beanType, IType... qualifiers) {
		Set<IBean> result = new HashSet<IBean>();
		IParametedType type = beanType;
		Set<IBean> beans = new HashSet<IBean>();
		synchronized(allBeans) {
			beans.addAll(allBeans);
		}
		for (IBean b: beans) {
			Set<IParametedType> types = b.getLegalTypes();
			if(containsType(types, type)) {
				try {
					Set<IQualifierDeclaration> qsb = b.getQualifierDeclarations(true);
					if(areMatchingQualifiers(qsb, qualifiers)) {
						result.add(b);
					}
				} catch (CoreException e) {
					CDICorePlugin.getDefault().logError(e);
				}
			}
		}
		
		return getResolvedBeans(result, attemptToResolveAmbiguousDependency);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getBeans(boolean, java.lang.String, java.lang.String[])
	 */
	public Set<IBean> getBeans(boolean attemptToResolveAmbiguousDependency,
			String fullyQualifiedBeanType,
			String... fullyQualifiedQualifiersTypes) {
		IType type = getNature().getType(fullyQualifiedBeanType);
		IParametedType beanType = getNature().getTypeFactory().newParametedType(type);
		List<IType> qualifiers = new ArrayList<IType>();
		if(fullyQualifiedQualifiersTypes != null) for (String s : fullyQualifiedQualifiersTypes) {
			type = getNature().getType(s);
			if(type != null) qualifiers.add(type);
		}
		
		return getBeans(attemptToResolveAmbiguousDependency, beanType, qualifiers.toArray(new IType[0]));
	}

	public Set<IInjectionPoint> getInjections(String fullyQualifiedTypeName) {
		Set<IInjectionPoint> result = injectionPointsByType.get(fullyQualifiedTypeName);
		if(result == null) result = Collections.emptySet();		
		return result;
	}

	/**
	 * For usage in TCK tests which contain many versions of beans.xml in packages.
	 * @param path
	 */
	public IPath replaceBeanXML(IPath path) {
		getNature().getDefinitions().newWorkingCopy(false);
		DefinitionContext context = getNature().getDefinitions().getWorkingCopy();
		
		Set<BeansXMLDefinition> beanXMLs = context.getBeansXMLDefinitions();
		Set<IPath> old = new HashSet<IPath>();
		for (BeansXMLDefinition d: beanXMLs) {
			IPath p = d.getPath();
			if(p != null && "beans.xml".equals(p.lastSegment())) {
				old.add(p);
			}
		}
		for (IPath p: old) context.clean(p);
		IFile f = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		if(f != null && f.exists()) {
			XModelObject beansXML = EclipseResourceUtil.getObjectByResource(f);
			if(beansXML == null) {
				beansXML = EclipseResourceUtil.createObjectForResource(f);
			}
			if(beansXML != null) {
				BeansXMLDefinition def = new BeansXMLDefinition();
				def.setPath(f.getFullPath());
				def.setBeansXML(beansXML);
				context.addBeanXML(f.getFullPath(), def);
			}
		}
		
		
		context.applyWorkingCopy();
		return old.isEmpty() ? null : old.iterator().next();
	}

}