package org.jboss.tools.seam.internal.core;

import org.eclipse.core.runtime.IPath;
import org.jboss.tools.seam.core.ISeamFactory;
import org.jboss.tools.seam.core.ScopeType;

public class SeamFactory implements ISeamFactory {
	/**
	 * Path of resource where this factory is declared.
	 */
	protected IPath source;
	/**
	 * Object that allows to identify this declaration.
	 */
	protected Object id;

	String name = null;
	String scope = null;
	ScopeType scopeType = ScopeType.UNSPECIFIED;

	public Object getId() {
		return id;
	}
	
	public void setId(Object id) {
		this.id = id;
	}

	public void setSourcePath(IPath path) {
		source = path;
	}
	
	public IPath getSourcePath() {
		return source;
	}

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
		scope = scopeType.toString();
	}

	public void setScopeAsString(String scope) {
		this.scope = scope;
		try {
			this.scopeType = scope == null || scope.length() == 0 ? ScopeType.UNSPECIFIED : ScopeType.valueOf(scope.toUpperCase());
		} catch (Exception e) {
			//ignore
		}
	}

}
