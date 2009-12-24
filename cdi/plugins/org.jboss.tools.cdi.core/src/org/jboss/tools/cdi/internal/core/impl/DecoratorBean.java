/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.internal.core.impl;

import java.util.Set;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IDecorator;
import org.jboss.tools.cdi.core.IParametedType;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class DecoratorBean extends ClassBean implements IDecorator {

	public DecoratorBean() {}

	public Set<IParametedType> getDecoratedTypes() {
		return ((TypeDefinition)definition).getInheritedTypes();
	}

	public IAnnotationDeclaration getDecoratorAnnotation() {
		return getDefinition().getDecoratorAnnotation();
	}

	public boolean isEnabled() {
		return !getCDIProject().getDecoratorClasses(getBeanClass().getFullyQualifiedName()).isEmpty();
	}

}
