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
package org.jboss.tools.cdi.ui.refactoring;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.ltk.internal.core.refactoring.Messages;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.internal.services.IWorkbenchLocationService;
import org.eclipse.ui.menus.AbstractContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.services.IServiceLocator;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.internal.core.refactoring.RenameNamedBeanProcessor;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * @author Daniel Azarov
 */
public class CDIRefactorContributionFactory extends AbstractContributionFactory {
	private static final String JAVA_EXT = "java"; //$NON-NLS-1$
	
	private Shell shell;
	
	public CDIRefactorContributionFactory(){
		super("","");
	}
	
	public CDIRefactorContributionFactory(String location, String namespace){
		super(location, namespace);
	}

	@Override
	public void createContributionItems(IServiceLocator serviceLocator,
			IContributionRoot additions) {
		
		if(serviceLocator.hasService(IWorkbenchLocationService.class)){
			IWorkbenchLocationService service = (IWorkbenchLocationService)serviceLocator.getService(IWorkbenchLocationService.class);
			IEditorPart editor = service.getWorkbenchWindow().getActivePage().getActiveEditor();
			shell = service.getWorkbench().getActiveWorkbenchWindow().getShell();
			
			if(!(editor.getEditorInput() instanceof FileEditorInput))
				return;
			
			FileEditorInput input = (FileEditorInput)editor.getEditorInput();
			
			IFile editorFile = input.getFile();
			String ext = editorFile.getFileExtension();
			
			if (!JAVA_EXT.equalsIgnoreCase(ext)	)
				return;
			
			if(CDICorePlugin.getCDI(editorFile.getProject(), true) == null)
				return;

			MenuManager mm = new MenuManager(CDIUIMessages.CDI_REFACTOR_CONTRIBUTOR_MENU_NAME);
			mm.setVisible(true);
			
			if(JAVA_EXT.equalsIgnoreCase(ext)){
				TextSelection selection = (TextSelection)editor.getEditorSite().getSelectionProvider().getSelection();
				IBean bean = getBean(editorFile, selection);
				if(bean != null){
					mm.add(new RenameNamedBeanAction(bean));
					
					additions.addContributionItem(new Separator(), null);
					additions.addContributionItem(mm, null);
				}
			}
		}
	}
	
	private IBean getBean(IFile file, TextSelection selection){
		CDICoreNature cdiNature = CDICorePlugin.getCDI(file.getProject(), true);
		if(cdiNature == null)
			return null;
		
		ICDIProject cdiProject = cdiNature.getDelegate();
		
		if(cdiProject == null)
			return null;
		
		Set<IBean> beans = cdiProject.getBeans(file.getFullPath());
		
		for(IBean bean : beans){
			if(bean.getName() != null){
				ITextSourceReference location = bean.getNameLocation();
				if(selection.getOffset() >= location.getStartPosition() && (selection.getOffset()+selection.getLength()) <= (location.getStartPosition()+location.getLength()))
					return bean;
			}
		}
		
		return null;
	}
	
	private static void saveAndBuild(){
		if(!CDIUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().saveAllEditors(true))
			return;
		
		try {
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
		} catch (InterruptedException e) {
			// do nothing
		}
	}
	
	public static void invokeRenameNamedBeanWizard(IBean bean, Shell activeShell) {
		saveAndBuild();
		
		RenameNamedBeanProcessor processor = new RenameNamedBeanProcessor(bean);
		RenameRefactoring refactoring = new RenameRefactoring(processor);
		RenameNamedBeanWizard wizard = new RenameNamedBeanWizard(refactoring, bean);
		RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard);
		try {
			String titleForFailedChecks = CDIUIMessages.CDI_REFACTOR_CONTRIBUTOR_ERROR;
			op.run(activeShell, titleForFailedChecks);
		} catch (final InterruptedException irex) {
			// operation was canceled
		}
	}
	
	class RenameNamedBeanAction extends Action{
		IBean bean;
		public RenameNamedBeanAction(IBean bean){
			super(Messages.format(CDIUIMessages.CDI_REFACTOR_CONTRIBUTOR_RENAME_NAMED_BEAN_ACTION_NAME, bean.getName()));
			this.bean = bean;
		}

		public void run(){
			saveAndBuild();

			invokeRenameNamedBeanWizard(bean, shell);
		}
	}
}
