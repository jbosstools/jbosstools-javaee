/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.refactoring;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import org.eclipse.osgi.util.NLS;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.jboss.tools.cdi.core.CDICoreMessages;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.ui.CDIUIPlugin;

public abstract class CDIRefactoringProcessor extends RefactoringProcessor {
	protected static final RefactoringParticipant[] EMPTY_REF_PARTICIPANT = new  RefactoringParticipant[0];	
	protected IFile file;
	protected RefactoringStatus status;
	protected String label;
	
	protected CompositeChange rootChange;
	protected TextFileChange change;
	protected IClassBean bean;

	
	public CDIRefactoringProcessor(IFile file, String label){
		this(label);
		this.file = file;
	}
	
	public CDIRefactoringProcessor(String label){
		this.label = label;
	}
	
	protected void createRootChange(){
		rootChange = new CompositeChange(label);
		change = new TextFileChange(file.getName(), file);
		
		if(isEditorOpened(file))
			change.setSaveMode(TextFileChange.LEAVE_DIRTY);
		else
			change.setSaveMode(TextFileChange.FORCE_SAVE);
		
		MultiTextEdit root = new MultiTextEdit();
		change.setEdit(root);
		rootChange.add(change);
		rootChange.markAsSynthetic();
	}
	
	protected boolean isEditorOpened(IFile file){
		IEditorInput ii = EditorUtility.getEditorInput(file);
		
		IWorkbenchWindow[] windows = CDIUIPlugin.getDefault().getWorkbench().getWorkbenchWindows();
		for(IWorkbenchWindow window : windows){
			IEditorPart editor = window.getActivePage().findEditor(ii);
			if(editor != null)
				return true;
		}
		return false;
	}
	
	private IClassBean findClassBean(){
		CDICoreNature cdiNature = CDICorePlugin.getCDI(file.getProject(), true);
		if(cdiNature == null)
			return null;
		
		ICDIProject cdiProject = cdiNature.getDelegate();
		
		if(cdiProject == null)
			return null;
		
		Set<IBean> beans = cdiProject.getBeans(file.getFullPath());
		
		for(IBean bean : beans){
			if(bean instanceof IClassBean)
				return (IClassBean)bean;
		}
		
		return null;
	}
	
	private boolean isFileCorrect(IFile file){
		if(!file.isSynchronized(IResource.DEPTH_ZERO)){
			status.addFatalError(NLS.bind(CDICoreMessages.CDI_RENAME_PROCESSOR_OUT_OF_SYNC_PROJECT, file.getProject().getFullPath().toString()));
			return false;
		}else if(file.isPhantom()){
			return false;
		}else if(file.isReadOnly()){
			status.addFatalError(NLS.bind(CDICoreMessages.CDI_RENAME_PROCESSOR_ERROR_READ_ONLY_FILE, file.getFullPath().toString()));
			return false;
		}
		return true;
	}

	@Override
	public Object[] getElements() {
		return new Object[]{file};
	}

	@Override
	public String getIdentifier() {
		return "";
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
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		status = new RefactoringStatus();
		
		if(isFileCorrect(file)){
			bean = findClassBean();
		}else
			status.addFatalError("CDI Bean Class not found");
		
		return status;
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

}
