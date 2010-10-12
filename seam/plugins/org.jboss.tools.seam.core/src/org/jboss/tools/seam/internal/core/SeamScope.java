/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.jboss.tools.seam.internal.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.tools.common.model.project.ext.event.Change;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamPackage;
import org.jboss.tools.seam.core.ISeamScope;
import org.jboss.tools.seam.core.ScopeType;

public class SeamScope extends SeamObject implements ISeamScope {
	//Contains all components for that scope.
	private Map<String,SeamComponent> componentMap = new HashMap<String, SeamComponent>();
	private List<ISeamComponent> components = new ArrayList<ISeamComponent>();
	private ScopeType scopeType = null;

	//Contains first level packages for that scope
	Map<String, ISeamPackage> packages = new HashMap<String, ISeamPackage>();
	
	SeamScope(SeamProject project, ScopeType scopeType) {
		setParent(project);
		this.scopeType = scopeType;
	}

	public List<ISeamComponent> getComponents() {
		return components;
	}
	
	public Collection<ISeamPackage> getPackages() {
		synchronized(packages) {
			return packages.values();
		}
	}
	
	public Collection<ISeamPackage> getAllPackages() {
		List<ISeamPackage> list = new ArrayList<ISeamPackage>();
		SeamPackageUtil.collectAllPackages(packages, list);
		return list;
	}
	
	public ISeamPackage getPackage(ISeamComponent c) {
		String pkg = SeamPackageUtil.getPackageName(c);
		return SeamPackageUtil.findPackage(this, packages, pkg);
	}

	public void addComponent(SeamComponent component) {
		components.add(component);
		componentMap.put(component.getName(), component);
	}
	
	public void removeComponent(ISeamComponent component) {
		components.remove(component);
		componentMap.remove(component.getName());
	}

	public ScopeType getType() {
		return scopeType;
	}

	void revalidatePackages() {
		List<Change> changes = SeamPackageUtil.revalidatePackages(this, componentMap, getComponents(), packages);
		((SeamProject)getSeamProject()).fireChanges(changes, false);
	}
	
	public void validatePackage(SeamComponent c) {
		SeamPackage p = (SeamPackage)SeamPackageUtil.findOrCreatePackage(this, packages, SeamPackageUtil.getPackageName(c));
		c.setScopePackage(p);
		p.getComponents().add(c);
	}
	
	public void removePackage(ISeamPackage p) {
		synchronized(packages) {
			packages.remove(p.getName());
		}
	}
}
