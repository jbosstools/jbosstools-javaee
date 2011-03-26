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
	Set<String> runtimes = new HashSet<String>();

	Map<String, ICDIExtension> instances = new HashMap<String, ICDIExtension>();
	Map<String, Set<ICDIExtension>> featureToExtensions = new HashMap<String, Set<ICDIExtension>>();
	
	public CDIExtensionManager(CDICoreNature n) {
		this.n = n;
	}

	public void setRuntimes(Set<String> runtimes) {
		Set<String> addedRuntimes = new HashSet<String>();
		Set<String> deletedRuntimes = new HashSet<String>();
		for (String runtime: this.runtimes) {
			if(!runtimes.contains(runtime)) {
				deletedRuntimes.add(runtime);
			}
		}
		for (String runtime: runtimes) {
			if(!this.runtimes.contains(runtime)) {
				addedRuntimes.add(runtime);
			}
		}
	
		for (String runtime: deletedRuntimes) {
			runtimes.remove(runtime);
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
	
		for (String runtime: addedRuntimes) {
			runtimes.add(runtime);
			Set<String> clss = CDIExtensionFactory.getInstance().getExtensionClassesByRuntime(runtime);
			for (String cls: clss) {
				ICDIExtension ext = CDIExtensionFactory.getInstance().createExtensionInstance(cls);
				if(ext == null) continue;
				//TODO initialize ext object.
				instances.put(cls, ext);
				Map<String, Set<String>> featureToDesign = CDIExtensionFactory.getInstance().featureToDesign;
				for (String feature: featureToDesign.keySet()) {
					if(featureToDesign.get(feature).contains(cls)) {
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
		
	}

	static Set<ICDIExtension> EMPTY = new HashSet<ICDIExtension>();

	public Set<ICDIExtension> getExtensions(String feature) {
		return featureToExtensions.containsKey(feature) ? featureToExtensions.get(feature) : EMPTY;
	}

}
