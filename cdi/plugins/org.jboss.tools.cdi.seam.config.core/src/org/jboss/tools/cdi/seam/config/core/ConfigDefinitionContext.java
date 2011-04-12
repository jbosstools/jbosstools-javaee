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
import org.jboss.tools.cdi.core.extension.AbstractDefinitionContextExtension;
import org.jboss.tools.cdi.seam.config.core.definition.SeamBeansDefinition;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class ConfigDefinitionContext extends AbstractDefinitionContextExtension {
	private Map<IPath, SeamBeansDefinition> beanXMLs = new HashMap<IPath, SeamBeansDefinition>();
	private Map<IPath, SeamBeansDefinition> seambeanXMLs = new HashMap<IPath, SeamBeansDefinition>();

	public ConfigDefinitionContext getWorkingCopy() {
		return (ConfigDefinitionContext)super.getWorkingCopy();
	}

	protected ConfigDefinitionContext copy(boolean clean) {
		ConfigDefinitionContext copy = new ConfigDefinitionContext();
		copy.root = root;
		if(!clean) {
			copy.beanXMLs.putAll(beanXMLs);
			copy.seambeanXMLs.putAll(seambeanXMLs);
			//TODO
		}

		return copy;
	}

	protected void doApplyWorkingCopy() {
		beanXMLs = ((ConfigDefinitionContext)workingCopy).beanXMLs;
		seambeanXMLs = ((ConfigDefinitionContext)workingCopy).seambeanXMLs;
	}

	public void clean() {
		synchronized (beanXMLs) {
			beanXMLs.clear();
		}
		synchronized (seambeanXMLs) {
			seambeanXMLs.clear();
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

}
