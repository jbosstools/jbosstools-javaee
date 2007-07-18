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
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamScope;
import org.jboss.tools.seam.internal.core.SeamComponentDeclaration;
import org.jboss.tools.seam.internal.core.SeamProject;

public class SeamLabelProvider extends LabelProvider {
	JavaElementImageProvider jip = new JavaElementImageProvider();

	public String getText(Object element) {
		if(element instanceof IWorkspaceRoot) {
			return "";
		} else if(element instanceof ISeamProject) {
			return ((IProjectNature)element).getProject().getName();
		} else if(element instanceof ISeamScope) {
			return ((ISeamScope)element).getType().getLabel();
		} else if(element instanceof ISeamComponent) {
			return ((ISeamComponent)element).getName();
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
		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		if (obj instanceof ISeamProject) {
			SeamProject p = (SeamProject)obj;
			return jip.getImageLabel(p.getProject(), 3);
//		   imageKey = org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJ_PROJECT;
		} else if(obj instanceof ISeamScope) {
			imageKey = ISharedImages.IMG_OBJ_FOLDER;
		} else if(obj instanceof ISeamComponent) {
			imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		} else if(obj instanceof IRole) {
			//
		} else if(obj instanceof ISeamJavaComponentDeclaration) {
			ISeamJavaComponentDeclaration d = (ISeamJavaComponentDeclaration)obj;
			IType type = (IType)d.getSourceMember();
			if(type != null) {
				if(type.isBinary()) {
					return new org.eclipse.jdt.internal.ui.SharedImages().getImage(JavaPluginImages.IMG_OBJS_CFILE);
				}
				IResource r = d.getResource();
				if(r != null) return jip.getImageLabel(r, 3);
				ICompilationUnit f = type.getCompilationUnit();
				return(f != null) ? jip.getImageLabel(f, 0) : jip.getImageLabel(type, 3);
			}
			return new org.eclipse.jdt.internal.ui.SharedImages().getImage(JavaPluginImages.IMG_OBJS_CFILECLASS);
		} else if(obj instanceof ISeamComponentDeclaration) {
			SeamComponentDeclaration d = (SeamComponentDeclaration)obj;
			IResource r = d.getResource();
			if(r != null) return jip.getImageLabel(r, 3);
			imageKey = ISharedImages.IMG_OBJ_FILE;
		}
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}

}
