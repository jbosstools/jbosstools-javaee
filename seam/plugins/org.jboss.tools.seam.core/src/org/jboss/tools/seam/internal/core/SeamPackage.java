package org.jboss.tools.seam.internal.core;

import java.util.HashSet;
import java.util.Set;

import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamPackage;

public class SeamPackage extends SeamObject implements ISeamPackage {
	Set<ISeamComponent> components = new HashSet<ISeamComponent>();
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

}
