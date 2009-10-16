/*******************************************************************************
  * Copyright (c) 2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core.refactoring;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.jboss.tools.common.el.core.refactoring.ProjectsSet;
import org.jboss.tools.common.model.project.ProjectHome;
import org.jboss.tools.seam.core.SeamProjectsSet;

public class ELProjectSet implements ProjectsSet {
	SeamProjectsSet projectsSet;
	IProject project;
	
	public void init(IProject project){
		projectsSet = new SeamProjectsSet(project);
		this.project = project;
	}

	public IProject[] getLinkedProjects() {
		IProject[] projects = projectsSet.getAllProjects(); 
		if(projects.length == 0 || projects[0] == null){
			return new IProject[]{project};
		}
		return projects;
	}

	public IContainer getViewFolder(IProject project){
		if(project.equals(projectsSet.getWarProject()))
			return projectsSet.getDefaultViewsFolder();
		else if(project.equals(projectsSet.getEarProject()))
			return projectsSet.getDefaultEarViewsFolder();
		
		IPath path = ProjectHome.getFirstWebContentPath(project);
		
		if(path != null)
			return project.getFolder(path.removeFirstSegments(1));
		
		return null;
	}

}
