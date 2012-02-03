/*******************************************************************************
 * Copyright (c) 2007-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.seam.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.search.SearchMessages;
import org.eclipse.jdt.internal.ui.search.SearchUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.model.ui.editor.EditorPartWrapper;
import org.jboss.tools.common.model.ui.texteditors.xmleditor.XMLTextEditor;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.el.SeamELCompletionEngine;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
import org.jboss.tools.seam.ui.search.SeamSearchQuery;
import org.jboss.tools.seam.ui.search.SeamSearchScope;

/**
 * Base class for Seam Find actions
 *
 * @author Jeremy
 */
abstract public class FindSeamAction extends Action implements IWorkbenchWindowActionDelegate, IActionDelegate2, ISelectionListener
{

	/**
	 * Constructs the Action object
	 */
	protected FindSeamAction() {
	}

	/**
	 * 	@Override
	 */
	public void run() {
		runWithEvent(null);
	}

	/*
	 *  Returns editor from a given workbench window
	 *  
	 * @param window
	 * @return
	 */
	private IEditorPart getCurrentEditor(IWorkbenchWindow window) {
		if (window == null || window.getActivePage() == null)
			return null;
		
		return window.getActivePage().getActiveEditor();
	}
	
	/*
	 * Returns viewer for a given editor
	 * 
	 * @param editor
	 * @return
	 */
	private ISourceViewer getEditorViewer(IWorkbenchPart editor) {
		ISourceViewer viewer = null;

		if (editor instanceof EditorPartWrapper) {
			editor = ((EditorPartWrapper)editor).getEditor();
		}
		if (editor instanceof JSPMultiPageEditor) {
			viewer = ((JSPMultiPageEditor)editor).getJspEditor().getTextViewer();
		} else if (editor instanceof XMLTextEditor) {
			viewer = ((XMLTextEditor)editor).getTextViewer();
		} else if (editor instanceof MultiPageEditorPart) {
			IEditorPart activeEditor = getActiveEditor((MultiPageEditorPart)editor);
			if (activeEditor instanceof AbstractTextEditor) {
				viewer = getSourceViewer((AbstractTextEditor)activeEditor);
			}
		} else if (editor instanceof AbstractTextEditor ) {
			viewer = getSourceViewer((AbstractTextEditor)editor);
		}
		else if (editor instanceof CompilationUnitEditor) {
			viewer = ((CompilationUnitEditor)editor).getViewer();
		}
		return viewer;
	}
	
	/*
	 * Returns {@link ITextSelection} for a given viewer 
	 * 
	 * @param viewer
	 * @return
	 */
	private ITextSelection getTextSelection(ITextViewer viewer) {
		if (viewer == null || viewer.getSelectionProvider() == null) 
				return null;
		
		return getTextSelection(viewer.getSelectionProvider().getSelection());
	}

	/*
	 * Returns {@link ITextSelection} for a given {@link ISelection}
	 *  
	 * @param selection
	 * @return
	 */
	private ITextSelection getTextSelection(ISelection selection) {
		if (selection == null || selection.isEmpty())
			return null;
		
		if (selection instanceof ITextSelection) {
			return (ITextSelection)selection;
		}
		return null;
	}
	
	/**
	 * 	@Override
	 */
	public void runWithEvent(Event e) {
		IWorkbenchPart activeWorkbenchPart = getCurrentEditor(getActiveWorkbenchWindow());
		if (activeWorkbenchPart == null)
			return;
		
		IEditorInput input = (activeWorkbenchPart instanceof IEditorPart ?
					((IEditorPart)activeWorkbenchPart).getEditorInput() : null);
		ISourceViewer viewer = getEditorViewer(activeWorkbenchPart);
		if (viewer == null)
			return;

		ITextSelection selection = getTextSelection(viewer);
		if (selection == null)
			return;

		int selectionOffset = selection.getOffset();
		
		IDocument document = viewer.getDocument();
		
		IFile file = null;
		
		if (input instanceof IFileEditorInput) {
			file = ((IFileEditorInput)input).getFile();
		}

		IProject project = (file == null ? null : file.getProject());

		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
		if (seamProject == null)
			return;

		ELInvocationExpression expression = SeamELCompletionEngine.findExpressionAtOffset(
				document, selectionOffset, 0, document.getLength()); 

		if (expression == null)
			return; // No EL Operand found

		try {
			performNewSearch(expression, file);
		} catch (JavaModelException jme) {
			SeamGuiPlugin.getPluginLog().logError(jme);
		} catch (InterruptedException ie) {
			SeamGuiPlugin.getPluginLog().logError(ie);
		}
		return;
	}

