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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.*;
import org.eclipse.ltk.core.refactoring.*;
import org.eclipse.ltk.core.refactoring.participants.*;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

public class JSFRenamePageParticipant extends RenameParticipant {
	public static final String PARTICIPANT_NAME="jsf-RenamePageParticipant"; //$NON-NLS-1$
	XModelObject object;

	protected boolean initialize(Object element) {
		if(!(element instanceof IFile)) return false;
		IFile f = (IFile)element;
		object = EclipseResourceUtil.getObjectByResource(f);
		if(object == null) return false;
		String entity = object.getModelEntity().getName();
		if(".FileJSP.FileHTML.FileXHTML.FileGIF.FileCSS.FileAny.FileAnyLong.".indexOf("." + entity + ".") < 0) return false; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
			String newName = getArguments().getNewName();
			JSFRenamePageFacesConfigChange change = new JSFRenamePageFacesConfigChange(object, newName);
			if(change.getChildren() == null || change.getChildren().length == 0) change = null;
			return change;
		}
		return null;
	}

}
