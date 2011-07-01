/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.internal.core.impl;

import org.jboss.tools.cdi.core.CDICoreNature;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class AnnotationDeclaration extends org.jboss.tools.common.java.impl.AnnotationDeclaration {
	protected CDICoreNature project;

	public AnnotationDeclaration() {}

	public AnnotationDeclaration(AnnotationDeclaration d) {
		d.copyTo(this);
	}

	protected void copyTo(AnnotationDeclaration other) {
		other.project = project;
		other.annotation = annotation;
	}

	public void setProject(CDICoreNature project) {
		this.project = project;
	}

}