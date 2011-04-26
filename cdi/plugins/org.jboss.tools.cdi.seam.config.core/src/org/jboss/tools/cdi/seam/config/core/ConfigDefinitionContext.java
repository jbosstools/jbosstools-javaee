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
package org.jboss.tools.cdi.seam.config.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.extension.AbstractDefinitionContextExtension;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.DefinitionContext;
import org.jboss.tools.cdi.seam.config.core.definition.SeamBeansDefinition;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class ConfigDefinitionContext extends AbstractDefinitionContextExtension {
	private Map<IPath, SeamBeansDefinition> beanXMLs = new HashMap<IPath, SeamBeansDefinition>();
	private Map<IPath, SeamBeansDefinition> seambeanXMLs = new HashMap<IPath, SeamBeansDefinition>();

	private Map<String, AnnotationDefinition> annotations = new HashMap<String, AnnotationDefinition>();

	public ConfigDefinitionContext getWorkingCopy() {
		return (ConfigDefinitionContext)super.getWorkingCopy();
	}

	protected ConfigDefinitionContext copy(boolean clean) {
		ConfigDefinitionContext copy = new ConfigDefinitionContext();
		copy.root = root;
		if(!clean) {
			copy.beanXMLs.putAll(beanXMLs);
			copy.seambeanXMLs.putAll(seambeanXMLs);
			copy.annotations.putAll(annotations);
			//TODO
		}

		return copy;
	}

	protected void doApplyWorkingCopy() {
		ConfigDefinitionContext copy = (ConfigDefinitionContext)workingCopy;
		beanXMLs = copy.beanXMLs;
		seambeanXMLs = copy.seambeanXMLs;
		
		for (String s: annotations.keySet()) {
			if(!copy.annotations.containsKey(s)) {
				//Remove from root and reload it in root.
				AnnotationDefinition d = annotations.get(s);
				IType type = d.getType();
				root.clean(type.getFullyQualifiedName());
				root.getAnnotationKind(type);
			}
		}
		annotations = copy.annotations;
	}

	public void clean() {
		synchronized (beanXMLs) {
			beanXMLs.clear();
		}
		synchronized (seambeanXMLs) {
			seambeanXMLs.clear();
		}
		synchronized (annotations) {
			annotations.clear();
		}
	}

	public void clean(IPath path) {
		synchronized (beanXMLs) {
			beanXMLs.remove(path);
		}
		synchronized (seambeanXMLs) {
			seambeanXMLs.remove(path);
		}
	}

	public void clean(String typeName) {
		synchronized(annotations) {
			annotations.remove(typeName);
		}
	}

	public void addBeanXML(IPath path, SeamBeansDefinition def) {
		synchronized (beanXMLs) {
			beanXMLs.put(path, def);
		}
		root.addToParents(path);
	}

	public void addSeamBeanXML(IPath path, SeamBeansDefinition def) {
		synchronized (seambeanXMLs) {
			seambeanXMLs.put(path, def);
		}
		root.addToParents(path);
	}

	public Set<SeamBeansDefinition> getSeamBeansDefinitions() {
		Set<SeamBeansDefinition> result = new HashSet<SeamBeansDefinition>();
		synchronized (beanXMLs) {
			result.addAll(beanXMLs.values());
		}
		synchronized (seambeanXMLs) {
			result.addAll(seambeanXMLs.values());
		}
		return result;
	}

	public SeamBeansDefinition getDefinition(IPath path) {
		if(beanXMLs.containsKey(path)) return beanXMLs.get(path);
		if(seambeanXMLs.containsKey(path)) return seambeanXMLs.get(path);
		return null;
	}

	public void addAnnotation(String typeName, AnnotationDefinition def) {
		IPath path = def.getResource().getFullPath();
//		root.clean(typeName);
		annotations.put(typeName, def);
		((DefinitionContext)root).addType(path, typeName, def);
	}

}
