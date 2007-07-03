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
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamFactory;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.ScopeType;

/**
 * @author glory
 */
public class SeamProject implements ISeamProject {
	IProject project;
	Map<String, SeamComponent> allComponents = new HashMap<String, SeamComponent>();
	protected Set<ISeamFactory> allFactories = new HashSet<ISeamFactory>();
	Set<ISeamContextVariable> allVariables = new HashSet<ISeamContextVariable>();
	
	Map<String, SeamJavaComponentDeclaration> javaDeclarations = new HashMap<String, SeamJavaComponentDeclaration>();

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

	public ISeamComponent getComponentByName(String name) {
		return allComponents.get(name);
	}

	public Set<ISeamComponent> getComponents() {
		Set<ISeamComponent> result = new HashSet<ISeamComponent>();
		result.addAll(allComponents.values());
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

		for (int i = 0; i < list.length; i++) {
			list[i].setSourcePath(source);
			String name = list[i].getName();
			SeamComponent c = getComponent(name);
			if(c == null) {
				c = newComponent(name);
				allComponents.put(name, c);
			}
			c.addDeclaration(list[i]);
			if(list[i] instanceof ISeamJavaComponentDeclaration) {
				javaDeclarations.put(c.getClassName(), (SeamJavaComponentDeclaration)list[i]);
				Set<ISeamComponent> cs = getComponentsByClass(c.getClassName());
				for (ISeamComponent ci: cs) {
					if(ci == c) continue;
					SeamComponent cii = (SeamComponent)ci;
					cii.addDeclaration(list[i]);
				}
			} else if(list[i] instanceof ISeamXmlComponentDeclaration) {
				ISeamXmlComponentDeclaration xml = (ISeamXmlComponentDeclaration)list[i];
				String className = xml.getClassName();
				SeamJavaComponentDeclaration j = javaDeclarations.get(className);
				if(j != null) c.addDeclaration(j);
			}			
		}
	}

	/**
	 * Package local method called by builder.
	 * @param component
	 * @param source
	 */	
	public void registerFactories(ISeamFactory[] list, IPath source) {
		
	}

	/**
	 * Package local method called by builder.
	 * @param source
	 */
	public void pathRemoved(IPath source) {
		Iterator<SeamComponent> iterator = allComponents.values().iterator();
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

	//deprecated
	public Set<ISeamComponent> getComponentsByName(String name) {
		Set<ISeamComponent> result = new HashSet<ISeamComponent>();
		ISeamComponent c = getComponentByName(name);
		if(c != null) result.add(c);
		return result;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#getComponentsByClass(java.lang.String)
	 */
	public Set<ISeamComponent> getComponentsByClass(String className) {
		Set<ISeamComponent> result = new HashSet<ISeamComponent>();
		for(SeamComponent component: allComponents.values()) {
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
		for(SeamComponent component: allComponents.values()) {
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
		allComponents.put(component.getName(), (SeamComponent)component);
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

	public void addFactory(ISeamFactory factory) {
		allFactories.add(factory);		
	}

	public Set<ISeamFactory> getFactories() {
		return allFactories;
	}

	public Set<ISeamFactory> getFactories(String name, ScopeType scope) {
		Set<ISeamFactory> result = new HashSet<ISeamFactory>();
		for (ISeamFactory f: allFactories) {
			if(name.equals(f.getName()) && scope.equals(f.getScope())) result.add(f);
		}
		return result;
	}

	public Set<ISeamFactory> getFactoriesByName(String name) {
		Set<ISeamFactory> result = new HashSet<ISeamFactory>();
		for (ISeamFactory f: allFactories) {
			if(name.equals(f.getName())) result.add(f);
		}
		return result;
	}

	public Set<ISeamFactory> getFactoriesByScope(ScopeType scope) {
		Set<ISeamFactory> result = new HashSet<ISeamFactory>();
		for (ISeamFactory f: allFactories) {
			if(scope.equals(f.getScope())) result.add(f);
		}
		return result;
	}

	public void removeFactory(ISeamFactory factory) {
		allFactories.remove(factory);
	}
	
	SeamComponent getComponent(String name) {
		return allComponents.get(name);
	}
	
	SeamComponent newComponent(String name) {
		SeamComponent c = new SeamComponent();
		c.setName(name);
		return c;
	}

}
