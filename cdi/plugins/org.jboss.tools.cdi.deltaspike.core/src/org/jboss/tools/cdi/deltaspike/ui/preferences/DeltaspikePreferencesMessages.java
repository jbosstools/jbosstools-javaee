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
package org.jboss.tools.cdi.deltaspike.ui.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * @author Viacheslav Kabanovich
 */
public class DeltaspikePreferencesMessages extends NLS {
	private static final String BUNDLE_NAME = DeltaspikePreferencesMessages.class.getName();

	//Section Deltaspike
	public static String CDIValidatorConfigurationBlock_section_deltaspike;
	public static String CDIValidatorConfigurationBlock_pb_notAHandlerBean_label;
	public static String CDIValidatorConfigurationBlock_pb_invalidHandlerType_label;
	public static String CDIValidatorConfigurationBlock_pb_ambiguousAuthorizer_label;
	public static String CDIValidatorConfigurationBlock_pb_unresolvedAuthorizer_label;
	public static String CDIValidatorConfigurationBlock_pb_invalidAuthorizer_label;

	static {
		NLS.initializeMessages(BUNDLE_NAME, DeltaspikePreferencesMessages.class);
	}
}
