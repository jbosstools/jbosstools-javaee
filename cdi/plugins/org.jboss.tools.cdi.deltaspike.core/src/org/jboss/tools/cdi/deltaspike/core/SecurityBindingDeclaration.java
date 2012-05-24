/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.deltaspike.core;

import org.jboss.tools.common.java.IAnnotationDeclaration;

/**
 * 
 * @author Viacheslav Kabanovih
 *
 */
public class SecurityBindingDeclaration {
	IAnnotationDeclaration declaration;
	IAnnotationDeclaration binding;

	public SecurityBindingDeclaration(IAnnotationDeclaration declaration, IAnnotationDeclaration binding) {
		this.declaration = declaration;
		this.binding = binding;
	}

	public IAnnotationDeclaration getDeclaration() {
		return declaration;
	}

	public IAnnotationDeclaration getBinding() {
		return binding;
	}

}
