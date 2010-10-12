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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.ltk.core.refactoring.Change;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamProjectRenameChange extends SeamProjectChange {

	protected String newName;
	protected String oldName;

	private List<String> relevantProjectNameProperties = new ArrayList<String>();
	private List<String> relevantSourceFolderProperties = new ArrayList<String>();

	/**
	 * @param project
	 * @param newName
	 * @param oldName
	 */
	public SeamProjectRenameChange(IProject project, String newName, String oldName) {
		super(project);
		this.newName = newName;
		this.oldName = oldName;
		IEclipsePreferences ps = getSeamPreferences();
		for (int i = 0; i < PROJECT_NAME_PROPERTIES.length; i++) {
			if(oldName.equals(ps.get(PROJECT_NAME_PROPERTIES[i], null))) {
				relevantProjectNameProperties.add(PROJECT_NAME_PROPERTIES[i]);
			} 
		}
		for (int i = 0; i < FOLDER_PROPERTIES.length; i++) {
			if(ps.get(FOLDER_PROPERTIES[i], "").startsWith("/" + oldName + "/")) {
				relevantSourceFolderProperties.add(FOLDER_PROPERTIES[i]);
			} 
		}
		findPreferences(ps);
	}
	
	private void findPreferences(IEclipsePreferences ps){
		for (String property: relevantProjectNameProperties) {
			if(oldName.equals(ps.get(property, null))) {
				preferences.put(property, newName);
			}
		}
		String oldPrefix = "/" + oldName + "/";
		for (String property: relevantSourceFolderProperties) {
			String oldProperty = ps.get(property, "");
			if(oldProperty.startsWith(oldPrefix) && oldProperty.length()>oldPrefix.length()) {
				preferences.put(property, "/" + newName + "/" + oldProperty.substring(oldPrefix.length()));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.refactoring.SeamProjectChange#isRelevant()
	 */
	@Override
	public boolean isRelevant() {
		return !relevantProjectNameProperties.isEmpty() || !relevantSourceFolderProperties.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#perform(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public Change perform(IProgressMonitor pm) throws CoreException {
		if(!isRelevant()) {
			return null;
		}
		try {
			pm.beginTask(getName(), 1);

			if(project.getName().equals(oldName)) {
				IResource newProject = ResourcesPlugin.getWorkspace().getRoot().findMember(newName);
				if(!project.exists() && (newProject instanceof IProject) && newProject.exists()) {
					project = (IProject)newProject;
				}
			}
			IEclipsePreferences ps = getSeamPreferences();
			
			for(String key : preferences.keySet()){
				ps.put(key, preferences.get(key));
			}

			try {
				ps.flush();
			} catch (BackingStoreException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
			return new SeamProjectRenameChange(project, oldName, newName);
		} finally {
			pm.done();
		}
	}
}