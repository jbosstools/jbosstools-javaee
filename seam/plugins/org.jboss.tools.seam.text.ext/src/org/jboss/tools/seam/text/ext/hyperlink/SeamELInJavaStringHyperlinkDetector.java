/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.seam.text.ext.hyperlink;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.text.JavaWordFinder;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.el.SeamELCompletionEngine;
import org.jboss.tools.seam.text.ext.SeamExtPlugin;

public class SeamELInJavaStringHyperlinkDetector extends
		AbstractHyperlinkDetector {

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		ITextEditor textEditor= (ITextEditor)getAdapter(ITextEditor.class);
		if (region == null || canShowMultipleHyperlinks || !(textEditor instanceof JavaEditor))
			return null;

		int offset= region.getOffset();

		IJavaElement input= EditorUtility.getEditorInputJavaElement(textEditor, false);
		if (input == null)
			return null;

		IDocument document= textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		IRegion wordRegion = JavaWordFinder.findWord(document, offset);

		if (wordRegion == null)
			return null;
		
		if (!checkStartPosition(document, offset))
				return null;
		
		IFile file = null;
		
		try {
			IResource resource = input.getCorrespondingResource();
			if (resource instanceof IFile)
				file = (IFile) resource;
		} catch (JavaModelException e) {
			// Ignore. It is probably because of Java element's resource is not found 
		}
		
		IJavaElement[] elements = findJavaElements(document, file, wordRegion);
		if (elements != null && elements.length > 0)
			return new IHyperlink[] {new SeamELInJavaStringHyperlink(wordRegion, elements)};

		return null;
	}

	public static IJavaElement[] findJavaElements(IDocument document, IFile file, IRegion region) {
	
		IProject project = (file == null ? null : file.getProject());

		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
		if (seamProject == null)
			return null;

		SeamELCompletionEngine engine= new SeamELCompletionEngine();

		String prefix= engine.getJavaElementExpression(document.get(), region.getOffset(), region);
		prefix = (prefix == null ? "" : prefix);

		List<IJavaElement> javaElements = null;
		
		try {
			javaElements = engine.getJavaElementsForExpression(
											seamProject, file, prefix);
		} catch (StringIndexOutOfBoundsException e) {
			SeamExtPlugin.getPluginLog().logError(e);  
		} catch (BadLocationException e) {
			SeamExtPlugin.getPluginLog().logError(e);  
		}

		return javaElements == null ? new IJavaElement[0] : javaElements.toArray(new IJavaElement[0]);
	}

	/*
	 * Checks if the EL start starting characters are present
	 * @param viewer
	 * @param offset
	 * @return
	 * @throws BadLocationException
	 */
	private boolean checkStartPosition(IDocument document, int offset) {
		try {
			while (--offset >= 0) {
				if ('}' == document.getChar(offset))
					return false;


				if ('"' == document.getChar(offset) &&
						(offset - 1) >= 0 && '\\' != document.getChar(offset - 1)) {
					return false;
				}


				if ('{' == document.getChar(offset) &&
						(offset - 1) >= 0 && 
						('#' == document.getChar(offset - 1) || 
								'$' == document.getChar(offset - 1))) {
					return true;
				}
			}
		} catch (BadLocationException e) {
			SeamExtPlugin.getPluginLog().logError(e);
		}
		return false;
	}

}
