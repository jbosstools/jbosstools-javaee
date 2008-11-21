package org.jboss.tools.seam.core;

import org.eclipse.core.runtime.IPath;

public interface ISeamNamespace {
	public IPath getSourcePath();
	public String getURI();
	public String getPackage();
}
