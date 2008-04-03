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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.search.ISearchRequestor;
import org.eclipse.search.core.text.TextSearchMatchAccess;
import org.eclipse.search.internal.core.text.PatternConstructor;
import org.eclipse.search.internal.ui.Messages;
import org.eclipse.search.internal.ui.text.FileMatch;
import org.eclipse.search.internal.ui.text.SearchResultUpdater;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.Match;
import org.jboss.tools.seam.core.ISeamDeclaration;
import org.jboss.tools.seam.core.ISeamJavaSourceReference;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.el.ELOperandToken;
import org.jboss.tools.seam.internal.core.el.SeamELCompletionEngine;
import org.jboss.tools.seam.internal.core.el.ElVarSearcher.Var;
import org.jboss.tools.seam.ui.SeamUIMessages;

/**
 * Seam search query implementation
 * 
 * @author Jeremy
 */
public class SeamSearchQuery implements ISearchQuery {

	/**
	 * Result collector is used be a holder for search results
	 *  
	 * @author Jeremy
	 */
	public final static class SeamSearchResultCollector extends SeamSearchRequestor {
		private final AbstractTextSearchResult fResult;
		private ArrayList<Match> fCachedMatches;
		private final ISearchRequestor fParentRequestor; 
		
		public SeamSearchResultCollector(AbstractTextSearchResult result, ISearchRequestor parentRequestor) {
			fResult= result;
			fParentRequestor = parentRequestor;
		}
		
		public boolean acceptFile(IFile file) throws CoreException {
			flushMatches();
			return true;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.search.core.text.TextSearchRequestor#reportBinaryFile(org.eclipse.core.resources.IFile)
		 */
		public boolean reportBinaryFile(IFile file) {
			return false;
		}

		public boolean acceptPatternMatch(TextSearchMatchAccess matchRequestor) throws CoreException {
			fCachedMatches.add(new FileMatch(matchRequestor.getFile(), matchRequestor.getMatchOffset(), matchRequestor.getMatchLength()));
			return true;
		}

		public boolean acceptSeamDeclarationSourceReferenceMatch(ISeamJavaSourceReference element) throws CoreException {
			fCachedMatches.add(new SeamElementMatch(element));
			return true;
		}
		
		public boolean acceptSeamDeclarationMatch(ISeamDeclaration element) throws CoreException {
			fCachedMatches.add(new SeamElementMatch(element));
			return true;
		}

		public void beginReporting() {
			fCachedMatches= new ArrayList();
		}
		
		public void endReporting() {
			flushMatches();
			fCachedMatches= null;
		}

		private void flushMatches() {
			if (!fCachedMatches.isEmpty()) {
				if (fResult != null) fResult.addMatches((Match[]) fCachedMatches.toArray(new Match[fCachedMatches.size()]));
				if (fParentRequestor != null) {
					for (Match match : fCachedMatches) {
						fParentRequestor.reportMatch(match);
					}
				}
				fCachedMatches.clear();
			}
		}
		
		public void reportMatch(Match match) {
			fCachedMatches.add(match);
		}

	}
	
	private List<ELOperandToken> fTokens;
	private IJavaElement[] fJavaElements;
	private final SeamSearchScope fScope;
	private final String[] fVariables;
	private final Var fVar;
	private SeamSearchResult fResult;
	private IFile fSourceFile;
	private ISearchRequestor fParentRequestor; 
	
	/**
	 * Constructs Seam search query for a given {@link ELOperandToken} objects list
	 *  
	 * @param tokens
	 * @param sourceFile
	 * @param scope
	 */
	public SeamSearchQuery(List<ELOperandToken> tokens, IFile sourceFile, SeamSearchScope scope) {
		fTokens = tokens;
		fJavaElements = null;
		fSourceFile = sourceFile;
		fVariables = null;
		fVar = null;
		fScope= scope;
	}
	
	/**
	 * Constructs Seam search query for a given {@link IJavaElement} objects array
	 * 
	 * @param javaElements
	 * @param sourceFile
	 * @param scope
	 */
	public SeamSearchQuery(IJavaElement[] javaElements, IFile sourceFile, SeamSearchScope scope) {
		fTokens = null;
		fJavaElements = javaElements;
		fSourceFile = sourceFile;
		fVariables = null;
		fVar = null;
		fScope= scope;
	}

	/**
	 * Constructs Seam search query for a given {@link Var} objects array
	 * 
	 * @param var
	 * @param sourceFile
	 * @param scope
	 */
	public SeamSearchQuery(Var var, IFile sourceFile, SeamSearchScope scope) {
		fTokens = null;
		fJavaElements = null;
		fSourceFile = sourceFile;
		fVariables = null;
		fVar = var;
		fScope= scope;
	}

	/**
	 * Sets up a parent ISearchRequestor
	 * 
	 * @param requestor
	 */
	public void setParentRequestor(ISearchRequestor requestor) {
		this.fParentRequestor = requestor;
	}

	/**
	 * Returns parent requestor
	 * 
	 * @return
	 */
	public ISearchRequestor getParentRequestor() {
		return this.fParentRequestor;
	}
	
