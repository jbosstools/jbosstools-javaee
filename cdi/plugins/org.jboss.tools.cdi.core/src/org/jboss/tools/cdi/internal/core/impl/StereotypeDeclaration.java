package org.jboss.tools.cdi.internal.core.impl;

import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;

public class StereotypeDeclaration extends AnnotationDeclaration implements IStereotypeDeclaration {
	protected StereotypeElement stereotype;

	public StereotypeDeclaration(AnnotationDeclaration d) {
		annotation = d.annotation;
		startPosition = d.startPosition;
		length = d.length;
		annotationTypeName = d.annotationTypeName;
		type = d.type;
	}

	public IStereotype getStereotype() {
		return stereotype;
	}

	public void setStereotype(StereotypeElement stereotype) {
		this.stereotype = stereotype;
	}

}
