package org.jboss.tools.seam.ui.search;

import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.core.resources.IFile;

import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.search.core.text.TextSearchScope;
import org.eclipse.search.internal.ui.SearchPlugin;

public abstract class SeamSearchEngine {
	
	/**
	 * Creates an instance of the search engine. By default this is the default text search engine (see {@link #createDefault()}),
	 * but extensions can offer more sophisticated search engine implementations.
	 * @return the created {@link TextSearchEngine}.
	 */
	public static SeamSearchEngine create() {
		return createDefault();
	}

	/**
	 * Creates the default, built-in, text search engine that implements a brute-force search, not using
	 * any search index.
	 * Note that clients should always use the search engine provided by {@link #create()}.
	 * @return an instance of the default text search engine {@link TextSearchEngine}.
	 */
	public static SeamSearchEngine createDefault() {
		return new SeamSearchEngine() {
			public IStatus search(TextSearchScope scope, SeamSearchRequestor requestor, Pattern[] searchPatterns, IProgressMonitor monitor) {
				return new SeamSearchVisitor(requestor, searchPatterns).search(scope, monitor);
			}
			
//			public IStatus search(IFile[] scope, SeamSearchRequestor requestor, Pattern[] searchPatterns, IProgressMonitor monitor) {
//				 return new SeamSearchVisitor(requestor, searchPatterns).search(scope, monitor);
//			}
		};
	}
	
	/**
	 * Uses a given search pattern to find matches in the content of workspace file resources. If a file is open in an editor, the
	 * editor buffer is searched.

	 * @param requestor the search requestor that gets the search results
	 * @param scope the scope defining the resources to search in
	 * 	@param searchPattern The search pattern used to find matches in the file contents.
	 * @param monitor the progress monitor to use
	 * @return the status containing information about problems in resources searched.
	 */
	public abstract IStatus search(TextSearchScope scope, SeamSearchRequestor requestor, Pattern[] searchPatterns, IProgressMonitor monitor);

	/**
	 * Uses a given search pattern to find matches in the content of workspace file resources. If a file is open in an editor, the
	 * editor buffer is searched.

	 * @param requestor the search requestor that gets the search results
	 * @param scope the files to search in
	 * 	@param searchPattern The search pattern used to find matches in the file contents.
	 * @param monitor the progress monitor to use
	 * @return the status containing information about problems in resources searched.
	 */
//	public abstract IStatus search(IFile[] scope, SeamSearchRequestor requestor, Pattern[] searchPatterns, IProgressMonitor monitor);
}
