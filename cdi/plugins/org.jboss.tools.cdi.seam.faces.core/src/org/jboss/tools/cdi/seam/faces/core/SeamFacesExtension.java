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
package org.jboss.tools.cdi.seam.faces.core;

import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.feature.IInjectionPointValidatorFeature;

/**
 * @author Alexey Kazakov
 */
public class SeamFacesExtension implements ICDIExtension, IInjectionPointValidatorFeature {

	private final static String SEAM_FACES_INPUT_FIELD_TYPE_NAME = "org.jboss.seam.faces.validation.InputField";
	private final static String SEAM_FACES_INPUT_ELEMENT_TYPE_NAME = "org.jboss.seam.faces.validation.InputElement";

	/**
	 * If the injection point annotated @InputField then don't try to resolve it. Also ignore @Inject InputElement field.
	 * See https://issues.jboss.org/browse/JBIDE-8576
	 * 
	 * @see org.jboss.tools.cdi.core.extension.feature.IInjectionPointValidatorFeature#shouldIgnoreInjection(org.eclipse.jdt.core.IType, org.jboss.tools.cdi.core.IInjectionPoint)
	 */
	@Override
	public boolean shouldIgnoreInjection(IType typeOfInjectionPoint, IInjectionPoint injection) {
		if(typeOfInjectionPoint!=null) {
			if(SEAM_FACES_INPUT_ELEMENT_TYPE_NAME.equals(typeOfInjectionPoint.getFullyQualifiedName()) || injection.getAnnotation(SEAM_FACES_INPUT_FIELD_TYPE_NAME)!=null) {
				return true;
			}
			if(injection instanceof IInjectionPointParameter) {
				IInjectionPointParameter param = (IInjectionPointParameter)injection;
				if(param.getBeanMethod().getAnnotation(SEAM_FACES_INPUT_FIELD_TYPE_NAME)!=null) {
					return true;
				}
			}
		}
		return false;
	}
}