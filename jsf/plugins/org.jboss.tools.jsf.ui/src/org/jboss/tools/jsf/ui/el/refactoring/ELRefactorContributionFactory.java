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
package org.jboss.tools.jsf.ui.el.refactoring;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.internal.services.IWorkbenchLocationService;
import org.eclipse.ui.menus.AbstractContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.services.IServiceLocator;
import org.jboss.tools.common.el.core.ELReference;
import org.jboss.tools.common.el.core.model.ELExpression;
import org.jboss.tools.common.el.core.resolver.ELCompletionEngine;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.el.core.resolver.ELResolution;
import org.jboss.tools.common.el.core.resolver.ELResolver;
import org.jboss.tools.common.el.core.resolver.ELSegment;
import org.jboss.tools.common.el.core.resolver.JavaMemberELSegment;
import org.jboss.tools.common.el.core.resolver.MessagePropertyELSegment;
import org.jboss.tools.common.model.ui.editor.EditorPartWrapper;
import org.jboss.tools.common.propertieseditor.PropertiesCompoundEditor;
import org.jboss.tools.jsf.el.refactoring.RenameELVariableProcessor;
import org.jboss.tools.jsf.el.refactoring.RenameMessagePropertyProcessor;
import org.jboss.tools.jsf.ui.JsfUIMessages;
import org.jboss.tools.jsf.ui.JsfUiPlugin;
import org.jboss.tools.jst.web.kb.PageContextFactory;
import org.jboss.tools.jst.web.ui.editors.WebCompoundEditor;

/**
 * @author Daniel Azarov
 */
public class ELRefactorContributionFactory extends AbstractContributionFactory {
	//private static final String ANNOTATION_NAME = "org.jboss.seam.annotations.Name"; //$NON-NLS-1$
	private static final String JAVA_EXT = "java"; //$NON-NLS-1$
	private static final String XML_EXT = "xml"; //$NON-NLS-1$
	private static final String XHTML_EXT = "xhtml"; //$NON-NLS-1$
	private static final String JSP_EXT = "jsp"; //$NON-NLS-1$
	private static final String PROPERTIES_EXT = "properties"; //$NON-NLS-1$
	
	//static private String selectedText;
	static private IFile editorFile;
	//private String fileContent;
	private IEditorPart editor;
	private Shell shell;
	
	public ELRefactorContributionFactory(){
		super("","");
	}
	
