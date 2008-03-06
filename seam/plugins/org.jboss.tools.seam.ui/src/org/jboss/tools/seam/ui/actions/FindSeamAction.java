package org.jboss.tools.seam.ui.actions;

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
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.jboss.tools.common.model.ui.ModelUIPlugin;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.el.ELOperandToken;
import org.jboss.tools.seam.internal.core.el.ElVarSearcher;
import org.jboss.tools.seam.internal.core.el.SeamELCompletionEngine;
import org.jboss.tools.seam.internal.core.el.SeamELOperandTokenizer;
import org.jboss.tools.seam.internal.core.el.SeamELOperandTokenizerForward;
import org.jboss.tools.seam.internal.core.el.ElVarSearcher.Var;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.search.SeamSearchQuery;
import org.jboss.tools.seam.ui.search.SeamSearchScope;

abstract public class FindSeamAction extends Action implements IWorkbenchWindowActionDelegate, IActionDelegate2 {

	protected FindSeamAction() {
	}

	public void run() {
		runWithEvent(null);
	}
	
	private Shell getShell() {
		try {
			return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		} catch (Throwable x) {
			return null;
		}
	}
	
	public void runWithEvent(Event e) {
		IEditorPart editor = ModelUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		IEditorInput input = editor.getEditorInput();
		IDocument document = null;
		

		ISourceViewer viewer = null;
		
		if (editor instanceof JSPMultiPageEditor) {
			viewer = ((JSPMultiPageEditor)editor).getJspEditor().getTextViewer();
		} else if (editor instanceof CompilationUnitEditor) {
			viewer = ((CompilationUnitEditor)editor).getViewer();
		}
		
		if (viewer == null)
			return;
		
		
		document = viewer.getDocument();
		
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		if (selection.isEmpty())
			return;
		
		
		int selectionOffset = 0;
		if (selection instanceof ITextSelection) {
			ITextSelection tSel = (ITextSelection)selection;
			selectionOffset = tSel.getOffset();
		} else {
			return;
		}
		
		IFile file = null;
		
		if (input instanceof IFileEditorInput) {
			file = ((IFileEditorInput)input).getFile();
		}

		IProject project = (file == null ? null : file.getProject());

		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
		if (seamProject == null)
			return;

		SeamELCompletionEngine engine= new SeamELCompletionEngine();

		int elStart = getELStart(document, selectionOffset);
		
		if (elStart == -1) 
			elStart = selectionOffset;
		
		SeamELOperandTokenizerForward tokenizer = new SeamELOperandTokenizerForward(document, elStart);
		List<ELOperandToken> tokens = tokenizer.getTokens();

		if (tokens == null || tokens.size() == 0)
			return; // No EL Operand found
		
		List<List<ELOperandToken>> variations = SeamELCompletionEngine.getPossibleVarsFromPrefix(tokens);

		// Define the Seam project variables to search for declarations 
		List<ISeamContextVariable> variables = new ArrayList<ISeamContextVariable>();
		
		for (List<ELOperandToken> variation : variations) {
			try {
				int start = variation.get(0).getStart();
				int end = variation.get(variation.size() - 1).getStart() + 
								variation.get(variation.size() - 1).getLength();
				String variationText = document.get(start, end - start);
				
				Set<ISeamContextVariable> vars = seamProject.getVariablesByName(variationText);
				variables.addAll(vars);
			} catch (BadLocationException e1) {
				SeamGuiPlugin.getPluginLog().logError(e1);
			}
		}

		if (variables.size() != 0) {
			// Some variable/variables are found - perform search for their declarations
			String[] varNamesToSearch = new String[variables.size()];
			for (int i = 0; i < variables.size(); i++) {
				varNamesToSearch[i] = variables.get(i).getName(); 
			}
			
			try {
				performNewSearch(varNamesToSearch, project);
			} catch (JavaModelException jme) {
				SeamGuiPlugin.getPluginLog().logError(jme);
			} catch (InterruptedException ie) {
				SeamGuiPlugin.getPluginLog().logError(ie);
			}
			return;
		}

		// Try to look into "var"/"variable" attributes (if we're in the XML-like document)
		
		if ("java".equalsIgnoreCase(file.getFileExtension())) { //$NON-NLS-1$
			return;
		}
		ElVarSearcher varSearcher = new ElVarSearcher(seamProject, file, engine);
		List<Var> allVars = ElVarSearcher.findAllVars(viewer, selectionOffset);
		if (allVars == null || allVars.size() == 0)
			return;
		
		int start = tokens.get(0).getStart();
		int end = tokens.get(tokens.size() - 1).getStart() + 
						tokens.get(tokens.size() - 1).getLength();
		
		String elText;
		try {
			elText = document.get(start, end - start);
		} catch (BadLocationException ble) {
			SeamGuiPlugin.getPluginLog().logError(ble);
			return;
		}
		if (elText == null || elText.length() == 0)
			return;
		
		Var var = varSearcher.findVarForEl(elText, allVars, false);
		if (var == null)
			return;
		try {
			performNewLocalSearch(var, file);
		} catch (JavaModelException jme) {
			SeamGuiPlugin.getPluginLog().logError(jme);
		} catch (InterruptedException ie) {
			SeamGuiPlugin.getPluginLog().logError(ie);
		}		
	}

	

