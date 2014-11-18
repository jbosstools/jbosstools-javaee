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
package org.jboss.tools.cdi.internal.core.validation;

import org.eclipse.osgi.util.NLS;

/**
 * CDI 1.2 is a maintenance version and most validating rules are not changed since CDI 1.1.
 * So, this bundle should contain only new and modified validation messages comparing to CDI 1.1.
 * 
 * Class CDIValidationMessages will use a constant from CDIValidationMessages11 for CDI 1.2
 * validation if the constant is missing in CDIValidationMessages12 and CDIValidationMessages 
 * does not specify the version rage for the rule. 
 * 
 * In respect of inherited, changed and new constants, CDIValidationMessages12 extends CDIValidationMessages11. 
 * However, Java inheritance is not used to correctly treat the case of a validation rule being obsolete 
 * in a newer version (CDIValidationMessages sets a range for it).
 * 
 * TODO add new messages
 * 
 * @author Viacheslav Kabanovich
 */
public class CDIValidationMessages12 {

	private static final String BUNDLE_NAME = "org.jboss.tools.cdi.internal.core.validation.messages12"; //$NON-NLS-1$

	static {
		NLS.initializeMessages(BUNDLE_NAME, CDIValidationMessages12.class);
	}
}