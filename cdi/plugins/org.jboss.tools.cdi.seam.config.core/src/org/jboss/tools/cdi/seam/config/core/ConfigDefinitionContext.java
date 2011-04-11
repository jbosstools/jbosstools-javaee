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
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.core.extension.IDefinitionContextExtension;
import org.jboss.tools.cdi.seam.config.core.definition.SeamBeansDefinition;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class ConfigDefinitionContext implements IDefinitionContextExtension {
	IRootDefinitionContext root;

	private Map<IPath, SeamBeansDefinition> beanXMLs = new HashMap<IPath, SeamBeansDefinition>();
	private Map<IPath, SeamBeansDefinition> seambeanXMLs = new HashMap<IPath, SeamBeansDefinition>();

	ConfigDefinitionContext workingCopy;
	ConfigDefinitionContext original;

	private ConfigDefinitionContext copy(boolean clean) {
		ConfigDefinitionContext copy = new ConfigDefinitionContext();
		copy.root = root;
		if(!clean) {
			copy.beanXMLs.putAll(beanXMLs);
			copy.seambeanXMLs.putAll(seambeanXMLs);
			//TODO
		}

		return copy;
	}

	public void newWorkingCopy(boolean forFullBuild) {
		if(original != null) return;
		workingCopy = copy(forFullBuild);
		workingCopy.original = this;
	}

	public void applyWorkingCopy() {
		if(original != null) {
			original.applyWorkingCopy();
			return;
		}
		if(workingCopy == null) {
			return;
		}
		
		beanXMLs = workingCopy.beanXMLs;
		seambeanXMLs = workingCopy.seambeanXMLs;
		
		workingCopy = null;
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

	public void setRootContext(IRootDefinitionContext context) {
		root = context;
	}

	public IRootDefinitionContext getRootContext() {
		return root;
	}

	public ConfigDefinitionContext getWorkingCopy() {
		if(original != null) {
			return this;
		}
		if(workingCopy != null) {
			return workingCopy;
		}
		workingCopy = copy(false);
		workingCopy.original = this;
		return workingCopy;
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
