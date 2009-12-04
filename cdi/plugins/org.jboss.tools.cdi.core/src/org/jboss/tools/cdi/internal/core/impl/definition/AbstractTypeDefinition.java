package org.jboss.tools.cdi.internal.core.impl.definition;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;

public class AbstractTypeDefinition extends AbstractMemberDefinition {
	protected String qualifiedName;
	IType type;

	public AbstractTypeDefinition() {}

	public String getQualifiedName() {
		return qualifiedName;
	}

	public IType getType() {
		return type;
	}

	public void setType(IType type, DefinitionContext context) {
		super.setAnnotatable(type, type,context);
	}

	@Override
	protected void init(IType contextType, DefinitionContext context) throws CoreException {
		this.type = contextType;
		super.init(contextType, context);
		qualifiedName = getType().getFullyQualifiedName();
	}

}