	/**
	 * Returns Seam Search Scope
	 * 
	 * @return
	 */
	public SeamSearchScope getSearchScope() {
		return fScope;
	}
	
	/**
	 * @Override
	 */
	public boolean canRunInBackground() {
		return false;
	}

	
	/**
	 * @Override
	 */
	public IStatus run(final IProgressMonitor monitor) {
		AbstractTextSearchResult textResult= (AbstractTextSearchResult) getSearchResult();
		textResult.removeAll();
		
		if (fJavaElements != null) {
			return queryByJavaElements(textResult, monitor);
		}

		if (fTokens != null) {
			return queryByTokens(textResult, monitor);
		}
		return Status.OK_STATUS;
	}
	
	private IStatus queryByTokens(AbstractTextSearchResult textResult, 
			final IProgressMonitor monitor) {
		SeamELCompletionEngine engine = new SeamELCompletionEngine();
		
		IProject project = (fSourceFile == null ? null : fSourceFile.getProject());

		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
		if (seamProject == null)
			return Status.OK_STATUS;

//		List<IJavaElement> elements = engine.getJavaElementsForELOperandTokens(seamProject, fSourceFile, fTokens)
		SeamSearchResultCollector collector= new SeamSearchResultCollector(textResult, getParentRequestor());
		return SeamSearchEngine.getInstance().search(fScope, collector, fSourceFile, fTokens, monitor);
	}

	private IStatus queryByJavaElements(
			AbstractTextSearchResult textResult, 
			final IProgressMonitor monitor) {
		SeamELCompletionEngine engine = new SeamELCompletionEngine();
		
		IProject project = (fSourceFile == null ? null : fSourceFile.getProject());

		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
		if (seamProject == null)
			return Status.OK_STATUS;

//		List<IJavaElement> elements = engine.getJavaElementsForELOperandTokens(seamProject, fSourceFile, fTokens)
		SeamSearchResultCollector collector= new SeamSearchResultCollector(textResult, getParentRequestor());
		return SeamSearchEngine.getInstance().search(fScope, collector, fSourceFile, fJavaElements, monitor);
	}

	private boolean isScopeAllFileTypes() {
		String[] fileNamePatterns= fScope.getFileNamePatterns();
		if (fileNamePatterns == null)
			return true;
		for (int i= 0; i < fileNamePatterns.length; i++) {
			if ("*".equals(fileNamePatterns[i])) { //$NON-NLS-1$
				return true;
			}
		}
		return false;
	}
	

	/**
	 * @see org.eclipse.search.ui.ISearchQuery#getLabel()
	 */
	public String getLabel() {
		Object[] args= { fScope.getLimitToDescription() };
		return Messages.format(SeamUIMessages.SeamSearchQuery_label, args);
	}

	/**
	 * Returns Search String
	 * @return
	 */
	public String getSearchString() {
		String searchString = "";
		if (fVariables != null) {
			StringBuffer buf= new StringBuffer();
			for (int i= 0; i < fVariables.length; i++) {
				if (i > 0) {
					buf.append(", "); //$NON-NLS-1$
				}
				buf.append(fVariables[i]);
			}
			searchString = buf.toString();
		} else if (fVar != null) {
			searchString = fVar.getName();
		}
		return searchString;
	}

	/**
	 * Returns Search Result Label
	 * 
	 * @param nMatches
	 * @return
	 */
	public String getResultLabel(int nMatches) {
		String searchString= getSearchString();
		if (searchString.length() > 0) {
			if (SeamSearchEngine.isSearchForDeclarations(fScope.getLimitTo())) {
				// search is limited to declarations only
				if (nMatches == 1) {
					Object[] args= { searchString, fScope.getDescription(), fScope.getLimitToDescription() };
					return Messages.format(SeamUIMessages.SeamSearchQuery_singularPatternWithLimitTo, args);
				}
				Object[] args= { searchString, new Integer(nMatches), fScope.getDescription(), fScope.getLimitToDescription() };
				return Messages.format(SeamUIMessages.SeamSearchQuery_pluralPatternWithLimitTo, args);
			}
			if (SeamSearchEngine.isSearchForReferences(fScope.getLimitTo())) {
				// text search
				if (isScopeAllFileTypes()) {
					// search all file extensions
					if (nMatches == 1) {
						Object[] args= { searchString, fScope.getDescription(), fScope.getLimitToDescription() };
						return Messages.format(SeamUIMessages.SeamSearchQuery_singularLabel, args);
					}
					Object[] args= { searchString, new Integer(nMatches), fScope.getDescription(), fScope.getLimitToDescription() };
					return Messages.format(SeamUIMessages.SeamSearchQuery_pluralPattern, args); 
				}
			}
		}

		return "";
	}

	/*
	 * returns a search pattern for a given name
	 */
	public static Pattern getSearchPattern(String variableName) {
		return PatternConstructor.createPattern(variableName, true, false);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchQuery#canRerun()
	 */
	public boolean canRerun() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchQuery#getSearchResult()
	 */
	public ISearchResult getSearchResult() {
		if (fResult == null) {
			fResult= new SeamSearchResult(this);
			new SearchResultUpdater(fResult);
		}
		return fResult;
	}
}
