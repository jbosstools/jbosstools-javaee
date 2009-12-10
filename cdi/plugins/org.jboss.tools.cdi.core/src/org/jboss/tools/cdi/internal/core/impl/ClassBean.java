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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInterceptorBindingDeclaration;
import org.jboss.tools.cdi.core.IObserverMethod;
import org.jboss.tools.cdi.core.IParametedType;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.cdi.core.ITypeDeclaration;
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
	protected List<BeanField> fields = new ArrayList<BeanField>();
	protected List<BeanMethod> methods = new ArrayList<BeanMethod>();

	public ClassBean() {}

	public void setDefinition(TypeDefinition definition) {
		super.setDefinition(definition);
		setAnnotations(definition.getAnnotations());
		List<MethodDefinition> ms = definition.getMethods();
		for (MethodDefinition m: ms) {
			BeanMethod bm = null;
			if(m.getProducesAnnotation() != null) {
				bm = new ProducerMethod();
			} else {
				//add observer case
				bm = new BeanMethod();
			}
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
			bf.setDefinition(f);
			fields.add(bf);
		}
	}

	public Set<IBeanMethod> getBeanConstructor() {
		Set<IBeanMethod> result = new HashSet<IBeanMethod>();
		for (BeanMethod m: methods) {
			if(m.getDefinition().isConstructor()) {
				result.add(m);
			}
		}
		return result;
	}

	public Set<IBeanMethod> getDisposers() {
		// TODO 
		return new HashSet<IBeanMethod>();
	}

	public Set<IInterceptorBindingDeclaration> getInterceptorBindings() {
		Set<IInterceptorBindingDeclaration> result = new HashSet<IInterceptorBindingDeclaration>();
		List<AnnotationDeclaration> as = definition.getAnnotations();
		for (AnnotationDeclaration a: as) {
			if(a instanceof InterceptorBindingDeclaration) {
				result.add((InterceptorBindingDeclaration)a);
			}
		}
		return result;
	}

	public Set<IObserverMethod> getObserverMethods() {
		Set<IObserverMethod> result = new HashSet<IObserverMethod>();
		for (BeanMethod m: methods) {
			if(m instanceof IObserverMethod) {
				result.add((IObserverMethod)m);
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
		// TODO Auto-generated method stub
		return null;
	}

	public IAnnotationDeclaration getAlternativeDeclaration() {
		return alternative;
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
			//get parameters that are injection points
		}
		return result;
	}

	public Set<IParametedType> getLegalTypes() {
		// TODO 
		return null;
	}

	public String getName() {
		String name = ((TypeDefinition)definition).getQualifiedName();
		if(name.length() > 0) {
			name = name.substring(0, 1).toLowerCase() + name.substring(1);
		}
		if(named == null) {
			return name;
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
		if(named != null) {
			return ValueInfo.getValueInfo(named.getDeclaration(), null);
		}
		return null;
	}

	public Set<ITypeDeclaration> getRestrictedTypeDeclaratios() {
		// TODO Auto-generated method stub
		return null;
	}

	public IBean getSpecializedBean() {
		// TODO Auto-generated method stub
		return null;
	}

	public IAnnotationDeclaration getSpecializesAnnotationDeclaration() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isAlternative() {
		if(alternative != null) return true;
		Set<IStereotypeDeclaration> ds = getStereotypeDeclarations();
		for (IStereotypeDeclaration d: ds) {
			IStereotype s = d.getStereotype();
			if(s != null && s.isAlternative()) return true;
		}		
		return false;
	}

	public boolean isDependent() {
		IType scope = getScope();
		return scope != null && CDIConstants.DEPENDENT_ANNOTATION_TYPE_NAME.equals(scope.getFullyQualifiedName());
	}

	public boolean isEnabled() {
		// TODO 
		return false;
	}

	public boolean isSpecializing() {
		return specializes != null;
	}

	public IType getScope() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<IAnnotationDeclaration> getScopeDeclarations() {
		// TODO Auto-generated method stub
		return null;
	}

}
