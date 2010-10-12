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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.ltk.core.refactoring.Change;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Alexey Kazakov
 */
public class SeamJavaPackageRenameChange extends SeamProjectChange {

	protected static String[] PACKAGE_NAME_PROPERTIES = {
		ISeamFacetDataModelProperties.ENTITY_BEAN_PACKAGE_NAME,
		ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_NAME,
		ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_NAME
	};
	protected static String[] SOURCE_NAME_PROPERTIES = {
		ISeamFacetDataModelProperties.ENTITY_BEAN_SOURCE_FOLDER,
		ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER,
		ISeamFacetDataModelProperties.TEST_SOURCE_FOLDER
	};

	private IPackageFragmentRoot source;
	private String newName;
	private String oldName;
	private List<Integer> relevantPropertyIndexes = new ArrayList<Integer>();

	/**
	 * @param project
	 */
	public SeamJavaPackageRenameChange(IProject project, IPackageFragmentRoot source, String newName, String oldName) {
		super(project);
		this.source = source;
		this.newName = newName;
		this.oldName = oldName;
		IEclipsePreferences ps = getSeamPreferences();
		for (int i = 0; i < PACKAGE_NAME_PROPERTIES.length; i++) {
			String name = ps.get(PACKAGE_NAME_PROPERTIES[i], null);
			if(name!=null && (oldName.equals(name) || name.startsWith(oldName + "."))) {
				relevantPropertyIndexes.add(new Integer(i));
			}
		}
		findPreferences(ps);
	}
	
	private void findPreferences(IEclipsePreferences ps){
		for (Integer index: relevantPropertyIndexes) {
			String sourceFolderProperty = ps.get(SOURCE_NAME_PROPERTIES[index], null);
			if(!source.getResource().getFullPath().toString().equals(sourceFolderProperty)) {
				continue;
			}
			String packageProperty = PACKAGE_NAME_PROPERTIES[index];
			String name = ps.get(packageProperty, null);
			if(name==null) {
				continue;
			}
			if(oldName.equals(name)) {
				preferences.put(packageProperty, newName);
			} else if(name.startsWith(oldName + ".")) {
				preferences.put(packageProperty, newName + name.substring(oldName.length()));
			}
		}

	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.refactoring.SeamProjectChange#isRelevant()
	 */
	@Override
	public boolean isRelevant() {
		return !relevantPropertyIndexes.isEmpty();
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
			return new SeamJavaPackageRenameChange(project, source, oldName, newName);
		} finally {
			pm.done();
		}
	}
}