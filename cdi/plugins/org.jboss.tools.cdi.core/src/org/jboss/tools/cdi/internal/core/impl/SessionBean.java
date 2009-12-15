package org.jboss.tools.cdi.internal.core.impl;

import org.eclipse.jdt.core.IAnnotation;
import org.jboss.tools.cdi.core.ISessionBean;

public class SessionBean extends ClassBean implements ISessionBean {

	public IAnnotation getStatefulDeclaration() {
		AnnotationDeclaration stateful = getDefinition().getStatefulAnnotation();
		return stateful != null ? stateful.getDeclaration() : null;
	}

	public boolean isStateful() {
		return getDefinition().getStatefulAnnotation() != null;
	}

}
