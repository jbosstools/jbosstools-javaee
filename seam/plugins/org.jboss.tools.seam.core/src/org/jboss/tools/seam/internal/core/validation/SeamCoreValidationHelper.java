/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.internal.core.validation;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.wst.validation.internal.operations.WorkbenchContext;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamTextSourceReference;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.AbstractContextVariable;
import org.jboss.tools.seam.internal.core.SeamComponentDeclaration;

public class SeamCoreValidationHelper extends WorkbenchContext {

	/**
	 * @return Seam project
	 */
	public ISeamProject getSeamProject() {
		ISeamProject project = null;
		try {
			project = SeamCorePlugin.getSeamProject(getProject(), true);
		} catch (Exception e) {
			SeamCorePlugin.getDefault().logError("Can't get Seam Project", e);
		}
		return project;
	}

	/**
	 * @param element
	 * @return Resource of seam model element
	 */
	public IResource getComponentResourceWithName(ISeamElement element) {
		if(element instanceof ISeamComponent) {
			Set declarations = ((ISeamComponent)element).getAllDeclarations();
			for (Object o : declarations) {
				SeamComponentDeclaration d = (SeamComponentDeclaration)o;
				if(d.getLocationFor(SeamComponentDeclaration.PATH_OF_NAME)!=null) {
					return d.getResource();
				}
			}
		}
		return element.getResource();
	}

	/**
	 * @param seam model element
	 * @return location of name attribute
	 */
	public ISeamTextSourceReference getLocationOfName(ISeamElement element) {
		return getLocationOfAttribute(element, SeamComponentDeclaration.PATH_OF_NAME);
	}

	/**
	 * @param seam model element
	 * @return location of attribute
	 */
	public ISeamTextSourceReference getLocationOfAttribute(ISeamElement element, String attributeName) {
		ISeamTextSourceReference location = null;
		if(element instanceof AbstractContextVariable) {
			location = ((AbstractContextVariable)element).getLocationFor(attributeName);
		} else if(element instanceof ISeamComponent) {
			Set declarations = ((ISeamComponent)element).getAllDeclarations();
			for (Object d : declarations) {
				location = ((SeamComponentDeclaration)d).getLocationFor(attributeName);
				if(location!=null) {
					break;
				}
			}
		} else if(element instanceof SeamComponentDeclaration) {
			location = ((SeamComponentDeclaration)element).getLocationFor(attributeName);
		}
		if(location==null && element instanceof ISeamTextSourceReference) {
			location = (ISeamTextSourceReference)element;
		}
		return location;
	}

	/**
	 * @param resource
	 * @return true if resource is Jar file
	 */
	public boolean isJar(IResource resource) {
		String ext = resource.getFileExtension();
		return ext!=null && ext.equalsIgnoreCase("jar");
	}

	/**
	 * @param element
	 * @return true if seam element packed in Jar file
	 */
	public boolean isJar(ISeamElement element) {
		return isJar(element.getResource());
	}

	/**
	 * @param componentXmlFile
	 * @return IType of component for <ComponentName>.component.xml
	 */
	public IType getClassTypeForComponentXml(IFile componentXmlFile) {
		String fileName  = componentXmlFile.getName();
		int firstDot = fileName.indexOf('.');
		if(firstDot==-1) {
			return null;
		}
		String className = fileName.substring(0, firstDot);		
		IProject p = getProject().getProject();
		try {
			IJavaProject jp = EclipseResourceUtil.getJavaProject(p);
			IPackageFragment packageFragment = jp.findPackageFragment(componentXmlFile.getFullPath().removeLastSegments(1));
			if(packageFragment==null) {
				return null;
			}
			return jp.findType(packageFragment.getElementName(), className);
		} catch (JavaModelException e) {
			SeamCorePlugin.getDefault().logError(e);
			return null;
		}
	}
}