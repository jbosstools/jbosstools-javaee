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

import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IInjectionPoint;

/**
 * @author Alexey Kazakov
 */
public interface IInjectionPointValidatorFeature extends ICDIFeature {

	/**
	 * Returns true if CDI Validator should ignore the injection point during lookup validation.
	 * @param typeOfInjectionPoint
	 * @param injection
	 * @return
	 */
	boolean shouldIgnoreInjection(IType typeOfInjectionPoint, IInjectionPoint injection);
}