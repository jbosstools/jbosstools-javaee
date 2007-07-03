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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ScopeType;

/**
 * @author glory
 */
public class SeamProject implements ISeamProject {
	IProject project;
	Set<SeamComponent> allComponents = new HashSet<SeamComponent>();
	Set<ISeamContextVariable> allVariables = new HashSet<ISeamContextVariable>();

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
		Set<ISeamComponent> result = new HashSet<ISeamComponent>();
		result.addAll(allComponents);
		return result;
	}

	/**
	 * Package local method called by builder.
	 * @param component
	 * @param source
	 */	
	public void registerComponents(SeamComponentDeclaration[] list, IPath source) {
		pathRemoved(source);
		if(list == null) return;
		//TODO
		for (int i = 0; i < list.length; i++) {
//			list[i].setSourcePath(source);
			
			//TODO !!!
//			allComponents.add(list[i]);
		}
	}

	/**
	 * Package local method called by builder.
	 * @param source
	 */
	public void pathRemoved(IPath source) {
		Iterator<SeamComponent> iterator = allComponents.iterator();
		while(iterator.hasNext()) {
			ISeamComponent c = iterator.next();
			Iterator<ISeamComponentDeclaration> ds = c.getAllDeclarations().iterator();
			while (ds.hasNext()) {
				SeamComponentDeclaration di = (SeamComponentDeclaration)ds.next();
				if(di.source.equals(source)) ds.remove();
			}
			if(c.getAllDeclarations().size() == 0) {
				iterator.remove();
			}
		}		
	}

	public Set<ISeamComponent> getComponentsByName(String name) {
		Set<ISeamComponent> result = new HashSet<ISeamComponent>();
		for(SeamComponent component: allComponents) {
			if(name.equals(component.getName())) {
				result.add(component);
			}
		}		
		return result;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#getComponentsByClass(java.lang.String)
	 */
	public Set<ISeamComponent> getComponentsByClass(String className) {
		Set<ISeamComponent> result = new HashSet<ISeamComponent>();
		for(SeamComponent component: allComponents) {
			if(className.equals(component.getClassName())) {
				result.add(component);
			}
		}		
		return result;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#getComponentsByScope(org.jboss.tools.seam.core.ScopeType)
	 */
	public Set<ISeamComponent> getComponentsByScope(ScopeType type) {
		Set<ISeamComponent> result = new HashSet<ISeamComponent>();
		for(SeamComponent component: allComponents) {
			if(type.equals(component.getScope())) {
				result.add(component);
			}
		}		
		return result;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#addComponent(org.jboss.tools.seam.core.ISeamComponent)
	 */
	public void addComponent(ISeamComponent component) {
		allComponents.add((SeamComponent)component);
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#removeComponent(org.jboss.tools.seam.core.ISeamComponent)
	 */
	public void removeComponent(ISeamComponent component) {
		allComponents.remove(component);
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#getVariables()
	 */
	public Set<ISeamContextVariable> getVariables() {
		return allVariables;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#getVariablesByName(java.lang.String)
	 */
	public Set<ISeamContextVariable> getVariablesByName(String name) {
		Set<ISeamContextVariable> result = new HashSet<ISeamContextVariable>();
		for (ISeamContextVariable v: allVariables) {
			if(name.equals(v.getName())) {
				result.add(v);
			}
		}
		return result;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#getVariablesByScope(org.jboss.tools.seam.core.ScopeType)
	 */
	public Set<ISeamContextVariable> getVariablesByScope(ScopeType scope) {
		Set<ISeamContextVariable> result = new HashSet<ISeamContextVariable>();
		for (ISeamContextVariable v: allVariables) {
			if(scope.equals(v.getScope())) {
				result.add(v);
			}
		}
		return result;
	}

}
