package org.jboss.tools.cdi.internal.core.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceReference;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IParameter;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.internal.core.impl.definition.ParameterDefinition;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IParametedType;

public class Parameter extends BeanMember implements IParameter {
	BeanMethod beanMethod;

	public Parameter() {}

	public ParameterDefinition getDefinition() {
		return (ParameterDefinition)definition;
	}

	public void setBeanMethod(BeanMethod beanMethod) {
		this.beanMethod = beanMethod;
		setParent(beanMethod);
	}

	public void setDefinition(ParameterDefinition definition) {
		super.setDefinition(definition);
		this.definition = definition;
	}

	public void setLocalVariable(ILocalVariable v) {
		setMember(v);
	}

	public String getName() {
		return getDefinition().getName();
	}

	public IParametedType getMemberType() {
		return getDefinition().getType();
	}

	public IParametedType getType() {
		if(getDefinition().getOverridenType() != null) {
			return getDefinition().getOverridenType();
		}
		return getDefinition().getType();
	}

	public IClassBean getClassBean() {
		return beanMethod.getClassBean();
	}

	public IMember getSourceMember() {
		return getDefinition().getMethodDefinition().getMethod();
	}

	protected ISourceReference getSourceReference() {
		return getDefinition().getVariable();
	}

	public Set<String> getAnnotationTypes() {
		return getDefinition().getAnnotationTypes();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IParameter#getBeanMethod()
	 */
	public IBeanMethod getBeanMethod() {
		return beanMethod;
	}

	public Set<IQualifier> getQualifiers() {
		Set<IQualifier> result = new HashSet<IQualifier>();
		Set<String> as = getAnnotationTypes();
		for (String s: as) {
			IQualifier q = getCDIProject().getQualifier(s);
			if (q != null) result.add(q);
		}
		return result;
	}

	public Set<IQualifierDeclaration> getQualifierDeclarations() {
		Set<IQualifierDeclaration> result = new HashSet<IQualifierDeclaration>();
		
		List<IAnnotationDeclaration> ds = definition.getAnnotations();
		for (IAnnotationDeclaration d: ds) {
			if(d instanceof IQualifierDeclaration) {
				result.add((IQualifierDeclaration)d);
			}
		}
		
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.ICDIElement#getSimpleJavaName()
	 */
	@Override
	public String getElementName() {
		return getName();
	}

	@Override
	public boolean isDeclaredFor(IJavaElement element) {
		if(getDefinition().getVariable() == element) {
			return true;
		}
		if(element instanceof ILocalVariable) {
			ILocalVariable vThat = (ILocalVariable)element;
			return getName().equals(vThat.getElementName()) && getBeanMethod().isDeclaredFor(vThat.getDeclaringMember());
		}
		return false;
	}
}