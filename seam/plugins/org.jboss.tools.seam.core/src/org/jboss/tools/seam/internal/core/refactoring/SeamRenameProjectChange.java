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
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamRenameProjectChange extends Change {
	IProject project;
	String newName;
	String oldName;
	
	static String[] PROPERTIES = {
		"seam.parent.project",
		"seam.ear.project",
		"seam.ejb.project",
		"seam.test.project"
	};
	
	List<String> relevantProperties = new ArrayList<String>();
	
	public SeamRenameProjectChange(IProject project, String newName, String oldName) {
		this.project = project;
		this.newName = newName;
		this.oldName = oldName;
		IEclipsePreferences ps = getSeamPreferences();
		for (int i = 0; i < PROPERTIES.length; i++) {
			if(oldName.equals(ps.get(PROPERTIES[i], null))) {
				relevantProperties.add(PROPERTIES[i]);
			}
		}
	}
	
	public boolean isRelevant() {
		return relevantProperties.size() > 0;
	}

	@Override
	public Object getModifiedElement() {
		return project;
	}

	@Override
	public String getName() {
		return project.getName();
	}

	@Override
	public void initializeValidationData(IProgressMonitor pm) {
	}

	@Override
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change perform(IProgressMonitor pm) throws CoreException {
		if(!isRelevant()) return null;
		try {
			pm.beginTask(getName(), 1);

			IEclipsePreferences ps = getSeamPreferences();
			for (String property: relevantProperties) {
				if(oldName.equals(ps.get(property, null))) {
					ps.put(property, newName);
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
