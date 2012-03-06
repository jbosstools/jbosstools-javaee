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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.zip.CRC32;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.search.core.text.TextSearchEngine;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.resolver.ElVarSearcher;
import org.jboss.tools.common.el.core.resolver.Var;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.internal.core.el.SeamELCompletionEngine;
import org.jboss.tools.seam.ui.SeamGuiPlugin;

/**
 * A helper class used for search operations
 * 
 * @author Jeremy
 */
public abstract class SeamSearchEngine {
	private static SeamSearchEngine fInstance = null;
    private final IProgressMonitor fMonitor = new NullProgressMonitor();
    private SearchParticipant fParticipant = null;
    private IPath fSeamUIPluginLocation = null;
    private final CRC32 fChecksumCalculator = new CRC32();

	/**
	 * Returns an instance. If the instance isn't initialized creates an instance 
	 * of the search engine. 
	 * @return the created {@link SeamSearchEngine}.
	 */
	public static SeamSearchEngine getInstance() {
		if (fInstance == null) {
			fInstance = createDefault();
		}
		return fInstance;
	}

	/**
	 * Creates the default, built-in, text search engine that implements a brute-force search, not using
	 * any search index.
	 * Note that clients should always use the search engine provided by {@link #create()}.
	 * @return an instance of the default text search engine {@link TextSearchEngine}.
	 */
	private static SeamSearchEngine createDefault() {
		return new SeamSearchEngine() {

			@Override
			public IStatus search(SeamSearchScope javaScope,
					SeamSearchRequestor requestor,
					IFile sourceFile,
					ELInvocationExpression tokens,
					IProgressMonitor monitor) {

				if (tokens == null /*|| tokens.size() == 0*/) {
					return Status.OK_STATUS;
				}

				IProject project = (sourceFile == null ? null : sourceFile.getProject());

				ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
				if (seamProject == null)
					return Status.OK_STATUS;

				SeamELCompletionEngine engine = new SeamELCompletionEngine();
				
				
				//Find Seam variable names
				// - if the tokens are the variable name only - search for variable declaration in Seam project
				String variableName = tokens.getText();   //SeamSearchVisitor.tokensToString(tokens);
				
				Set<ISeamContextVariable> variables = seamProject.getVariablesByName(variableName);
				if (variables != null && !variables.isEmpty()) {
					return search(javaScope, requestor, sourceFile, variables.toArray(new ISeamContextVariable[0]), monitor);
				}

				// - else try to find Java Elements and declarations/references for them
				List<IJavaElement> elements = null;
				try {
					elements = engine.getJavaElementsForELOperandTokens(seamProject, sourceFile, tokens);
				} catch (StringIndexOutOfBoundsException e) {
					SeamGuiPlugin.getPluginLog().logError(e);
					return Status.OK_STATUS;
				} catch (BadLocationException e) {
					SeamGuiPlugin.getPluginLog().logError(e);
					return Status.OK_STATUS;
				}

				if (elements != null && !elements.isEmpty()) {
					return search(javaScope, requestor, sourceFile, elements.toArray(new IJavaElement[0]), monitor);
				}
				
				// Try to find a local Var (a pair of variable-value attributes)
				ElVarSearcher varSearcher = new ElVarSearcher(sourceFile, engine);
				// Find a Var in the EL 
				int start = tokens.getStartPosition();
				int end = tokens.getEndPosition();
				
				StringBuffer elText = new StringBuffer();
				elText.append(tokens.toString());

				if (elText == null || elText.length() == 0)
					return Status.OK_STATUS;
				
				List<Var> allVars= varSearcher.findAllVars(sourceFile, tokens.getStartPosition());
				Var var = varSearcher.findVarForEl(elText.toString(), allVars, true);
				if (var == null) {
					// Find a Var in the current offset assuming that it's a node with var/value attribute pair
					var = varSearcher.findVar(sourceFile, tokens.getStartPosition());
				}
				if (var == null)
					return Status.OK_STATUS;

				if (tokens.getLeft() == null) {
					// The only Var is selected to search for
					if (isSearchForDeclarations(javaScope.getLimitTo())) {
						
						boolean res= SeamSearchVisitor.acceptPaternMatch(
								requestor, sourceFile, var.getDeclarationOffset(), var.getDeclarationLength());
						if (!res) {
							return Status.OK_STATUS; // no further reporting requested
						}
					} else {
						return search(javaScope, requestor, sourceFile, new Var[] {var}, monitor);
					}

					
					return Status.OK_STATUS;
				}

				// Need to extract the var value and search for the real elements
				
				return Status.OK_STATUS;
			}
				
			
			@Override
			public IStatus search(SeamSearchScope scope, 
					SeamSearchRequestor requestor, 
					IFile sourceFile,
					IJavaElement[] elements, 
					IProgressMonitor monitor) {
				IProject project = (sourceFile == null ? null : sourceFile.getProject());
				return new SeamSearchVisitor(requestor, elements, project).search(scope, monitor);
			}

			@Override
			public IStatus search(SeamSearchScope scope, 
					SeamSearchRequestor requestor, 
					IFile sourceFile,
					Var[] vars, 
					IProgressMonitor monitor) {
				IProject project = (sourceFile == null ? null : sourceFile.getProject());
				return new SeamSearchVisitor(requestor, vars, sourceFile).search(scope, monitor);
			}

			@Override
		    public IStatus search(String searchText, SeamSearchScope scope, 
		    		int searchFor, int limitTo, int matchMode, boolean isCaseSensitive, SearchRequestor requestor) {

		        SeamJavaSearchJob job = new SeamJavaSearchJob(searchText, scope, searchFor, limitTo, matchMode, isCaseSensitive, requestor);
		        setCanceled(false);
		        job.setUser(true);
		        job.schedule();
		        return Status.OK_STATUS;
		    }


			@Override
			public IStatus search(SeamSearchScope scope,
					SeamSearchRequestor requestor, IFile sourceFile,
					ISeamContextVariable[] variables, IProgressMonitor monitor) {
				IProject project = (sourceFile == null ? null : sourceFile.getProject());
				return new SeamSearchVisitor(requestor, variables, project).search(scope, monitor);
			}
		};
	}

