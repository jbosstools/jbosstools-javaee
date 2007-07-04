package org.jboss.tools.seam.internal.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;

public abstract class SeamComponentDeclaration implements ISeamComponentDeclaration {
	/**
	 * Path of resource where this component is declared.
	 */
	protected IPath source;

	/**
	 * Object that allows to identify this declaration.
	 */
	protected Object id;

	/**
	 * Seam component name.
	 */
	protected String name;
	
	public Object getId() {
		return id;
	}
	
	public void setId(Object id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setSourcePath(IPath path) {
		source = path;
	}
	
	public IPath getSourcePath() {
		return source;
	}

	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	public IResource getResource() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getStartPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

}
