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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
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
import org.jboss.tools.seam.internal.core.SeamProject;

/**
 * Base Helper for Seam Validators. 
 * @author Alexey Kazakov
 */
public class SeamValidationHelper extends WorkbenchContext {

	protected SeamValidationContext validationContext;

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
		IProject p = getProject().getProject();
		try {
			IJavaProject jp = EclipseResourceUtil.getJavaProject(p);
			IPackageFragment packageFragment = jp.findPackageFragment(componentXmlFile.getFullPath().removeLastSegments(1));
			if(packageFragment==null) {
				return null;
			}
			return packageFragment.getElementName() + "." + className;
		} catch (JavaModelException e) {
			SeamCorePlugin.getDefault().logError(e);
			return null;
		}
	}

	/**
	 * Find setter for property
	 * @param type
	 * @param propertyName
	 * @return
	 */
	public IMethod findSetter(IType type, String propertyName) {
		if(propertyName == null || propertyName.length()==0) {
			return null;
		}
		String firstLetter = propertyName.substring(0, 1).toUpperCase();
		String nameWithoutFirstLetter = propertyName.substring(1);
		String setterName = "set" + firstLetter + nameWithoutFirstLetter;
		try {
			return findSetterInHierarchy(type, setterName);
		} catch (JavaModelException e) {
			SeamCorePlugin.getDefault().logError(e);
		}
		return null;
	}

	private IMethod findSetterInHierarchy(IType type, String setterName) throws JavaModelException {
		IMethod[] methods = type.getMethods();
		for (int i = 0; i < methods.length; i++) {
			if(methods[i].getElementName().equals(setterName) && methods[i].getParameterNames().length==1) {
				return methods[i];
			}
		}
		String superclassName = type.getSuperclassName();
		if(superclassName!=null) {
			String[][] packages = type.resolveType(superclassName);
			if(packages!=null) {
				for (int i = 0; i < packages.length; i++) {
					String packageName = packages[i][0];
					if(packageName!=null && packageName.length()>0) {
						packageName = packageName + "."; 
					} else {
						packageName = "";
					}
					String qName = packageName + packages[i][1];
					IType superclass = type.getJavaProject().findType(qName);
					if(superclass!=null) {
						IMethod method = findSetterInHierarchy(superclass, setterName);
						if(method!=null) {
							return method;
						}
					}
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.operations.WorkbenchContext#registerResource(org.eclipse.core.resources.IResource)
	 */
	@Override
	public void registerResource(IResource resource) {
		if(resource instanceof IFile) {
			IFile file = (IFile)resource;
			if(!file.exists()) {
				getValidationContext().addRemovedFile(file);
			} else {
				getValidationContext().registerFile(file);
			}
		}
	}

	/**
	 * @return Set of changed resources
	 */
	public Set<IFile> getChangedFiles() {
		Set<IFile> result = new HashSet<IFile>();
		String[] uris = getURIs();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (int i = 0; i < uris.length; i++) {
			IFile currentFile = root.getFile(new Path(uris[i]));
			result.add(currentFile);
		}
		result.addAll(getValidationContext().getRemovedFiles());
		return result;
	}

	public SeamValidationContext getValidationContext() {
		if(validationContext==null) {
			validationContext = ((SeamProject)getSeamProject()).getValidationContext();
		}
		return validationContext;
	}
}