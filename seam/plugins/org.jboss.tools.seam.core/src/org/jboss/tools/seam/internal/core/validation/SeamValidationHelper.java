 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core.validation;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.wst.validation.internal.operations.WorkbenchContext;
import org.jboss.tools.common.model.project.ext.ITextSourceReference;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.AbstractContextVariable;
import org.jboss.tools.seam.internal.core.SeamComponentDeclaration;

/**
 * Base Helper for Seam Validators.
 * @author Alexey Kazakov
 */
public class SeamValidationHelper extends WorkbenchContext {

	protected ISeamProject seamProject;

	/**
	 * @return Seam project
	 */
	public ISeamProject getSeamProject() {
		if(seamProject==null) {
			ISeamProject project = null;
			seamProject = SeamCorePlugin.getSeamProject(getProject(), true);
		}
		return seamProject;
	}

	public void setSeamProject(ISeamProject project) {
		seamProject = project;
	}

	/**
	 * @return Java project
	 */
	public IJavaProject getJavaProject() {
		return EclipseResourceUtil.getJavaProject(getProject());
	}

	/**
	 * @param element
	 * @return Resource of seam model element
	 */
	public IResource getComponentResourceWithName(ISeamElement element) {
		if(element instanceof ISeamComponent) {
			Set<ISeamComponentDeclaration> declarations = ((ISeamComponent)element).getAllDeclarations();
			for (Object o : declarations) {
				SeamComponentDeclaration d = (SeamComponentDeclaration)o;
				ITextSourceReference location = d.getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
				if(!SeamCoreValidator.isEmptyLocation(location)) {
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
	public ITextSourceReference getLocationOfName(ISeamElement element) {
		return getLocationOfAttribute(element, SeamComponentDeclaration.PATH_OF_NAME);
	}

	/**
	 * @param seam model element
	 * @return location of attribute
	 */
	public ITextSourceReference getLocationOfAttribute(ISeamElement element, String attributeName) {
		ITextSourceReference location = null;
		if(element instanceof AbstractContextVariable) {
			location = ((AbstractContextVariable)element).getLocationFor(attributeName);
		} else if(element instanceof ISeamComponent) {
			Set<ISeamComponentDeclaration> declarations = ((ISeamComponent)element).getAllDeclarations();
			for (ISeamComponentDeclaration d : declarations) {
				location = ((SeamComponentDeclaration)d).getLocationFor(attributeName);
				if(!SeamCoreValidator.isEmptyLocation(location)) {
					break;
				}
			}
		} else if(element instanceof SeamComponentDeclaration) {
			location = ((SeamComponentDeclaration)element).getLocationFor(attributeName);
		}
		if(SeamCoreValidator.isEmptyLocation(location) && element instanceof ITextSourceReference) {
			location = (ITextSourceReference)element;
		}
		return location;
	}

	/**
	 * @param resource
	 * @return true if resource is Jar file
	 */
	public boolean isJar(IPath path) {
		if(path == null) {
			throw new IllegalArgumentException(SeamCoreMessages.SEAM_VALIDATION_HELPER_RESOURCE_MUST_NOT_BE_NULL);
		}
		String ext = path.getFileExtension();
		return ext != null && ext.equalsIgnoreCase("jar"); //$NON-NLS-1$
	}

	/**
	 * @param element
	 * @return true if seam element packed in Jar file
	 */
	public boolean isJar(ISeamElement element) {
		return isJar(element.getSourcePath());
	}

	/**
	 * @param componentXmlFile
	 * @return IType of component for <ComponentName>.component.xml
	 */
	public IType getClassTypeForComponentXml(IFile componentXmlFile) {
		String className = getClassNameForComponentXml(componentXmlFile);
		if(className==null) {
			return null;
		}
		return findType(className);
	}

	/**
	 * @param type name
	 * @return IType
	 */
	public IType findType(String fullyQualifiedName) {
		IProject p = getProject().getProject();
		try {
			IJavaProject jp = EclipseResourceUtil.getJavaProject(p);
			return jp.findType(fullyQualifiedName);
		} catch (JavaModelException e) {
			SeamCorePlugin.getDefault().logError(e);
			return null;
		}
	}

	/**
	 * @param componentXmlFile
	 * @return name of component class for <ComponentName>.component.xml
	 */
	public String getClassNameForComponentXml(IFile componentXmlFile) {
		String fileName  = componentXmlFile.getName();
		int firstDot = fileName.indexOf('.');
		if(firstDot==-1) {
			return null;
		}
		String className = fileName.substring(0, firstDot);		
		IProject p = getProject();
		try {
			IJavaProject jp = EclipseResourceUtil.getJavaProject(p);
			IPackageFragment packageFragment = jp.findPackageFragment(componentXmlFile.getFullPath().removeLastSegments(1));
			if(packageFragment==null) {
				return null;
			}
			return packageFragment.getElementName() + "." + className; //$NON-NLS-1$
		} catch (JavaModelException e) {
			SeamCorePlugin.getDefault().logError(e);
			return null;
		}
	}

	/**
	 * Find a setter or a field for a property.
	 * @param type
	 * @param propertyName
	 * @return IMethod (setter) or IFiled (field)
	 */
	public IMember findProperty(IType type, String propertyName) {
		if(propertyName == null || propertyName.length()==0) {
			return null;
		}
		try {
			return findPropertyInHierarchy(type, propertyName);
		} catch (JavaModelException e) {
			SeamCorePlugin.getDefault().logError(e);
		}
		return null;
	}

	private IMember findPropertyInHierarchy(IType type, String propertyName) throws JavaModelException {
		String firstLetter = propertyName.substring(0, 1).toUpperCase();
		String nameWithoutFirstLetter = propertyName.substring(1);
		String setterName = "set" + firstLetter + nameWithoutFirstLetter; //$NON-NLS-1$

		IMethod[] methods = type.getMethods();
		for (int i = 0; i < methods.length; i++) {
			if(methods[i].getElementName().equals(setterName) && methods[i].getParameterNames().length==1) {
				return methods[i];
			}
		}
		IField[] fields = type.getFields();
		for (int i = 0; i < fields.length; i++) {
			if(fields[i].getElementName().equals(propertyName)) {
				return fields[i];
			}
		}

		String superclassName = type.getSuperclassName();
		if(superclassName!=null) {
			String[][] packages = type.resolveType(superclassName);
			if(packages!=null) {
				for (int i = 0; i < packages.length; i++) {
					String packageName = packages[i][0];
					if(packageName!=null && packageName.length()>0) {
						packageName = packageName + ".";  //$NON-NLS-1$
					} else {
						packageName = ""; //$NON-NLS-1$
					}
					String qName = packageName + packages[i][1];
					IType superclass = type.getJavaProject().findType(qName);
					if(superclass!=null) {
						IMember property = findPropertyInHierarchy(superclass, propertyName);
						if(property!=null) {
							return property;
						}
					}
				}
			}
		}
		return null;
	}
}