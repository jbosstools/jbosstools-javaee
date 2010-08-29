/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.cdi.internal.core.validation;

/**
 * @author Alexey Kazakov
 */
public class CDICoreValidationDelegate {

	protected CDICoreValidator validator;

	public CDICoreValidationDelegate(CDICoreValidator validator) {
		this.validator = validator;
	}

	public CDICoreValidator getValidator() {
		return validator;
	}

	public void setValidator(CDICoreValidator validator) {
		this.validator = validator;
	}
}