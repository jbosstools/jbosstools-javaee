/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.batch.ui.hyperlink;

import org.eclipse.osgi.util.NLS;

public class BatchHyperlinkMessages extends NLS{
	private static final String BUNDLE_NAME = BatchHyperlinkMessages.class.getName();

	public static String OPEN_JAVA_CLASS;
	public static String GO_TO_NODE;
	public static String OPEN_JAVA_FIELD;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, BatchHyperlinkMessages.class);
	}
}
