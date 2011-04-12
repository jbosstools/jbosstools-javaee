/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core.extension;

import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IDefinitionContext;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;

/**
 * Context to keep definitions loaded by CDI extensions.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface IDefinitionContextExtension extends IDefinitionContext {

	/**
	 * Removes definitions loaded from type
	 * 
	 * @param typeName
	 */
	public void clean(String typeName);

	public void setRootContext(IRootDefinitionContext context);

	/**
	 * Returns the entire context of CDI project.
	 *  
	 * @return
	 */
	public IRootDefinitionContext getRootContext();

	/**
	 * Returns existing working copy of original context, or this object if it is a working copy.
	 * 
	 * @return
	 */
	public IDefinitionContextExtension getWorkingCopy();

	/**
	 * Contributes to computing of annotation kind by root context.
	 *   
	 * @param annotationType
	 * @return
	 */
	public void computeAnnotationKind(AnnotationDefinition annotation);

}
