/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.test.testmodel;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.IJavaSearchScope;

public class JavaSearchScope implements IJavaSearchScope {

	@Override
	public boolean encloses(String resourcePath) {
		return true;
	}

	@Override
	public boolean encloses(IJavaElement element) {
		return true;
	}

	@Override
	public IPath[] enclosingProjectsAndJars() {
		return new IPath[]{JavaProject.defaultPath};
	}

	@Override
	public boolean includesBinaries() {
		return false;
	}

	@Override
	public boolean includesClasspaths() {
		return true;
	}

	@Override
	public void setIncludesBinaries(boolean includesBinaries) {
	}

	@Override
	public void setIncludesClasspaths(boolean includesClasspaths) {
	}

}