	/**
	 * Uses a given tokens to find matches in the content of 
	 * workspace file resources. If a file is open in an editor, the
	 * editor buffer is searched.
	 * 
	 * @param scope
	 * @param requestor
	 * @param sourceFile
	 * @param tokens
	 * @param monitor
	 * @return the status containing information about problems in resources searched.
	 */
	public abstract IStatus search(SeamSearchScope scope, SeamSearchRequestor requestor, 
			IFile sourceFile, ELInvocationExpression tokens, IProgressMonitor monitor);

	/**
	 * Uses a given IJavaElement-s to find matches in the content of 
	 * workspace file resources. If a file is open in an editor, the
	 * editor buffer is searched.
     * 
	 * @param scope
	 * @param requestor
	 * @param sourceFile
	 * @param elements
	 * @param monitor
	 * @return
	 */
	public abstract IStatus search(SeamSearchScope scope, SeamSearchRequestor requestor, 
			IFile sourceFile, IJavaElement[] elements, IProgressMonitor monitor);

	/**
	 * Uses a given {@link ISeamContextVariable} objects to find matches in the content of 
	 * workspace file resources. If a file is open in an editor, the
	 * editor buffer is searched.
	 * 
	 * @param scope
	 * @param requestor
	 * @param sourceFile
	 * @param variables
	 * @param monitor
	 * @return
	 */
	public abstract IStatus search(SeamSearchScope scope, SeamSearchRequestor requestor, 
			IFile sourceFile, ISeamContextVariable[] variables, IProgressMonitor monitor);

	/**
	 * Uses a given {@link Var} objects to find matches in the content of 
	 * workspace file resources. If a file is open in an editor, the
	 * editor buffer is searched.
	 * 
	 * @param scope
	 * @param requestor
	 * @param sourceFile
	 * @param vars
	 * @param monitor
	 * @return
	 */
	public abstract IStatus search(SeamSearchScope scope, SeamSearchRequestor requestor, 
			IFile sourceFile, Var[] vars, IProgressMonitor monitor);

