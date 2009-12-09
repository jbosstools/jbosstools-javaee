package org.jboss.tools.cdi.internal.core.impl;

import org.jboss.tools.cdi.core.IInterceptorBinding;
import org.jboss.tools.cdi.core.IInterceptorBindingDeclaration;

public class InterceptorBindingDeclaration extends AnnotationDeclaration
		implements IInterceptorBindingDeclaration {

	public InterceptorBindingDeclaration(AnnotationDeclaration d) {
		d.copyTo(this);
	}

	public IInterceptorBinding getInterceptorBinding() {
		return project.getDelegate().getInterceptorBinding(getTypeName());
	}

}
