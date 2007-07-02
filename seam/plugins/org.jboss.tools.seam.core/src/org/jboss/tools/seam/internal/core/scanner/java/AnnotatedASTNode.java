package org.jboss.tools.seam.internal.core.scanner.java;

import org.eclipse.jdt.core.dom.ASTNode;

public class AnnotatedASTNode {
	ASTNode node;
	ResolvedAnnotation[] annotations = null;
	
	public AnnotatedASTNode(ASTNode node) {
		this.node = node;
	}
	
	public void addAnnotation(ResolvedAnnotation annotation) {
		if(annotations == null) {
			annotations = new ResolvedAnnotation[]{annotation};
		} else {
			ResolvedAnnotation[] a = new ResolvedAnnotation[annotations.length + 1];
			System.arraycopy(annotations, 0, a, 0, annotations.length);
			a[annotations.length] = annotation;
			annotations = a;
		}
	}
	
	public ASTNode getNode() {
		return node;
	}
	
	public ResolvedAnnotation[] getAnnotations() {
		return annotations;
	}

}
