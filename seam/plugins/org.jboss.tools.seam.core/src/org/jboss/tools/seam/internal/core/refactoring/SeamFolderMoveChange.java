 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core.refactoring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.ltk.core.refactoring.Change;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Alexey Kazakov
 */
public class SeamFolderMoveChange extends SeamProjectChange {

	private IResource oldResource;
	private IContainer destination;
	private List<String> relevantProperties = new ArrayList<String>();

	/**
	 * @param project
	 */
	public SeamFolderMoveChange(IProject project, IResource oldResource, IContainer destination) {
		super(project);
		this.oldResource = oldResource;
		this.destination = destination;

		if(oldResource.getProject().equals(destination.getProject())) {
			IEclipsePreferences ps = getSeamPreferences();
			for (int i = 0; i < FOLDER_PROPERTIES.length; i++) {
				String propertyValue = ps.get(FOLDER_PROPERTIES[i], null);
				if(propertyValue==null) {
					continue;
				}
				String oldPathString = oldResource.getFullPath().toString();
				if(propertyValue.equals(oldPathString) ||
						propertyValue.startsWith(oldPathString + "/")) {
					relevantProperties.add(FOLDER_PROPERTIES[i]);
				} 
			}
			findPreferences(ps);
		}
	}
	
	private void findPreferences(IEclipsePreferences ps){
		for (String propertyName: relevantProperties) {
			String propertyValue = ps.get(propertyName, "");
			String oldPathString = oldResource.getFullPath().toString();
			String newPath  = destination.getFullPath().append(oldResource.getName()).toString();
			if(propertyValue.equals(oldPathString)) {
				preferences.put(propertyName, newPath);
			} else if(propertyValue.startsWith(oldPathString + "/")) {
				newPath = newPath + propertyValue.substring(oldPathString.length());
				preferences.put(propertyName, newPath);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.refactoring.SeamProjectChange#isRelevant()
	 */
	@Override
	public boolean isRelevant() {
		return !relevantProperties.isEmpty();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#perform(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public Change perform(IProgressMonitor pm) throws CoreException {
		if(!isRelevant()) {
			return null;
		}
		try {
			pm.beginTask(getName(), 1);

			IEclipsePreferences ps = getSeamPreferences();
			
			for(String key : preferences.keySet()){
				ps.put(key, preferences.get(key));
			}
			
			try {
				ps.flush();
			} catch (BackingStoreException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
			IResource newResource = destination.getFile(new Path(oldResource.getName()));
			IContainer oldContainer = oldResource.getParent();
			return new SeamFolderMoveChange(project, newResource, oldContainer);
		} finally {
			pm.done();
		}
	}
}