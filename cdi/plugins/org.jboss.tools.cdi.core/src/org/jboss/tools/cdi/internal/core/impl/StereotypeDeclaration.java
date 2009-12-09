package org.jboss.tools.cdi.internal.core.impl;

import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;

public class StereotypeDeclaration extends AnnotationDeclaration implements IStereotypeDeclaration {

	public StereotypeDeclaration() {}

	public StereotypeDeclaration(AnnotationDeclaration d) {
		d.copyTo(this);
	}

	public IStereotype getStereotype() {
		return project.getDelegate().getStereotype(getTypeName());
	}

}
