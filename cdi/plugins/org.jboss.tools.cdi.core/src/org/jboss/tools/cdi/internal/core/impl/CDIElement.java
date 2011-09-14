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
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIElement;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.extension.CDIExtensionManager;
import org.jboss.tools.common.java.ParametedType;
import org.jboss.tools.common.java.ParametedTypeFactory;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public abstract class CDIElement implements ICDIElement {
	protected CDIElement parent;
	protected IPath source;

	@Override
	public CDIProject getCDIProject() {
		return parent != null ? parent.getCDIProject() : null;
	}

	@Override
	public ICDIProject getDeclaringProject() {
		return parent != null ? parent.getDeclaringProject() : null;
	}

	public CDIExtensionManager getExtensionManager() {
		CDIProject project = getCDIProject();
		return project == null ? null : project.getNature().getExtensionManager();
	}

	protected ParametedType getObjectType(IMember context) {
		try {
			return getCDIProject().getNature().getTypeFactory().getParametedType(context, ParametedTypeFactory.OBJECT);
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
			return null;
		}
	}

	public void setParent(CDIElement parent) {
		this.parent = parent;
	}

	public CDIElement getParent() {
		return parent;
	}

	@Override
	public IResource getResource() {
		IPath path = getSourcePath();
		if(path == null) return null;
		IResource r = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		if(r == null || !r.exists()) return null;
		return r;
	}

	@Override
	public IPath getSourcePath() {
		return source != null ? source : parent != null ? parent.getSourcePath() : null;
	}

	public void setSourcePath(IPath source) {
		this.source = source;
	}

	@Override
	public boolean exists() {
		return parent != null && parent.exists();
	}
}
