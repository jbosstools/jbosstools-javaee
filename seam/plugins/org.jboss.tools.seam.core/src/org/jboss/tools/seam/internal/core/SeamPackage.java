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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamPackage;

public class SeamPackage extends SeamObject implements ISeamPackage {
	Set<ISeamComponent> components = new HashSet<ISeamComponent>();
	Map<String, ISeamPackage> packages = new HashMap<String, ISeamPackage>();

	public String name;
	
	public SeamPackage() {}

	public SeamPackage(String name) {
		setName(name);
	}
	
	public Set<ISeamComponent> getComponents() {
		return components;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
		setId(name);
	}

	public Map<String,ISeamPackage> getPackages() {
		return packages;
	}

	public String getQualifiedName() {
		if(parent instanceof ISeamPackage) {
			return ((ISeamPackage)parent).getQualifiedName() + "." + getName(); //$NON-NLS-1$
		}
		return getName();
	}

}
