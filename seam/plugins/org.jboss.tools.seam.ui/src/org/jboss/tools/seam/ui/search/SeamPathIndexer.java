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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchPattern;
import org.jboss.tools.seam.ui.SeamGuiPlugin;

/**
 * Seam path indexer. Used to determine visible Seam paths
 *  
 * @author Jeremy
 *
 */
public class SeamPathIndexer {

	private class FileVisitor implements IResourceProxyVisitor {
		// hash map forces only one of each file
		private HashMap fPaths = new HashMap();
		IJavaSearchScope fScope = null;
		SearchPattern fPattern = null;

		public FileVisitor(SearchPattern pattern, IJavaSearchScope scope) {
			this.fPattern = pattern;
			this.fScope = scope;
		}

		public boolean visit(IResourceProxy proxy) throws CoreException {

			if(SeamSearchEngine.getInstance().isCanceled())
				return false;
			
			if (proxy.getType() == IResource.FILE) {

				IFile file = (IFile)proxy.requestResource();
				if(!file.isSynchronized(IResource.DEPTH_ZERO)) {
					// The resource is out of sync with the file system
					// Just ignore this resource.
					return false;
				}

				IContentDescription contentDescription = file.getContentDescription();
				String ctId = null;
				if (contentDescription != null) {
					ctId = contentDescription.getContentType().getId();
				}

				if (this.fScope.encloses(proxy.requestFullPath().toString())) {
					fPaths.put(file.getParent().getFullPath(), SeamSearchEngine.getInstance().computeIndexLocation(file.getParent().getFullPath()));
				}
						
				// don't search deeper for files
				return false;
			}
			return true;
		}

		public IPath[] getPaths() {
			return (IPath[]) fPaths.values().toArray(new IPath[fPaths.size()]);
		}
	}

	/**
	 * Returns visible Seam paths
	 * 
	 * @param pattern
	 * @param scope
	 * @return
	 */
	public IPath[] getVisibleSeamPaths(SearchPattern pattern, IJavaSearchScope scope) {

		FileVisitor seamFileVisitor = new FileVisitor(pattern, scope);
		try {
			ResourcesPlugin.getWorkspace().getRoot().accept(seamFileVisitor, 0);
		}
		catch (CoreException e) {
//			e.printStackTrace();
			SeamGuiPlugin.getPluginLog().logError(e);
		}
		return seamFileVisitor.getPaths();
	}
}

