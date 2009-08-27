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
package org.jboss.tools.seam.core.project.facet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.internal.core.project.facet.SeamFacetPreferenceInitializer;

/**
 * This class is responsible for managing available SeamRuntime list.
 * 	  
 * @author eskimo
 */
public class SeamRuntimeManager {

	private static SeamRuntimeListConverter converter = new SeamRuntimeListConverter();

	private Map<String, SeamRuntime> runtimes = new HashMap<String, SeamRuntime>();

	private SeamRuntime defaultRt = null;

	/**
	 * Private constructor
	 */
	private SeamRuntimeManager() {
		String configVersion = SeamProjectPreferences
				.getStringPreference(SeamProjectPreferences.RUNTIME_CONFIG_FORMAT_VERSION);
		String runtimeListString = SeamProjectPreferences
				.getStringPreference(SeamProjectPreferences.RUNTIME_LIST);

		runtimes = converter.getMap(runtimeListString);
	}

	/**
	 *	This class make Java Runtime responsible for solving synchronization 
	 *	problems during initialization if there is any  
	 *  
	 *  @author eskimo
	 */
	static class SeamRuntimeManagerHolder {
		private static final SeamRuntimeManager INSTANCE = new SeamRuntimeManager();
	}

	/**
	 * Return SeamRuntimeManaher instance
	 * 
	 * @return
	 * 		SeamRuntimeManager instance
	 */
	public static SeamRuntimeManager getInstance() {
		return SeamRuntimeManagerHolder.INSTANCE;
	}

	/**
	 * Return Array of configured SeamRuntimes
	 * 
	 * @return
	 * 		SeamRuntime[]
	 */
	public SeamRuntime[] getRuntimes() {
		Collection<SeamRuntime> c = runtimes.values();
		return c.toArray(new SeamRuntime[runtimes.size()]);
	}

	/**
	 * @return the latest version of installed Seam runtimes. If there are a few runtimes with the same version
	 * then the default one will be returned.
	 */
	public SeamRuntime getLatestSeamRuntime() {
		SeamVersion latestVersion = SeamVersion.SEAM_1_2;
		for (SeamRuntime runtime : runtimes.values()) {
			if(runtime.getVersion().compareTo(latestVersion)>=0) {
				latestVersion = runtime.getVersion();
			}
		}
		SeamRuntime runtime = getDefaultRuntime(latestVersion);
		if(runtime==null) {
			SeamRuntime[] runtimes = getRuntimes(latestVersion);
			if(runtimes.length>0) {
				runtime = runtimes[0];
			}
		}
		return runtime;
	}

	/**
	 * Return array of SeamRuntimes that is compatible with given version
	 * 
	 * @param version
	 * 	SeamVersion 
	 * @return
	 * 	SeamRuntime[]
	 */
	public SeamRuntime[] getRuntimes(SeamVersion version) {
		Collection<SeamRuntime> c = runtimes.values();
		List<SeamRuntime> rts = new ArrayList<SeamRuntime>();
		for (SeamRuntime seamRuntime : c) {
			if (seamRuntime.getVersion() == version) {
				rts.add(seamRuntime);				
			}
		}
		return rts.toArray(new SeamRuntime[rts.size()]);
	}

	/**
	 * Add new SeamRuntime
	 * 
	 * @param runtime
	 * 		SeamRuntime
	 */
	public void addRuntime(SeamRuntime runtime) {
		if (runtimes.size() == 0) {
			runtime.setDefault(true);
		}

		SeamRuntime oldDefaultRuntime = getDefaultRuntime(runtime.getVersion());
		if (oldDefaultRuntime != null && runtime.isDefault()) {
			oldDefaultRuntime.setDefault(false);
		}
		runtimes.put(runtime.getName(), runtime);
		save();
	}

	/**
	 * Add new SeamRuntime with given parameters
	 * 
	 * @param name
	 * 	String - runtime name
	 * @param path
	 * 	String - runtime home folder
	 * @param version
	 * 	String - string representation of version number 
	 * @param defaultRt
	 * 	boolean - default flag
	 */
	public void addRuntime(String name, String path, SeamVersion version,
			boolean defaultRt) {
		SeamRuntime seamRt = new SeamRuntime();
		seamRt.setVersion(version);
		seamRt.setHomeDir(path);
		seamRt.setName(name);
		seamRt.setDefault(defaultRt);
		addRuntime(seamRt);
	}

	/**
	 * Return SeamRuntime by given name
	 * 
	 * @param name
	 * 	String - SeamRuntime name
	 * @return
	 * 	SeamRuntime - found SeamRuntime instance or null
	 */
	public SeamRuntime findRuntimeByName(String name) {
		for (SeamRuntime seamRuntime : runtimes.values()) {
			if (seamRuntime.getName().equals(name)) {
				return seamRuntime;
			}
		}
		return null;
	}

	/**
	 * Remove given SeamRuntime from manager
	 * @param rt
	 * 	SeamRuntime
	 */
	public void removeRuntime(SeamRuntime rt) {
		runtimes.remove(rt.getName());
	}

