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
package org.jboss.tools.seam.internal.core.refactoring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamRenameProjectChange extends Change {
	IProject project;
	String newName;
	String oldName;

	static String[] PROJECT_NAME_PROPERTIES = {
		ISeamFacetDataModelProperties.SEAM_PARENT_PROJECT,
		ISeamFacetDataModelProperties.SEAM_EAR_PROJECT,
		ISeamFacetDataModelProperties.SEAM_EJB_PROJECT,
		ISeamFacetDataModelProperties.SEAM_TEST_PROJECT
	};

	static String[] SOURCE_FOLDER_PROPERTIES = {
		ISeamFacetDataModelProperties.ENTITY_BEAN_SOURCE_FOLDER,
		ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER,
		ISeamFacetDataModelProperties.TEST_SOURCE_FOLDER,
		ISeamFacetDataModelProperties.WEB_CONTENTS_FOLDER
	};

	List<String> relevantProjectNameProperties = new ArrayList<String>();
	List<String> relevantSourceFolderProperties = new ArrayList<String>();

	public SeamRenameProjectChange(IProject project, String newName, String oldName) {
		this.project = project;
		this.newName = newName;
		this.oldName = oldName;
		IEclipsePreferences ps = getSeamPreferences();
		for (int i = 0; i < PROJECT_NAME_PROPERTIES.length; i++) {
			if(oldName.equals(ps.get(PROJECT_NAME_PROPERTIES[i], null))) {
				relevantProjectNameProperties.add(PROJECT_NAME_PROPERTIES[i]);
			} 
		}
		for (int i = 0; i < SOURCE_FOLDER_PROPERTIES.length; i++) {
			if(ps.get(SOURCE_FOLDER_PROPERTIES[i], "").startsWith("/" + oldName + "/")) {
				relevantSourceFolderProperties.add(SOURCE_FOLDER_PROPERTIES[i]);
			} 
		}
	}

	public boolean isRelevant() {
		return relevantProjectNameProperties.size() > 0 || relevantSourceFolderProperties.size() > 0;
	}

	@Override
	public Object getModifiedElement() {
		return project;
	}

	@Override
	public String getName() {
		return "Update Seam Project Properties for " + project.getName();
	}

	@Override
	public void initializeValidationData(IProgressMonitor pm) {
	}

	@Override
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException,	OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change perform(IProgressMonitor pm) throws CoreException {
		if(!isRelevant()) return null;
		try {
			pm.beginTask(getName(), 1);

			if(project.getName().equals(oldName)) {
				IResource newProject = ResourcesPlugin.getWorkspace().getRoot().findMember(newName);
				if(!project.exists() && (newProject instanceof IProject) && newProject.exists()) {
					project = (IProject)newProject;
				}
			}
			IEclipsePreferences ps = getSeamPreferences();
			for (String property: relevantProjectNameProperties) {
				if(oldName.equals(ps.get(property, null))) {
					ps.put(property, newName);
				}
			}
			String oldPrefix = "/" + oldName + "/";
			for (String property: relevantSourceFolderProperties) {
				String oldProperty = ps.get(property, "");
				if(oldProperty.startsWith(oldPrefix) && oldProperty.length()>oldPrefix.length()) {
					ps.put(property, "/" + newName + "/" + oldProperty.substring(oldPrefix.length()));
				}
			}

			try {
				ps.flush();
			} catch (BackingStoreException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
			return new SeamRenameProjectChange(project, oldName, newName);
		} finally {
			pm.done();
		}
	}

	public IEclipsePreferences getSeamPreferences() {
		IScopeContext projectScope = new ProjectScope(project);
		return projectScope.getNode(SeamCorePlugin.PLUGIN_ID);
	}
}