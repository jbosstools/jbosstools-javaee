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
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.search.core.text.TextSearchScope;
import org.eclipse.search.internal.ui.Messages;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.ui.SeamGuiPlugin;

/**
 * Seam Search Scope object
 * 
 * @author Jeremy
 *
 */
public class SeamSearchScope extends TextSearchScope implements IJavaSearchScope {
	public static final int SEARCH_FOR_DECLARATIONS = IJavaSearchConstants.DECLARATIONS;
	public static final int SEARCH_FOR_REFERENCES = IJavaSearchConstants.REFERENCES;
	
	private static final String[] FILE_NAMES = new String[] {
		"*" 
	};
	
	int fLimitTo;
	String fDescription;
	FileTextSearchScope fFileTextSearchScope;
	IJavaSearchScope fJavaSearchScope;
	
	/**
	 * Constructs SeamSearchScope object using a given {@link IJavaSearchScope}
	 *  
	 * @param javaScope
	 * @param limitTo
	 */
	public SeamSearchScope(IJavaSearchScope javaScope, int limitTo) {
		fJavaSearchScope = javaScope;
		
		ProjectVisitor projectVisitor = new ProjectVisitor(fJavaSearchScope);
		try {
			ResourcesPlugin.getWorkspace().getRoot().accept(projectVisitor, 0);
		}
		catch (CoreException e) {
//			e.printStackTrace();
			SeamGuiPlugin.getPluginLog().logError(e);
		}

		IResource[] projects = projectVisitor.getProjects();

		fFileTextSearchScope = FileTextSearchScope.newSearchScope(projects, 
				FILE_NAMES, true);
		fDescription = getScopeDescription(projects);
		fLimitTo = limitTo;
	}
	
	/**
	 * Constructs SeamSearchScope object using a given {@link IResources} set
	 *  
	 * @param javaScope
	 * @param limitTo
	 */
	public SeamSearchScope(IResource[] resources, int limitTo) {
		fFileTextSearchScope = FileTextSearchScope.newSearchScope(resources, 
				FILE_NAMES, true);
		fDescription = getScopeDescription(resources);
		fLimitTo = limitTo;
	}
	
	private String getScopeDescription(IResource[] resources) {
		String description;
		if (resources.length == 0) {
			description= SeamCoreMessages.SeamSearchScope_scope_empty;
		} else if (resources.length == 1) {
			String label= SeamCoreMessages.SeamSearchScope_scope_single;
			description= Messages.format(label, resources[0].getName());
		} else if (resources.length == 2) {
			String label= SeamCoreMessages.SeamSearchScope_scope_double;
			description= Messages.format(label, new String[] { resources[0].getName(), resources[1].getName()});
		} else {
			String label= SeamCoreMessages.SeamSearchScope_scope_multiple;
			description= Messages.format(label, new String[] { resources[0].getName(), resources[1].getName()});
		}
		return description;
	}

	/** 
	 * Returns limitTo flag value
	 */
	public int getLimitTo() {
		return fLimitTo;
	}

	@Override
	public boolean contains(IResourceProxy proxy) {
		return fFileTextSearchScope.contains(proxy);
	}
	
	/**
	 * Returns the file name pattern configured for this scope or <code>null</code> to match
	 * all file names.
	 * 
	 * @return the file name pattern strings
	 */
	public String[] getFileNamePatterns() {
		return fFileTextSearchScope.getFileNamePatterns();
	}

	/**
	 * Returns the description of the scope
	 * 
	 * @return the description of the scope
	 */
	public String getDescription() {
		return fDescription;
	}

	/**
	 * Returns the description of the scope
	 * 
	 * @return the description of the scope
	 */
	public String getLimitToDescription() {
		return SeamSearchEngine.isSearchForDeclarations(getLimitTo()) ?
			SeamCoreMessages.SeamSearchScope_scope_LimitToDeclarations : 
			SeamCoreMessages.SeamSearchScope_scope_LimitToReferences;
	}

