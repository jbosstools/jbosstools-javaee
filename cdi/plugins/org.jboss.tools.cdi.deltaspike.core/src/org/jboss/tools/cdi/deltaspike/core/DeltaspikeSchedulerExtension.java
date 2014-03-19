/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.deltaspike.core;

import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.feature.IProcessAnnotatedTypeFeature;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.common.java.IParametedType;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
@SuppressWarnings("restriction")
public class DeltaspikeSchedulerExtension implements ICDIExtension, IProcessAnnotatedTypeFeature, DeltaspikeConstants {

	@Override
	public void processAnnotatedType(TypeDefinition typeDefinition, IRootDefinitionContext context) {
		if(isImplementingScheduler(typeDefinition)) {
			typeDefinition.veto();
		}
	}

	private boolean isImplementingScheduler(TypeDefinition typeDefinition) {
		for (IParametedType t: typeDefinition.getAllTypes()) {
			IType type = t.getType();
			if(type != null && SCHEDULER_TYPE.equals(type.getFullyQualifiedName())) {
				return true;
			}
		}		
		return false;
	}

}
