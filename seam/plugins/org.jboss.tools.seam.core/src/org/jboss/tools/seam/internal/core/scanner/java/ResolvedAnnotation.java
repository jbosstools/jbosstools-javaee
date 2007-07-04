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

import org.eclipse.jdt.core.dom.Annotation;

/**
 * This object keeps resolved type name for annotation.
 * 
 * @author Viacheslav Kabanovich
 */
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
