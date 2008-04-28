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

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;

/**
 * @author Alexey Kazakov
 */
public class SeamJavaPackageRenameParticipant extends SeamRenameParticipant {

	public static final String PARTICIPANT_NAME="seam-JavaPackageRenameParticipant";

	private String oldName;
	private IPackageFragmentRoot source;

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#getName()
	 */
	@Override
	public String getName() {
		return PARTICIPANT_NAME;
	}

	@Override
	protected boolean initialize(Object element) {
		if(!(element instanceof IPackageFragment)) {
			return false;
		}
		IPackageFragment packageFragment = (IPackageFragment)element;
		oldName = packageFragment.getElementName();
		IJavaElement parent = packageFragment.getParent();
		if(parent instanceof IPackageFragmentRoot) {
			source = (IPackageFragmentRoot)parent;
			return true;
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.refactoring.SeamRenameParticipant#createChange(org.eclipse.core.resources.IProject, java.lang.String)
	 */
	@Override
	protected SeamProjectChange createChange(IProject project, String newName) {
		return new SeamJavaPackageRenameChange(project, source, newName, oldName);
	}
}