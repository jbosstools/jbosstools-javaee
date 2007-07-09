package org.jboss.tools.seam.internal.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamTextSourceReference;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.event.Change;
import org.jboss.tools.seam.internal.core.scanner.java.ValueInfo;

public class AbstractContextVariable implements ISeamContextVariable, ISeamTextSourceReference {
	/**
	 * Path of resource where this factory is declared.
	 */
	protected IPath source;

	protected IResource resource = null;

	/**
	 * Object that allows to identify this declaration.
	 */
	protected Object id;

	protected String name;
	protected ScopeType scopeType;
	protected String scope;

	protected Map<String,ValueInfo> attributes = new HashMap<String, ValueInfo>();
	
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
	
	public void setName(String name) {
		this.name = name;
	}

	public ScopeType getScope() {
		return scopeType;
	}

	public void setScope(ScopeType type) {
		scopeType = type;
		scope = scopeType == null ? null : scopeType.toString();
	}

	public void setScopeAsString(String scope) {
		try {
			if(scope != null && scope.indexOf('.') > 0) {
				scope = scope.substring(scope.lastIndexOf('.'));
			}
			this.scopeType = scope == null || scope.length() == 0 ? ScopeType.UNSPECIFIED : ScopeType.valueOf(scope.toUpperCase());
		} catch (Exception e) {
			//ignore
		}
	}

	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	public IResource getResource() {
		if(resource != null) return resource;
		if(source != null) {
			resource = ResourcesPlugin.getWorkspace().getRoot().getFile(source);
		}
		return resource;
	}

	public int getStartPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Merges loaded data into currently used declaration.
	 * If changes were done returns a list of changes. 
	 * @param f
	 * @return list of changes
	 */
	public List<Change> merge(AbstractContextVariable f) {
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

	public void setName(ValueInfo value) {
		attributes.put(ISeamXmlComponentDeclaration.NAME, value);
		name = value == null ? null : value.getValue();
	}

	public void setScope(ValueInfo value) {
		attributes.put(ISeamXmlComponentDeclaration.SCOPE, value);
		setScopeAsString(value == null ? null : value.getValue());
	}

}
