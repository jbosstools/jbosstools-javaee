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

import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.feature.IInjectionPointValidatorFeature;

/**
 * Runtime
 * org.apache.deltaspike.core.impl.config.injectable.extension.ConfigPropertyExtension
 * 
 * @author Viacheslav Kabanovich
 */
public class DeltaspikeConfigPropertyExtension implements ICDIExtension, IInjectionPointValidatorFeature, DeltaspikeConstants {

	@Override
	public boolean shouldIgnoreInjection(IType typeOfInjectionPoint,
			IInjectionPoint injection) {
		if(injection.getAnnotation(CONFIG_PROPERTY_ANNOTATION_TYPE_NAME) != null) {
			return true;
		}
		return false;
	}

}
