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
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.jboss.tools.common.el.core.refactoring.RefactorSearcher;
import org.jboss.tools.seam.core.SeamProjectsSet;

public abstract class SeamRefactorSearcher extends RefactorSearcher {
	SeamProjectsSet projectsSet;
	
	public SeamRefactorSearcher(IFile file, String name){
		super(file, name);
		projectsSet = new SeamProjectsSet(file.getProject());
	}
	
	public SeamRefactorSearcher(IFile file, String name, IJavaElement element){
		this(file, name);
		javaElement = element;
	}
	
	protected IProject[] getProjects(){
		return projectsSet.getAllProjects();
	}
	
	protected IContainer getViewFolder(IProject project){
		if(project.equals(projectsSet.getWarProject()))
			return projectsSet.getDefaultViewsFolder();
		else if(project.equals(projectsSet.getEarProject()))
			return projectsSet.getDefaultEarViewsFolder();
		
		return null;
	}
	
	@Override
	protected boolean isFileCorrect(IFile file){
		if(!file.isSynchronized(IResource.DEPTH_ZERO)){
			return false;
		}else if(file.isPhantom()){
			return false;
		}else if(file.isReadOnly()){
			return false;
		}
		return true;
	}

}
