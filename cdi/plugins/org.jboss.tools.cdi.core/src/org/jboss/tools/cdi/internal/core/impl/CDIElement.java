/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.internal.core.impl;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.jboss.tools.cdi.core.ICDIElement;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CDIElement implements ICDIElement {
	protected CDIElement parent;
	protected IPath source;

	public CDIProject getCDIProject() {
		return parent != null ? parent.getCDIProject() : null;
	}

	public void setParent(CDIElement parent) {
		this.parent = parent;
	}

	public CDIElement getParent() {
		return parent;
	}

	public IResource getResource() {
		IPath path = getSourcePath();
		if(path == null) return null;
		IResource r = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		if(r == null || !r.exists()) return null;
		return r;
	}

	public IPath getSourcePath() {
		return source != null ? source : parent != null ? parent.getSourcePath() : null;
	}

	public void setSourcePath(IPath source) {
		this.source = source;
	}

}
