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

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.navigator.ILinkHelper;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.seam.core.IOpenableElement;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamLinkHelper implements ILinkHelper {
	
	public SeamLinkHelper() {
	}

	public void activateEditor(IWorkbenchPage page,
			IStructuredSelection selection) {
		if(selection == null || selection.isEmpty()) return;
		Object o = selection.getFirstElement();
		if(o instanceof IOpenableElement) {
			ISeamElement e = (ISeamElement)o;
			IPath path = e.getSourcePath();
			IFile f = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			if(f != null && f.exists() && !"jar".equals(path.getFileExtension())) {
				IEditorInput fileInput = new FileEditorInput(f);
				IEditorPart editor = null;
				if ((editor = page.findEditor(fileInput)) != null) {
					page.bringToTop(editor);
				}
			} else {
				((IOpenableElement)o).open();
			}
		}
		
	}

	public IStructuredSelection findSelection(IEditorInput anInput) {
		IFile file = ResourceUtil.getFile(anInput);
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(file.getProject(), true);
		if(seamProject == null) return null;
		Set<ISeamComponent> set = seamProject.getComponentsByPath(file.getFullPath());
		if(set == null || set.isEmpty()) return null;
		ISeamComponent c = set.iterator().next();
		Set<ISeamComponentDeclaration> ds = c.getAllDeclarations();
		for (ISeamComponentDeclaration d: ds) {
			if(file.getFullPath().equals(d.getSourcePath())) return new StructuredSelection(d);
		}
		return null;
	}

}
