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
import org.eclipse.jdt.core.IJavaProject;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.extension.feature.ICDIFeature;

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
	 * Maps implementation class to its features (IAdaptable cannot be used without a way to give all features)
	 */
	private Map<Class<? extends ICDIExtension>, Set<Class<?>>> designToFeatures = new HashMap<Class<? extends ICDIExtension>, Set<Class<?>>>();
	/**
	 * Maps fully qualified names of implementations of ICDIExtention to their Class objects.
	 */
	Map<String, Class<? extends ICDIExtension>> designToClass = new HashMap<String, Class<? extends ICDIExtension>>();

	/**
	 * Maps CDI runtime fully qualified names of extension implementations to
	 * objects that check if a Java project contains that extension.
	 */
	Map<String, IExtensionRecognizer> recognizers = new HashMap<String, IExtensionRecognizer>();
	
	private CDIExtensionFactory() {
		load();
	}

	private void load() {
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(POINT_ID);
		IConfigurationElement[] cs = point.getConfigurationElements();
		for (IConfigurationElement c: cs) {
			String runtime = c.getAttribute("runtime");
			String cls = c.getAttribute("class");
			String recognizer = c.getAttribute("recognizer");
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
			
			if(recognizer != null) {
				try {
					Object o = c.createExecutableExtension("recognizer");
					if(!(o instanceof IExtensionRecognizer)) {
						CDICorePlugin.getDefault().logError("CDI extension recognizer " + recognizer + " should implement IExtensionRecognizer.");
					} else {
						recognizers.put(runtime, (IExtensionRecognizer)o);
					}
				} catch (CoreException e) {
					CDICorePlugin.getDefault().logError(e);
					continue;
				}
			}
		}
	}

	public Set<Class<?>> getFeatures(ICDIExtension extension) {
		Set<Class<?>> result = designToFeatures.get(extension.getClass());
		if(result == null) {
			result = new HashSet<Class<?>>();
			getFeatures(extension.getClass(), result);
		}
		return result;
	}

	Map<Class<?>, Boolean> featureCheck = new HashMap<Class<?>, Boolean>();
	
	void getFeatures(Class<?> cls, Set<Class<?>> result) {
		if(cls == ICDIFeature.class) {
			return;
		}
		if(isFeature(cls)) {
			result.add(cls);
		}
		Class<?>[] is = cls.getInterfaces();
		for (Class<?> c: is) {
			getFeatures(c, result);
		}
		if(!cls.isInterface()) {
			Class<?> s = cls.getSuperclass();
			if(s != null) getFeatures(s, result);
		}
	}

	boolean isFeature(Class<?> cls) {
		if(!cls.isInterface()) {
			return false;
		}
		Boolean b = featureCheck.get(cls);
		if(b == null) {
			Class<?>[] is = cls.getInterfaces();
			for (Class<?> c: is) {
				if(c == ICDIFeature.class || isFeature(c)) {
					b = Boolean.TRUE;
				}
			}
			if(b == null) {
				b = Boolean.FALSE;
			}
			featureCheck.put(cls, b);
		}
		return b.booleanValue();
	}

	public Set<String> getExtensionClassesByRuntime(String qualifiedName) {
		return runtimeToDesign.get(qualifiedName);
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

	/**
	 * Returns set of CDI extensions, represented by runtime id,
	 * for which the assigned recognizer determined that 
	 * the given Java project contains that runtime.
	 * This method is not supposed to check if runtime is 
	 * registered in META-INF/services/javax.enterprise.inject.spi.Extension.
	 * 
	 * @param javaProject
	 * @return
	 */
	public Set<String> getRecognizedRuntimes(IJavaProject javaProject) {
		Set<String> result = new HashSet<String>();
		for (Map.Entry<String, IExtensionRecognizer> entry: recognizers.entrySet()) {
			String runtime = entry.getKey();
			IExtensionRecognizer recognizer = entry.getValue();
			if(recognizer.containsExtension(runtime, javaProject)) {
				result.add(runtime);
			}
		}
		return result;
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
