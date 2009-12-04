package org.jboss.tools.cdi.internal.core.impl.definition;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.internal.core.impl.AnnotationDeclaration;

public abstract class AbstractMemberDefinition {
	protected List<AnnotationDeclaration> annotations = new ArrayList<AnnotationDeclaration>();
	protected IAnnotatable member;

	public AbstractMemberDefinition() {}

	protected void setAnnotatable(IAnnotatable member, IType contextType, DefinitionContext context) {
		this.member = member;
		try {
			init(contextType, context);
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	protected void init(IType contextType, DefinitionContext context) throws CoreException {
		IAnnotation[] ts = member.getAnnotations();
		for (int i = 0; i < annotations.size(); i++) {
			AnnotationDeclaration a = new AnnotationDeclaration();
			a.setDeclaration(ts[i], contextType);
			annotations.add(a);
		}
	}

}
