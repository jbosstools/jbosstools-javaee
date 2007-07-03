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
package org.jboss.tools.seam.internal.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.seam.core.ISeamAnnotatedFactory;
import org.jboss.tools.seam.core.ScopeType;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamAnnotatedFactory extends SeamFactory implements ISeamAnnotatedFactory {
	IMethod javaSource = null;

	public IMethod getSourceMethod() {
		return javaSource;
	}
	
	public void setMethod(IMethod method) {
		this.javaSource = method;
	}

	public IMember getSourceMember() {
		return javaSource;
	}

	public int getLength() {
		if(javaSource == null) return 0;
		try {
			if(javaSource.getSourceRange() == null) return 0;
			return javaSource.getSourceRange().getLength();
		} catch (JavaModelException e) {
			//ignore
			return 0;
		}
	}

	public IResource getResource() {
		return javaSource == null ? null : javaSource.getTypeRoot().getResource();
	}

	public int getStartPosition() {
		if(javaSource == null) return 0;
		try {
			if(javaSource.getSourceRange() == null) return 0;
			return javaSource.getSourceRange().getOffset();
		} catch (JavaModelException e) {
			//ignore
			return 0;
		}
	}

}
