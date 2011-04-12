package org.jboss.tools.cdi.core.extension;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;

public abstract class AbstractDefinitionContextExtension implements IDefinitionContextExtension {
	protected IRootDefinitionContext root;
	
	protected AbstractDefinitionContextExtension original;
	protected AbstractDefinitionContextExtension workingCopy;
	
	protected abstract AbstractDefinitionContextExtension copy(boolean clean);

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
		
		doApplyWorkingCopy();
		
		workingCopy = null;
	}

	protected void doApplyWorkingCopy() {}

	public void clean() {
	}

	public void clean(IPath path) {
	}

	public void clean(String typeName) {
	}

	public void setRootContext(IRootDefinitionContext context) {
		this.root = context;
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

	public void computeAnnotationKind(AnnotationDefinition annotation) {
		
	}

}
