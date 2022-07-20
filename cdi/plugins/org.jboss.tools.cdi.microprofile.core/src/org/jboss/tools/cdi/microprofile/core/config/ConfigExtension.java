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
package org.jboss.tools.cdi.microprofile.core.config;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.feature.IInjectionPointValidatorFeature;

/**
 * @author Red Hat Developers
 *
 */
public class ConfigExtension implements ICDIExtension, IInjectionPointValidatorFeature {

	@Override
	public boolean shouldIgnoreInjection(IType typeOfInjectionPoint, IInjectionPoint injection) {
		try {
			return hasOfMethod(typeOfInjectionPoint) || hasValueOfMethod(typeOfInjectionPoint) ||
					hasParseMethod(typeOfInjectionPoint) || hasStringConstructor(typeOfInjectionPoint);
		} catch (JavaModelException e) {
			return false;
		}
	}

	/**
	 * @param type type to analyze
	 * @return true if method found
	 * @throws JavaModelException 
	 */
	private boolean hasOfMethod(IType type) throws JavaModelException {
		return hasMethod(type, "of");
	}

	/**
	 * @param type type to analyze
	 * @return true if method found
	 * @throws JavaModelException 
	 */
	private boolean hasValueOfMethod(IType type) throws JavaModelException {
		return hasMethod(type, "valueOf");
	}

	/**
	 * @param type type to analyze
	 * @return true if method found
	 * @throws JavaModelException 
	 */
	private boolean hasParseMethod(IType type) throws JavaModelException {
		return hasMethod(type, "parse");
	}

	/**
	 * @param type type to analyze
	 * @return true if method found
	 * @throws JavaModelException 
	 */
	private boolean hasStringConstructor(IType type) throws JavaModelException {
		return hasMethod(type, type.getElementName());
	}

	/**
	 * @param type type to analyze
	 * @param name method name to look up
	 * @return true if method found
	 * @throws JavaModelException 
	 */
	private boolean hasMethod(IType type, String name) throws JavaModelException {
		for(IMethod method : type.getMethods()) {
			if (method.getNumberOfParameters() == 1 && name.equals(type.getElementName())) {
				String parmType = method.getParameterTypes()[0];
				if ("QString;".equals(parmType) || "Qjava.lang.String;".equals(parmType) ||
						"Ljava.lang.String;".equals(parmType)) {
					return true;
				}
			}
		}
		return false;
	}
}
