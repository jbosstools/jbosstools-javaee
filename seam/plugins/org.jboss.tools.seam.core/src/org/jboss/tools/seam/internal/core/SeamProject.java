/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.internal.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamVariable;
import org.jboss.tools.seam.core.ScopeType;

/**
 * @author glory
 */
public class SeamProject implements ISeamProject {
	IProject project;
	Map<String,SeamComponent> components = new HashMap<String, SeamComponent>();

	public SeamProject() {}

	public void configure() throws CoreException {
	}

	public void deconfigure() throws CoreException {
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
		load();
	}

	/**
	 * Loads results of last build, which are considered 
	 * actual until next build.
	 */	
	protected void load() {
	}

	/**
	 * Stores results of last build, so that on exit/enter Eclipse
	 * load them without rebuilding project
	 */
	protected void store() {
	}

//	public ISeamComponent getComponentByName(String name) {
//		return components.get(name);
//	}

	public Set<ISeamComponent> getComponents() {
		//TODO store cash
		Set<ISeamComponent> set = new HashSet<ISeamComponent>();
		set.addAll(components.values());
		return set; 
	}

	/**
	 * Package local method called by builder.
	 * @param component
	 * @param source
	 */	
	public void registerComponents(SeamComponent[] list, IPath source) {
		pathRemoved(source);
		if(list == null) return;
		for (int i = 0; i < list.length; i++) {
			list[i].setSource(source);
			components.put(list[i].getName(), list[i]);
		}
	}

	/**
	 * Package local method called by builder.
	 * @param source
	 */
	public void pathRemoved(IPath source) {
		Iterator<SeamComponent> iterator = components.values().iterator();
		while(iterator.hasNext()) {
			SeamComponent c = iterator.next();
			if(c.source != null && source.isPrefixOf(c.source)) {
				iterator.remove();
			}
		}		
	}

	public ISeamComponent getComponent(String modelObjectId) {
		return components.get(modelObjectId);
	}

	public Set<ISeamComponent> getComponentsByName(String name) {
		Set<ISeamComponent> result = new HashSet<ISeamComponent>();
		Iterator<SeamComponent> iterator = components.values().iterator();
		while(iterator.hasNext()) {
			SeamComponent component = iterator.next();
			if(name.equals(component.getName())) {
				result.add(component);
			}
		}		
		return result;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamProject#getComponentsByClass(java.lang.String)
	 */
	public Set<ISeamComponent> getComponentsByClass(String className) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamProject#getComponentsByScope(org.jboss.tools.seam.core.ScopeType)
	 */
	public Set<ISeamComponent> getComponentsByScope(ScopeType type) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamProject#addComponent(org.jboss.tools.seam.core.ISeamComponent)
	 */
	public void addComponent(ISeamComponent component) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamProject#removeComponent(org.jboss.tools.seam.core.ISeamComponent)
	 */
	public void removeComponent(ISeamComponent component) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamProject#getVariables()
	 */
	public Set<ISeamVariable> getVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamProject#getVariablesByName(java.lang.String)
	 */
	public Set<ISeamVariable> getVariablesByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamProject#getVariablesByScope(org.jboss.tools.seam.core.ScopeType)
	 */
	public Set<ISeamVariable> getVariablesByScope(ScopeType scope) {
		// TODO Auto-generated method stub
		return null;
	}
}