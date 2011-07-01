/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jsf.jsf2.bean.model;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

public interface IJSF2Project {

	public IJSF2ManagedBean[] getManagedBeans();

	public Set<IJSF2ManagedBean> getManagedBeans(IPath path);

	public IProject getProject();

	public Set<? extends IJSF2Project> getUsedProjects();

	public void addUsedProject(IJSF2Project project);

	public void addDependentProject(IJSF2Project project);

	public void removeUsedProject(IJSF2Project project);

	public void pathRemoved(IPath path);

	public boolean isStorageResolved();
	public void resolve();
	public void update();

}
