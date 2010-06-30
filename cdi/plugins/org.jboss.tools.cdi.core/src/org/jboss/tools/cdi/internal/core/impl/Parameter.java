package org.jboss.tools.cdi.internal.core.impl;

import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IMember;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IParametedType;
import org.jboss.tools.cdi.core.IParameter;
import org.jboss.tools.cdi.internal.core.impl.definition.ParameterDefinition;
import org.jboss.tools.common.text.ITextSourceReference;

public class Parameter extends CDIElement implements IParameter {
	ParameterDefinition definition;
	BeanMethod beanMethod;

	public Parameter() {}

	public void setBeanMethod(BeanMethod beanMethod) {
		this.beanMethod = beanMethod;
		setParent(beanMethod);
	}

	public void setDefinition(ParameterDefinition definition) {
		this.definition = definition;
	}

	public Set<IAnnotationDeclaration> getAnnotationDeclarations() {
		throw new RuntimeException("Not implemented because limitations of JDT model.");
	}

	public String getName() {
		return definition.getName();
	}

	public IParametedType getType() {
		return definition.getType();
	}

	public IClassBean getClassBean() {
		return beanMethod.getClassBean();
	}

	public IMember getSourceMember() {
		return definition.getMethodDefinition().getMethod();
	}

	public int getLength() {
		ITextSourceReference p = definition.getPosition();
		return p == null ? 0 : p.getLength();
	}

	public int getStartPosition() {
		ITextSourceReference p = definition.getPosition();
		return p == null ? 0 : p.getStartPosition();
	}

	public IAnnotationDeclaration getAnnotation(String annotationTypeName) {
		throw new RuntimeException("Not implemented because limitations of JDT model.");
	}

	public List<IAnnotationDeclaration> getAnnotations() {
		throw new RuntimeException("Not implemented because limitations of JDT model.");
	}

	public boolean isAnnotationPresent(String annotationTypeName) {
		return definition.isAnnotationPresent(annotationTypeName);
	}

	public ITextSourceReference getAnnotationPosition(String annotationTypeName) {
		return definition.getAnnotationPosition(annotationTypeName);
	}

	public Set<String> getAnnotationTypes() {
		return definition.getAnnotationTypes();
	}

	public String getValue(String annotationTypeName) {
		String text = definition.getAnnotationText(annotationTypeName);
		if(text != null) {
			int i = text.indexOf('(');
			int j = text.lastIndexOf(')');
			if(i >= 0 && j > i) {
				String values = text.substring(i + 1, j).trim();
				if(values.startsWith("\"") && values.endsWith("\"")) {
					return values.substring(1, values.length() - 1);
				}
				//TODO improve
				return values;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IParameter#getBeanMethod()
	 */
	public IBeanMethod getBeanMethod() {
		return beanMethod;
	}
}