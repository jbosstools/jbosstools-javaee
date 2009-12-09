package org.jboss.tools.cdi.internal.core.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IInterceptorBinding;
import org.jboss.tools.cdi.core.IInterceptorBindingDeclaration;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;

public class InterceptorBindingElement extends CDIElement implements IInterceptorBinding {
	AnnotationDefinition definition;

	public InterceptorBindingElement() {}

	public void setDefinition(AnnotationDefinition definition) {
		this.definition = definition;
//		setAnnotations(definition.getAnnotations());
	}	

	public Set<IInterceptorBindingDeclaration> getInterceptorBindingDeclarations() {
		Set<IInterceptorBindingDeclaration> result = new HashSet<IInterceptorBindingDeclaration>();
		List<AnnotationDeclaration> as = definition.getAnnotations();
		for (AnnotationDeclaration a: as) {
			if(a instanceof InterceptorBindingDeclaration) {
				result.add((InterceptorBindingDeclaration)a);
			}
		}
		return result;
	}

	public IType getSourceType() {
		return definition.getType();
	}

}
