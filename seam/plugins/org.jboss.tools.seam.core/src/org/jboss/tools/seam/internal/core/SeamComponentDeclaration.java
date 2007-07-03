package org.jboss.tools.seam.internal.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;

public abstract class SeamComponentDeclaration implements ISeamComponentDeclaration {
	
	protected IPath source;
	
	protected String name;

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setSourcePath(IPath path) {
		source = path;
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
