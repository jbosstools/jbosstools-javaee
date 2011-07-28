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
package org.jboss.tools.cdi.seam.core.servlet;

import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.feature.IInjectionPointValidatorFeature;

/**
 * @author Alexey Kazakov
 */
public class SeamServletExtension implements ICDIExtension,	IInjectionPointValidatorFeature {

	private final static String SEAM_SERVLET_REQUEST_PARAM_TYPE_NAME = "org.jboss.seam.servlet.http.RequestParam";
	private final static String SEAM_SERVLET_HEADER_PARAM_TYPE_NAME = "org.jboss.seam.servlet.http.HeaderParam";
	private final static String SEAM_SERVLET_COOKIE_PARAM_TYPE_NAME = "org.jboss.seam.servlet.http.CookieParam";

	/**
	 * If the injection point annotated @RequestParam, @HeaderParam or @CookieParam then don't try to resolve it.
	 * See https://issues.jboss.org/browse/JBIDE-9389
	 * 
	 * @see org.jboss.tools.cdi.core.extension.feature.IInjectionPointValidatorFeature#shouldIgnoreInjection(org.eclipse.jdt.core.IType, org.jboss.tools.cdi.core.IInjectionPoint)
	 */
	@Override
	public boolean shouldIgnoreInjection(IType typeOfInjectionPoint, IInjectionPoint injection) {
		return injection.getAnnotation(SEAM_SERVLET_COOKIE_PARAM_TYPE_NAME) != null
				|| injection.getAnnotation(SEAM_SERVLET_HEADER_PARAM_TYPE_NAME) != null
				|| injection.getAnnotation(SEAM_SERVLET_REQUEST_PARAM_TYPE_NAME) != null;
	}
}