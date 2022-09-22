/*******************************************************************************
 * Copyright (c) 2022 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.faces.core;

import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.feature.IInjectionPointValidatorFeature;

/**
 * @author Red Hat Developers
 *
 */
public class JakarkaFacesExtension implements ICDIExtension, IInjectionPointValidatorFeature {

	@Override
	public boolean shouldIgnoreInjection(IType typeOfInjectionPoint, IInjectionPoint injection) {
		var name = typeOfInjectionPoint.getFullyQualifiedName();
		return name.equals("jakarta.faces.application.ResourceHandler") ||
				name.equals("jakarta.faces.context.ExternalContext") ||
				name.equals("jakarta.faces.context.FacesContext") || name.equals("jakarta.faces.context.Flash") ||
				name.equals("jakarta.faces.push.PushContext");
	}
}
