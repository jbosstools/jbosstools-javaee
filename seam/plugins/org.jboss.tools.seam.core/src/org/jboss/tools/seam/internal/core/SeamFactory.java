package org.jboss.tools.seam.internal.core;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.jboss.tools.seam.core.ISeamFactory;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.event.Change;

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

	/**
	 * Merges loaded data into currently used declaration.
	 * If changes were done returns a list of changes. 
	 * @param f
	 * @return list of changes
	 */
	public List<Change> merge(SeamFactory f) {
		List<Change> changes = null;

		source = f.source;
		id = f.id;
		
		if(!stringsEqual(name, f.name)) {
			changes = Change.addChange(changes, new Change(this, ISeamXmlComponentDeclaration.NAME, name, f.name));
			name = f.name;
		}
		if(!stringsEqual(scope, f.scope)) {
			changes = Change.addChange(changes, new Change(this, ISeamXmlComponentDeclaration.SCOPE, scope, f.scope));
			scope = f.scope;
			scopeType = f.scopeType;
		}
	
		return changes;
	}

	boolean stringsEqual(String s1, String s2) {
		return s1 == null ? s2 == null : s1.equals(s2);
	}

}
