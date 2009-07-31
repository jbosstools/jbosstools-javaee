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
package org.jboss.tools.jsf.model.handlers.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.FolderImpl;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.web.JSFWebHelper;
import org.jboss.tools.jst.web.project.WebProject;

public class JSFRenameFacesConfigWebAppChange extends Change {
	XModelObject folder;
	XModelObject object;
	String oldName;
	String newName;
	
	public JSFRenameFacesConfigWebAppChange(XModelObject object, String oldName, String newName) {
		this.object = object;
		folder = object.getParent();
		this.newName = newName;
		this.oldName = oldName;
	}

	public String getName() {
		return JSFUIMessages.UPDATE_WEB_XML;
	}

	public void initializeValidationData(IProgressMonitor pm) {}

	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		return null;
	}

	public Change perform(IProgressMonitor pm) throws CoreException {
		String path = WebProject.getInstance(folder.getModel()).getPathInWebRoot(folder);
		if(path == null) path = "" + XModelObjectLoaderUtil.getResourcePath(folder); //$NON-NLS-1$
		if(!path.startsWith("/")) path += "/"; //$NON-NLS-1$ //$NON-NLS-2$
		path += newName;
		if(folder instanceof FolderImpl) ((FolderImpl)folder).update();
		folder.getModel().update();
		JSFWebHelper.registerFacesConfigRename(object.getModel(), oldName, newName, path);
		return null;
	}

	public Object getModifiedElement() {
		return null;
	}

}
