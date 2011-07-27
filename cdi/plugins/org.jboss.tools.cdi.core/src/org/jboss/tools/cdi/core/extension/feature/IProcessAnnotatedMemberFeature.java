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
package org.jboss.tools.cdi.core.extension.feature;

import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.internal.core.impl.definition.BeanMemberDefinition;

/**
 * This feature corresponds to ProcessAnnotatedTypeEvent in CDI runtime.
 * 
 * It is invoked as soon as builder have read member definition, but has not yet 
 * added it to the parent type definition. That moment is important for extensions 
 * that would recognize this member as a CDI artifact (producer, injection, observer) 
 * even though it is not annotated so. Extensions that do not need to interfere 
 * with CDI artifact recognition, may use IProcessAnnotatedTypeFeature instead, 
 * which is invoked when all type definitions with members are built completely,
 * and get access to members as child elements of the type definition.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface IProcessAnnotatedMemberFeature extends ICDIFeature {

	public void processAnnotatedMember(BeanMemberDefinition memberDefinition, IRootDefinitionContext context);

}
