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
package org.jboss.tools.batch.internal.core.impl;

import org.jboss.tools.common.java.impl.AnnotationDeclaration;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BatchAnnotationDeclaration extends AnnotationDeclaration {
	protected BatchProject project;

	public BatchAnnotationDeclaration() {}

	public BatchAnnotationDeclaration(BatchAnnotationDeclaration d) {
		d.copyTo(this);
	}

	protected void copyTo(BatchAnnotationDeclaration other) {
		other.project = project;
		other.annotation = annotation;
		other.values = values;
		other.constants = constants;
	}

	public void setProject(BatchProject project) {
		this.project = project;
	}

}