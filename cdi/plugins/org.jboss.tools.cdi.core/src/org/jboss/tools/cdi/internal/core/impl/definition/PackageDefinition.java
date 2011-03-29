/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.internal.core.impl.definition;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class PackageDefinition extends AbstractMemberDefinition {
	protected String qualifiedName;

	public PackageDefinition() {}

	public String getQualifiedName() {
		return qualifiedName;
	}

	public void setPackage(IPackageDeclaration pkg, DefinitionContext context) {
		IType contextType = null;
		ICompilationUnit u = null;
		if(pkg.getParent() instanceof ICompilationUnit) {
			try {
				u = ((ICompilationUnit)pkg.getParent()).getWorkingCopy(new NullProgressMonitor());
				contextType = u.createType("class A {}", null, false, new NullProgressMonitor());
			} catch (JavaModelException e) {
				
			}
		}
		super.setAnnotatable(pkg, contextType, context);
		if (u != null) {
			try {
				u.discardWorkingCopy();
			} catch (JavaModelException e) {
				
			}
		}
	}

}
