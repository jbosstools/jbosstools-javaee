/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core.extension;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.tools.cdi.core.CDICoreNature;

public class CDIExtensionManager {
	CDICoreNature n;
	/**
	 * Mapping of jar path to CDI extensions declared in it. 
	 */
	Map<String, Set<String>> runtimes = new HashMap<String, Set<String>>();

	/**
	 * Mapping of extension ids (class names) to instances.
	 */
	Map<String, ICDIExtension> instances = new HashMap<String, ICDIExtension>();
	
	/**
	 * Mapping of feature ids to extension instances.
	 */
	Map<String, Set<ICDIExtension>> featureToExtensions = new HashMap<String, Set<ICDIExtension>>();
	
	public CDIExtensionManager(CDICoreNature n) {
		this.n = n;
	}

	public void pathRemoved(String path) {
		Set<String> rs = runtimes.remove(path);
		if(rs == null) return;
		for (String runtime: rs) {
			deleteRuntime(runtime);
		}
	}

	public void setRuntimes(String path, Set<String> newRuntimes) {
		Set<String> oldRuntimes = runtimes.get(path);
		if(oldRuntimes == null) {
			if(newRuntimes.isEmpty()) return;
			oldRuntimes = new HashSet<String>();
		}
		for (String runtime: oldRuntimes) {
			if(!newRuntimes.contains(runtime)) {
				deleteRuntime(runtime);
			}
		}
		for (String runtime: newRuntimes) {
			if(!oldRuntimes.contains(runtime)) {
				addRuntime(runtime);
			}
		}
		if(newRuntimes.isEmpty()) runtimes.remove(path); else runtimes.put(path, newRuntimes);
	}

	private void addRuntime(String runtime) {
		CDIExtensionFactory factory = CDIExtensionFactory.getInstance();
		Set<String> clss = factory.getExtensionClassesByRuntime(runtime);
		if(clss != null) for (String cls: clss) {
			ICDIExtension ext = factory.createExtensionInstance(cls);
			if(ext == null) continue;
			//TODO initialize ext object.
			instances.put(cls, ext);
			for (String feature: CDIExtensionFactory.getInstance().getFeatures()) {
				if(factory.getExtensionClassesByfeature(feature).contains(cls)) {
					Set<ICDIExtension> es = featureToExtensions.get(feature);
					if(es == null) {
						es = new HashSet<ICDIExtension>();
						featureToExtensions.put(feature, es);
					}
					es.add(ext);
				}
			}
		}
	}

	private void deleteRuntime(String runtime) {
		Set<String> clss = CDIExtensionFactory.getInstance().getExtensionClassesByRuntime(runtime);
		for (String cls: clss) {
			ICDIExtension ext = instances.remove(cls);
			if(ext != null) {
				String[] is = featureToExtensions.keySet().toArray(new String[0]);
				for (String feature: is) {
					Set<ICDIExtension> es = featureToExtensions.get(feature);
					if(es != null) {
						es.remove(ext);
						if(es.isEmpty()) featureToExtensions.remove(feature);
					}
				}
			}
		}
	}

	static Set<ICDIExtension> EMPTY = new HashSet<ICDIExtension>();

	public Set<ICDIExtension> getExtensions(String feature) {
		return featureToExtensions.containsKey(feature) ? featureToExtensions.get(feature) : EMPTY;
	}

}
