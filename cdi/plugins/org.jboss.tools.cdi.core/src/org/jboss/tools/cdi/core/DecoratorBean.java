package org.jboss.tools.cdi.core;

import java.util.Set;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.internal.core.impl.ClassBean;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;

public class DecoratorBean extends ClassBean implements IDecorator {

	public DecoratorBean() {}

	public Set<IType> getDecoratedTypes() {
		return ((TypeDefinition)definition).getInheritedTypes();
	}

	public IAnnotationDeclaration getDecoratorAnnotation() {
		return decorator;
	}

}
