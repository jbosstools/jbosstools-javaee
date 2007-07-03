package org.jboss.tools.seam.internal.core;

import org.jboss.tools.seam.core.ISeamFactory;
import org.jboss.tools.seam.core.ScopeType;

public class SeamFactory implements ISeamFactory {
	String name = null;	
	ScopeType scopeType = ScopeType.UNSPECIFIED;

	public String getName() {
		return name;
	}

	public ScopeType getScope() {
		return scopeType;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setScope(ScopeType type) {
		this.scopeType = type;
	}

}
