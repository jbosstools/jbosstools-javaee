package org.jboss.tools.seam.internal.core.scanner.java;

import org.eclipse.jdt.core.dom.Annotation;

public class ResolvedAnnotation {
	String type;
	Annotation annotation;
	
	public ResolvedAnnotation(String type, Annotation annotation) {
		this.type = type;
		this.annotation = annotation;
	}
	
	public String getType() {
		return type;
	}
	
	public Annotation getAnnotation() {
		return annotation;
	}

}
