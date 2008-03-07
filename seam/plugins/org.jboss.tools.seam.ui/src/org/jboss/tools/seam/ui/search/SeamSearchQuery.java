package org.jboss.tools.seam.ui.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jdt.ui.search.IMatchPresentation;
import org.eclipse.jdt.ui.search.IQueryParticipant;
import org.eclipse.jdt.ui.search.ISearchRequestor;
import org.eclipse.jdt.ui.search.QuerySpecification;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.search.core.text.TextSearchMatchAccess;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.search.core.text.TextSearchScope;
import org.eclipse.search.internal.core.text.PatternConstructor;
import org.eclipse.search.internal.core.text.TextSearchVisitor;
import org.eclipse.search.internal.ui.Messages;
//import org.eclipse.search.internal.ui.SearchMessages;
import org.eclipse.search.internal.ui.text.FileMatch;
import org.eclipse.search.internal.ui.text.FileSearchResult;
import org.eclipse.search.internal.ui.text.SearchResultUpdater;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.search.ui.text.IFileMatchAdapter;
import org.eclipse.search.ui.text.Match;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamDeclaration;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamJavaSourceReference;
import org.jboss.tools.seam.internal.core.el.ElVarSearcher.Var;
import org.jboss.tools.seam.ui.SeamUIMessages;

public class SeamSearchQuery implements ISearchQuery {
	
	private final static class SeamSearchResultCollector extends SeamSearchRequestor {
		private final AbstractTextSearchResult fResult;
//		private final boolean fSearchInBinaries;
		private ArrayList fCachedMatches;
		
		private SeamSearchResultCollector(AbstractTextSearchResult result) {
			fResult= result;
//			fSearchInBinaries= searchInBinaries;
			
		}
		
		public boolean acceptFile(IFile file) throws CoreException {
//			if (fIsFileSearchOnly) {
//				fResult.addMatch(new FileMatch(file, 0, 0));
//			}
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
				fResult.addMatches((Match[]) fCachedMatches.toArray(new Match[fCachedMatches.size()]));
				fCachedMatches.clear();
			}
		}
	}
	
	private final SeamSearchScope fScope;
	private final String[] fVariables;
	private final Var fVar;
	private SeamSearchResult fResult;
	
	public SeamSearchQuery(String[] variables, SeamSearchScope scope) {
		fVariables = variables;
		fVar = null;
		fScope= scope;
	}

	public SeamSearchQuery(Var var, SeamSearchScope scope) {
		fVariables = null;
		fVar = var;
		fScope= scope;
	}
	
	public SeamSearchScope getSearchScope() {
		return fScope;
	}
	
	public boolean canRunInBackground() {
		return true;
	}

	
	public IStatus run(final IProgressMonitor monitor) {
		AbstractTextSearchResult textResult= (AbstractTextSearchResult) getSearchResult();
		textResult.removeAll();
		
		if (fVariables != null) {
			Pattern[] searchPatterns = new Pattern[fVariables == null ? 0 : fVariables.length];
			for (int i = 0; i < searchPatterns.length; i++) { 
				searchPatterns[i]= getSearchPattern(fVariables[i]);
			}
			SeamSearchResultCollector collector= new SeamSearchResultCollector(textResult);
			return SeamSearchEngine.create().search(fScope, collector, searchPatterns, monitor);
		} else if (fVar != null) {
			if (fScope.isLimitToDeclarations()) {
				textResult.addMatch(new SeamElementMatch((IFile)fScope.getRoots()[0], fVar.getDeclarationOffset(), fVar.getDeclarationLength()));
			} else {
				Pattern[] searchPatterns = new Pattern[fVar == null ? 0 : 1];
				if (searchPatterns.length > 0) {
					searchPatterns[0]= getSearchPattern(fVar.getName());
				}
				SeamSearchResultCollector collector= new SeamSearchResultCollector(textResult);
				return SeamSearchEngine.create().search(fScope, collector, searchPatterns, monitor);
				
			}
			return Status.OK_STATUS;
		}
		return Status.OK_STATUS;
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
	

	public String getLabel() {
		Object[] args= { fScope.getLimitToDescription() };
		return Messages.format(SeamUIMessages.SeamSearchQuery_label, args);
	}
	
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
	
	public String getResultLabel(int nMatches) {
		String searchString= getSearchString();
		if (searchString.length() > 0) {
			if (fScope.isLimitToDeclarations()) {
				// search is limited to declarations only
				if (nMatches == 1) {
					Object[] args= { searchString, fScope.getDescription(), fScope.getLimitToDescription() };
					return Messages.format(SeamUIMessages.SeamSearchQuery_singularPatternWithLimitTo, args);
				}
				Object[] args= { searchString, new Integer(nMatches), fScope.getDescription(), fScope.getLimitToDescription() };
				return Messages.format(SeamUIMessages.SeamSearchQuery_pluralPatternWithLimitTo, args);
			}
			if (fScope.isLimitToReferences()) {
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
/*
 * 
 			// text search
			if (isScopeAllFileTypes()) {
				// search all file extensions
				if (nMatches == 1) {
					Object[] args= { searchString, fScope.getDescription() };
					return Messages.format(SearchMessages.FileSearchQuery_singularLabel, args);
				}
				Object[] args= { searchString, new Integer(nMatches), fScope.getDescription() };
				return Messages.format(SearchMessages.FileSearchQuery_pluralPattern, args); 
			}
			// search selected file extensions
			if (nMatches == 1) {
				Object[] args= { searchString, fScope.getDescription(), fScope.getFilterDescription() };
				return Messages.format(SearchMessages.FileSearchQuery_singularPatternWithFileExt, args);
			}
			Object[] args= { searchString, new Integer(nMatches), fScope.getDescription(), fScope.getFilterDescription() };
			return Messages.format(SearchMessages.FileSearchQuery_pluralPatternWithFileExt, args);
*/
		}
/*
 * 
 		// file search
		if (nMatches == 1) {
			Object[] args= { fScope.getFilterDescription(), fScope.getDescription() };
			return Messages.format(SearchMessages.FileSearchQuery_singularLabel_fileNameSearch, args); 
		}
		Object[] args= { fScope.getFilterDescription(), new Integer(nMatches), fScope.getDescription() };
		return Messages.format(SearchMessages.FileSearchQuery_pluralPattern_fileNameSearch, args); 
*/
		return "";
	}


	protected Pattern getSearchPattern(String variableName) {
		return PatternConstructor.createPattern(variableName, true, false);
	}
	
	public boolean canRerun() {
		return true;
	}

	public ISearchResult getSearchResult() {
		if (fResult == null) {
			fResult= new SeamSearchResult(this);
			new SearchResultUpdater(fResult);
		}
		return fResult;
	}
}
