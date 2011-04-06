package org.jboss.tools.cdi.seam.config.core;

import org.eclipse.core.runtime.IPath;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.core.extension.IDefinitionContextExtension;

public class ConfigDefinitionContext implements IDefinitionContextExtension {
	IRootDefinitionContext root;

	ConfigDefinitionContext workingCopy;
	ConfigDefinitionContext original;

	private ConfigDefinitionContext copy(boolean clean) {
		ConfigDefinitionContext copy = new ConfigDefinitionContext();
		copy.root = root;
		//TODO
		
		return copy;
	}

	public void newWorkingCopy(boolean forFullBuild) {
		if(original != null) return;
		workingCopy = copy(forFullBuild);
		workingCopy.original = this;
	}

	public void applyWorkingCopy() {
		if(original != null) {
			original.applyWorkingCopy();
			return;
		}
		if(workingCopy == null) {
			return;
		}
		
		// TODO
		
	}

	public void clean() {
		// TODO		
	}

	public void clean(IPath path) {
		// TODO		
	}

	public void setRootContext(IRootDefinitionContext context) {
		root = context;
	}

	public IRootDefinitionContext getRootContext() {
		return root;
	}

	public IDefinitionContextExtension getWorkingCopy() {
		if(original != null) {
			return this;
		}
		if(workingCopy != null) {
			return workingCopy;
		}
		workingCopy = copy(false);
		workingCopy.original = this;
		return workingCopy;
	}

}
