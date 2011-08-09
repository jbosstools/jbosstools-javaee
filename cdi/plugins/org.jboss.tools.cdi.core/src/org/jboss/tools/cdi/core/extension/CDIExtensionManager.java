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
import org.jboss.tools.cdi.core.extension.feature.IAmbiguousBeanResolverFeature;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;
import org.jboss.tools.cdi.core.extension.feature.ICDIFeature;
import org.jboss.tools.cdi.core.extension.feature.IProcessAnnotatedMemberFeature;
import org.jboss.tools.cdi.core.extension.feature.IProcessAnnotatedTypeFeature;
import org.jboss.tools.cdi.core.extension.feature.IValidatorFeature;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CDIExtensionManager {
	CDICoreNature n;
	/**
	 * Mapping of jar path to CDI extensions declared in it. 
	 */
	Map<String, Set<String>> runtimes = new HashMap<String, Set<String>>();
	Set<String> allRuntimes = new HashSet<String>();

	/**
	 * Mapping of extension ids (class names) to instances.
	 */
	Map<String, ICDIExtension> instances = new HashMap<String, ICDIExtension>();
	
	/**
	 * Mapping of feature ids to extension instances.
	 */
	Map<Class<?>, Set<ICDIExtension>> featureToExtensions = new HashMap<Class<?>, Set<ICDIExtension>>();

	Map<Class<? extends ICDIFeature>, Set<?>> featureStorage = new HashMap<Class<? extends ICDIFeature>, Set<?>>();
	
	public CDIExtensionManager() {
	}

	public void setProject(CDICoreNature n) {
		this.n = n;
	}

	public boolean isCDIExtensionAvailable(String runtimeClassName) {
		return allRuntimes.contains(runtimeClassName);
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
		allRuntimes.add(runtime);
		CDIExtensionFactory factory = CDIExtensionFactory.getInstance();
		Set<String> clss = factory.getExtensionClassesByRuntime(runtime);
		if(clss != null) {
			for (String cls: clss) {
				ICDIExtension ext = factory.createExtensionInstance(cls);
				if(ext == null) continue;
				instances.put(cls, ext);
				for (Class<?> feature: CDIExtensionFactory.getInstance().getFeatures(ext)) {
					Set<ICDIExtension> es = featureToExtensions.get(feature);
					if(es == null) {
						es = new HashSet<ICDIExtension>();
						featureToExtensions.put(feature, es);
					}
					es.add(ext);
				}
				featureStorage.clear();
			}
		}
	}

	private void deleteRuntime(String runtime) {
		allRuntimes.remove(runtime);
		Set<String> clss = CDIExtensionFactory.getInstance().getExtensionClassesByRuntime(runtime);
		if(clss != null) {
			for (String cls: clss) {
				ICDIExtension ext = instances.remove(cls);
				if(ext != null) {
					Class<?>[] is = featureToExtensions.keySet().toArray(new Class<?>[0]);
					for (Class<?> feature: is) {
						Set<ICDIExtension> es = featureToExtensions.get(feature);
						if(es != null) {
							es.remove(ext);
							if(es.isEmpty()) featureToExtensions.remove(feature);
						}
					}
				}
			}
			if(!clss.isEmpty()) {
				featureStorage.clear();
			}
		}
	}

	static Set<ICDIExtension> EMPTY = new HashSet<ICDIExtension>();

	public Set<ICDIExtension> getExtensions(Class<?> feature) {
		return featureToExtensions.containsKey(feature) ? featureToExtensions.get(feature) : EMPTY;
	}

	public Set<IProcessAnnotatedMemberFeature> getProcessAnnotatedMemberFeatures() {
		return getFeatures(IProcessAnnotatedMemberFeature.class);
	}

	public Set<IProcessAnnotatedTypeFeature> getProcessAnnotatedTypeFeatures() {
		return getFeatures(IProcessAnnotatedTypeFeature.class);
	}

	public Set<IBuildParticipantFeature> getBuildParticipantFeatures() {
		return getFeatures(IBuildParticipantFeature.class);
	}

	public Set<IAmbiguousBeanResolverFeature> getAmbiguousBeanResolverFeatures() {
		return getFeatures(IAmbiguousBeanResolverFeature.class);
	}

	public Set<IValidatorFeature> getValidatorFeatures() {
		return getFeatures(IValidatorFeature.class);
	}

	/**
	 * Returns set of feature implementation objects by feature class.
	 *  
	 * @param cls
	 * @return set of feature implementation objects by feature class
	 */
	public <F extends ICDIFeature> Set<F> getFeatures(Class<F> cls) {
		@SuppressWarnings("unchecked")
		Set<F> result = (Set<F>)featureStorage.get(cls);
		if(result == null) {
			result = new HashSet<F>();
			Set<ICDIExtension> extensions = getExtensions(cls);
			if(!extensions.isEmpty()) {
				for (ICDIExtension ext: extensions) {
					F feature = CDIExtensionFactory.adaptTo(ext, cls);
					if(feature != null) {
						result.add(feature);
					}
				}
			}
			featureStorage.put(cls, result);
		}
		return result;
	}
}