	/**
	 * Finds the variable names for the selected ELOperandToken tokens
	 * 
	 * @param seamProject
	 * @param document
	 * @param tokens
	 * @return
	 */
	public static String[] findVariableNames(ISeamProject seamProject, IDocument document, ELInvocationExpression tokens) {
		String[] varNames = null;
		if(tokens == null) return varNames;
		
		// Define the Seam project variables to search for declarations 
		List<ISeamContextVariable> variables = new ArrayList<ISeamContextVariable>();
		
		while(tokens != null) {
			try {
				int start = tokens.getStartPosition();
				int end = tokens.getEndPosition();
				String variationText = document.get(start, end - start);
				
				Set<ISeamContextVariable> vars = seamProject.getVariablesByName(variationText);
				if (vars != null)
					variables.addAll(vars);
				
			} catch (BadLocationException e1) {
				SeamGuiPlugin.getPluginLog().logError(e1);
			}
			tokens = tokens.getLeft();
		}
		
		if (!variables.isEmpty()) {
			// Some variable/variables are found - perform search for their declarations
			varNames = new String[variables.size()];
			for (int i = 0; i < variables.size(); i++) {
				varNames[i] = variables.get(i).getName(); 
			}
		}
		
		return varNames;
	}

	// IWorkbenchWindowActionDelegate
	/**
	 * @Override 
	 */
	public void run(IAction action) {
		run();
	}

	/**
	 * @Override
	 */
	public void dispose() {
		// do nothing.
	}

	/**
	 * @Override
	 */
	public void init(IWorkbenchWindow window) {
	}

	/**
	 * @Override
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		selectionChanged(getCurrentEditor(getActiveWorkbenchWindow()), selection);
	}
	

	// IActionDelegate2

	/**
	 * @Override
	 */
	public void runWithEvent(IAction action, Event event) {
		runWithEvent(event);
	}
	
	IAction fDelegatorAction =null;
	/**
	 * @Override
	 */
	public void init(IAction action) {
		fDelegatorAction = action;
	}

	private SeamSearchQuery createQuery(ELInvocationExpression tokens, IFile sourceFile) throws JavaModelException, InterruptedException {
		
		SeamSearchScope scope  = new SeamSearchScope(new IProject[] {sourceFile.getProject()}, getLimitTo());

		return new SeamSearchQuery(tokens, sourceFile, scope);
	}

	/**
	 * Returns the limitTo flag. The possible values are: 
	 *  - SeamSearchScope.SEARCH_FOR_DECLARATIONS
	 *  - SeamSearchScope.SEARCH_FOR_REFERENCES
	 * @return
	 */
	abstract protected int getLimitTo();

