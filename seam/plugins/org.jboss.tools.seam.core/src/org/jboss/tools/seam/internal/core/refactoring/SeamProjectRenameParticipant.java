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

/**
 * @author Viacheslav Kabanovich
 */
public class SeamProjectRenameParticipant extends SeamRenameParticipant {

	public static final String PARTICIPANT_NAME="seam-ProjectRenameParticipant";

	private String oldName;

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#getName()
	 */
	@Override
	public String getName() {
		return PARTICIPANT_NAME;
	}

	@Override
	protected boolean initialize(Object element) {
		if(!(element instanceof IProject)) {
			return false;
		}
		oldName = ((IProject)element).getName();
		return true;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.refactoring.SeamRenameParticipant#createChange(org.eclipse.core.resources.IProject, java.lang.String)
	 */
	@Override
	protected SeamProjectChange createChange(IProject project, String newName) {
		return new SeamProjectRenameChange(project, newName, oldName);
	}
}