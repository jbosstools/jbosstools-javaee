/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.seam.ui.actions;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickAssistProcessor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.search.internal.ui.Messages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.internal.core.el.SeamELCompletionEngine;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
import org.jboss.tools.seam.ui.SeamUiImages;
import org.jboss.tools.seam.ui.handlers.FindSeamDeclarationsHandler;
import org.jboss.tools.seam.ui.handlers.FindSeamReferencesHandler;

/**
 * Quick Assist processor. Allows invokation of Find Seam Actions from QuickFix pop-up  
 * @author Jeremy
 */
public class SeamFindQuickAssistProcessor implements IQuickAssistProcessor {

	/**
	 * Constructs SeamFind
	 */
	public SeamFindQuickAssistProcessor() {
	}

	/**
	 * @Override
	 */
	public boolean hasAssists(IInvocationContext context) throws CoreException {
		ISeamProject seamProject = getSeamProject(context);
		if (seamProject==null)
			return false;
		
		IDocument document = getDocument( context.getCompilationUnit() );
		
		//TODO compute region start and end
		int start = 0;
		int end = document.getLength();
		
		ASTNode node = context.getCoveringNode();
		
		System.out.println("Covering node=" + node);

		String[] varNames = getVariableNames(seamProject, document, context.getSelectionOffset(), start, end);

		return (varNames != null && varNames.length != 0);
	}
	
	private ISeamProject getSeamProject(IInvocationContext context) {
		ICompilationUnit cu = context.getCompilationUnit();
		if (cu == null)
			return null;

		IResource javaFile = cu.getResource();
		if (javaFile == null)
			return null;
		
		return SeamCorePlugin.getSeamProject(javaFile.getProject(), true);
	}
	
	private String[] getVariableNames(ISeamProject seamProject, IDocument document, int offset,
			int start, int end) {
		SeamELCompletionEngine engine = new SeamELCompletionEngine();
		ELInvocationExpression tokens = engine.findExpressionAtOffset(
				document, offset, start, end);
		
		if (tokens == null)
			return null;
		
		return FindSeamAction.findVariableNames(seamProject, 
				document, tokens);
	}
	
	/**
	 * @Override
	 */
	public IJavaCompletionProposal[] getAssists(IInvocationContext context,
			IProblemLocation[] locations) throws CoreException {
		
		IJavaCompletionProposal[] result = new IJavaCompletionProposal[0];
		if(!hasAssists( context )) return result;
		
		IDocument document = getDocument( context.getCompilationUnit() );
		try {
			String contents = document.get( context.getSelectionOffset(), context.getSelectionLength() );
			String searchString = "";
			
			ISeamProject seamProject = getSeamProject(context);
			if (seamProject == null)
				return result;

			ELInvocationExpression tokens = SeamELCompletionEngine.findExpressionAtOffset(
					document, 
					context.getSelectionOffset(),
					0,							//TODO compute region start
					document.getLength()		//TODO compute region end
					);				
			if (tokens == null /*|| tokens.size() == 0*/)
				return result;
			
			searchString = tokens.getText();

			result = new IJavaCompletionProposal[2];			
			
			result[0] = new ExternalActionQuickAssistProposal(
					contents, 
					SeamUiImages.getImage("find_seam_declarations.gif"), 
					Messages.format(
							SeamCoreMessages.SeamQuickFixFindDeclarations, 
							new Object[] {searchString}),
					context) {
				public void apply(IDocument target) {
					try {
						new FindSeamDeclarationsHandler().execute(null);
					} catch (ExecutionException e) {
						SeamGuiPlugin.getPluginLog().logError(e);
					}
				}
			};
			result[1] = new ExternalActionQuickAssistProposal(
					contents, 
					SeamUiImages.getImage("find_seam_references.gif"), 
					Messages.format(
							SeamCoreMessages.SeamQuickFixFindReferences, 
							new Object[] {searchString}),
					context) {
				public void apply(IDocument target) {
					try {
						new FindSeamReferencesHandler().execute(null);
					} catch (ExecutionException e) {
						SeamGuiPlugin.getPluginLog().logError(e);
					}
				}
			};
		}
		catch (BadLocationException e) {
			SeamGuiPlugin.getPluginLog().logError( "Could not get document contents for Seam Find Quick Assist", e );
		}
		return result;
	}
	
	private IDocument getDocument(ICompilationUnit cu) throws JavaModelException {
		IFile file= (IFile) cu.getResource();
		IDocument document= JavaUI.getDocumentProvider().getDocument(new FileEditorInput(file));
		if (document == null) {
			return new Document(cu.getSource()); // only used by test cases
		}
		return document;
	}
	
	/**
	 * Custom Quick Assist Proposal
	 */
	public abstract class ExternalActionQuickAssistProposal implements
			IJavaCompletionProposal {
		
		private String contents;
		private ICompletionProposal proposal;
		
		public ExternalActionQuickAssistProposal(String contents, Image image, String description, IInvocationContext context) {
			this.contents = contents;
			
			proposal = new CompletionProposal("",context.getSelectionLength(),0,context.getSelectionOffset()+context.getSelectionLength(), image, description, null,null);
		}
		
		public String getContents() {
			return contents;
		}
		
		public String getName() {
			return null;
		}
		
		public int getRelevance() {
			return 0;
		}
		
		abstract public void apply(IDocument document);
		
		public String getAdditionalProposalInfo() {
			return proposal.getAdditionalProposalInfo();
		}
		
		public IContextInformation getContextInformation() {
			return proposal.getContextInformation();
		}
		
		public String getDisplayString() {
			return proposal.getDisplayString();
		}
		
		public Image getImage() {
			return proposal.getImage();
		}
		
		public Point getSelection(IDocument document) {
			return proposal.getSelection( document );
		}
	}
}
