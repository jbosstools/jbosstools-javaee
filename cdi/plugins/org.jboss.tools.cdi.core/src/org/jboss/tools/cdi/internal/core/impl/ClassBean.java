/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInitializerMethod;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInterceptorBinding;
import org.jboss.tools.cdi.core.IInterceptorBindingDeclaration;
import org.jboss.tools.cdi.core.IObserverMethod;
import org.jboss.tools.cdi.core.IParameter;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.core.IScopeDeclaration;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractTypeDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.FieldDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.MethodDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.java.ITypeDeclaration;
import org.jboss.tools.common.java.ParametedType;
import org.jboss.tools.common.java.TypeDeclaration;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class ClassBean extends AbstractBeanElement implements IClassBean {
	protected ClassBean superClassBean = null;
	protected Map<String, ClassBean> specializingClassBeans = new HashMap<String, ClassBean>();
	
	protected List<BeanField> fields = new ArrayList<BeanField>();
	protected List<BeanMethod> methods = new ArrayList<BeanMethod>();

	protected IScope scope = null;

	public ClassBean() {}

	@Override
	public boolean exists() {
		IType t = getBeanClass();
		return t != null && t.exists();
	}

	public void setDefinition(TypeDefinition definition) {
		setSourcePath(definition.getType().getPath());
		super.setDefinition(definition);
		List<MethodDefinition> ms = definition.getMethods();
		for (MethodDefinition m: ms) {
			if(!m.getMethod().exists()) {
				//update may be run on project that was not rebuilt
				continue;
			}
			BeanMethod bm = null;
			if(m.getProducesAnnotation() != null) {
				bm = newProducerMethod(m);
			} else if(m.getInjectAnnotation() != null) {
				bm = new InitializerMethod();
			} else if(m.isObserver()) {
				bm = new ObserverMethod();
			} else if(m.isDisposer()) {
				bm = new DisposerMethod();
			} else {
				//add other cases
				bm = new BeanMethod();
			}
			bm.setClassBean(this);
			bm.setDefinition(m);
			methods.add(bm);
		}
		List<FieldDefinition> fs = definition.getFields();
		for (FieldDefinition f: fs) {
			if(!f.getField().exists()) {
				//update may be run on project that was not rebuilt
				continue;
			}
			BeanField bf = null;
			if(f.getProducesAnnotation() != null) {
				bf = newProducerField(f);
			} else if(f.getInjectAnnotation() != null) {
				bf = new InjectionPointField();
			} else {
				//add observer case
				bf = new BeanField();
			}
			bf.setClassBean(this);
			bf.setDefinition(f);
			fields.add(bf);
		}
	}

	protected ProducerMethod newProducerMethod(MethodDefinition m) {
		return new ProducerMethod();
	}

	protected ProducerField newProducerField(FieldDefinition f) {
		return new ProducerField();
	}

	public TypeDefinition getDefinition() {
		return (TypeDefinition)definition;
	}

	public ICDIProject getDeclaringProject() {
		ICDIProject result = definition.getDeclaringProject().getDelegate();
		if(result == null) {
			result = getCDIProject();
		}				
		return result;
	}

	public Collection<IBeanMethod> getBeanConstructors() {
		Collection<IBeanMethod> result = new ArrayList<IBeanMethod>();
		IBeanMethod defaultConstructor = null;
		for (BeanMethod m: methods) {
			if(m.getDefinition().isConstructor()) {
				if(m.getAnnotation(CDIConstants.INJECT_ANNOTATION_TYPE_NAME)==null && m.getMethod().getNumberOfParameters()==0) {
					defaultConstructor = m;
				} else {
					result.add(m);
				}
			}
		}
		// If a bean class does not explicitly declare a constructor using @Inject, the constructor that accepts no parameters is the bean constructor.
		if(result.isEmpty() && defaultConstructor!=null) {
			result.add(defaultConstructor);
		}
		return result;
	}

	public void setSuperClassBean(IClassBean bean) {
		if(!(bean instanceof ClassBean) || bean == this) return;

		if(bean.getSuperClassBean() != null) {
			HashSet<IClassBean> beans = new HashSet<IClassBean>();
			beans.add(this);
			IClassBean b = bean;
			while(b != null) {
				if(beans.contains(b)) {
					bean = null;
					break;
				}
				b = b.getSuperClassBean();
			}
		}		
		
		superClassBean = (ClassBean)bean;
		if(superClassBean != null && isSpecializing()) {
			superClassBean.addSpecializingClassBean(this);
		}
		if(superClassBean != null) {
			Map<String, ProducerMethod> thisProducers = getProducerMethodsForSignatures();
			Map<String, ProducerMethod> superProducers = superClassBean.getProducerMethodsForSignatures();
			for (String s: thisProducers.keySet()) {
				ProducerMethod thisProducer = thisProducers.get(s);
				ProducerMethod superProducer = superProducers.get(s);
				if(thisProducer != null && superProducer != null) {
					if(thisProducer.getSpecializesAnnotationDeclaration() != null) {
						thisProducer.setSpecializedBean(superProducer);
					}
				}
			}			
		}
	}

	Map<String, ProducerMethod> getProducerMethodsForSignatures() {
		Map<String, ProducerMethod> result = new HashMap<String, ProducerMethod>();
		for (BeanMethod b: methods) {
			if(b instanceof ProducerMethod) {
				String s = b.getMethod().getElementName();
				try {
					s += ":" + b.getMethod().getSignature();
				} catch (JavaModelException e) {
					CDICorePlugin.getDefault().logError(e);
				}
				result.put(s, (ProducerMethod)b);
			}
		}
		return result;
	}

	 synchronized void addSpecializingClassBean(ClassBean bean) {
		if(specializingClassBeans == null) {
			specializingClassBeans = new Hashtable<String, ClassBean>();
		}
		specializingClassBeans.put(bean.getBeanClass().getFullyQualifiedName(), bean);
	}

	public ClassBean getSuperClassBean() {
		return superClassBean;
	}

	public Collection<IBeanMethod> getDisposers() {
		Collection<IBeanMethod> result = new ArrayList<IBeanMethod>();
		for (BeanMethod m: methods) {
			if(m.isDisposer()) {
				result.add(m);
			}
		}
		return result;
	}

	public static Collection<IInterceptorBindingDeclaration> getInterceptorBindingDeclarations(AbstractMemberDefinition definition) {
		Collection<IInterceptorBindingDeclaration> result = new ArrayList<IInterceptorBindingDeclaration>();
		List<IAnnotationDeclaration> as = definition.getAnnotations();
		for (IAnnotationDeclaration a: as) {
			if(a instanceof InterceptorBindingDeclaration) {
				result.add((InterceptorBindingDeclaration)a);
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IClassBean#getInterceptorBindings()
	 */
	public Collection<IInterceptorBinding> getInterceptorBindings() {
		return CDIUtil.getAllInterceptorBindings(this);
	}

	public Collection<IObserverMethod> getObserverMethods() {
		Collection<IObserverMethod> result = new ArrayList<IObserverMethod>();
		for (BeanMethod m: methods) {
			if(m.isObserver() && m instanceof IObserverMethod) {
				result.add((IObserverMethod)m);
			}
		}
		return result;
	}

	public Collection<IProducer> getProducers() {
		Collection<IProducer> result = new ArrayList<IProducer>();
		for (BeanMethod m: methods) {
			if(m instanceof IProducer) {
				result.add((IProducer)m);
			}
		}
		for (BeanField f: fields) {
			if(f instanceof IProducer) {
				result.add((IProducer)f);
			}
		}
		return result;
	}

	public Collection<ITypeDeclaration> getAllTypeDeclarations() {
		Collection<IParametedType> ps = getDefinition().getInheritedTypes();
		Set<ITypeDeclaration> result = new HashSet<ITypeDeclaration>();
		for (IParametedType p: ps) {
			if(p instanceof TypeDeclaration) {
				result.add((TypeDeclaration)p);
			}
		}
		IParametedType p = getDefinition().getParametedType();
		if(p != null) {
			if(p instanceof TypeDeclaration) {
				result.add((TypeDeclaration)p);
			}
		}
		return result;
	}

	public IAnnotationDeclaration getAlternativeDeclaration() {
		return getDefinition().getAlternativeAnnotation();
	}

	public IType getBeanClass() {
		return ((TypeDefinition)definition).getType();
	}

	@Override
	public Collection<IInitializerMethod> getInitializers() {
		Collection<IInitializerMethod> result = new ArrayList<IInitializerMethod>();
		for (BeanMethod m: methods) {
			if(m instanceof IInitializerMethod) {
				result.add((IInitializerMethod)m);
			}
		}
		return result;
	}

	public Collection<IInjectionPoint> getInjectionPoints() {
		return getInjectionPoints(true);
	}

	/**
	 * If all=false, injection points of producer methods are not included.
	 * 
	 * @param all
	 * @return
	 */
	public Collection<IInjectionPoint> getInjectionPoints(boolean all) {
		Collection<IInjectionPoint> result = new ArrayList<IInjectionPoint>();
		for (BeanField f: fields) {
			if(f instanceof IInjectionPoint) {
				result.add((IInjectionPoint)f);
			}
		}
		for (BeanMethod m: methods) {
			if(!all && (m instanceof IBean)) {
				continue;
			}
			List<IParameter> ps = m.getParameters();
			for (IParameter p: ps) {
				if(p instanceof IInjectionPoint) {
					result.add((IInjectionPoint)p);
				}
			}
			
		}
		return result;
	}

	public Collection<IParametedType> getLegalTypes() {
		Set<IParametedType> result = new HashSet<IParametedType>();
		AnnotationDeclaration d = getDefinition().getTypedAnnotation();
		Collection<IParametedType> all = getAllTypes();
		if(d != null) {
			result.addAll(getRestrictedTypeDeclarations(all));
			ParametedType object = getObjectType(getBeanClass());
			if(object != null) {
				result.add(object);
			}
			return result;
		}
		return all;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBean#getAllTypes()
	 */
	public Collection<IParametedType> getAllTypes() {
		return getDefinition().getAllTypes();
	}

	public Collection<ITypeDeclaration> getRestrictedTypeDeclaratios() {
		return getRestrictedTypeDeclarations(getAllTypes());
	}

	public String getName() {
		ClassBean specialized = getSpecializedBean();
		if(specialized != null) {
			String name = specialized.getName();
			if(name != null) {
				return name;
			}
		}
	
		AnnotationDeclaration named = findNamedAnnotation();
		if(named == null) return null;

		String name = ((AbstractTypeDefinition)definition).getType().getElementName();
		if(name.length() > 0) {
			name = name.substring(0, 1).toLowerCase() + name.substring(1);
		}

		Object value = named.getMemberValue(null);
		if(value != null && value.toString().trim().length() > 0) {
			return value.toString().trim();
		}
		return name;
	}

	public ITextSourceReference getNameLocation(boolean stereotypeLocation) {
		return (stereotypeLocation) ? CDIUtil.getNamedDeclaration(this) : findNamedAnnotation();
	}

	public ClassBean getSpecializedBean() {
		if(getDefinition().getSpecializesAnnotation() == null) {
			return null;
		}
		return superClassBean;
	}

	public synchronized Collection<ClassBean> getSpecializingBeans() {
		return specializingClassBeans == null ? Collections.<ClassBean>emptySet() : specializingClassBeans.values();
	}

	public IAnnotationDeclaration getSpecializesAnnotationDeclaration() {
		return getDefinition().getSpecializesAnnotation();
	}

	public boolean isDependent() {
		IScope scope = getScope();
		return scope != null && CDIConstants.DEPENDENT_ANNOTATION_TYPE_NAME.equals(scope.getSourceType().getFullyQualifiedName());
	}

	synchronized boolean hasEnabledSpecializingClassBean() {
		if(specializingClassBeans != null) {
			for (ClassBean sb: specializingClassBeans.values()) {
				if(sb.hasEnabledSpecializingClassBean() || sb.isEnabled()) return true;
			}
		}
		return false;
	}

	public boolean isEnabled() {
		if(hasEnabledSpecializingClassBean()) {
			return false;
		}
		if(isAlternative()) {
			if(getCDIProject().isClassAlternativeActivated(getDefinition().getQualifiedName())) {
				return true;
			}
			for (IStereotypeDeclaration d: getStereotypeDeclarations(true)) {
				IStereotype s = d.getStereotype();
				if(s != null && s.isAlternative() && !getCDIProject().getAlternatives(s.getSourceType().getFullyQualifiedName()).isEmpty()) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	public boolean isSpecializing() {
		return getDefinition().getSpecializesAnnotation() != null;
	}

	public IScope getScope() {
		if(scope == null) {
			computeScope();
		} 
		return scope;
	}

	protected void computeScope() {
		//1. Declaration of scope in the class.
		Collection<IScopeDeclaration> scopes = getScopeDeclarations();
		if(!scopes.isEmpty()) {
			scope = scopes.iterator().next().getScope();
			return;
		}
		//2. Declaration of inheritable scope in a superclass.
		ClassBean scb = getSuperClassBean();
		while(scb != null) {
			scopes = scb.getScopeDeclarations();
			if(!scopes.isEmpty()) {
				scope = scopes.iterator().next().getScope();
				if(scope.getInheritedDeclaration() == null) {
					scope = getCDIProject().getScope(CDIConstants.DEPENDENT_ANNOTATION_TYPE_NAME);
					return;
				} else {
					return;
				}
			}
			scb = scb.getSuperClassBean();
		}
		//3. Get default scope from stereotype.
		Set<IScope> defaults = new HashSet<IScope>();
		for (IStereotypeDeclaration d: getStereotypeDeclarations()) {
			IStereotype s = d.getStereotype();
			IScope sc = s.getScope();
			if(sc != null) {
				defaults.add(sc);
			}
		}
		scb = getSuperClassBean();
		while(scb != null) {
			for (IStereotypeDeclaration d: scb.getStereotypeDeclarations()) {
				IStereotype s = d.getStereotype();
				if(s.getInheritedDeclaration() == null) {
					continue;
				}
				IScope sc = s.getScope();
				if(sc != null) {
					defaults.add(sc);
				}
			}
			scb = scb.getSuperClassBean();
		}
		if(defaults.size() == 1) {
			scope = defaults.iterator().next();
		} else if(defaults.size() > 1) {
			scope = getCDIProject().getScope(CDIConstants.DEPENDENT_ANNOTATION_TYPE_NAME);
		} else {
			//4. Scope is @Dependent
			scope = getCDIProject().getScope(CDIConstants.DEPENDENT_ANNOTATION_TYPE_NAME);
		}
	}

	protected Collection<IQualifierDeclaration> getInheritedQualifierDeclarations() {
		if(superClassBean == null) return Collections.emptySet();
		Collection<IQualifierDeclaration> result = new ArrayList<IQualifierDeclaration>();
		for (IQualifierDeclaration d: superClassBean.getQualifierDeclarations(true)) {
			if(d.getQualifier() != null && d.getQualifier().getInheritedDeclaration() != null) {
				result.add(d);
			} else if(isSpecializing()) {
				result.add(d);
			}
		}
		return result;
	}

	protected Collection<IInterceptorBindingDeclaration> getInheritedInterceptorBindingDeclarations() {
		if(superClassBean == null) return Collections.emptyList();
		Set<IInterceptorBindingDeclaration> result = new HashSet<IInterceptorBindingDeclaration>();
		for (IInterceptorBindingDeclaration d: superClassBean.getInterceptorBindingDeclarations(true)) {
			if(d.getInterceptorBinding() != null && d.getInterceptorBinding().getInheritedDeclaration() != null) {
				result.add(d);
			} else if(isSpecializing()) {
				result.add(d);
			}
		}
		return result;
	}

	public Set<IStereotypeDeclaration> getInheritedStereotypDeclarations() {
		if(superClassBean == null) return Collections.emptySet();
		Set<IStereotypeDeclaration> result = new HashSet<IStereotypeDeclaration>();
		for (IStereotypeDeclaration d: superClassBean.getStereotypeDeclarations(true)) {
			if(d.getStereotype() != null && d.getStereotype().getInheritedDeclaration() != null) {
				result.add(d);
			} else if(isSpecializing()) {
				result.add(d);
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBean#isNullable()
	 */
	public boolean isNullable() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBean#isSelectedAlternative()
	 */
	public boolean isSelectedAlternative() {
		if(getDefinition().getAlternativeAnnotation() != null && getCDIProject().isTypeAlternative(getBeanClass().getFullyQualifiedName())) {
			return true;
		}
		for (IStereotypeDeclaration d: getStereotypeDeclarations(true)) {
			IStereotype s = d.getStereotype();
			if(s != null && s.isAlternative() && 
					getCDIProject().isStereotypeAlternative(s.getSourceType().getFullyQualifiedName())	) return true;
		}		
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IClassBean#getAllMethods()
	 */
	public Collection<IBeanMethod> getAllMethods() {
		return new ArrayList<IBeanMethod>(methods);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBean#getSimpleJavaName()
	 */
	@Override
	public String getElementName() {
		String result = getBeanClass().getElementName();
		if(result.length() == 0) {
			result = getBeanClass().getFullyQualifiedName();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IClassBean#getSuperType()
	 */
	public ParametedType getSuperType() {
		return getDefinition().getSuperType();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.java.IJavaMemberReference#getSourceMember()
	 */
	@Override
	public IMember getSourceMember() {
		return getBeanClass();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.java.IJavaReference#getSourceElement()
	 */
	@Override
	public IJavaElement getSourceElement() {
		return getSourceMember();
	}

	public synchronized void cleanCache() {
		specializingClassBeans = null;
		scope = null;
		getDefinition().resetParametedType();
		for (BeanMethod m: methods) {
			m.setMethod(m.getMethod()); // type update
		}
		for (BeanField f: fields) {
			f.setField(f.getField()); // type update
		}
	}
}