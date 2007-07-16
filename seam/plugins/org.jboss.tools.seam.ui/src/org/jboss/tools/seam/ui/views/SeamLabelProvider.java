package org.jboss.tools.seam.ui.views;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamJavaSourceReference;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamScope;

public class SeamLabelProvider extends LabelProvider {

	public String getText(Object element) {
		if(element instanceof ISeamProject) {
			return ((ISeamProject)element).getProject().getName();
		} else if(element instanceof ISeamScope) {
			return ((ISeamScope)element).getType().getLabel();
		} else if(element instanceof ISeamComponent) {
			return ((ISeamComponent)element).getName();
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
		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		if (obj instanceof ISeamProject) {
		   imageKey = org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJ_PROJECT;
		} else if(obj instanceof ISeamScope) {
			imageKey = ISharedImages.IMG_OBJ_FOLDER;
		} else if(obj instanceof ISeamComponent) {
			imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		} else if(obj instanceof ISeamComponentDeclaration) {
			imageKey = ISharedImages.IMG_OBJ_FILE;
		}
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}

}
