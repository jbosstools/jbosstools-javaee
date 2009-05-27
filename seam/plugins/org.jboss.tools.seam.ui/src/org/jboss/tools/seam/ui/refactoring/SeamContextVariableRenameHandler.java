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
package org.jboss.tools.seam.ui.refactoring;

import java.io.IOException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.ui.text.FastJavaPartitionScanner;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.refactoring.RenameComponentProcessor;
import org.jboss.tools.seam.internal.core.refactoring.RenameComponentRefactoring;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.wizard.RenameComponentWizard;

/**
 * @author Daniel Azarov
 */
public class SeamContextVariableRenameHandler extends SeamAbstractHandler {
	private static final String JAVA_EXT = "java"; //$NON-NLS-1$
	private static final String XML_EXT = "xml"; //$NON-NLS-1$
	private static final String XHTML_EXT = "xhtml"; //$NON-NLS-1$
	private static final String JSP_EXT = "jsp"; //$NON-NLS-1$
	private static final String PROPERTIES_EXT = "properties"; //$NON-NLS-1$
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

		ISelection sel = editor.getEditorSite().getSelectionProvider().getSelection();
		
		if(sel == null || sel.isEmpty())
			invokeRenameWizard(null, activeShell);
		
		if(sel instanceof TextSelection && editor.getEditorInput() instanceof FileEditorInput){
			TextSelection selection = (TextSelection)sel;
			
			String text = selection.getText();
			
			System.out.println("Selection text - "+text);
		
			FileEditorInput input = (FileEditorInput)editor.getEditorInput();
		
			IFile file = input.getFile();
			
			String ext = file.getFileExtension();
			String content = null;
			try {
				content = FileUtil.readStream(file.getContents());
			} catch (CoreException e) {
				SeamCorePlugin.getPluginLog().logError(e);
				return null;
			}
			if(ext.equalsIgnoreCase(JAVA_EXT)){
				findContextVariableInJava(file, content, selection);
			} else if(ext.equalsIgnoreCase(XML_EXT) || ext.equalsIgnoreCase(XHTML_EXT) || ext.equalsIgnoreCase(JSP_EXT))
				findContextVariableInDOM(file, content, selection);
			else if(ext.equalsIgnoreCase(PROPERTIES_EXT))
				findContextVariableInProperties(file, content, selection);
		}
		return null;
	}
	
	private void findContextVariableInJava(IFile file, String content, TextSelection selection){
		try {
			FastJavaPartitionScanner scaner = new FastJavaPartitionScanner();
			Document document = new Document(content);
			scaner.setRange(document, 0, document.getLength());
			IToken token = scaner.nextToken();
			while(token!=null && token!=Token.EOF) {
				if(IJavaPartitions.JAVA_STRING.equals(token.getData())) {
					int length = scaner.getTokenLength();
					int offset = scaner.getTokenOffset();
					String value = document.get(offset, length);
					if(value.indexOf('{')>-1) {
						//scanString(file, value, offset);
					}
				}
				token = scaner.nextToken();
			}
		} catch (BadLocationException e) {
			SeamCorePlugin.getDefault().logError(e);
		}
	}
	
	private void findContextVariableInDOM(IFile file, String content, TextSelection selection){
		IModelManager manager = StructuredModelManager.getModelManager();
		if(manager == null) {
			return;
		}
		IStructuredModel model = null;		
		try {
			model = manager.getModelForRead(file);
			if (model instanceof IDOMModel) {
				IDOMModel domModel = (IDOMModel) model;
				IDOMDocument document = domModel.getDocument();
				//scanChildNodes(file, document);
			}
		} catch (CoreException e) {
			SeamCorePlugin.getDefault().logError(e);
        } catch (IOException e) {
        	SeamCorePlugin.getDefault().logError(e);
		} finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
	}

	private void findContextVariableInProperties(IFile file, String content, TextSelection selection){
	
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
	
	
}
