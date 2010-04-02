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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
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
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

/**
 * @author Daniel Azarov
 */
public class CDIRefactorContributionFactory extends AbstractContributionFactory {
	private static final String ANNOTATION_NAMED = "javax.inject.Named"; //$NON-NLS-1$
	private static final String JAVA_EXT = "java"; //$NON-NLS-1$
	
	static private IFile editorFile;
	private IEditorPart editor;
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
			editor = service.getWorkbenchWindow().getActivePage().getActiveEditor();
			shell = service.getWorkbench().getActiveWorkbenchWindow().getShell();
			
			if(!(editor.getEditorInput() instanceof FileEditorInput))
				return;
			
			FileEditorInput input = (FileEditorInput)editor.getEditorInput();
			
			editorFile = input.getFile();
			String ext = editorFile.getFileExtension();
			
			if (!JAVA_EXT.equalsIgnoreCase(ext)	)
				return;
			
			if(CDICorePlugin.getCDI(editorFile.getProject(), true) == null)
				return;
			
			MenuManager mm = new MenuManager(CDIUIMessages.CDI_REFACTOR_CONTRIBUTOR_MENU_NAME);
			mm.setVisible(true);
			
			boolean separatorIsAdded = false;
			
			if(JAVA_EXT.equalsIgnoreCase(ext)){
				IBean bean = getBean(editorFile);
				if(bean != null){
					mm.add(new RenameNamedBeanAction());
					
					additions.addContributionItem(new Separator(), null);
					additions.addContributionItem(mm, null);
					separatorIsAdded = true;
				}
			}
			
		}
	}
	
	private IBean getBean(IFile file){
		IProject project = file.getProject();
		CDICoreNature cdiNature = CDICorePlugin.getCDI(file.getProject(), true);
		if(cdiNature == null)
			return null;
		
		ICDIProject cdiProject = cdiNature.getDelegate();
		
		if(cdiProject == null)
			return null;
		
		Set<IBean> beans = cdiProject.getBeans(file.getFullPath());
		
		for(IBean bean : beans){
			if(bean.getName() != null)
				return bean;
		}
		
		return null;
	}
	
	private IAnnotation getNamedAnnotation(IFile file){
		try{
			ICompilationUnit unit = getCompilationUnit(file);
			for(IType type : unit.getAllTypes()){
				for(IAnnotation annotation : type.getAnnotations()){
					if(EclipseJavaUtil.resolveType(type, annotation.getElementName()).equals(ANNOTATION_NAMED))
						return annotation;
					}
			}
		}catch(CoreException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
		return null;
	}
	
	private ICompilationUnit getCompilationUnit(IFile file) throws CoreException {
		IProject project = file.getProject();
		IJavaProject javaProject = (IJavaProject)project.getNature(JavaCore.NATURE_ID);
		for (IResource resource : EclipseResourceUtil.getJavaSourceRoots(project)) {
			if(resource.getFullPath().isPrefixOf(file.getFullPath())) {
				IPath path = file.getFullPath().removeFirstSegments(resource.getFullPath().segmentCount());
				IJavaElement element = javaProject.findElement(path);
				if(element instanceof ICompilationUnit) {
					return (ICompilationUnit)element;
				}
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
		public RenameNamedBeanAction(){
			super(CDIUIMessages.CDI_REFACTOR_CONTRIBUTOR_RENAME_NAMED_BEAN_ACTION_NAME);
		}

		public void run(){
			saveAndBuild();

			IBean bean = getBean(editorFile);
			invokeRenameNamedBeanWizard(bean, shell);
		}
	}
}
