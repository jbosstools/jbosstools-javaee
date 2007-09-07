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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.SeamPreferenceInitializer;
import org.jboss.tools.seam.internal.core.project.facet.SeamFacetPreferenceInitializer;

/**
 * @author eskimo
 *
 */
public class SeamRuntimeManager {
	
	private static SeamRuntimeListConverter1 converter = new SeamRuntimeListConverter1();
	
	private Map<String, SeamRuntime> runtimes = new HashMap<String, SeamRuntime>();

	private SeamRuntime defaultRt = null;
	
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
		String configVersion = SeamFacetPreference.getStringPreference(
				            SeamFacetPreference.RUNTIME_CONFIG_FORMAT_VERSION);
		String runtimeListString = SeamFacetPreference.getStringPreference(
	            SeamFacetPreference.RUNTIME_LIST);
		
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
		runtimes.put(runtime.getName(),runtime);
		if(getDefaultRuntime()!=null && runtime.isDefault()) {
			getDefaultRuntime().setDefault(false);
		}
	
		save();
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
	 * 
	 */
	public void save() {
		SeamCorePlugin.getDefault().getPluginPreferences().setValue(
				SeamFacetPreference.RUNTIME_LIST, converter.getString(runtimes));
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
}
