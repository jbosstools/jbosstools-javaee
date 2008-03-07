package org.jboss.tools.seam.ui.search;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.jboss.tools.seam.core.ISeamDeclaration;
import org.jboss.tools.seam.core.ISeamJavaSourceReference;

public class SeamSearchRequestor extends TextSearchRequestor {

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
	public boolean acceptSeamDeclarationSourceReferenceMatch(ISeamJavaSourceReference reference) throws CoreException {
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

}
