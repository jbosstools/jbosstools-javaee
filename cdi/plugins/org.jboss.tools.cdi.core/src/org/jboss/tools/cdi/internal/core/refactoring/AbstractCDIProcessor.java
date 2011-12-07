/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.internal.core.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.RenameProcessor;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.cdi.core.CDICoreMessages;

public abstract class AbstractCDIProcessor extends RenameProcessor {
	private static final RefactoringParticipant[] EMPTY_REF_PARTICIPANT = new  RefactoringParticipant[0];
	
	protected RefactoringStatus status;
	
	protected CompositeChange rootChange;
	
	private String label;
	
	public AbstractCDIProcessor(String label){
		this.label = label;
	}
	
	@Override
	public String getProcessorName() {
		return label;
	}
	
	@Override
	public boolean isApplicable() throws CoreException {
		return true;
	}
	
	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		
		return rootChange;
	}
	
	@Override
	public RefactoringParticipant[] loadParticipants(RefactoringStatus status,
			SharableParticipants sharedParticipants) throws CoreException {
		return EMPTY_REF_PARTICIPANT;
	}
	
	public String getLabel(){
		return label;
	}
	
	protected boolean isFileCorrect(IFile file){
		if(file == null){
			status.addFatalError(CDICoreMessages.CDI_RENAME_PROCESSOR_ERROR_FILE_NOT_FOUND);
			return false;
		}else if(!file.isSynchronized(IResource.DEPTH_ZERO)){
			status.addFatalError(NLS.bind(CDICoreMessages.CDI_RENAME_PROCESSOR_ERROR_OUT_OF_SYNC_PROJECT, file.getProject().getFullPath().toString()));
			return false;
		}else if(file.isPhantom()){
			status.addFatalError(NLS.bind(CDICoreMessages.CDI_RENAME_PROCESSOR_ERROR_PHANTOM_FILE, file.getFullPath().toString()));
			return false;
		}else if(file.isReadOnly()){
			status.addFatalError(NLS.bind(CDICoreMessages.CDI_RENAME_PROCESSOR_ERROR_READ_ONLY_FILE, file.getFullPath().toString()));
			return false;
		}
		return true;
	}
	
	@Override
	public String getIdentifier() {
		return getClass().getName();
	}
}
