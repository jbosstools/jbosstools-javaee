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
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.cdi.core.CDICorePlugin;

/**
 * Loads Eclipse extension point 'org.jboss.tools.cdi.core.cdiextensions'
 * @author Viacheslav Kabanovich
 *
 */
public class CDIExtensionFactory {
	static CDIExtensionFactory factory = null;
	public static String POINT_ID = "org.jboss.tools.cdi.core.cdiextensions";
	
	public static CDIExtensionFactory getInstance() {
		if(factory == null) {
			factory = new CDIExtensionFactory();
		}
		return factory;
	}

	/**
	 * Maps CDI runtime fully qualified names of extension implementations to JBoss Tools CDI Model fully 
	 * qualified names of implementations of ICDIExtention.
	 */
	private Map<String, Set<String>> runtimeToDesign = new HashMap<String, Set<String>>();

	/**
	 * Maps features to fully qualified names of implementations of ICDIExtention.
	 */
	private Map<String, Set<String>> featureToDesign = new HashMap<String, Set<String>>();

	/**
	 * Maps fully qualified names of implementations of ICDIExtention to their Class objects.
	 */
	Map<String, Class<? extends ICDIExtension>> designToClass = new HashMap<String, Class<? extends ICDIExtension>>();
	
	private CDIExtensionFactory() {
		load();
	}

	private void load() {
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(POINT_ID);
		IConfigurationElement[] cs = point.getConfigurationElements();
		for (IConfigurationElement c: cs) {
			String runtime = c.getAttribute("runtime");
			String cls = c.getAttribute("class");
			ICDIExtension extension = null;
			try {
				extension = (ICDIExtension)c.createExecutableExtension("class");
			} catch (CoreException e) {
				CDICorePlugin.getDefault().logError(e);
				continue;
			}
			if(extension == null) continue;

			designToClass.put(cls, extension.getClass());

			Set<String> classes = runtimeToDesign.get(runtime);
			if(classes == null) {
				classes = new HashSet<String>();
				runtimeToDesign.put(runtime, classes);
			}
			classes.add(cls);

			String features = c.getAttribute("features");
			StringTokenizer st = new StringTokenizer(features, ",");
			while(st.hasMoreTokens()) {
				String feature = st.nextToken();
				classes = featureToDesign.get(feature);
				if(classes == null) {
					classes = new HashSet<String>();
					featureToDesign.put(feature, classes);
				}
				classes.add(cls);
			}
		}
	}

	public Set<String> getExtensionClassesByRuntime(String qualifiedName) {
		return runtimeToDesign.get(qualifiedName);
	}

	public Set<String> getFeatures() {
		return featureToDesign.keySet();
	}
	public Set<String> getExtensionClassesByfeature(String featureName) {
		return featureToDesign.get(featureName);
	}

	public ICDIExtension createExtensionInstance(String qualifiedName) {
		Class<? extends ICDIExtension> cls = designToClass.get(qualifiedName);
		if(cls != null) {
			try {
				return cls.newInstance();
			} catch (InstantiationException e1) {
				CDICorePlugin.getDefault().logError(e1);
			} catch (IllegalAccessException e2) {
				CDICorePlugin.getDefault().logError(e2);
			}
		}
		
		return null;
	}

}
