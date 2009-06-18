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

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamFactory;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * @author Daniel Azarov
 */
public class RenameSeamContextVariableProcessor extends SeamRenameProcessor {
	IFile file;
	/**
	 * @param file where refactor was called
	 */
	public RenameSeamContextVariableProcessor(IFile file, String oldName) {
		super();
		this.file = file;
		setOldName(oldName);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#checkFinalConditions(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext)
	 */
	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws CoreException,
			OperationCanceledException {
		pm.beginTask("", 1); //$NON-NLS-1$
		try {
			RefactoringStatus status = new RefactoringStatus();
			ISeamComponent component = checkComponent();
			if(component != null){
				checkDeclarations(component, status);
				
				rootChange = new CompositeChange(SeamCoreMessages.RENAME_SEAM_CONTEXT_VARIABLE_PROCESSOR_TITLE);
				
				renameComponent(component);
			}else{
				Set<ISeamFactory> factories = checkFactories();
				if(factories != null)
					renameFactories(factories);
			}
			checkResources(status);
			return status;
		} finally {
			pm.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#checkInitialConditions(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		RefactoringStatus result = new RefactoringStatus();
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#createChange(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		
		
		
		return rootChange;
	}
	
	private ISeamComponent checkComponent(){
		IProject project = file.getProject();
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
		if (seamProject != null) {
			return seamProject.getComponent(getOldName());
//			Set<ISeamComponent> components = seamProject.getComponentsByPath(file.getFullPath());
//			for(ISeamComponent component : components){
//				ISeamJavaComponentDeclaration declaration = component.getJavaDeclaration();
//				if(declaration != null){
//					IResource resource = declaration.getResource();
//					if(resource != null && resource.getFullPath().equals(file.getFullPath())){
//						if(declaration.getName().equals(component.getName())){
//							return component;
//						}
//					}
//				}
//			}
		}
		return null;
	}
	
	private Set<ISeamFactory> checkFactories(){
		IProject project = file.getProject();
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
		if (seamProject != null) {
			return seamProject.getFactoriesByName(getOldName());
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getElements()
	 */
	@Override
	public Object[] getElements() {
		return new String[]{getNewName()};
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return getClass().getName();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getProcessorName()
	 */
	@Override
	public String getProcessorName() {
		return SeamCoreMessages.RENAME_SEAM_CONTEXT_VARIABLE_PROCESSOR_TITLE;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#isApplicable()
	 */
	@Override
	public boolean isApplicable() throws CoreException {
		return getNewName()!=null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#loadParticipants(org.eclipse.ltk.core.refactoring.RefactoringStatus, org.eclipse.ltk.core.refactoring.participants.SharableParticipants)
	 */
	@Override
	public RefactoringParticipant[] loadParticipants(RefactoringStatus status,
			SharableParticipants sharedParticipants) throws CoreException {
		return null;
	}
}