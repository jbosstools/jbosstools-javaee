package org.jboss.tools.cdi.internal.core.impl;

import java.util.Set;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IDecorator;
import org.jboss.tools.cdi.core.IParametedType;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;

public class DecoratorBean extends ClassBean implements IDecorator {

	public DecoratorBean() {}

	public Set<IParametedType> getDecoratedTypes() {
		return ((TypeDefinition)definition).getInheritedTypes();
	}

	public IAnnotationDeclaration getDecoratorAnnotation() {
		return decorator;
	}

}
