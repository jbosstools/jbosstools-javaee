package org.jboss.tools.cdi.internal.core.impl;

import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IInjectionPointField;

public class InjectionPointField extends BeanField implements IInjectionPointField {

	public InjectionPointField() {}

	public IAnnotationDeclaration getDecoratorAnnotation() {
		return decorator;
	}

	public IType getType() {
		return typeDeclaration == null ? null : typeDeclaration.getType();
	}

	public boolean isDelegate() {
		return delegate != null;
	}

}