	private void performNewSearch(ELInvocationExpression expression, IFile sourceFile) throws JavaModelException, InterruptedException {
		SeamSearchQuery query= createQuery(expression, sourceFile);
		if (query.canRunInBackground()) {
			/*
			 * This indirection with Object as parameter is needed to prevent the loading
			 * of the Search plug-in: the VM verifies the method call and hence loads the
			 * types used in the method signature, eventually triggering the loading of
			 * a plug-in (in this case ISearchQuery results in Search plug-in being loaded).
			 */
			SearchUtil.runQueryInBackground(query);
		} else {
			IProgressService progressService= PlatformUI.getWorkbench().getProgressService();
			/*
			 * This indirection with Object as parameter is needed to prevent the loading
			 * of the Search plug-in: the VM verifies the method call and hence loads the
			 * types used in the method signature, eventually triggering the loading of
			 * a plug-in (in this case it would be ISearchQuery).
			 */
			IStatus status= SearchUtil.runQueryInForeground(progressService, query);
			if (status.matches(IStatus.ERROR | IStatus.INFO | IStatus.WARNING)) {
				ErrorDialog.openError(getShell(), SearchMessages.Search_Error_search_title, SearchMessages.Search_Error_search_message, status); 
			}
		}
	}

	/**
	 * Returns the active editor from active MultipageEditor 
	 * 
	 * @param multiPageEditor
	 * @return
	 */
	public static IEditorPart getActiveEditor(MultiPageEditorPart multiPageEditor) {
		Class editorClass = multiPageEditor.getClass();
		while (editorClass != null) {
			try {
				Method m = editorClass.getDeclaredMethod("getActiveEditor", new Class[] {});
				
				if(m != null) {  
					m.setAccessible(true);
					Object result = m.invoke(multiPageEditor, new Object[]{});
					return (result instanceof IEditorPart ? (IEditorPart)result : null);
				}
			} catch (NoSuchMethodException e) {
				// ignore
			} catch (IllegalArgumentException e) {
				// ignore
			} catch (IllegalAccessException e) {
				// ignore
			} catch (InvocationTargetException e) {
				// ignore
			}
			editorClass = editorClass.getSuperclass();
		}
		return null;
		
	}	
	
	/**
	 * Returns the source viewer from AbstractTextEditor 
	 * 
	 * @param multiPageEditor
	 * @return
	 */
	public static ISourceViewer getSourceViewer(AbstractTextEditor editor) {
		Class editorClass = editor.getClass();
		while (editorClass != null) {
			try {
				Method m = editorClass.getDeclaredMethod("getSourceViewer", new Class[] {});
				
				if(m != null) {  
					m.setAccessible(true);
					Object result = m.invoke(editor, new Object[]{});
					return (result instanceof ISourceViewer ? (ISourceViewer)result : null);
				}
			} catch (NoSuchMethodException e) {
				// ignore
			} catch (IllegalArgumentException e) {
				// ignore
			} catch (IllegalAccessException e) {
				// ignore
			} catch (InvocationTargetException e) {
				// ignore
			}
			editorClass = editorClass.getSuperclass();
		}
		return null;
		
	}	

	/*
	 * Returns current Shell
	 * 
	 * @return
	 */
	private Shell getShell() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}

//	IWorkbenchPart fActiveWorkbenchPart = null;

	/*
	 *	Returns the workbench
	 * 
	 * @return
	 */
	private IWorkbench getWorkbench() {
		return PlatformUI.getWorkbench();
	}
	
	/*
	 * Returns active workbench window
	 * @return
	 */
	private IWorkbenchWindow getActiveWorkbenchWindow() {
		return (getWorkbench() == null ? null : getWorkbench().getActiveWorkbenchWindow());
	}
	
	/*
	 * Updates availability on the action delegate 
	 *  
	 * @param selection
	 */
	private void update(ISelection selection) {
		boolean enabled = false;
		try {
			IWorkbenchPart activeWorkbenchPart = getCurrentEditor(getActiveWorkbenchWindow()); 
			if (!(activeWorkbenchPart instanceof IEditorPart))
				return;
			
			ISourceViewer viewer = getEditorViewer((IEditorPart)activeWorkbenchPart);
			if (viewer == null)
				return;
	
			enabled = (getTextSelection(selection) != null);
		} finally {
			setEnabled(enabled);
			if (fDelegatorAction != null) {
				fDelegatorAction.setEnabled(enabled);
			}
		}
	}

	// ISelectionListener
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		update(getTextSelection(getEditorViewer(part)));
	}
}
