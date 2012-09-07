/******************************************************************************* 
 * Copyright (c) 2007-2011 Red Hat, Inc. 
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanMember;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.IBuiltInBean;
import org.jboss.tools.cdi.core.ICDIAnnotation;
import org.jboss.tools.cdi.core.ICDICache;
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
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.cdi.core.extension.feature.IAmbiguousBeanResolverFeature;
import org.jboss.tools.cdi.core.extension.feature.IBeanStoreFeature;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;
import org.jboss.tools.cdi.internal.core.event.CDIProjectChangeEvent;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.BeansXMLDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.DefinitionContext;
import org.jboss.tools.cdi.internal.core.impl.definition.FieldDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.MethodDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.cdi.internal.core.scanner.ImplementationCollector;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IJavaReference;
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
public class CDIProject extends CDIElement implements ICDIProject, Cloneable {
	CDICoreNature n;
	private ICDIProject declaringProject = this;

	private CDICache cache = new CDICache();
	private ICDICache dbCache;

	BeansXMLData allBeansXMLData = new BeansXMLData();
	BeansXMLData projectBeansXMLData = new BeansXMLData();

	public CDIProject() {
		dbCache = CDICorePlugin.getDefault().getDBCache();
	}

	public CDIProject getModifiedCopy(IFile file, Collection<IBean> beans) {
		CDIProject p = null;
		try {
			p = (CDIProject)clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		synchronized (cache) {
			p.cache = cache.getModifiedCopy(file, beans);
		}
		p.declaringProject = this;
		
		return p;
	}

	@Override
	public CDICoreNature getNature() {
		return n;
	}

	@Override
	public void setNature(CDICoreNature n) {
		this.n = n;
	}

	@Override
	public boolean exists() {
		return n != null && n.getProject() != null && n.getProject().isAccessible();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getBeans()
	 */
	public IBean[] getBeans() {
		return cache.getBeans();
	}

	public Collection<IBean> getDeclaredBeans() {
		return cache.getDeclaredBeans();
	}

	public List<INodeReference> getAlternativeClasses() {
		List<INodeReference> result = new ArrayList<INodeReference>();
		synchronized (allBeansXMLData) {
			result.addAll(allBeansXMLData.getTypeAlternatives());
		}
		return result;
	}

	public List<INodeReference> getAlternativeStereotypes() {
		List<INodeReference> result = new ArrayList<INodeReference>();
		synchronized (allBeansXMLData) {
			result.addAll(allBeansXMLData.getStereotypeAlternatives());
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
		synchronized (allBeansXMLData) {
			Collection<INodeReference> typeAlternatives = allBeansXMLData.getTypeAlternatives();
			Collection<INodeReference> stereotypeAlternatives = allBeansXMLData.getStereotypeAlternatives();
			for (INodeReference r: typeAlternatives) {
				if(fullQualifiedTypeName.equals(r.getValue())) result.add(r);
			}
			for (INodeReference r: stereotypeAlternatives) {
				if(fullQualifiedTypeName.equals(r.getValue())) result.add(r);
			}
		}
		return result;
	}

	public IClassBean getBeanClass(IType type) {
		IPath path = type.getPath();
		synchronized (cache) {
			Collection<IBean> bs = cache.getBeans(path);
			for (IBean b: bs) {
				if(b instanceof IClassBean) {
					IClassBean result = (IClassBean)b;
					if(type.getFullyQualifiedName().equals(result.getBeanClass().getFullyQualifiedName())) {
						return result;
					}
				}
			}
		}
		return null;
	}

	public Collection<IBean> getBeans(String name,	boolean attemptToResolveAmbiguousNames) {
		Set<IBean> result = new HashSet<IBean>();
		synchronized (cache) {
			Collection<IBean> beans = cache.getBeans(name);
			if(beans.isEmpty()) {
				return result;
			}
			result.addAll(beans);
		}
		return getResolvedBeans(result, attemptToResolveAmbiguousNames);
	}

	public Collection<IBean> getResolvedBeans(Collection<IBean> result, boolean attemptToResolveAmbiguousness) {
		if(result.size() > 1) {
			Iterator<IBean> it = result.iterator();
			while(it.hasNext()) {
				IBean b = it.next();
				if(b instanceof IBuiltInBean) {
					it.remove();
				}
			}
		}
		if(result.isEmpty() || !attemptToResolveAmbiguousness) {
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
			if(b instanceof IProducer && b instanceof IBeanMember) {
				IBeanMember p = (IBeanMember)b;
				if(p.getClassBean() != null && p.getClassBean().isAlternative()) {
					containsAlternatives = true;
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
		
		if(result.size() < 2) {
			return result;
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

	public Collection<IBean> getBeans(boolean attemptToResolveAmbiguousDependency,
			IParametedType beanType, IQualifierDeclaration... qualifiers) {
		Set<IBean> result = new HashSet<IBean>();
		IParametedType type = beanType;
		if(type == null) {
			return result;
		}

		Set<IQualifierDeclaration> qs = new HashSet<IQualifierDeclaration>();
		if(qualifiers != null) for (IQualifierDeclaration d: qualifiers) qs.add(d);
		
		for (IBean b: cache.getBeansByLegalType(type)) {
			if(containsType(b.getLegalTypes(), type)) {
				try {
					Collection<IQualifierDeclaration> qsb = b.getQualifierDeclarations(true);
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
	public Collection<IBean> getBeans(boolean attemptToResolveAmbiguousDependency, IInjectionPoint injectionPoint) {
		if(injectionPoint.getDeclaringProject() != getDeclaringProject()) {
			return injectionPoint.getDeclaringProject().getBeans(attemptToResolveAmbiguousDependency, injectionPoint);
		}
		Set<IBean> result = new HashSet<IBean>();
		IParametedType type = injectionPoint.getType();
		if(type == null) {
			return result;
		}
		
		IType jType = type.getType();
		boolean isObjectType = jType != null && "java.lang.Object".equals(jType.getFullyQualifiedName());
		
		if(isObjectType && injectionPoint.getAnnotation(CDIConstants.ANY_QUALIFIER_TYPE_NAME) != null) {
			synchronized(cache) {
				result.addAll(cache.getAllBeans());
			}
			return getResolvedBeans(result, attemptToResolveAmbiguousDependency);
		}
	
		if(jType != null && CDIConstants.EVENT_TYPE_NAME.equals(jType.getFullyQualifiedName())) {
			List<? extends IParametedType> ps = type.getParameters();
			if(ps.size() == 1) {
				EventBean eventBean = new EventBean(type, injectionPoint);
				eventBean.setParent(this);
				eventBean.setSourcePath(injectionPoint.getSourcePath());
				result.add(eventBean);
				return result;
			}
		}
		
		if(jType != null && (CDIConstants.INSTANCE_TYPE_NAME.equals(jType.getFullyQualifiedName())
						|| CDIConstants.PROVIDER_TYPE_NAME.equals(jType.getFullyQualifiedName()))) {
			List<? extends IParametedType> ps = type.getParameters();
			if(ps.size() == 1) {
				type = ps.get(0);
			}
		}
		
		Collection<IQualifierDeclaration> qs = injectionPoint.getQualifierDeclarations();
		for (IQualifierDeclaration d: qs) {
			if(CDIConstants.NEW_QUALIFIER_TYPE_NAME.equals(d.getTypeName())) {
				IBean b = createNewBean(type, d);
				if(b != null) {
					if(containsType(b.getLegalTypes(), type)) {
						result.add(b);
					}
				}
				return result;
			}				
		}
	
		boolean delegateInjectionPoint = injectionPoint.isDelegate();

		String injectionPointName = injectionPoint.getBeanName();
		
		Collection<IBean> beans = null;
		
		//DB
		if(dbCache != null) {
			beans = dbCache.getBeansByLegalType(this, type.getType().getFullyQualifiedName());
		}
		//Compare with result from cache.

		for (IBean b: cache.getBeansByLegalType(type)) {
			if(isObjectType || containsType(b.getLegalTypes(), type)) {
				try {
					if(delegateInjectionPoint && b == injectionPoint.getClassBean()) {
						continue;
					}
					if(injectionPointName != null && !injectionPointName.equals(b.getName())) {
						//
						continue;
					}
					Collection<IQualifierDeclaration> qsb = b.getQualifierDeclarations(true);
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

	/**
	 * Returns null if type is not managed bean.
	 * 
	 * @param type
	 * @param newDeclaration
	 * @return
	 */
	public IBean createNewBean(IParametedType type, IAnnotationDeclaration newDeclaration) {
		IType t = type.getType();
		if(t == null || !t.exists()) {
			return null;
		}
		try {
			if(newDeclaration.getJavaAnnotation() == null) {
				return null;
			}
			IJavaElement dType = newDeclaration.getJavaAnnotation().getAncestor(IJavaElement.TYPE);
			if(!(dType instanceof IType)) {
				return null;
			}
			Object value = newDeclaration.getMemberValue(null);
			if(value != null && value.toString().length() > 0) {
				ParametedType p = getNature().getTypeFactory().getParametedType((IType)dType, "Q" + value.toString() + ";");
				if(p != null && p.getType() != null && p.getType().getTypeParameters().length > 0) {
					String s = type.getSignature();				
					if(s.indexOf("<") >= 0 && s.lastIndexOf('>') > 0) {
						String cls = value.toString() + s.substring(s.indexOf('<'), s.lastIndexOf('>') + 1);
						ParametedType p1 = getNature().getTypeFactory().getParametedType((IType)dType, "Q" + cls + ";");
						if(p1 != null) {
							p = p1;
						}
					}
				}
				if(p == null) {
					return null;
				}
				p.getAllTypes();
				type = p;
				t = p.getType();
			}
			if(t.isInterface()) {
				return null;
			}
			TypeDefinition def = new TypeDefinition();
			def.setType(type.getType(), getNature().getDefinitions(), 0);
			def.setParametedType(type);
			if(!def.hasBeanConstructor()) {
				return null;
			}
			Iterator<MethodDefinition> ms = def.getMethods().iterator();
			while(ms.hasNext()) {
				MethodDefinition m = ms.next();
				if(m.isObserver() || m.isDisposer() || m.isAnnotationPresent(CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME)) {
					ms.remove();
				}
			}
			Iterator<FieldDefinition> fs = def.getFields().iterator();
			while(fs.hasNext()) {
				FieldDefinition f = fs.next();
				if(f.isAnnotationPresent(CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME)) {
					fs.remove();
				}
			}
			IAnnotationDeclaration[] as = def.getAnnotations().toArray(new IAnnotationDeclaration[0]);
			for (IAnnotationDeclaration a: as) {
				if(a instanceof IQualifierDeclaration || a instanceof IStereotypeDeclaration) {
					def.removeAnnotation(a);
				}
			}
			def.addAnnotation(((AnnotationDeclaration)newDeclaration).getDeclaration(), getNature().getDefinitions());
			
			ClassBean cb = new NewBean();
			cb.setParent(this);
			cb.setDefinition(def);
			
			return cb;
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return null;
	}

	public static boolean containsType(Collection<IParametedType> types, IParametedType type) {
		if(type == null) {
			return false;
		}
		IType jType = type.getType();
		if(jType == null) return false;
		String typeName = jType.getFullyQualifiedName();
		for (IParametedType t: types) {
			IType jType1 = t.getType();
			if(jType1 == null || !jType.getElementName().equals(jType1.getElementName()) || !typeName.equals(jType1.getFullyQualifiedName())) continue;
			if(((ParametedType)t).getArrayIndex() != ((ParametedType)type).getArrayIndex()) continue;
			if(((ParametedType)t).isAssignableTo((ParametedType)type, false)) {
				return true;
			}
		}
		return false;
	}

	public static boolean areMatchingQualifiers(Collection<IQualifierDeclaration> beanQualifiers, Collection<IQualifierDeclaration> injectionQualifiers) throws CoreException {
		if(beanQualifiers.isEmpty() && injectionQualifiers.isEmpty()) {
			return true;
		}

		Set<String> keys = new HashSet<String>();
		for (IQualifierDeclaration d: injectionQualifiers) {
			keys.add(getAnnotationDeclarationKey(d));
		}

		if(keys.contains(CDIConstants.ANY_QUALIFIER_TYPE_NAME)) {
			return true;
		}
		if(keys.isEmpty()) {
			keys.add(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME);
		}

		if(beanQualifiers.isEmpty()) {
			keys.remove(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME);
		} else {
			int beanKeySize = 0;
			String beanKey = null;
			for (IQualifierDeclaration d: beanQualifiers) {
				beanKeySize++;
				beanKey = getAnnotationDeclarationKey(d);
				keys.remove(beanKey);
			}
			if(beanKeySize == 1 && beanKey.startsWith(CDIConstants.NAMED_QUALIFIER_TYPE_NAME)) {
				keys.remove(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME);
			}
		}

		return keys.isEmpty();
	}

	/**
	 * Simplified implementation that does not compare members.
	 * @param beanQualifiers
	 * @param injectionQualifiers
	 * @return
	 * @throws CoreException
	 */
	public static boolean areMatchingQualifiers(Collection<IQualifierDeclaration> beanQualifiers, IType... injectionQualifiers) throws CoreException {
		if(beanQualifiers.isEmpty() && injectionQualifiers.length == 0) {
			return true;
		}

		Set<String> keys = new HashSet<String>();
		for (IType d: injectionQualifiers) {
			keys.add(d.getFullyQualifiedName().replace('$', '.'));
		}
	
		if(keys.contains(CDIConstants.ANY_QUALIFIER_TYPE_NAME)) {
			return true;
		}

		if(keys.isEmpty()) {
			keys.add(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME);
		}
		if(beanQualifiers.isEmpty()) {
			keys.remove(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME);
		} else {
			int beanKeySize = 0;
			String beanKey = null;
			for (IAnnotationDeclaration d: beanQualifiers) {
				beanKeySize++;
				beanKey = d.getTypeName().replace('$', '.');
				keys.remove(beanKey);
			}
			if(beanKeySize == 1 && beanKey.startsWith(CDIConstants.NAMED_QUALIFIER_TYPE_NAME)) {
				keys.remove(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME);
			}
		}
	
		return keys.isEmpty();
	}

	public static boolean areMatchingEventQualifiers(Collection<IQualifierDeclaration> eventQualifiers, Collection<IQualifierDeclaration> paramQualifiers) throws CoreException {
		if(!paramQualifiers.isEmpty()) {

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
		Collection<IMethod> nb = annotation == null ? new HashSet<IMethod>() : annotation.getNonBindingMethods();
		return getAnnotationDeclarationKey(d, nb);
	}

	private static String getAnnotationDeclarationKey(IAnnotationDeclaration d, Collection<IMethod> ignoredMembers) throws CoreException {
		Collection<IMethod> nb = ignoredMembers == null ? new ArrayList<IMethod>() : ignoredMembers;
		IType type = d.getType();
		StringBuffer result = new StringBuffer();
		result.append(d.getTypeName());
		if(CDIConstants.NAMED_QUALIFIER_TYPE_NAME.equals(d.getTypeName())) {
			//Declared name is excluded from comparison; names should be compared by invoking getName() method.
			return result.toString();
		}
		IMethod[] ms = type.getMethods();
		if(ms.length > 0) {
			TreeMap<String, String> values = new TreeMap<String, String>();
			IMemberValuePair[] ps = d.getMemberValuePairs();
			if (ps != null) for (IMemberValuePair p: ps) {
				String n = p.getMemberName();
				Object o = d.getMemberValue(n);
				values.put(n, o == null ? "" : o.toString());

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
						// Default value can be null since JDT does not computes complex values
						// E.g. values (char)7 or (2 + 3) will be resolved to null.  
						if(!values.containsKey(n) && o != null) {
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

	public Collection<IBean> getBeans(IPath path) {
		return cache.getBeans(path);
	}
	
	static int q = 0;

	public Set<IBean> getBeans(IJavaElement element) {
		Set<IBean> result = new HashSet<IBean>();
		synchronized (cache) {
		for (IBean bean: cache.getAllBeans()) {
			if(bean instanceof IJavaReference) {
				if(((IJavaReference)bean).getSourceMember().equals(element)) {
					result.add(bean);
				}
			}
		}
		}
		return result;
	}

	public List<INodeReference> getDecoratorClasses() {
		List<INodeReference> result = new ArrayList<INodeReference>();
		synchronized (allBeansXMLData) {
			result.addAll(allBeansXMLData.getDecorators());
		}
		return result;
	}

	public List<INodeReference> getDecoratorClasses(String fullQualifiedTypeName) {
		List<INodeReference> result = new ArrayList<INodeReference>();
		synchronized (allBeansXMLData) {
			for (INodeReference r: allBeansXMLData.getDecorators()) {
				if(fullQualifiedTypeName.equals(r.getValue())) result.add(r);
			}
		}
		return result;
	}

	public List<INodeReference> getInterceptorClasses() {
		List<INodeReference> result = new ArrayList<INodeReference>();
		synchronized (allBeansXMLData) {
			result.addAll(allBeansXMLData.getInterceptors());
		}
		return result;
	}

	public List<INodeReference> getInterceptorClasses(
			String fullQualifiedTypeName) {
		List<INodeReference> result = new ArrayList<INodeReference>();
		synchronized (allBeansXMLData) {
			for (INodeReference r: allBeansXMLData.getInterceptors()) {
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
		return cache.getQualifiers();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getStereotypes()
	 */
	public IStereotype[] getStereotypes() {
		return cache.getStereotypes();
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getAlternatives()
	 */
	public IBean[] getAlternatives() {
		return cache.getAlternatives();
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getDecorators()
	 */
	public IDecorator[] getDecorators() {
		return cache.getDecorators();
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getInterceptors()
	 */
	public IInterceptor[] getInterceptors() {
		return cache.getInterceptors();
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

	public Collection<IBean> resolve(Collection<IBean> beans) {
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

	/**
	 * This method looks for observed methods in all the set of related projects.
	 * To this end, it activates all CDI projects in workspace. 
	 */
	public Collection<IObserverMethod> resolveObserverMethods(IInjectionPoint injectionPoint) {
		Collection<IObserverMethod> result = new ArrayList<IObserverMethod>();
		IParametedType eventType = getEventType(injectionPoint);
		if(eventType != null) {
			synchronized(cache) {
				for (IBean b: cache.getAllBeans()) {
					if(b instanceof IClassBean) {
						collectObserverMethods((IClassBean)b, eventType, injectionPoint, result);
					}
				}
			}
			for (IProject p: ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
				if(p.isAccessible()) {
					CDICorePlugin.getCDI(p, true);
				}
			}
			CDICoreNature[] ns = getNature().getAllDependentProjects(true);
			for (CDICoreNature n: ns) {
				if(n.getDelegate() instanceof CDIProject) {
					CDIProject p = (CDIProject)n.getDelegate();
					for (IBean b: p.getDeclaredBeans()) {
						if(b instanceof IClassBean) {
							collectObserverMethods((IClassBean)b, eventType, injectionPoint, result);
						}
					}
				}
			}
		}
		return result;
	}

	private void collectObserverMethods(IClassBean b, IParametedType eventType, IInjectionPoint injectionPoint, Collection<IObserverMethod> result) {
		for (IObserverMethod m: b.getObserverMethods()) {
			Collection<IParameter> params = m.getObservedParameters();
			if(!params.isEmpty()) {
				IParameter param = params.iterator().next();
				IParametedType paramType = param.getType();
				if(((ParametedType)eventType).isAssignableTo((ParametedType)paramType, true)
						&& areMatchingEventQualifiers(param, injectionPoint)) {
					result.add(m);
				}
			}
		}
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

	/**
	 * This method looks for observed events in all the set of related projects.
	 * To this end, it activates all CDI projects in workspace. 
	 */
	public Set<IInjectionPoint> findObservedEvents(IParameter observedEventParameter) {
		Map<IField, IInjectionPoint> result = new HashMap<IField, IInjectionPoint>();

		if(observedEventParameter.getBeanMethod() instanceof IObserverMethod) {
			synchronized(cache) {
				for (IBean b: cache.getAllBeans()) {
					if(b instanceof IClassBean) {
						collectObserverEvents((IClassBean)b, observedEventParameter, result);
					}
				}
			}
			for (IProject p: ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
				if(p.isAccessible()) {
					CDICorePlugin.getCDI(p, true);
				}
			}
			CDICoreNature[] ns = getNature().getAllDependentProjects(true);
			for (CDICoreNature n: ns) {
				if(n.getDelegate() instanceof CDIProject) {
					CDIProject p = (CDIProject)n.getDelegate();
					for (IBean b: p.getDeclaredBeans()) {
						if(b instanceof IClassBean) {
							collectObserverEvents((IClassBean)b, observedEventParameter, result);
						}
					}
				}					
			}
		}

		return new HashSet<IInjectionPoint>(result.values());
	}

	private void collectObserverEvents(IClassBean b, IParameter observedEventParameter, Map<IField, IInjectionPoint> result) {
		Collection<IInjectionPoint> ps = b.getInjectionPoints();
		for (IInjectionPoint p: ps) {
			if(p instanceof IInjectionPointField) {
				IParametedType eventType = getEventType(p);
				if(eventType != null && ((ParametedType)eventType).isAssignableTo((ParametedType)observedEventParameter.getType(), true)) {
					if(areMatchingEventQualifiers(observedEventParameter, p)) {
						 result.put(((IInjectionPointField)p).getField(), p);
					 }
				}
			}
		}
	}

	public Set<IBeanMethod> resolveDisposers(IProducerMethod producer) {
		Set<IBeanMethod> result = new HashSet<IBeanMethod>();
		IClassBean cb = producer.getClassBean();
		if(cb != null) {

			Collection<IParametedType> types = producer.getLegalTypes();
			Collection<IQualifierDeclaration> qs = producer.getQualifierDeclarations(true);
	
			for (IBeanMethod m: cb.getDisposers()) {
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
		return declaringProject;
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
		return cache.getStereotype(qualifiedName);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getStereotype(org.eclipse.core.runtime.IPath)
	 */
	public StereotypeElement getStereotype(IPath path) {
		return cache.getStereotype(path);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getStereotype(org.eclipse.jdt.core.IType)
	 */
	public StereotypeElement getStereotype(IType type) {
		return getStereotype(type.getPath());
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getInterceptorBindings()
	 */
	public IInterceptorBinding[] getInterceptorBindings() {
		return cache.getInterceptorBindings();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getInterceptorBinding(java.lang.String)
	 */
	public InterceptorBindingElement getInterceptorBinding(String qualifiedName) {
		return cache.getInterceptorBinding(qualifiedName);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getInterceptorBinding(org.eclipse.core.runtime.IPath)
	 */
	public IInterceptorBinding getInterceptorBinding(IPath path) {
		return cache.getInterceptorBinding(path);
	}

	public QualifierElement getQualifier(String qualifiedName) {
		return cache.getQualifier(qualifiedName);
	}

	public QualifierElement getQualifier(IPath path) {
		return cache.getQualifier(path);
	}

	public Set<String> getScopeNames() {
		return cache.getScopeNames();
	}

	public ScopeElement getScope(String qualifiedName) {
		return cache.getScope(qualifiedName);
	}

	public IScope getScope(IPath path) {
		return cache.getScope(path);
	}

	public void update(boolean updateDependent) {
		synchronized (cache) {
			
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

		// DB
		if(dbCache != null) {
			dbCache.rebuild(this, cache.getAllBeans());
		}
		
		Set<IBeanStoreFeature> beanStores = n.getExtensionManager().getFeatures(IBeanStoreFeature.class);
		for (IBeanStoreFeature bp: beanStores) {
			bp.updateCaches(this);
		}

		if(updateDependent) {
			CDICoreNature[] ps = n.getAllDependentProjects();
			for (CDICoreNature p: ps) {
				if(p.getProject() != null && p.getProject().isAccessible() && p.getDelegate() != null) {
					p.getDelegate().update(false);
				}
			}
		}
		CDICorePlugin.fire(new CDIProjectChangeEvent(this));
		
		}
	}

	void rebuildAnnotationTypes() {
		synchronized (cache) {
			
		cache.cleanAnnotations();
		List<AnnotationDefinition> ds = n.getAllAnnotations();
		for (AnnotationDefinition d: ds) {
			if((d.getKind() & AnnotationDefinition.STEREOTYPE) > 0) {
				StereotypeElement s = new StereotypeElement();
				initAnnotationElement(s, d);
				cache.add(s);
			}
			if((d.getKind() & AnnotationDefinition.INTERCEPTOR_BINDING) > 0) {
				InterceptorBindingElement s = new InterceptorBindingElement();
				initAnnotationElement(s, d);
				cache.add(s);
			}
			if((d.getKind() & AnnotationDefinition.QUALIFIER) > 0) {
				QualifierElement s = new QualifierElement();
				initAnnotationElement(s, d);
				cache.add(s);
			}
			if((d.getKind() & AnnotationDefinition.SCOPE) > 0) {
				ScopeElement s = new ScopeElement();
				initAnnotationElement(s, d);
				cache.add(s);
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
		Set<String> vetoedTypes = n.getAllVetoedTypes();
		List<IBean> beans = new ArrayList<IBean>();

		Set<IType> newAllTypes = new HashSet<IType>();
		for (TypeDefinition d: typeDefinitions) {
			newAllTypes.add(d.getType());
		}
		Map<TypeDefinition, ClassBean> newDefinitionToClassbeans = new HashMap<TypeDefinition, ClassBean>();
		Map<IType, IClassBean> newClassBeans = new HashMap<IType, IClassBean>();
		
		int updateLevel = getUpdateLevel(newAllTypes);
	
		ImplementationCollector ic = new ImplementationCollector(typeDefinitions);

		for (TypeDefinition typeDefinition : typeDefinitions) {
			ClassBean bean = cache.definitionToClassbeans.get(typeDefinition);
			if(bean != null && (bean.getDefinition() == typeDefinition)
				&& (updateLevel == 0 || (updateLevel == 1 && typeDefinition.getType().isBinary()))) {
				//Type definitions are rebuilt when changed, otherwise old bean should be reused.
				bean.cleanCache();
			} else {
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
			}
			
			newDefinitionToClassbeans.put(typeDefinition, bean);

			String typeName = typeDefinition.getType().getFullyQualifiedName();
			if(!typeDefinition.isVetoed() 
					    //Type is defined in another project and modified/replaced in config in this (dependent) project
					    //We should reject type definition based on type, but we have to accept 
					&& !(vetoedTypes.contains(typeName) && getNature().getDefinitions().getTypeDefinition(typeName) == null && typeDefinition.getOriginalDefinition() == null)) {
				if(typeDefinition.hasBeanConstructor()) {
					beans.add(bean);
					newClassBeans.put(typeDefinition.getType(), bean);
				} else {
					beans.add(bean);
				}

				for (IProducer producer: bean.getProducers()) {
					beans.add(producer);
				}
			}
		}
	
		for (String builtin: BuiltInBeanFactory.BUILT_IN) {
			IType type = n.getType(builtin);
			if(type != null && type.exists() && !newClassBeans.containsKey(type)) {
				TypeDefinition t = new TypeDefinition();
				t.setType(type, n.getDefinitions(), TypeDefinition.FLAG_NO_ANNOTATIONS);
				t.setBeanConstructor(true);
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

		cache.clean();

		//Variable size of beans-by-type cache.
		int beansByTypeSize = typeDefinitions.size() / 10;
		if(beansByTypeSize < 10) beansByTypeSize = 10;
		if(beansByTypeSize > 367) beansByTypeSize = 367;

		cache.setBeansByTypeSize(beansByTypeSize);

		cache.classBeans = newClassBeans;
		cache.definitionToClassbeans = newDefinitionToClassbeans;
		cache.allTypes = newAllTypes;
		for (IBean bean: beans) {
			addBean(bean);
		}
	
	}
	
	/**
	 * Compares sets this.allTypes and newAllTypes
	 * Returns 
	 *     0 if sets are identical,
	 *         all beans may be reused;
	 *     2 if some binary type is present in exactly one of two sets
	 *         no bean may be reused;
	 *     1 otherwize (if some non-binary types present in exactly one of two sets)
	 *         only beans based binary types may be reused.
	 *     Note that bean will be reused, if its type definition object is not modified.
	 * @param newAllTypes
	 * @return
	 */
	private int getUpdateLevel(Set<IType> newAllTypes) {
		int result = 0;
		for (IType t: cache.getAllTypes()) {
			if(!t.exists() || !newAllTypes.contains(t)) {
				if(t.isBinary()) {
					return 2;
				} else {
					result = 1;
				}
			}
		}
		for (IType t: newAllTypes) {
			if(!cache.containsType(t)) {
				if(t.isBinary()) {
					return 2;
				} else {
					result = 1;
				}
			}
		}
		return result;
	}

	public void addBean(IBean bean) {
		if(((CDIElement)bean).getDeclaringProject() != this && getNature().getClassPath().hasPath(bean.getSourcePath())) {
			//Prevented double bean from library common for this and used project
			return;
		}
		cache.addBean(bean, bean.getDeclaringProject() == this);
	}

	void rebuildXML() {
		synchronized(allBeansXMLData) {
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
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getNamedBeans(boolean)
	 */
	public Collection<IBean> getNamedBeans(boolean attemptToResolveAmbiguousNames) {
		//TODO use a cache for named beans with attemptToResolveAmbiguousNames==true
		Collection<IBean> result = new HashSet<IBean>();
		synchronized (cache) {
			if(attemptToResolveAmbiguousNames) {
				Set<String> names = new HashSet<String>();
				for (IBean bean : cache.getNamedBeans()) {
					if(!names.contains(bean.getName())) {
						Collection<IBean> beans = getBeans(bean.getName(), attemptToResolveAmbiguousNames);
						if(beans.isEmpty()) {
							result.add(bean);
						} else {
							result.addAll(beans);
							names.add(bean.getName());
						}
					}
				}
			} else {
				result.addAll(cache.getNamedBeans());
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBeanManager#getBeans(boolean, org.eclipse.jdt.core.IType, org.eclipse.jdt.core.IType[])
	 */
	public Collection<IBean> getBeans(boolean attemptToResolveAmbiguousDependency,
			IParametedType beanType, IType... qualifiers) {
		Collection<IBean> result = new HashSet<IBean>();
		IParametedType type = beanType;

		for (IBean b: cache.getBeansByLegalType(type)) {
			if(containsType(b.getLegalTypes(), type)) {
				try {
					Collection<IQualifierDeclaration> qsb = b.getQualifierDeclarations(true);
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
	public Collection<IBean> getBeans(boolean attemptToResolveAmbiguousDependency,
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
		return cache.getInjections(fullyQualifiedTypeName);
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

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.ICDIElement#getSimpleJavaName()
	 */
	@Override
	public String getElementName() {
		return getNature().getProject().getName();
	}
}