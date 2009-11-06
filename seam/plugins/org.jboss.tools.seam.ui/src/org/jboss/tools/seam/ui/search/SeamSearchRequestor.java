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
import org.eclipse.jdt.ui.search.ISearchRequestor;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.search.ui.text.Match;
import org.jboss.tools.common.java.IJavaSourceReference;
import org.jboss.tools.seam.core.ISeamDeclaration;

/**
 * Seam Search Requestor
 * 
 * @author Jeremy
 */
public class SeamSearchRequestor extends TextSearchRequestor implements ISearchRequestor {
	/**
	 * Notification sent before search starts in the given Seam Java Source Reference. This method is called for all Seam Java Source References are contained
	 * in the search scope.
	 * Implementors can decide if the Seam Java Source Reference's content should be searched for search matches or not.
	 * <p>
	 * The default behaviour is to search the file for matches.
	 * </p>
	 * @param Seam Java Source Reference the file resource to be searched.
	 * @return If false, no pattern matches will be reported for the content of this file.
	 * @throws CoreException implementors can throw a {@link CoreException} if accessing the resource fails or another
	 * problem prevented the processing of the search match.
	 */
	public boolean acceptSeamDeclarationSourceReferenceMatch(IJavaSourceReference reference) throws CoreException {
		return true;
	}

	/**
	 * Notification sent before search starts in the given Seam Element. This method is called for all Seam Java Source References are contained
	 * in the search scope.
	 * Implementors can decide if the Seam Java Source Reference's content should be searched for search matches or not.
	 * <p>
	 * The default behaviour is to search the file for matches.
	 * </p>
	 * @param Seam Java Source Reference the file resource to be searched.
	 * @return If false, no pattern matches will be reported for the content of this file.
	 * @throws CoreException implementors can throw a {@link CoreException} if accessing the resource fails or another
	 * problem prevented the processing of the search match.
	 */
	public boolean acceptSeamDeclarationMatch(ISeamDeclaration element) throws CoreException {
		return true;
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.ui.search.ISearchRequestor#reportMatch(org.eclipse.search.ui.text.Match)
	 */
	public void reportMatch(Match match) {
	}

}
