package org.jboss.tools.seam.internal.core;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamScope;
import org.jboss.tools.seam.core.ScopeType;

public class SeamScope extends SeamObject implements ISeamScope {
	private List<ISeamComponent> components = new ArrayList<ISeamComponent>();
	private ScopeType scopeType = null;
	
	SeamScope(SeamProject project, ScopeType scopeType) {
		setParent(project);
		this.scopeType = scopeType;
	}

	public List<ISeamComponent> getComponents() {
		return components;
	}
	
	public void addComponent(ISeamComponent component) {
		components.add(component);		
	}
	
	public void removeComponent(ISeamComponent component) {
		components.remove(component);
	}

	public ScopeType getType() {
		return scopeType;
	}

}
