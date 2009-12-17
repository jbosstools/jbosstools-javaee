/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.internal.core.impl;

import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IQualifierDeclaration;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class QualifierDeclaration extends AnnotationDeclaration implements IQualifierDeclaration {

	public QualifierDeclaration() {}

	public QualifierDeclaration(AnnotationDeclaration d) {
		d.copyTo(this);
	}

	public IQualifier getQualifier() {
		return project.getDelegate().getQualifier(getTypeName());
	}

}