	public ELRefactorContributionFactory(String location, String namespace){
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
			
			if (!JAVA_EXT.equalsIgnoreCase(ext)
					&& !XML_EXT.equalsIgnoreCase(ext)
					&& !XHTML_EXT.equalsIgnoreCase(ext)
					&& !JSP_EXT.equalsIgnoreCase(ext)
					&& !PROPERTIES_EXT.equalsIgnoreCase(ext))
				return;
			
			MenuManager mm = new MenuManager(JsfUIMessages.REFACTOR_CONTRIBUTOR_MAIN_MENU);
			mm.setVisible(true);
			
			boolean separatorIsAdded = false;
			
			ISelection sel = editor.getEditorSite().getSelectionProvider().getSelection();
			
			if(sel == null || sel.isEmpty())
				return;
			
			if(sel instanceof StructuredSelection){
				if(editor instanceof PropertiesCompoundEditor){
					sel = ((PropertiesCompoundEditor)editor).getActiveEditor().getSite().getSelectionProvider().getSelection();
				}else if(editor instanceof EditorPartWrapper){
					EditorPartWrapper wrapperEditor = (EditorPartWrapper)editor;
					if(wrapperEditor.getEditor() instanceof WebCompoundEditor){
						WebCompoundEditor xmlEditor = (WebCompoundEditor)wrapperEditor.getEditor();
						sel = xmlEditor.getActiveEditor().getSite().getSelectionProvider().getSelection();
					}
				}else if(editor instanceof WebCompoundEditor)
					sel = ((WebCompoundEditor)editor).getActiveEditor().getSite().getSelectionProvider().getSelection();
			}

			if(sel instanceof TextSelection){
				TextSelection selection = (TextSelection)sel;

				ELSegment segment = findELSegment(editorFile, selection);
				if(segment == null)
					return;
				if(segment instanceof MessagePropertyELSegment){
					mm.add(new RenameMessagePropertyAction((MessagePropertyELSegment)segment));

					if(!separatorIsAdded){
						additions.addContributionItem(new Separator(), null);
						separatorIsAdded = true;
					}
				}

				if(segment instanceof JavaMemberELSegment){
					mm.add(new RenameELVariableAction((JavaMemberELSegment)segment));

					if(!separatorIsAdded){
						additions.addContributionItem(new Separator(), null);
						separatorIsAdded = true;
					}
				}
				if(mm.getSize() > 0)
					additions.addContributionItem(mm, null);
			}
		}
	}
	
	
	private ELSegment findELSegment(IFile file, TextSelection selection){
		ELContext context = PageContextFactory.createPageContext(file);
		
		if(context == null)
			return null;
		
		ELReference reference = context.getELReference(selection.getOffset());
		if(reference == null)
			return null;
		
		ELResolver[] resolvers = context.getElResolvers();
		
		for(ELExpression operand : reference.getEl()){
			for (ELResolver resolver : resolvers) {
				ELResolution resolution = resolver.resolve(context, operand, selection.getOffset());
				
				if(resolution == null)
					continue;
				
				List<ELSegment> segments = resolution.getSegments();
				
				for(ELSegment segment : segments){
					if(!segment.isResolved())
						break;
					
					if(selection.getOffset() <= reference.getStartPosition()+segment.getSourceReference().getStartPosition() &&
						selection.getOffset()+selection.getLength() >= reference.getStartPosition()+segment.getSourceReference().getStartPosition()+segment.getSourceReference().getLength() &&
						(segment instanceof MessagePropertyELSegment || segment instanceof JavaMemberELSegment)){
							return segment;
					}
				}

			}
		}
		return null;
	}
	
	private static void saveAndBuild(){
		if(!JsfUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().saveAllEditors(true))
			return;
		
		try {
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
		} catch (InterruptedException e) {
			// do nothing
		}
	}
	
	public static void invokeRenameELVariableWizard(String oldName, Shell activeShell) {
		saveAndBuild();
		
		RenameELVariableProcessor processor = new RenameELVariableProcessor(editorFile, oldName);
		RenameRefactoring refactoring = new RenameRefactoring(processor);
		RenameELVariableWizard wizard = new RenameELVariableWizard(refactoring, editorFile);
		RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard);
		try {
			String titleForFailedChecks = JsfUIMessages.EL_REFACTOR_RENAME_HANDLER_ERROR;
			op.run(activeShell, titleForFailedChecks);
		} catch (final InterruptedException irex) {
			// operation was canceled
		}
	}

	public static void invokeRenameMessagePropertyWizard(MessagePropertyELSegment segment, Shell activeShell) {
		saveAndBuild();
		
		RenameMessagePropertyProcessor processor = new RenameMessagePropertyProcessor(editorFile, segment);
		RenameRefactoring refactoring = new RenameRefactoring(processor);
		RenameMessagePropertyWizard wizard = new RenameMessagePropertyWizard(refactoring, editorFile);
		RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard);
		try {
			String titleForFailedChecks = JsfUIMessages.EL_REFACTOR_RENAME_HANDLER_ERROR;
			op.run(activeShell, titleForFailedChecks);
		} catch (final InterruptedException irex) {
			// operation was canceled
		}
	}
	
	class RenameELVariableAction extends Action{
		JavaMemberELSegment segment;
		public RenameELVariableAction(JavaMemberELSegment segment){
			super(JsfUIMessages.REFACTOR_CONTRIBUTOR_RENAME_EL_VARIABLE);
			this.segment = segment;
		}
		public void run(){
			saveAndBuild();
			
			invokeRenameELVariableWizard(segment.getToken().getText(), shell);
		}
	}

	class RenameMessagePropertyAction extends Action{
		MessagePropertyELSegment segment;
		public RenameMessagePropertyAction(MessagePropertyELSegment segment){
			super(JsfUIMessages.REFACTOR_CONTRIBUTOR_RENAME_MESSAGE_PROPERTY);
			this.segment = segment;
		}
		public void run(){
			saveAndBuild();
			
			invokeRenameMessagePropertyWizard(segment, shell);
		}
	}
	
}
