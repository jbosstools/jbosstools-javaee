/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core.test.tck.validation.extension;

import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.feature.IProcessAnnotatedTypeFeature;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;

/**
 * @author Alexey Kazakov
 */
public class ExcludeExtension implements ICDIExtension, IProcessAnnotatedTypeFeature {

	public static final String EXCLUDE_ANNOTATION = "org.jboss.jsr299.tck.tests.jbt.validation.inject.incremental.packageinfo.zoo.TestExclude";

	@Override
	public void processAnnotatedType(TypeDefinition typeDefinition, IRootDefinitionContext context) {
		if (typeDefinition.isAnnotationPresent(EXCLUDE_ANNOTATION)
				|| (typeDefinition.getPackageDefinition() != null && typeDefinition
						.getPackageDefinition().isAnnotationPresent(EXCLUDE_ANNOTATION))) {
			typeDefinition.veto();
		}
	}
}