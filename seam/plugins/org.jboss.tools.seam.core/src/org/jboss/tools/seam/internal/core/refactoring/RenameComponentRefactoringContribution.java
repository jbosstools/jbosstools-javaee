 /*******************************************************************************
  * Copyright (c) 2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core.refactoring;

import java.util.Map;

import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

/**
 * @author Alexey Kazakov
 */
public class RenameComponentRefactoringContribution extends RefactoringContribution {

	@Override
	public RefactoringDescriptor createDescriptor(String id, String project, String description, String comment, Map arguments, int flags) {
//		return new IntroduceIndirectionDescriptor(project, description, comment, arguments);
		return super.createDescriptor();
	}

	@Override
	public Map retrieveArgumentMap(RefactoringDescriptor descriptor) {
//		if (descriptor instanceof IntroduceIndirectionDescriptor)
//			return ((IntroduceIndirectionDescriptor) descriptor).getArguments();
		return super.retrieveArgumentMap(descriptor);
	}
}