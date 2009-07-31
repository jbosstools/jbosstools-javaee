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

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.*;
import org.eclipse.ltk.core.refactoring.participants.*;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.FileAnyImpl;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.jsf.web.JSFWebHelper;
import org.jboss.tools.jst.web.project.WebProject;

public class JSFRenameFacesConfigParticipant extends RenameParticipant {
	public static final String PARTICIPANT_NAME="jsf-RenameFacesConfigParticipant"; //$NON-NLS-1$
	XModelObject object;

	protected boolean initialize(Object element) {
		if(!(element instanceof IFile)) return false;
		IFile f = (IFile)element;
		object = EclipseResourceUtil.getObjectByResource(f);
		if(object == null) return false;
		String entity = object.getModelEntity().getName();
		if(!entity.startsWith("FacesConfig")) return false; //$NON-NLS-1$
		return true;
	}

	public String getName() {
		return PARTICIPANT_NAME;
	}

	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) throws OperationCanceledException {
		return null;
	}

	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		if (!pm.isCanceled()) {
			String path = WebProject.getInstance(object.getModel()).getPathInWebRoot(object);
			if(path == null) path =	XModelObjectLoaderUtil.getResourcePath(object);
			if(!JSFWebHelper.isRegisterFacesConfig(object.getModel(), path)
				&& !JSFWebHelper.isConfigFileDefault(path, JSFWebHelper.FACES_CONFIG_DATA)) return null;
			
			String newName = getArguments().getNewName();
			if(newName == null || newName.trim().length() == 0) return null;
			String oldName = FileAnyImpl.toFileName(object);
			JSFRenameFacesConfigWebAppChange change = new JSFRenameFacesConfigWebAppChange(object, oldName, newName);
			return change;
		}
		return null;
	}

}
