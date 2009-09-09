/*******************************************************************************
  * Copyright (c) 2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;

public class SeamRenameMethodParticipant extends RenameParticipant{
	private IMethod method;
	private String oldName;
	private String newName;
	private SeamRenameMethodSearcher searcher;
	
	
	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		return null;
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		return null;
	}

	@Override
	public String getName() {
		return oldName;
	}

	@Override
	protected boolean initialize(Object element) {
		if(element instanceof IMethod){
			method = (IMethod)element;
			oldName = method.getElementName();
			newName = getArguments().getNewName();
			
			searcher = new SeamRenameMethodSearcher((IFile)method.getResource(), oldName);
			return true;
		}
		return false;
	}
	
	class SeamRenameMethodSearcher extends SeamRefactorSearcher{
		public SeamRenameMethodSearcher(IFile file, String name){
			super(file, name);
		}

		@Override
		protected boolean isFileCorrect(IFile file) {
			return false;
		}

		@Override
		protected void match(IFile file, int offset, int length) {
		}
	}

}
