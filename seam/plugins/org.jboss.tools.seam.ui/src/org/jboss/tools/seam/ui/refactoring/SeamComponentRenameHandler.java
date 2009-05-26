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
package org.jboss.tools.seam.ui.refactoring;

import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.refactoring.RenameComponentProcessor;
import org.jboss.tools.seam.internal.core.refactoring.RenameComponentRefactoring;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.wizard.RenameComponentWizard;

/**
 * @author Alexey Kazakov
 */
public class SeamComponentRenameHandler extends AbstractHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		Shell activeShell = HandlerUtil.getActiveShell(event);
		
		saveAndBuild();

		IEditorInput input = editor.getEditorInput();
		if (input instanceof IFileEditorInput) {
			IFile file = ((IFileEditorInput)input).getFile();
			
			IProject project = file.getProject();
			ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
			if (seamProject != null) {
				Set<ISeamComponent> components = seamProject.getComponentsByPath(file.getFullPath());
				for(ISeamComponent component : components){
					ISeamJavaComponentDeclaration declaration = component.getJavaDeclaration();
					if(declaration != null){
						IResource resource = declaration.getResource();
						if(resource != null && resource.getFullPath().equals(file.getFullPath())){
							if(declaration.getName().equals(component.getName())){
								invokeRenameWizard(component, activeShell);
								return null;
							}
						}
					}
				}
			}
		}
		invokeRenameWizard(null, activeShell);
		return null;
	}

	public static void invokeRenameWizard(ISeamComponent component, Shell activeShell) {
		saveAndBuild();
		
		RenameComponentProcessor processor = new RenameComponentProcessor(component);
		RenameComponentRefactoring refactoring = new RenameComponentRefactoring(processor);
		RenameComponentWizard wizard = new RenameComponentWizard(refactoring, component);
		RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard);
		try {
			String titleForFailedChecks = SeamUIMessages.SEAM_COMPONENT_RENAME_HANDLER_ERROR;
			op.run(activeShell, titleForFailedChecks);
		} catch (final InterruptedException irex) {
			// operation was canceled
		}
	}
	
	private static void saveAndBuild(){
		if(!SeamGuiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().saveAllEditors(true))
			return;
		
		try {
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
		} catch (InterruptedException e) {
			// do nothing
		}
	}
}
