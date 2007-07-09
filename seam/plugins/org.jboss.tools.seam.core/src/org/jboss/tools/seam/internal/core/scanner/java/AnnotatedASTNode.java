 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core.scanner.java;

import org.eclipse.jdt.core.dom.ASTNode;

/**
 * This object binds ASTNode to annotations.
 * 
 * @author Viacheslav Kabanovich
 */
public class AnnotatedASTNode<T extends ASTNode> {
	T node;
	ResolvedAnnotation[] annotations = null;
	
	public AnnotatedASTNode(T node) {
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
	
	public T getNode() {
		return node;
	}
	
	public ResolvedAnnotation[] getAnnotations() {
		return annotations;
	}

}
