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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.extension.feature.IAmbiguousBeanResolverFeature;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;
import org.jboss.tools.cdi.core.extension.feature.IProcessAnnotatedMemberFeature;
import org.jboss.tools.cdi.core.extension.feature.IProcessAnnotatedTypeFeature;

/**
 * Loads Eclipse extension point 'org.jboss.tools.cdi.core.cdiextensions'
 * Element:
 * 'cdiextension'
 * Attributes:
 * 'runtime' - 	Qualified name of CDI runtime extension implementation class.
 * 'class' - 	Qualified name of JBoss Tools CDI model extension implementation class.
 * Implements:
 * IAdaptable - It is not planned to add regular methods to ICDIExtension, 
 * 				all features that are to be provided by implementations, will be available 
 * 				through IAdaptable. In that way, adding to core support of a new feature 
 * 				will not require to implement new methods in existing extensions.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CDIExtensionFactory {
	static CDIExtensionFactory factory = null;
	public static String POINT_ID = "org.jboss.tools.cdi.core.cdiextensions";

	public static Class<?>[] FEATURES = {
		IBuildParticipantFeature.class,
		IProcessAnnotatedMemberFeature.class,
		IProcessAnnotatedTypeFeature.class,
		IAmbiguousBeanResolverFeature.class
	};
	
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
	private Map<Class<?>, Set<String>> featureToDesign = new HashMap<Class<?>, Set<String>>();

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
				Object o = c.createExecutableExtension("class");
				if(!(o instanceof ICDIExtension)) {
					CDICorePlugin.getDefault().logError("CDI extension " + cls + " should implement ICDIExtension.");
				} else {
					extension = (ICDIExtension)o;
				}
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
			
			for (Class<?> f: FEATURES) {
				Object adapter = adaptTo(extension, f);
				if(adapter != null) {
					classes = featureToDesign.get(f);
					if(classes == null) {
						classes = new HashSet<String>();
						featureToDesign.put(f, classes);
					}
					classes.add(cls);
				}
			}
		}
	}

	public Set<String> getExtensionClassesByRuntime(String qualifiedName) {
		return runtimeToDesign.get(qualifiedName);
	}

	public Set<Class<?>> getFeatures() {
		return featureToDesign.keySet();
	}
	public Set<String> getExtensionClassesByFeature(Class<?> featureName) {
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

	static <F> F adaptTo(ICDIExtension extension, Class<F> feature) {
		if(extension == null || feature == null) return null;
		Class<?> cls = extension.getClass();
		if(feature.isAssignableFrom(cls)) {
			return (F)extension;
		}
		if(extension instanceof IAdaptable) {
			return (F)((IAdaptable)extension).getAdapter(feature);
		}
		return null;
	}

}
