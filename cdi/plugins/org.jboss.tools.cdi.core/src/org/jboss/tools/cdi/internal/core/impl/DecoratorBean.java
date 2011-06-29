/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.internal.core.impl;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IDecorator;
import org.jboss.tools.common.java.IParametedType;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class DecoratorBean extends ClassBean implements IDecorator {

	public DecoratorBean() {}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IDecorator#getDecoratedTypes()
	 */
	public Set<IParametedType> getDecoratedTypes() {
		Set<IParametedType> result = new HashSet<IParametedType>();

		Set<IParametedType> legalTypes = getLegalTypes();
		for (IParametedType pt: legalTypes) {
			IType t = pt.getType();
			try {
				if(!t.isInterface()) continue;
			} catch (JavaModelException e) {
				CDICorePlugin.getDefault().logError(e);
				continue;
			}
			if(!"java.io.Serializable".equals(t.getFullyQualifiedName())) {
				result.add(pt);
			}			
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IDecorator#getDecoratorAnnotation()
	 */
	public IAnnotationDeclaration getDecoratorAnnotation() {
		return getDefinition().getDecoratorAnnotation();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.internal.core.impl.ClassBean#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return !getCDIProject().getDecoratorClasses(getBeanClass().getFullyQualifiedName()).isEmpty();
	}
}