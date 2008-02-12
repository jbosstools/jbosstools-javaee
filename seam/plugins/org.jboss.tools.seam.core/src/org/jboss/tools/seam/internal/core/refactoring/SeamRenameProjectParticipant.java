/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.internal.core.refactoring;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamRenameProjectParticipant extends RenameParticipant {
	public static final String PARTICIPANT_NAME="seam-RenameProjectParticipant";
	
	IProject project;
	String oldName;
	
	public SeamRenameProjectParticipant() {}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) throws OperationCanceledException {
		return null;
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		if (!pm.isCanceled()) {
			String newName = getArguments().getNewName();
			if(newName == null || newName.trim().length() == 0) return null;
			CompositeChange change = new CompositeChange("Update Seam projects");
			IProject[] ps = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (int i = 0; i < ps.length; i++) {
				SeamRenameProjectChange c = new SeamRenameProjectChange(ps[i], newName, oldName);
				if(c.isRelevant()) change.add(c);
			}
			if(change.getChildren().length > 0) return change;
		}
		return null;
	}

	@Override
	public String getName() {
		return PARTICIPANT_NAME;
	}

	@Override
	protected boolean initialize(Object element) {
		if(!(element instanceof IProject)) {
			return false;
		}
		project = (IProject)element;
		oldName = project.getName();
		return true;
	}

}
