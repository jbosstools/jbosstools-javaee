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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInterceptorBinding;
import org.jboss.tools.cdi.core.IInterceptorBindingDeclaration;
import org.jboss.tools.cdi.core.IParametedType;
import org.jboss.tools.cdi.core.IParameter;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.core.IScopeDeclaration;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.cdi.core.ITypeDeclaration;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.FieldDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.MethodDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.common.model.project.ext.impl.ValueInfo;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class ClassBean extends AbstractBeanElement implements IClassBean {
	protected ClassBean superClassBean = null;
	protected Set<ClassBean> specializingClassBeans = new HashSet<ClassBean>();
	
	protected List<BeanField> fields = new ArrayList<BeanField>();
	protected List<BeanMethod> methods = new ArrayList<BeanMethod>();

	protected IScope scope = null;

	public ClassBean() {}

	public void setDefinition(TypeDefinition definition) {
		setSourcePath(definition.getType().getPath());
		super.setDefinition(definition);
		List<MethodDefinition> ms = definition.getMethods();
		for (MethodDefinition m: ms) {
			BeanMethod bm = null;
			if(m.getProducesAnnotation() != null) {
				bm = new ProducerMethod();
			} else if(m.getInjectAnnotation() != null) {
				bm = new InjectionPointMethod();
			} else if(m.isObserver()) {
				bm = new ObserverMethod();
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
			BeanField bf = null;
			if(f.getProducesAnnotation() != null) {
				bf = new ProducerField();
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

	public TypeDefinition getDefinition() {
		return (TypeDefinition)definition;
	}

	public Set<IBeanMethod> getBeanConstructors() {
		Set<IBeanMethod> result = new HashSet<IBeanMethod>();
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

	public void setSuperClassBean(ClassBean bean) {
		superClassBean = bean;
		if(superClassBean != null && isSpecializing()) {
			superClassBean.addSpecializingClassBean(this);
		}
		if(bean != null) {
			Map<String, ProducerMethod> thisProducers = getProducerMethodsForSignatures();
			Map<String, ProducerMethod> superProducers = bean.getProducerMethodsForSignatures();
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

	void addSpecializingClassBean(ClassBean bean) {
		specializingClassBeans.add(bean);
	}

	public ClassBean getSuperClassBean() {
		return superClassBean;
	}

	public Set<IBeanMethod> getDisposers() {
		Set<IBeanMethod> result = new HashSet<IBeanMethod>();
		for (BeanMethod m: methods) {
			if(m.isDisposer()) {
				result.add(m);
			}
		}
		return result;
	}

	public static Set<IInterceptorBindingDeclaration> getInterceptorBindingDeclarations(AbstractMemberDefinition definition) {
		Set<IInterceptorBindingDeclaration> result = new HashSet<IInterceptorBindingDeclaration>();
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
	public Set<IInterceptorBinding> getInterceptorBindings() {
		return CDIUtil.getAllInterceptorBindings(this);
	}

	public Set<IBeanMethod> getObserverMethods() {
		Set<IBeanMethod> result = new HashSet<IBeanMethod>();
		for (BeanMethod m: methods) {
			if(m.isObserver()) {
				result.add(m);
			}
		}
		return result;
	}

	public Set<IProducer> getProducers() {
		Set<IProducer> result = new HashSet<IProducer>();
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

	public Set<ITypeDeclaration> getAllTypeDeclarations() {
		Set<IParametedType> ps = getDefinition().getInheritedTypes();
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

	public Set<IInjectionPoint> getInjectionPoints() {
		Set<IInjectionPoint> result = new HashSet<IInjectionPoint>();
		for (BeanField f: fields) {
			if(f instanceof IInjectionPoint) {
				result.add((IInjectionPoint)f);
			}
		}
		for (BeanMethod m: methods) {
			if(m instanceof IInjectionPoint) {
				result.add((IInjectionPoint)m);
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

	public Set<IParametedType> getLegalTypes() {
		Set<IParametedType> result = new HashSet<IParametedType>();
		AnnotationDeclaration d = getDefinition().getTypedAnnotation();
		Set<IParametedType> all = getAllTypes();
		if(d != null) {
			Set<ITypeDeclaration> ts = getRestrictedTypeDeclarations(all);
			result.addAll(ts);
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
	public Set<IParametedType> getAllTypes() {
		return getDefinition().getAllTypes();
	}

	public Set<ITypeDeclaration> getRestrictedTypeDeclaratios() {
		return getRestrictedTypeDeclarations(getAllTypes());
	}

	public String getName() {
		ClassBean specialized = getSpecializedBean();
		if(specialized != null) {
			return specialized.getName();
		}
		AnnotationDeclaration named = findNamedAnnotation();
		if(named == null) return null;

		String name = ((TypeDefinition)definition).getType().getElementName();
		if(name.length() > 0) {
			name = name.substring(0, 1).toLowerCase() + name.substring(1);
		}

		IAnnotation a = named.getDeclaration();
		try {
			IMemberValuePair[] vs = a.getMemberValuePairs();
			if(vs != null && vs.length > 0) {
				Object value = vs[0].getValue();
				if(value != null && value.toString().trim().length() > 0) {
					return value.toString().trim();
				}
			}
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return name;
	}

	public ITextSourceReference getNameLocation() {
		AnnotationDeclaration named = getDefinition().getNamedAnnotation();
		if(named != null) {
			return ValueInfo.getValueInfo(named.getDeclaration(), null);
		}
		return null;
	}

	public ClassBean getSpecializedBean() {
		if(getDefinition().getSpecializesAnnotation() == null) {
			return null;
		}
		return superClassBean;
	}

	public Set<ClassBean> getSpecializingBeans() {
		return specializingClassBeans;
	}

	public IAnnotationDeclaration getSpecializesAnnotationDeclaration() {
		return getDefinition().getSpecializesAnnotation();
	}

	public boolean isDependent() {
		IScope scope = getScope();
		return scope != null && CDIConstants.DEPENDENT_ANNOTATION_TYPE_NAME.equals(scope.getSourceType().getFullyQualifiedName());
	}

	boolean hasEnabledSpecializingClassBean() {
		for (ClassBean sb: specializingClassBeans) {
			if(sb.hasEnabledSpecializingClassBean() || sb.isEnabled()) return true;
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
			Set<IStereotypeDeclaration> ds = getStereotypeDeclarations();
			for (IStereotypeDeclaration d: ds) {
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
		Set<IScopeDeclaration> scopes = getScopeDeclarations();
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
		Set<IStereotypeDeclaration> ss = getStereotypeDeclarations();
		for (IStereotypeDeclaration d: ss) {
			IStereotype s = d.getStereotype();
			IScope sc = s.getScope();
			if(sc != null) {
				defaults.add(sc);
			}
		}
		scb = getSuperClassBean();
		while(scb != null) {
			ss = scb.getStereotypeDeclarations();
			for (IStereotypeDeclaration d: ss) {
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

	protected Set<IQualifierDeclaration> getInheritedQualifierDeclarations() {
		if(superClassBean == null) return Collections.emptySet();
		Set<IQualifierDeclaration> result = new HashSet<IQualifierDeclaration>();
		Set<IQualifierDeclaration> ds = superClassBean.getQualifierDeclarations(true);
		for (IQualifierDeclaration d: ds) {
			if(d.getQualifier() != null && d.getQualifier().getInheritedDeclaration() != null) {
				result.add(d);
			} else if(isSpecializing()) {
				result.add(d);
			}
		}
		return result;
	}

	protected Set<IInterceptorBindingDeclaration> getInheritedInterceptorBindingDeclarations() {
		if(superClassBean == null) return Collections.emptySet();
		Set<IInterceptorBindingDeclaration> result = new HashSet<IInterceptorBindingDeclaration>();
		Set<IInterceptorBindingDeclaration> ds = superClassBean.getInterceptorBindingDeclarations(true);
		for (IInterceptorBindingDeclaration d: ds) {
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
		Set<IStereotypeDeclaration> ds = superClassBean.getStereotypeDeclarations(true);
		for (IStereotypeDeclaration d: ds) {
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
		Set<IStereotypeDeclaration> ds = getStereotypeDeclarations();
		for (IStereotypeDeclaration d: ds) {
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
	public Set<IBeanMethod> getAllMethods() {
		Set<IBeanMethod> result = new HashSet<IBeanMethod>();
		result.addAll(methods);
		return result;
	}
}