	// ---- IWorkbenchWindowActionDelegate
	// ------------------------------------------------

	public void run(IAction action) {
		run();
	}

	public void dispose() {
		// do nothing.
	}

	public void init(IWorkbenchWindow window) {
		// do nothing.
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing. Action doesn't depend on selection.
	}
	
	// ---- IActionDelegate2
	// ------------------------------------------------

	public void runWithEvent(IAction action, Event event) {
		runWithEvent(event);
	}
	
	public void init(IAction action) {
		// do nothing.
	}

	/* 
	 * Scans the document from the offset to the beginning to find start of Seam EL operand
	 * Returns the start position of first Seam EL operand token 
	 */
	private int getELStart(IDocument document, int offset) {
		SeamELOperandTokenizer tokenizer = new SeamELOperandTokenizer(document, offset);
		List<ELOperandToken> tokens = tokenizer.getTokens();

		if (tokens == null || tokens.size() == 0)
			return -1;
		
		ELOperandToken firstToken = tokens.get(0);
		return firstToken.getStart();
	}
	
	/* 
	 * Scans the document from the offset to the beginning to find start of Seam EL operand
	 * Returns the end position of last Seam EL operand token 
	 */
	private int getELEnd(IDocument document, int offset) {
		SeamELOperandTokenizer tokenizer = new SeamELOperandTokenizerForward(document, offset);
		List<ELOperandToken> tokens = tokenizer.getTokens();

		if (tokens == null || tokens.size() == 0)
			return -1;
		
		ELOperandToken lastToken = tokens.get(tokens.size() - 1);
		return lastToken.getStart() + lastToken.getLength();
	}

	private SeamSearchQuery createQuery(String[] patterns, IProject root) throws JavaModelException, InterruptedException {
		
		SeamSearchScope scope  = new SeamSearchScope(new IProject[] {root}, getLimitTo());

		return new SeamSearchQuery(patterns, scope);
	}

	private SeamSearchQuery createQuery(Var var, IFile root) throws JavaModelException, InterruptedException {
		
		SeamSearchScope scope  = new SeamSearchScope(new IFile[] {root}, getLimitTo());

		return new SeamSearchQuery(var, scope);
	}

	abstract protected int getLimitTo();
	
	private void performNewSearch(String[] patterns, IProject root) throws JavaModelException, InterruptedException {
		SeamSearchQuery query= createQuery(patterns, root);
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

	private void performNewLocalSearch(Var var, IFile root) throws JavaModelException, InterruptedException {
		SeamSearchQuery query= createQuery(var, root);
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

}
