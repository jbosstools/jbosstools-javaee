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
package org.jboss.tools.cdi.core;

import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.extension.IDefinitionContextExtension;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface IRootDefinitionContext extends IDefinitionContext {

	/**
	 * Returns cdi project for which this context is created.
	 * 
	 * @return
	 */
	public CDICoreNature getProject();

	/**
	 * Registers path in context so that when its parent resource is removed from project,
	 * definitions load from that path should be cleaned from context.
	 * 
	 * @param file
	 */
	public void addToParents(IPath file);

	/**
	 * Registers type name in context so that when its parent resource is removed from project,
	 * definitions load from that path should be cleaned from context.
	 * 
	 * @param file
	 */
	public void addType(IPath file, String typeName);

	public int getAnnotationKind(IType annotationType);

	public AnnotationDefinition getAnnotation(String fullyQualifiedName);

	public Set<IDefinitionContextExtension> getExtensions();

	public void addDependency(IPath source, IPath target);

}
