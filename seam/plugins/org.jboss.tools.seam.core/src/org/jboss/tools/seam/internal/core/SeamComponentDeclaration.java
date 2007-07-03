package org.jboss.tools.seam.internal.core;

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


}
