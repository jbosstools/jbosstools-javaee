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
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

public class JSFMovePageParticipant extends MoveParticipant {
	public static final String PARTICIPANT_NAME="jsf-MovePageParticipant"; //$NON-NLS-1$
	IFile f;
	XModelObject object;

	protected boolean initialize(Object element) {
		if(!(element instanceof IFile)) return false;
		f = (IFile)element;
		object = EclipseResourceUtil.getObjectByResource(f);
		if(object == null) return false;
		String entity = object.getModelEntity().getName();
		if(".FileJSP.FileHTML.FileXHTML.".indexOf("." + entity + ".") < 0) return false; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return true;
	}

	public String getName() {
		return PARTICIPANT_NAME;
	}

	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) throws OperationCanceledException {
		return null;
	}

	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		if(pm.isCanceled()) return null;
		Object destination = getArguments().getDestination();
		if(!(destination instanceof IContainer)) return null;
		IContainer folder = (IContainer)destination;
		if(folder.getProject() != f.getProject()) return null;
		XModelObject dp = EclipseResourceUtil.getObjectByResource(object.getModel(), folder);
		if(dp == null) return null;
		JSFRenamePageFacesConfigChange change = new JSFRenamePageFacesConfigChange(object, f.getName(), dp);
		if(change.getChildren() == null || change.getChildren().length == 0) change = null;
		return change;
	}

}