    /**
     * Perform a java search w/ the given parameters. Runs in a background Job
     * (results may still come in after this method call)
     * 
     * @param searchText
     *            the string of text to search on
     * @param searchFor
     *            IJavaSearchConstants.TYPE, METHOD, FIELD, PACKAGE, etc...
     * @param limitTo
     *            IJavaSearchConstants.DECLARATIONS,
     *            IJavaSearchConstants.REFERENCES,
     *            IJavaSearchConstants.IMPLEMENTORS, or
     *            IJavaSearchConstants.ALL_OCCURRENCES
     * @param matchMode
     *            allow * wildcards or not
     * @param isCaseSensitive
     * @param requestor
     *            passed in to accept search matches (and do "something" with
     *            them)
     */
    public abstract IStatus search(String searchText, SeamSearchScope scope, int searchFor, int limitTo, int matchMode, boolean isCaseSensitive, SearchRequestor requestor);

    /**
     * Seam Indexing and Search jobs check this
     * 
     * @return
     */
    public synchronized final void setCanceled(boolean cancel) {
        fMonitor.setCanceled(cancel);
    }

    /**
     * Seam Indexing and Search jobs check this
     * 
     * @return
     */
    public synchronized final boolean isCanceled() {
        return fMonitor.isCanceled();
    }

    // This is called from SeamPathIndexer
    public final IPath computeIndexLocation(IPath containerPath) {

        IPath indexLocation = null;
        String pathString = containerPath.toOSString();
        this.fChecksumCalculator.reset();
        this.fChecksumCalculator.update(pathString.getBytes());
        String fileName = Long.toString(this.fChecksumCalculator.getValue()) + ".index"; //$NON-NLS-1$
        indexLocation = getSeamUIPluginWorkingLocation().append(fileName);
        JavaModelManager.getJavaModelManager().getIndexManager().indexLocations.put(containerPath, indexLocation);
        return indexLocation;
    }

    // copied from JDT IndexManager
    public IPath getSeamUIPluginWorkingLocation() {

        if (this.fSeamUIPluginLocation != null)
            return this.fSeamUIPluginLocation;

        // Append the folder name "seamsearch" to keep the state location area cleaner
        IPath stateLocation = SeamGuiPlugin.getDefault().getStateLocation().append("seamsearch");

        String device = stateLocation.getDevice();
        if (device != null && device.charAt(0) == '/')
            stateLocation = stateLocation.setDevice(device.substring(1));

        // ensure that it exists on disk
        File folder = new File(stateLocation.toOSString());
		if (!folder.isDirectory()) {
			try {
				folder.mkdir();
			}
			catch (SecurityException e) {
			}
		}

        return this.fSeamUIPluginLocation = stateLocation;
    }

    /**
     * This operation ensures that the live resource's search markers show up in
     * the open editor. It also allows the ability to pass in a ProgressMonitor
     */
    private class SeamJavaSearchJob extends Job implements IJavaSearchConstants {

        String fSearchText = ""; //$NON-NLS-1$

        IJavaSearchScope fScope = null;

        int fSearchFor = FIELD;

        int fLimitTo = ALL_OCCURRENCES;

        int fMatchMode = SearchPattern.R_PATTERN_MATCH;

        boolean fIsCaseSensitive = false;

        SearchRequestor fRequestor = null;

        IJavaElement fElement = null;

        // constructor w/ java element
        public SeamJavaSearchJob(IJavaElement element, IJavaSearchScope scope, SearchRequestor requestor) {
            super(SeamCoreMessages.SeamSearch + element.getElementName());
            this.fElement = element;
            this.fScope = scope;
            this.fRequestor = requestor;
        }

        // constructor w/ search text
        public SeamJavaSearchJob(String searchText, IJavaSearchScope scope, int searchFor, int limitTo, int matchMode, boolean isCaseSensitive, SearchRequestor requestor) {
            super(SeamCoreMessages.SeamSearch + searchText);
            this.fSearchText = searchText;
            this.fScope = scope;
            this.fSearchFor = searchFor;
            this.fLimitTo = limitTo;
            this.fMatchMode = matchMode;
            this.fIsCaseSensitive = isCaseSensitive;
            this.fRequestor = requestor;
        }

