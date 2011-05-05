/*******************************************************************************
 * Copyright (c) 2008-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.seam.text.ext.hyperlink;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.text.FastJavaPartitionScanner;
import org.eclipse.jdt.internal.ui.text.JavaWordFinder;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jboss.tools.common.el.core.model.ELArgumentInvocation;
import org.jboss.tools.common.el.core.model.ELExpression;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.model.ELPropertyInvocation;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamMessages;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.el.SeamELCompletionEngine;
import org.jboss.tools.seam.text.ext.SeamExtPlugin;

public class SeamELInJavaStringHyperlinkDetector extends
		AbstractHyperlinkDetector {

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		ITextEditor textEditor= (ITextEditor)getAdapter(ITextEditor.class);
		if (region == null /*|| canShowMultipleHyperlinks*/ || !(textEditor instanceof JavaEditor))
			return null;

		int offset= region.getOffset();

		IJavaElement input= EditorUtility.getEditorInputJavaElement(textEditor, false);
		if (input == null)
			return null;

		IDocument document= textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		IRegion wordRegion = JavaWordFinder.findWord(document, offset);

		if (wordRegion == null)
			return null;

		int[] range = null;
		FastJavaPartitionScanner scanner = new FastJavaPartitionScanner();
		scanner.setRange(document, 0, document.getLength());
		while(true) {
			IToken token = scanner.nextToken();
			if(token == null || token.isEOF()) break;
			int start = scanner.getTokenOffset();
			int end = start + scanner.getTokenLength();
			if(start <= offset && end >= offset) {
				range = new int[]{start, end};
				break;
			}
			if(start > offset) break;
		}

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
		
		if(range == null) range = new int[]{0, document.getLength()};
		
		Map<String, ISeamMessages> messages = findMessagesComponents(document, file, wordRegion, range[0], range[1]);
		if (messages != null && !messages.isEmpty())
			return new IHyperlink[] {new SeamELInJavaStringHyperlink(wordRegion, messages)};
		
		IJavaElement[] elements = findJavaElements(document, file, wordRegion, range[0], range[1]);
		if (elements != null && elements.length > 0)
			return new IHyperlink[] {new SeamELInJavaStringHyperlink(wordRegion, elements)};

		return null;
	}

	public static IJavaElement[] findJavaElements(IDocument document, IFile file, IRegion region, int start, int end) {
	
		IProject project = (file == null ? null : file.getProject());

		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
		if (seamProject == null)
			return null;

		SeamELCompletionEngine engine = new SeamELCompletionEngine();

		String prefix= engine.getJavaElementExpression(document, region.getOffset(), region, start, end);
		prefix = (prefix == null ? "" : prefix);

		List<IJavaElement> javaElements = null;
		
		try {
			javaElements = engine.getJavaElementsForExpression(
											seamProject, file, prefix, region.getOffset());
		} catch (StringIndexOutOfBoundsException e) {
			SeamExtPlugin.getPluginLog().logError(e);  
		} catch (BadLocationException e) {
			SeamExtPlugin.getPluginLog().logError(e);  
		}

		return javaElements == null ? new IJavaElement[0] : javaElements.toArray(new IJavaElement[0]);
	}
	
	public static Map<String, ISeamMessages> findMessagesComponents(IDocument document, IFile file, IRegion region, int start, int end) {
		IProject project = (file == null ? null : file.getProject());

		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
		if (seamProject == null)
			return null;

		SeamELCompletionEngine engine = new SeamELCompletionEngine();

		String prefix= engine.getJavaElementExpression(document, region.getOffset(), region, start, end);
		prefix = (prefix == null ? "" : prefix);

		ELExpression exp = engine.parseOperand(prefix);
		if (exp == null)
			return null; // No EL Operand found
		
		Map<ELInvocationExpression, List<ISeamContextVariable>> map = new HashMap<ELInvocationExpression, List<ISeamContextVariable>>();

		exp.getModel().shift(region.getOffset() - exp.getFirstToken().getStart());

		if (	!(exp instanceof ELInvocationExpression) &&
				!(exp instanceof ELPropertyInvocation) && 
				!(exp instanceof ELArgumentInvocation))
			return null;
		
		String propertyName = null;
		if (exp instanceof ELPropertyInvocation) {
			propertyName = ((ELPropertyInvocation)exp).getMemberName();
		} else if (exp instanceof ELArgumentInvocation) {
			propertyName = Utils.trimQuotes(
					((ELArgumentInvocation)exp).getArgument().getArgument().getText());
			
		}
		
		if (propertyName == null)
			return null;
		
//		ScopeType scope = SeamELCompletionEngine.getScope(seamProject, file);

		ELInvocationExpression expr = (ELInvocationExpression)exp;
		
		ELInvocationExpression left = expr;

		if (expr.getLeft() != null) {
			while (left != null) {
				List<ISeamContextVariable> resolvedVars = new ArrayList<ISeamContextVariable>();
				resolvedVars = engine.resolveVariables(seamProject, file, left,
						left == expr, true);
				if (resolvedVars != null && !resolvedVars.isEmpty()) {
					map.put(left, resolvedVars);
					break;
				}
				left = (ELInvocationExpression) left.getLeft();
			}
		}

		
		// At the moment map contains the resolved EL parts mapped to their vars
		// And now we need to extract SeamXmlFactory vars to the real ones 
		// and filter out all non-SeamMessagesComponent vars
		// Next we need to map the SeamMessagesComponent vars to properties
		Map<String, ISeamMessages> messages = new HashMap<String, ISeamMessages>();
		if (map != null && !map.isEmpty()) {
			for (ELInvocationExpression l : map.keySet()) {
				List<ISeamContextVariable> variables = map.get(l);
				for (ISeamContextVariable variable : variables) {
					ISeamMessages messagesVariable = SeamELCompletionEngine.getSeamMessagesComponentVariable(variable);
					if (messagesVariable != null) {
						messages.put(propertyName, messagesVariable);
					}
				}
			}
		}

		return messages;

		
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
