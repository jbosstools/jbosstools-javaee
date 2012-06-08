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
package org.jboss.tools.cdi.deltaspike.core.validation;

import org.eclipse.osgi.util.NLS;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class DeltaspikeValidationMessages extends NLS {
	private static final String BUNDLE_NAME = DeltaspikeValidationMessages.class.getPackage().getName() + ".messages"; //$NON-NLS-1$

	public static String NOT_A_HANDLER_BEAN;
	public static String INVALID_HANDLER_TYPE;

	public static String AMBIGUOUS_AUTHORIZER;
	public static String UNRESOLVED_AUTHORIZER;
	public static String INVALID_AUTHORIZER_MULTIPLE_BINDINGS;
	public static String INVALID_AUTHORIZER_NO_BINDINGS;
	public static String INVALID_AUTHORIZER_NOT_BOOLEAN;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, DeltaspikeValidationMessages.class);
	}

}
