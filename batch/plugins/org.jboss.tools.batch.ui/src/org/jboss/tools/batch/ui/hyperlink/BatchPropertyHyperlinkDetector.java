package org.jboss.tools.batch.ui.hyperlink;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.text.JavaWordFinder;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jboss.tools.batch.core.IBatchArtifact;
import org.jboss.tools.batch.core.IBatchProperty;
import org.jboss.tools.batch.internal.core.impl.BatchProject;
import org.jboss.tools.batch.internal.core.impl.BatchProjectFactory;
import org.jboss.tools.batch.ui.BatchUIPlugin;

public class BatchPropertyHyperlinkDetector extends AbstractHyperlinkDetector {
	protected IFile file;
	
	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region,
			boolean canShowMultipleHyperlinks) {
		List<IHyperlink> links = new ArrayList<IHyperlink>();

		ITextEditor textEditor= (ITextEditor)getAdapter(ITextEditor.class);
		if (region == null || !(textEditor instanceof JavaEditor))
			return null;
		
		if(textEditor.getEditorInput() instanceof IFileEditorInput){
			file = ((IFileEditorInput)textEditor.getEditorInput()).getFile();
		}
		
		int offset= region.getOffset();
		
		ITypeRoot input= EditorUtility.getEditorInputJavaElement(textEditor, true);
		if (input == null)
			return null;

		IDocument document= textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		IRegion wordRegion= JavaWordFinder.findWord(document, offset);
		if (wordRegion == null)
			return null;
		
		IProject project = input.getJavaProject().getProject();
		
		BatchProject batchProject = (BatchProject) BatchProjectFactory.getBatchProjectWithProgress(project);

		if (batchProject == null) {
			return null;
		}
		
		IJavaElement[] elements = null;
		
		try {
			elements = input.codeSelect(wordRegion.getOffset(), wordRegion.getLength());
			
			if(elements.length != 1)
				return null;
			
			ArrayList<IHyperlink> hyperlinks = new ArrayList<IHyperlink>();
			
			IField field = null;
			
			if(elements[0] instanceof IType){
				elements[0] = input.getElementAt(wordRegion.getOffset());
			}
			
			if(elements[0] instanceof IField){
				field = (IField) elements[0];
			}
			
			if(field != null){
				IType type = field.getDeclaringType();
				IBatchArtifact artifact = batchProject.getArtifact(type);
				if (artifact != null) {
					IBatchProperty property = artifact.getProperty(field);
					if(property != null){
						hyperlinks.add(new BatchPropertyHyperlink(region, property, document));
					}
				}
			}
			if (hyperlinks != null && !hyperlinks.isEmpty()) {
				return (IHyperlink[])hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
			}
		} catch (JavaModelException jme) {
			BatchUIPlugin.getDefault().logError(jme);
		}
		return null;
	}

}