        public IStatus run(IProgressMonitor jobMonitor) {

            if (jobMonitor != null && jobMonitor.isCanceled())
                return Status.CANCEL_STATUS;
            if (SeamSearchEngine.getInstance().isCanceled())
                return Status.CANCEL_STATUS;

            SearchPattern javaSearchPattern = null;
            // if an element is available, use that to create search pattern
            // (eg. LocalVariable)
            // otherwise use the text and other paramters
            boolean searchForDeclarations = isSearchForDeclarations(this.fLimitTo);
            boolean searchForReferences = isSearchForDeclarations(this.fLimitTo);

            if (this.fElement != null) {
                javaSearchPattern = SearchPattern.createPattern(this.fElement, this.fLimitTo);
            } else {
                javaSearchPattern = SearchPattern.createPattern(this.fSearchText, this.fSearchFor, this.fLimitTo, this.fMatchMode);
            }

            // if is searching for the declarations:
            // if (fElement != null): 
            // - No components can be found for the element (the declaration for the element itself will be found by java search)
            // - No properties/methods can be found for the Variable/Method (the declaration for the element itself will be found by java search)
            // if (fSearchText != null):
            // try to find the Seam Variable or the Seam variable's Method/Property and search for its declaration.
            
            if (isSearchForDeclarations(this.fLimitTo)) {
                if (this.fElement != null) {
                    javaSearchPattern = SearchPattern.createPattern(this.fElement, this.fLimitTo);
                } else {
                    javaSearchPattern = SearchPattern.createPattern(this.fSearchText, this.fSearchFor, this.fLimitTo, this.fMatchMode);
                }
            }
            // if is searching for the references:
            // if (fElement != null): 
            // ? Search for references to the components which are declared by this element's class
            // - Search for properties/methods can be found for the Variable/Method
            // if (fSearchText != null):
            // try to find the Seam Variable or the Seam variable's Method/Property and search for its declaration.
            if (isSearchForReferences(this.fLimitTo)) {
	            if (this.fElement != null) {
	                javaSearchPattern = SearchPattern.createPattern(this.fElement, this.fLimitTo);
	                
	            } else {
	                javaSearchPattern = SearchPattern.createPattern(this.fSearchText, this.fSearchFor, this.fLimitTo, this.fMatchMode);
	            }
            }
            
            if (javaSearchPattern != null) {
            	
            }
            return Status.OK_STATUS;
        }
        
    }

    /**
     * Checks if the given limitTo flag is limited to declarations
     * 
     * @param limitTo
     * @return
     */
    public static boolean isSearchForDeclarations(int limitTo) {
    	int maskedLimitTo = limitTo & ~(IJavaSearchConstants.IGNORE_DECLARING_TYPE+IJavaSearchConstants.IGNORE_RETURN_TYPE);
    	if (maskedLimitTo == IJavaSearchConstants.DECLARATIONS || maskedLimitTo == IJavaSearchConstants.ALL_OCCURRENCES) {
    		return true;
    	}
    	
    	return false;
    }
    
    /**
     * Checks if the given limitTo flag is limited to references
     * 
     * @param limitTo
     * @return
     */
    public static boolean isSearchForReferences(int limitTo) {
    	int maskedLimitTo = limitTo & ~(IJavaSearchConstants.IGNORE_DECLARING_TYPE+IJavaSearchConstants.IGNORE_RETURN_TYPE);
    	if (maskedLimitTo == IJavaSearchConstants.REFERENCES || maskedLimitTo == IJavaSearchConstants.ALL_OCCURRENCES) {
    		return true;
    	}
    
    	return false;
    }
    
    /**
     * Checks if the given element is IField
     * 
     * @param element
     * @return
     */
    public static boolean isField(IJavaElement element) {
    	if (element == null)
    		return false;
    	
    	return (element.getElementType() == IJavaElement.FIELD);
    }

    /**
     * Checks if the given element is IMethod
     * 
     * @param element
     * @return
     */
    public static boolean isMethod(IJavaElement element) {
    	if (element == null)
    		return false;
    	
    	return (element.getElementType() == IJavaElement.METHOD);
    }

    /**
     * Checks if the given element is IType
     * 
     * @param element
     * @return
     */
    public static boolean isType(IJavaElement element) {
    	if (element == null)
    		return false;
 
    	return (element.getElementType() == IJavaElement.TYPE);
    } 
}