	/**
	 * Returns a description describing the file name patterns and content types.
	 * 
	 * @return the description of the scope
	 */
	public String getFilterDescription() {
		return fFileTextSearchScope.getFilterDescription();
	}
	
	/**
	 * Returns the resources that form the root. Roots can not contain each other. Root elements are only part of the
	 * scope if they are also accepted by {@link #contains(IResourceProxy)}.
	 * 
	 * @return returns the set of root resources. The default behavior is to return the workspace root.
	 */
	public IResource[] getRoots() {
		return fFileTextSearchScope.getRoots();
	}

	
	/**
	 * Evaluates all Seam Projects in this scope.
	 * 
	 * @param status a {@link MultiStatus} to collect the error status that occurred while collecting resources.
	 * @return returns the files in the scope.
	 */
	public ISeamProject[] evaluateSeamProjectsInScope(MultiStatus status) {
		IFile[] files = evaluateFilesInScope(status);
		ArrayList<IProject> projects = new ArrayList<IProject>();
		ArrayList<ISeamProject> seamProjects = new ArrayList<ISeamProject>();
		
		for (int i = 0; files != null && i < files.length; i++) {
			IProject project = (files[i] == null ? null : files[i].getProject());
			if (project == null || projects.contains(project)) 
				continue;
			ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
			if (seamProject != null && !seamProjects.contains(seamProject)) {
				projects.add(project);
				seamProjects.add(seamProject);
			}
		}
		return (ISeamProject[]) seamProjects.toArray(new ISeamProject[seamProjects.size()]);
	}

	
	
	// visitor that retieves all the files for JavaScope in workspace
	class ProjectVisitor implements IResourceProxyVisitor {
		// hash map forces only one of each file
		private HashMap fProjects = new HashMap();
		IJavaSearchScope fScope = null;

		public ProjectVisitor(IJavaSearchScope scope) {
			this.fScope = scope;
		}

		public boolean visit(IResourceProxy proxy) throws CoreException {

			if(SeamSearchEngine.getInstance().isCanceled())
				return false;
			
			if (proxy.getType() == IResource.PROJECT) {

				IProject project = (IProject)proxy.requestResource();
				fProjects.put(project.getParent().getFullPath(), project);
				return true;
			}
			if (proxy.getType() == IResource.FILE) {
				// don't search deeper for files
				return false;
			}

			return true;
		}

		public IProject[] getProjects() {
			return (IProject[]) fProjects.values().toArray(new IProject[fProjects.size()]);
		}
	}


	/**
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.core.search.IJavaSearchScope#encloses(java.lang.String)
	 */
	public boolean encloses(String resourcePath) {
		return (fJavaSearchScope == null ? true : fJavaSearchScope.encloses(resourcePath));
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.core.search.IJavaSearchScope#encloses(org.eclipse.jdt.core.IJavaElement)
	 */
	public boolean encloses(IJavaElement element) {
		return (fJavaSearchScope == null ? true : fJavaSearchScope.encloses(element));
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.core.search.IJavaSearchScope#enclosingProjectsAndJars()
	 */
	public IPath[] enclosingProjectsAndJars() {
		return (fJavaSearchScope == null ? new IPath[0] : fJavaSearchScope.enclosingProjectsAndJars());
	}

	
	/**
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.core.search.IJavaSearchScope#includesBinaries()
	 */
	public boolean includesBinaries() {
		return (fJavaSearchScope == null ? true : fJavaSearchScope.includesBinaries());
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.core.search.IJavaSearchScope#includesClasspaths()
	 */
	public boolean includesClasspaths() {
		return (fJavaSearchScope == null ? true : fJavaSearchScope.includesClasspaths());
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.core.search.IJavaSearchScope#setIncludesBinaries(boolean)
	 */
	public void setIncludesBinaries(boolean includesBinaries) {
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.core.search.IJavaSearchScope#setIncludesClasspaths(boolean)
	 */
	public void setIncludesClasspaths(boolean includesClasspaths) {
	}

}
