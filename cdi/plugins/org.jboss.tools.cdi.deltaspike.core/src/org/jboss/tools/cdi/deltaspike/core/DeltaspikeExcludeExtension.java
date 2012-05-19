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
package org.jboss.tools.cdi.deltaspike.core;

import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.feature.IProcessAnnotatedTypeFeature;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;

/**
 * Runtime
 * org.apache.deltaspike.core.impl.exclude.extension.ExcludeExtension
 * 
 * @author Viacheslav Kabanovich
 */
public class DeltaspikeExcludeExtension implements ICDIExtension, IProcessAnnotatedTypeFeature, DeltaspikeConstants {

	public DeltaspikeExcludeExtension() {
	}

	@Override
	public void processAnnotatedType(TypeDefinition typeDefinition, IRootDefinitionContext context) {
		processExclude(typeDefinition, context);
	}

	// @Exclude
	private boolean processExclude(TypeDefinition typeDefinition, IRootDefinitionContext context) {
		if (typeDefinition.isAnnotationPresent(EXCLUDE_ANNOTATION_TYPE_NAME)
				|| (typeDefinition.getPackageDefinition() != null && typeDefinition
						.getPackageDefinition().isAnnotationPresent(EXCLUDE_ANNOTATION_TYPE_NAME))) {
			typeDefinition.veto();
			return true;
		}
		return false;
	}
}
