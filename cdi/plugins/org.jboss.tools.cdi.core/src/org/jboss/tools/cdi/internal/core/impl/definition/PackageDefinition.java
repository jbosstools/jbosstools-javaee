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
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.common.util.EclipseJavaUtil;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class PackageDefinition extends AbstractMemberDefinition {
	IType binaryType = null;
	protected String qualifiedName;

	public PackageDefinition() {}

	public String getQualifiedName() {
		return qualifiedName;
	}

	public void setBinaryType(IType pkg, IRootDefinitionContext context) {
		binaryType = pkg;
		setAnnotatable(pkg, pkg, context, 0);
		qualifiedName = pkg.getPackageFragment().getElementName();
	}

	public void setPackage(IPackageDeclaration pkg, IRootDefinitionContext context) {
		qualifiedName = pkg.getElementName();
		IType contextType = null;
		ICompilationUnit u = null;
		if(pkg.getParent() instanceof ICompilationUnit) {
			try {
				u = ((ICompilationUnit)pkg.getParent()).getWorkingCopy(new NullProgressMonitor());
				contextType = u.createType("class A {}", null, false, new NullProgressMonitor());
			} catch (JavaModelException e) {
				
			}
		}
		super.setAnnotatable(pkg, contextType, context, 0);
		if (u != null) {
			try {
				u.discardWorkingCopy();
			} catch (JavaModelException e) {
				
			}
		}
	}

	public String resolveType(String typeName) {
		if(binaryType != null) {
			return typeName;
		}
		String result = typeName;
		IPackageDeclaration pkg = (IPackageDeclaration)member;
		IType contextType = null;
		ICompilationUnit u = null;
		if(pkg.getParent() instanceof ICompilationUnit) {
			try {
				u = ((ICompilationUnit)pkg.getParent()).getWorkingCopy(new NullProgressMonitor());
				contextType = u.createType("class A {}", null, false, new NullProgressMonitor());
			} catch (JavaModelException e) {
				
			}
		}
		
		if(contextType != null) {
			result = EclipseJavaUtil.resolveType(contextType, typeName);
		}
		
		if (u != null) {
			try {
				u.discardWorkingCopy();
			} catch (JavaModelException e) {
				
			}
		}
		
		return result == null ? typeName : result;
	}

	

}
