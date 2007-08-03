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
package org.jboss.tools.seam.ui.views;

import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.seam.core.IRole;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamJavaSourceReference;
import org.jboss.tools.seam.core.ISeamPackage;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamScope;
import org.jboss.tools.seam.internal.core.SeamComponentDeclaration;
import org.jboss.tools.seam.internal.core.SeamProject;
import org.jboss.tools.seam.ui.SeamUiImages;
import org.jboss.tools.seam.ui.views.actions.ScopePresentationActionProvider;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamLabelProvider extends LabelProvider {
	JavaElementImageProvider jip = new JavaElementImageProvider();

	public String getText(Object element) {
		if(element instanceof IWorkspaceRoot) {
			return "";
		} else if(element instanceof ISeamProject) {
			return ((IProjectNature)element).getProject().getName();
		} else if(element instanceof ISeamScope) {
			return ((ISeamScope)element).getType().getLabel();
		} else if(element instanceof ISeamPackage) {
			return ((ISeamPackage)element).getName();
		} else if(element instanceof ISeamComponent) {
			ISeamComponent c = (ISeamComponent)element;
			String name = c.getName();
			if(ScopePresentationActionProvider.isScopePresentedAsLabel()) {
				name += " (" + ((ISeamScope)c.getParent()).getType().getLabel() + ")";
			}
			return name; 
		} else if (element instanceof IRole) {
			return "" + ((IRole)element).getName();
		} else if(element instanceof ISeamJavaSourceReference) {
			ISeamJavaSourceReference d = (ISeamJavaSourceReference)element;
			IMember m = d.getSourceMember();
			IType type = (m instanceof IType) ? (IType)m : m.getTypeRoot().findPrimaryType();
			if(type.isBinary()) { 
				IResource r = ((ISeamElement)element).getResource();
				String s = (r == null) ? "<no name>" : r.getName();
				return  s + "/" + type.getFullyQualifiedName();
			} else {
				return type.getFullyQualifiedName();
			}

		} else if(element instanceof ISeamComponentDeclaration) {
			IResource r = ((ISeamComponentDeclaration)element).getResource();
			return r == null ? "???" : r.getName();
		}
		return element == null ? "" : element.toString();//$NON-NLS-1$
	}

	public Image getImage(Object obj) {
		if (obj instanceof ISeamProject) {
			return SeamUiImages.PROJECT_IMAGE;
		} else if(obj instanceof ISeamScope) {
			return SeamUiImages.SCOPE_IMAGE;
		} else if(obj instanceof ISeamPackage) {
			return SeamUiImages.PACKAGE_IMAGE;
		} else if(obj instanceof ISeamComponent) {
			return SeamUiImages.COMPONENT_IMAGE;
		} else if(obj instanceof IRole) {
			return SeamUiImages.ROLE_IMAGE;
		} else if(obj instanceof ISeamJavaComponentDeclaration) {
			ISeamJavaComponentDeclaration d = (ISeamJavaComponentDeclaration)obj;
			IType type = (IType)d.getSourceMember();
			if(type != null) {
				if(type.isBinary()) {
					return SeamUiImages.JAVA_BINARY_IMAGE;
				}
				return SeamUiImages.JAVA_IMAGE;
			}
			return SeamUiImages.JAVA_IMAGE;
		} else if(obj instanceof ISeamComponentDeclaration) {
			SeamComponentDeclaration d = (SeamComponentDeclaration)obj;
			IResource r = d.getResource();
			if(r != null) return jip.getImageLabel(r, 3);
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
		}
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
	}

}
