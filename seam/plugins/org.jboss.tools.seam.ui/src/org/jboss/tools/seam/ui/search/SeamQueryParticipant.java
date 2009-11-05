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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.internal.core.search.JavaSearchScope;
import org.eclipse.jdt.ui.search.IMatchPresentation;
import org.eclipse.jdt.ui.search.IQueryParticipant;
import org.eclipse.jdt.ui.search.ISearchRequestor;
import org.eclipse.jdt.ui.search.PatternQuerySpecification;
import org.eclipse.jdt.ui.search.QuerySpecification;

/**
 * Implements {@link IQueryParticipant} to build-in a Search for Seam references 
 * into a Java search 
 * 
 * @author Jeremy
 */
public class SeamQueryParticipant implements IQueryParticipant {

	/**
	 * @see org.eclipse.jdt.ui.search.IQueryParticipant#estimateTicks(org.eclipse.jdt.ui.search.QuerySpecification)
	 */
	public int estimateTicks(QuerySpecification specification) {
		return 1;
	}

	/**
	 * @see org.eclipse.jdt.ui.search.IQueryParticipant#getUIParticipant()
	 */
	public IMatchPresentation getUIParticipant() {
		return new SeamMatchPresentation();
	}

	/**
	 * @see org.eclipse.jdt.ui.search.IQueryParticipant#search(org.eclipse.jdt.ui.search.ISearchRequestor, org.eclipse.jdt.ui.search.QuerySpecification, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void search(ISearchRequestor requestor, QuerySpecification querySpecification, IProgressMonitor monitor) throws CoreException {
		
		//indexIfNeeded();
		// >>> Fix for JBIDE-5093: If the querySpecification is an ElementQuerySpecification  then we don't need to perform a search here
		/*
		// do search based on the particular Java query
		if(querySpecification instanceof ElementQuerySpecification) {
			// element search (eg. from global find references in Java file)
			ElementQuerySpecification elementQuery = (ElementQuerySpecification)querySpecification;
			IJavaSearchScope javaScope = querySpecification.getScope();
			
			
			// Search only for references to java element
			if (!SeamSearchEngine.isSearchForReferences(querySpecification.getLimitTo()))
				return;
			
			IJavaElement element = elementQuery.getElement();
			IResource project = element.getResource().getProject();

			SeamSearchScope scope  = new SeamSearchScope(querySpecification.getScope(), querySpecification.getLimitTo());
			
			SeamSearchQuery query = new SeamSearchQuery(
					new IJavaElement[] {element}, 
					(IFile)element.getResource(), 
					scope);
			query.setParentRequestor(requestor);

			query.run(monitor);
		} else */
		// Fix for JBIDE-5093 <<<
		if(querySpecification instanceof PatternQuerySpecification) {
			// pattern search (eg. from Java search page)
			PatternQuerySpecification patternQuery = (PatternQuerySpecification)querySpecification;
			String pattern = patternQuery.getPattern();
			
			SeamJavaSearchRequestor seamRequestor = new SeamJavaSearchRequestor(requestor);

			SeamSearchScope scope  = new SeamSearchScope(new JavaSearchScope(), patternQuery.getLimitTo());
			
			SeamSearchEngine.getInstance().search(pattern, 
													scope, 
													patternQuery.getSearchFor(), 
													patternQuery.getLimitTo(), 
													SearchPattern.R_PATTERN_MATCH, 
													false, 
													seamRequestor);
		}
	}
}
