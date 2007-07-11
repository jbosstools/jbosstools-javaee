package org.jboss.tools.seam.core;

import java.util.List;

public interface ISeamScope extends ISeamElement {
	
	public ScopeType getType();
	
	public List<ISeamComponent> getComponents();

}
