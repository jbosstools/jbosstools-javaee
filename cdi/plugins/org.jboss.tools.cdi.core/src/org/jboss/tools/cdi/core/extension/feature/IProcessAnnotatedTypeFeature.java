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
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;

/**
 * This feature corresponds to ProcessAnnotatedTypeEvent in CDI runtime.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface IProcessAnnotatedTypeFeature {

	/**
	 * Method is called after CDI builder loaded type definitions and before they are 
	 * used to build beans. Client may change type definitions or their members or veto them.
	 * 
	 * @param typeDefinition
	 * @param context
	 */
	public void processAnnotatedType(TypeDefinition typeDefinition, IRootDefinitionContext context);

}