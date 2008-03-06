package org.jboss.tools.seam.ui.search;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.search.core.text.TextSearchScope;
import org.eclipse.search.internal.core.text.FilesOfScopeCalculator;
import org.eclipse.search.internal.core.text.PatternConstructor;
import org.eclipse.search.internal.ui.Messages;
import org.eclipse.search.internal.ui.SearchMessages;
import org.eclipse.search.internal.ui.WorkingSetComparator;
import org.eclipse.search.internal.ui.util.FileTypeEditor;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.ui.IWorkingSet;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.ui.SeamUIMessages;

public class SeamSearchScope extends TextSearchScope {
	public static final int SEARCH_FOR_DECLARATIONS = 0;
	public static final int SEARCH_FOR_REFERENCES = 1;
	
	private static final String[] FILE_NAMES = new String[] {
		"*" 
	};
	
	int fLimitTo;
	String fDescription;
	FileTextSearchScope fFileTextSearchScope;

	public SeamSearchScope(IResource[] resources, int limitTo) {
		fFileTextSearchScope = FileTextSearchScope.newSearchScope(resources, 
				FILE_NAMES, true);

		String description;

		if (resources.length == 0) {
			description= SeamUIMessages.SeamSearchScope_scope_empty;
		} else if (resources.length == 1) {
			String label= SeamUIMessages.SeamSearchScope_scope_single;
			description= Messages.format(label, resources[0].getName());
		} else if (resources.length == 2) {
			String label= SeamUIMessages.SeamSearchScope_scope_double;
			description= Messages.format(label, new String[] { resources[0].getName(), resources[1].getName()});
		} else {
			String label= SeamUIMessages.SeamSearchScope_scope_multiple;
			description= Messages.format(label, new String[] { resources[0].getName(), resources[1].getName()});
		}

		fLimitTo = limitTo;
	}
	
	public int getLimitTo() {
		return fLimitTo;
	}
	
	public boolean isLimitToDeclarations() {
		return (SEARCH_FOR_DECLARATIONS == fLimitTo);
	}

	public boolean isLimitToReferences() {
		return (SEARCH_FOR_REFERENCES == fLimitTo);
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
		return fFileTextSearchScope.getDescription();
	}

	/**
	 * Returns the description of the scope
	 * 
	 * @return the description of the scope
	 */
	public String getLimitToDescription() {
		return isLimitToDeclarations() ?
			SeamUIMessages.SeamSearchScope_scope_LimitToDeclarations : 
			SeamUIMessages.SeamSearchScope_scope_LimitToReferences;
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

}
