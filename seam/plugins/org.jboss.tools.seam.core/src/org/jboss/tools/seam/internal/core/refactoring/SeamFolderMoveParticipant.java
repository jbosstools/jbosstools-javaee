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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

/**
 * @author Alexey Kazakov
 */
public class SeamFolderMoveParticipant extends SeamMoveParticipant {

	private IResource oldResource;

	public static final String PARTICIPANT_NAME="seam-FolderMoveParticipant";

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#getName()
	 */
	@Override
	public String getName() {
		return PARTICIPANT_NAME;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#initialize(java.lang.Object)
	 */
	@Override
	protected boolean initialize(Object element) {
		if(!(element instanceof IFolder)) {
			return false;
		}
		oldResource = (IResource)element;

		return true;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.refactoring.SeamMoveParticipant#createChange(org.eclipse.core.resources.IProject, java.lang.Object)
	 */
	@Override
	protected SeamProjectChange createChange(IProject project, Object destination) {
		if(destination instanceof IContainer) {
			IContainer container = (IContainer)destination;
			return new SeamFolderMoveChange(project, oldResource, container);
		}
		return null;
	}
}