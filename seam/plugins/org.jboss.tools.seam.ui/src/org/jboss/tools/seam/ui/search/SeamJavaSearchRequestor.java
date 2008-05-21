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

package org.jboss.tools.seam.ui.search;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.ui.search.ISearchRequestor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.Match;
import org.jboss.tools.seam.ui.SeamGuiPlugin;

/**
 * Wrapper for JavaSearchRequestor. Used to process Search for IJavaElement requests
 *  
 * @author Jeremy
 */
public class SeamJavaSearchRequestor extends SearchRequestor {
	private ISearchRequestor fJavaRequestor = null;
	
	/**
	 * Constructs the SeamJavaSearchRequestor object by default
	 */
	public SeamJavaSearchRequestor() {
		super();
	}
	
	/**
	 * Constructs the SeamJavaSearchRequestor object for parent requestor object
	 */
	public SeamJavaSearchRequestor(ISearchRequestor javaRequestor) {
		// need to report matches to javaRequestor
		this.fJavaRequestor = javaRequestor;
	}

	/**
	 * Maps java search coordinates to corresponding JSP coordinates.
	 * Adds the matches to the Search Results view.
	 * @see org.eclipse.jdt.core.search.SearchRequestor#acceptSearchMatch(org.eclipse.jdt.core.search.SearchMatch)
	 */
	public void acceptSearchMatch(SearchMatch match) throws CoreException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.core.search.SearchRequestor#beginReporting()
	 */
	public void beginReporting() {
	}

	/**
	 * @see org.eclipse.jdt.core.search.SearchRequestor#endReporting()
	 */
	public void endReporting() {
	}

	/**
	 * @see org.eclipse.jdt.core.search.SearchRequestor#enterParticipant(org.eclipse.jdt.core.search.SearchParticipant)
	 */
	public void enterParticipant(SearchParticipant participant) {
	}

	/**
	 * @see org.eclipse.jdt.core.search.SearchRequestor#exitParticipant(org.eclipse.jdt.core.search.SearchParticipant)
	 */
	public void exitParticipant(SearchParticipant participant) {
	}
	
	/**
	 * @param searchDoc
	 * @param start
	 * @param end
	 * @param text
	 * @throws CoreException
	 */
	protected void addSearchMatch(IDocument document, IFile file, int start, int end, String text) {
		
		if(!file.exists())
			return;

		int lineNumber = -1;
		try {
			lineNumber = document.getLineOfOffset(start);
		} catch (BadLocationException e) {
			SeamGuiPlugin.getPluginLog().logError("offset: " + Integer.toString(start), e);
		}
		createSearchMarker(file, start, end, lineNumber);
		
		if(this.fJavaRequestor != null) {
			Match match = new Match(file, start, end - start);
			this.fJavaRequestor.reportMatch(match);
		}
	}

	/**
	 * Creates a search marker on a file
	 * 
	 * @param File
	 * @param start
	 * @param end
	 */
	private void createSearchMarker(IFile file, int start, int end, int lineNumber) {
		
		try {
			IMarker marker = file.createMarker(NewSearchUI.SEARCH_MARKER);
			HashMap attributes = new HashMap(4);
			attributes.put(IMarker.CHAR_START, new Integer(start));
			attributes.put(IMarker.CHAR_END, new Integer(end));
			attributes.put(IMarker.LINE_NUMBER, new Integer(lineNumber));
			marker.setAttributes(attributes);
			
		} catch (CoreException e) {
			SeamGuiPlugin.getPluginLog().logError(e);
		}
	}

}
