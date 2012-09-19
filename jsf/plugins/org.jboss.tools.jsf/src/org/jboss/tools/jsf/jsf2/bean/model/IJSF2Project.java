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

	/**
	 * Returns all managed beans declared in Java classes with @ManagedBeans annotation
	 * 
	 * @return
	 */
	public Set<IJSF2ManagedBean> getManagedBeans();

	/**
	 * Returns JSF2 managed beans declared in resource at given path.
	 * 
	 * @param path
	 * @return
	 */
	public Set<IJSF2ManagedBean> getManagedBeans(IPath path);

	/**
	 * Returns JSF2 managed beans with given name.
	 * 
	 * @param name
	 * @return
	 */
	public Set<IJSF2ManagedBean> getManagedBeans(String name);

	/**
	 * Returns true, if file /WEB-INF/faces-config.xml exist and declares
	 * metadata-complete="true", otherwise returns false.
	 * 
	 * When metadata is complete, all managed beans loaded from annotations are disabled.
	 * 
	 * @return
	 */
	public boolean isMetadataComplete();

	/**
	 * Returns current project.
	 * 
	 * @return
	 */
	public IProject getProject();

	/**
	 * Returns set of existing JSF2 projects declared in class path of current project.
	 * 
	 * @return
	 */
	public Set<? extends IJSF2Project> getUsedProjects();

	public void addUsedProject(IJSF2Project project);

	public void addDependentProject(IJSF2Project project);

	public void removeUsedProject(IJSF2Project project);

	/**
	 * Cleans from model objects loaded at given path.
	 * @param path
	 */
	public void pathRemoved(IPath path);

	/**
	 * Returns true, if model is fully loaded.
	 * 
	 * @return
	 */
	public boolean isStorageResolved();

	/**
	 * Fully loads model if was not loaded yet.
	 */
	public void resolve();

	/**
	 * Updates model by loaded definitions.
	 */
	public void update(boolean updateDependent);

}
