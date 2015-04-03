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
package org.jboss.tools.batch.ui.participants;

import org.eclipse.osgi.util.NLS;

public class BatchParticipantMessages extends NLS{
	private static final String BUNDLE_NAME = BatchParticipantMessages.class.getName();

	public static String Updating_Batch_Artifacts_References;
	public static String Searching_For_Batch_Artifacts_References;
	public static String Cannot_Change_Read_Only_File;
	public static String Cannot_Read_Out_Of_Sync_Resource;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, BatchParticipantMessages.class);
	}
}
