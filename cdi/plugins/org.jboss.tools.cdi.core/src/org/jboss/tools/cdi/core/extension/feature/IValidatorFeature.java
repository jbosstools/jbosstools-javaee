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

import org.eclipse.core.resources.IFile;
import org.jboss.tools.cdi.internal.core.validation.CDICoreValidator;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface IValidatorFeature {

	/**
	 * Contributes to validation of resource in CDICoreValidator.validateResource(IFile)
	 *
	 * @param file
	 * @param context
	 */
	public void validateResource(IFile file, CDICoreValidator validator);

}
