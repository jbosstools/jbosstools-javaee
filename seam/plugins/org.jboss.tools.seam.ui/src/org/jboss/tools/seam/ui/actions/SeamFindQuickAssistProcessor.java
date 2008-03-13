package org.jboss.tools.seam.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
import org.jboss.tools.seam.ui.SeamUiImages;

public class SeamFindQuickAssistProcessor implements IQuickAssistProcessor {

	public SeamFindQuickAssistProcessor() {
	}

	public boolean hasAssists(IInvocationContext context) throws CoreException {
		ICompilationUnit cu = context.getCompilationUnit();
		if (cu == null)
			return false;

		IResource javaFile = cu.getResource();
		if (javaFile == null)
			return false;
		
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(javaFile.getProject(), true);
		return (seamProject!=null);
	}

	public IJavaCompletionProposal[] getAssists(IInvocationContext context,
			IProblemLocation[] locations) throws CoreException {
		
		IJavaCompletionProposal[] result = new IJavaCompletionProposal[0];
		if(!hasAssists( context )) return result;
		
		IDocument document = getDocument( context.getCompilationUnit() );
		try {
			String contents = document.get( context.getSelectionOffset(), context.getSelectionLength() );
			result = new IJavaCompletionProposal[2];			
			
			result[0] = new ExternalActionQuickAssistProposal(contents, SeamUiImages.getImage("find_seam_declarations.gif"), "Find Seam Declarations", context) {
				public void apply(IDocument target) {
					new FindSeamDeclarationsAction().run();
				}
			};
			result[1] = new ExternalActionQuickAssistProposal(contents, SeamUiImages.getImage("find_seam_references.gif"), "Find Seam References", context) {
				public void apply(IDocument target) {
					new FindSeamReferencesAction().run();
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
