/*******************************************************************************
 * Copyright (c) 2007-2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.ui.test.wizard;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.jst.firstrun.messages"; //$NON-NLS-1$
	public static String JBossASAdapterInitializer_AppServer32;
	public static String JBossASAdapterInitializer_AppServer40;
	public static String JBossASAdapterInitializer_AppServer42;
	public static String JBossASAdapterInitializer_AppServer50;
	public static String JBossASAdapterInitializer_CannotCreateDriver;
	public static String JBossASAdapterInitializer_CannotCreateProfile;
	public static String JBossASAdapterInitializer_CannotCreateServer;
	public static String JBossASAdapterInitializer_JBossASHypersonicEmbeddedDB;
	public static String JBossASAdapterInitializer_Runtime;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