	/**
	 * Return SeamRuntime instance for given project
	 * 
	 * @param project
	 * 	IProject
	 * @return
	 * 	SeamRuntime - instance of available SeamRuntime or null
	 */
	public SeamRuntime getRuntimeForProject(IProject project) {
		ISeamProject seamProject = SeamCorePlugin
				.getSeamProject(project, false);
		return (seamProject == null) ? null : seamProject.getRuntime();
	}

	/**
	 * Save preference value and force save changes to disk
	 */
	public void save() {
		SeamCorePlugin.getDefault().getPluginPreferences().setValue(
				SeamProjectPreferences.RUNTIME_LIST,
				converter.getString(runtimes));
		IPreferenceStore store = SeamCorePlugin.getDefault()
				.getPreferenceStore();
		if (store instanceof IPersistentPreferenceStore) {
			try {
				((IPersistentPreferenceStore) store).save();
			} catch (IOException e) {
				SeamCorePlugin.getPluginLog().logError(
						"Seam Runtime List was not saved", e);
			}
		}
	}

	/**
	 * Marks this runtime as default. Marks other runtimes with the same version as not default.
	 * @param runtime
	 */
	public void setDefaultRuntime(SeamRuntime runtime) {
		SeamRuntime[] runtimes = getRuntimes(runtime.getVersion());
		for (int i = 0; i < runtimes.length; i++) {
			runtimes[i].setDefault(false);
		}
		runtime.setDefault(true);
	}

	/**
	 * Return first default SeamRuntime
	 * 
	 * @return
	 * 	SeamRuntime
	 */
	public SeamRuntime getDefaultRuntime() {
		for (SeamRuntime rt : runtimes.values()) {
			if (rt.isDefault()) {
				return rt;
			}
		}
		return null;
	}

	/**
	 * If project has seam facet then this method returns default seam runtime for proper version of facet.
	 * Otherwise return first default runtime.  
	 * @param project
	 * @return
	 */
	public static SeamRuntime getDefaultRuntimeForProject(IProject project) {
		if(project==null) {
			throw new IllegalArgumentException("Project must not be null.");
		}
		try {
			IProjectFacet facet = ProjectFacetsManager.getProjectFacet(ISeamFacetDataModelProperties.SEAM_FACET_ID);
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);
			if(facetedProject!=null) {
				IProjectFacetVersion facetVersion = facetedProject.getInstalledVersion(facet);
				if(facetVersion==null) return null;
				SeamVersion seamVersion = SeamVersion.parseFromString(facetVersion.getVersionString());
				return getInstance().getDefaultRuntime(seamVersion);
			}
		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		} catch (IllegalArgumentException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
		return getInstance().getDefaultRuntime();
	}

	/**
	 * Return default runtime for given SeamVersion
	 * 
	 * @param version
	 * 	given SeamVersion  
	 * @return
	 * 	Default SeamRuntime for given version 
	 */	
	public SeamRuntime getDefaultRuntime(SeamVersion version) {
		for (SeamRuntime rt : runtimes.values()) {
			if (rt.isDefault() && rt.getVersion() == version) {
				return rt;
			}
		}
		return null;
	}

	/**
	 * Return version of configuration
	 * 
	 * @return	
	 * 	String - string representation of version 
	 */
	public String getConfigurationVersion() {
		return SeamFacetPreferenceInitializer.RUNTIME_CONFIG_FORMAT_VERSION;
	}

	/**
	 * Return list of available SeamRuntime names
	 * 
	 * @return
	 * 	List&lt;String&gt;
	 */
	public List<String> getRuntimeNames() {
		SeamRuntime[] rts = getRuntimes(SeamVersion.SEAM_1_2);
		List<String> result = new ArrayList<String>();
		for (SeamRuntime seamRuntime : rts) {
			result.add(seamRuntime.getName());
		}
		return result;
	}

	/**
	 * Return a list of all runtime names
	 * 
	 * @return
	 * 	List of all runtime names
	 */
	public List<String> getAllRuntimeNames() {
		SeamRuntime[] rts = getRuntimes();
		List<String> result = new ArrayList<String>();
		for (SeamRuntime seamRuntime : rts) {
			result.add(seamRuntime.getName());
		}
		return result;
	}

	/**
	 * TBD
	 * 
	 * @param oldName
	 * 	old runtime name
	 * @param newName
	 * 	new runtime name
	 */
	public void changeRuntimeName(String oldName, String newName) {
		SeamRuntime o = findRuntimeByName(oldName);
		if (o == null) {
			return;
		}
		o.setName(newName);
		onRuntimeNameChanged(oldName, newName);
	}

	private void onRuntimeNameChanged(String oldName, String newName) {
		IProject[] ps = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int i = 0; i < ps.length; i++) {
			ISeamProject sp = SeamCorePlugin.getSeamProject(ps[i], false);
			if (sp != null && oldName.equals(sp.getRuntimeName())) {
				sp.setRuntimeName(newName);
			}
		}
	}
}