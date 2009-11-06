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
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonLabelProvider;
import org.eclipse.ui.navigator.IExtensionStateModel;
import org.jboss.tools.common.java.IJavaSourceReference;
import org.jboss.tools.seam.core.IRole;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamFactory;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamPackage;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamScope;
import org.jboss.tools.seam.ui.SeamUiImages;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamLabelProvider extends LabelProvider implements ICommonLabelProvider {
	private IExtensionStateModel fStateModel;
	private AbstractSeamContentProvider contentProvider;
	
	boolean isFlatLayout = true;
	boolean isScopeLable = false;
	
	IPropertyChangeListener scopePropertyListener;
	IPropertyChangeListener layoutPropertyListener;

	JavaElementImageProvider jip = new JavaElementImageProvider();

	public void init(ICommonContentExtensionSite commonContentExtensionSite) {
		fStateModel = commonContentExtensionSite.getExtensionStateModel();
		contentProvider = (AbstractSeamContentProvider) commonContentExtensionSite.getExtension().getContentProvider();

		scopePropertyListener = new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (ViewConstants.SCOPE_PRESENTATION.equals(event.getProperty())) {
					if (event.getNewValue() != null) {
						boolean newValue = ((Boolean) event.getNewValue()).booleanValue();
						setIsScopeLable(newValue);
					}
				}
			}
		};
		fStateModel.addPropertyChangeListener(scopePropertyListener);

		layoutPropertyListener = new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (ViewConstants.PACKAGE_STRUCTURE.equals(event.getProperty())) {
					if (event.getNewValue() != null) {
						boolean newValue = ((Boolean)event.getNewValue()).booleanValue();
						setIsFlatLayout(newValue);
					}
				}
			}
		};
		fStateModel.addPropertyChangeListener(layoutPropertyListener);
	}
	
	void setIsFlatLayout(boolean b) {
		isFlatLayout = b;
	}
	
	void setIsScopeLable(boolean b) {
		isScopeLable = b;
	}

	@Override
	public String getText(Object element) {
		if(element instanceof IWorkspaceRoot) {
			return ""; //$NON-NLS-1$
		} else if(element instanceof ISeamProject) {
			return ((IProjectNature)element).getProject().getName();
		} else if(element instanceof ISeamScope) {
			return ((ISeamScope)element).getType().getLabel();
		} else if(element instanceof ISeamPackage) {
			if(isFlatLayout/* ScopePresentationActionProvider.isPackageStructureFlat()*/) {
				return ((ISeamPackage)element).getQualifiedName();
			} else {
				return ((ISeamPackage)element).getName();
			}
		} else if(element instanceof ISeamComponent) {
			ISeamComponent c = (ISeamComponent)element;
			String name = c.getName();
			
			int lastIndexOf = name.lastIndexOf('.'); 
			if(lastIndexOf!=-1&&lastIndexOf!=name.length()) {
				name = name.substring(lastIndexOf+1); // temp fix for JBIDE-644; shouldn't need to do this here. shold be a method to getShortName or similar but ISeamComponent extends ISeamContextVariable so ended up being weird to do clean.
			}

			if(isScopeLable /* ScopePresentationActionProvider.isScopePresentedAsLabel()*/) {
				name += " (" + ((ISeamScope)c.getParent()).getType().getLabel() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			return name; 
		} else if (element instanceof IRole) {
			return "" + ((IRole)element).getName(); //$NON-NLS-1$
		} else if (element instanceof ISeamFactory) {
			ISeamFactory f = (ISeamFactory)element;
			return f.getName() + " - " + f.getSourcePath();
		} else if(element instanceof IJavaSourceReference) {
			IJavaSourceReference d = (IJavaSourceReference)element;
			IMember m = d.getSourceMember();
			IType type = (m instanceof IType) ? (IType)m : m.getTypeRoot().findPrimaryType();
			if(type.isBinary()) { 
				IResource r = ((ISeamElement)element).getResource();
				String s = (r == null) ? "<no name>" : r.getName(); //$NON-NLS-1$
				return  s + "/" + type.getFullyQualifiedName(); //$NON-NLS-1$
			} else {
				return type.getFullyQualifiedName();
			}

		} else if(element instanceof ISeamComponentDeclaration) {
			IResource r = ((ISeamComponentDeclaration)element).getResource();
			return r == null ? "???" : r.getName(); //$NON-NLS-1$
		}
		return element == null ? "" : element.toString();//$NON-NLS-1$
	}

	@Override
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
		} else if(obj instanceof ISeamFactory) {
			return SeamUiImages.FACTORY_IMAGE;
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
			ISeamComponentDeclaration d = (ISeamComponentDeclaration)obj;
			IResource r = d.getResource();
			if(r != null) return jip.getImageLabel(r, 3);
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
		}
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
	}

	public void restoreState(IMemento memento) {
	}

	public void saveState(IMemento memento) {
	}

	public String getDescription(Object anElement) {
		return ""; //$NON-NLS-1$
	}

	@Override
	public void dispose() { 
		super.dispose();
		fStateModel.removePropertyChangeListener(layoutPropertyListener);
		fStateModel.removePropertyChangeListener(scopePropertyListener);
	}

}
