/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.batch.internal.core.scanner.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

/**
 * Monitors class path of project and loads seam components of it.
 *  
 * @author Viacheslav Kabanovich
 */
public abstract class AbstractClassPathMonitor implements Libs.LibsListener {
	Libs libs;
	
	protected List<String> paths = new ArrayList<String>();
	private boolean loaded = false;

	protected Map<IPath, String> paths2 = new HashMap<IPath, String>();
	boolean libsModified = false;
	
	protected Set<String> processedPaths = new HashSet<String>();

	public AbstractClassPathMonitor() {
	}

	public IProject getProject() {
		return getProjectResource();
	}

	/**
	 * Initialization of inner model.
	 */
	public void init() {
		if(libs == null && getProject() != null) {
			libs = new Libs(getProject());
			libs.addListener(this);
			libs.init();
		}
	}

	public abstract IProject getProjectResource();
	
	/**
	 * Returns true if class path was up-to-date.
	 * Otherwise, updates inner model and disables class loader.
	 * @return
	 */
	public boolean update() {
		if(libs == null) {
			return false;
		}
		libs.update();
		synchronized(this) {
			List<String> newPaths = libs.getPaths();
			boolean result = libsModified || !loaded;
			if(newPaths != null) {
				paths = newPaths;
				loaded = true;
			} else {
				paths = new ArrayList<String>();
				loaded = false;
			}
			if(result) {
				paths2.clear();
				paths2.putAll(libs.getPathsAsMap());
			}
			libsModified = false;
			return result;
		}
	}
	
	public void pathLoaded(IPath path) {
		String p = paths2.get(path);
		if(p != null) synchronized (this) {
			processedPaths.add(p);
		}
	}
	
	public boolean hasPath(IPath path) {
		return paths2.get(path) != null;
	}

	public void clean() {
		if(libs != null) {
			libs.requestForUpdate();
		}
		paths = new ArrayList<String>();
		loaded = false;
		if(paths2 != null) paths2.clear();
		synchronized (this) {
			processedPaths.clear();
		}
	}

	public synchronized void reset() {
		processedPaths.clear();
	}

	protected List<String> syncProcessedPaths() {
		ArrayList<String> removed = new ArrayList<String>();
		synchronized (this) {
			Iterator<String> it = processedPaths.iterator();
			while(it.hasNext()) {
				String p = it.next();
				if(paths.contains(p)) continue;
				removed.add(p);
				it.remove();
			}
		}
		return removed;
	}

	protected boolean requestForLoad(String p) {
		synchronized (this) {
			return processedPaths.add(p);
		}
	}

	public void pathsChanged(List<String> paths) {
		synchronized (this) {
			libsModified = true;
		}
	}

	public void libraryChanged(String path) {
		synchronized (this) {
			libsModified = true;
			processedPaths.remove(path);
		}
	}

	public void dispose() {
		if(libs != null) {
			libs.destroy();
			libs = null;
		}
	}
}
