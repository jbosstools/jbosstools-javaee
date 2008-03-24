 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core.refactoring;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

/**
 * Updates seam settings of seam projects if somebody renames source folder.
 * @author Alexey Kazakov
 */
public class SeamFolderRenameParticipant extends SeamRenameParticipant {

	public static final String PARTICIPANT_NAME="seam-FolderRenameParticipant";

	private IPath oldPath;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#getName()
	 */
	@Override
	public String getName() {
		return PARTICIPANT_NAME;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#initialize(java.lang.Object)
	 */
	@Override
	protected boolean initialize(Object element) {
		if(!(element instanceof IFolder)) {
			return false;
		}
		oldPath = ((IResource)element).getFullPath();

		return true;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.refactoring.SeamRenameParticipant#createChange(org.eclipse.core.resources.IProject, java.lang.String)
	 */
	@Override
	protected SeamProjectChange createChange(IProject project, String newName) {
		return new SeamFolderRenameChange(project, newName, oldPath);
	}
}