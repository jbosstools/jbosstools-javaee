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
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.project.facet.SeamFacetPreferenceInitializer;
import org.jboss.tools.seam.internal.core.validation.SeamRuntimeValidation;

/**
 * @author eskimo
 *
 */
public class SeamRuntimeManager {
	
	private static SeamRuntimeListConverter converter = new SeamRuntimeListConverter();
	
	private Map<String, SeamRuntime> runtimes = new HashMap<String, SeamRuntime>();

	private SeamRuntime defaultRt = null;
	
	private SeamRuntimeValidation validator = new SeamRuntimeValidation();
	
	/**
	 * 
	 */
	static class SeamRuntimeManagerHolder {
		private static final SeamRuntimeManager INSTANCE = new SeamRuntimeManager();
	}
	
	/**
	 * 
	 * @return
	 */
	public static SeamRuntimeManager getInstance() {
		return SeamRuntimeManagerHolder.INSTANCE;
	}
	
	/**
	 * 
	 */
	private SeamRuntimeManager() {
		String configVersion = SeamProjectPreferences.getStringPreference(
				            SeamProjectPreferences.RUNTIME_CONFIG_FORMAT_VERSION);
		String runtimeListString = SeamProjectPreferences.getStringPreference(
	            SeamProjectPreferences.RUNTIME_LIST);
		
		runtimes = converter.getMap(runtimeListString);
	}
	
	/**
	 * 
	 * @return
	 */
	public SeamRuntime[] getRuntimes() {
		Collection<SeamRuntime> c = runtimes.values();
		return c.toArray(new SeamRuntime[runtimes.size()]);
	}
	
	/**
	 * 
	 * @param version
	 * @return
	 */
	public SeamRuntime[] getRuntimes(SeamVersion version) {
		Collection<SeamRuntime> c = runtimes.values();
		List<SeamRuntime> rts = new ArrayList<SeamRuntime>();
		for (SeamRuntime seamRuntime : c) {
			if(seamRuntime.getVersion()==version)rts.add(seamRuntime);
		}
		return rts.toArray(new SeamRuntime[rts.size()]);
	}
	
	/**
	 * 
	 * @param runtime
	 */
	public void addRuntime(SeamRuntime runtime) {
		if(runtimes.size()==0) {
			runtime.setDefault(true);
		} 
		
		if(getDefaultRuntime()!=null && runtime.isDefault()) {
			getDefaultRuntime().setDefault(false);
		}
		runtimes.put(runtime.getName(),runtime);	
		save();
		validateProjects();
	}
	
	/**
	 * 
	 * @param name
	 * @param path
	 * @param version
	 * @param defaultRt
	 */
	public void addRuntime(String name, String path, SeamVersion version, boolean defaultRt) {
		SeamRuntime seamRt = new SeamRuntime();
		seamRt.setVersion(version);
		seamRt.setHomeDir(path);
		seamRt.setName(name);
		seamRt.setDefault(defaultRt);
		addRuntime(seamRt);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public SeamRuntime findRuntimeByName(String name) {
		for (SeamRuntime seamRuntime : runtimes.values()) {
			if(seamRuntime.getName().equals(name))return seamRuntime;
		}
		return null;
	}
	
	/**
	 * 
	 * @param rt
	 */
	public void removeRuntime(SeamRuntime rt) {
		runtimes.remove(rt.getName());
		validateProjects();
	}
	
	/**
	 * 
	 * @param project
	 * @return
	 */
	public SeamRuntime getRuntimeForProject(IProject project) {
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, false);
		return (seamProject == null) ? null : seamProject.getRuntime();
	}
	
	/**
	 * Save preference value and force save changes to disk
	 */
	public void save() {
		SeamCorePlugin.getDefault().getPluginPreferences().setValue(
				SeamProjectPreferences.RUNTIME_LIST, converter.getString(runtimes));
		IPreferenceStore store = SeamCorePlugin.getDefault().getPreferenceStore();
		if(store instanceof IPersistentPreferenceStore) {
			try {
				((IPersistentPreferenceStore)store).save();
			} catch (IOException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public SeamRuntime getDefaultRuntime() {
		for (SeamRuntime rt : runtimes.values()) {
			if(rt.isDefault())return rt;
		}
		return null;
	}
	
	/**
	 * 
	 */
	public String getConfigurationVersion() {
		return SeamFacetPreferenceInitializer.RUNTIME_CONFIG_FORMAT_VERSION;
	}

	/**
	 * @return
	 */
	public List<String> getRuntimeNames() {
		SeamRuntime[] rts = getRuntimes(SeamVersion.SEAM_1_2);
		List<String> result = new ArrayList<String>();
		for(SeamRuntime seamRuntime : rts) {
			result.add(seamRuntime.getName());
		}
		return result;
	}

	private void validateProjects() {
		IProject[] ps = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int i = 0; i < ps.length; i++) {
			ISeamProject sp = SeamCorePlugin.getSeamProject(ps[i], false);
			if(sp == null) continue;
			try {
				validator.validate(sp);
			} catch (CoreException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
		}
	}

}
