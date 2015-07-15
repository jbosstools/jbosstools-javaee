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
package org.jboss.tools.batch.ui.quickfixes;

import org.eclipse.osgi.util.NLS;

public class BatchQuickFixMessages extends NLS{
	private static final String BUNDLE_NAME = BatchQuickFixMessages.class.getName();

	public static String CREATE_BATCHLET;
	public static String CREATE_DECIDER;
	public static String CREATE_ITEM_READER;
	public static String CREATE_ITEM_WRITER;
	public static String CREATE_ITEM_PROCESSOR;
	public static String CREATE_CHECKPOINT_ALGORYTHM;
	public static String CREATE_PARTITION_MAPPER;
	public static String CREATE_PARTITION_REDUCER;
	public static String CREATE_PARTITION_COLLECTOR;
	public static String CREATE_PARTITION_ANALYZER;
	public static String CREATE_JOB_LISTENER;
	public static String CREATE_STEP_LISTENER;
	public static String CREATE_CHUNK_LISTENER;
	public static String CREATE_ITEM_READ_LISTENER;
	public static String CREATE_ITEM_PROCESS_LISTENER;
	public static String CREATE_ITEM_WRITE_LISTENER;
	public static String CREATE_SKIP_READ_LISTENER;
	public static String CREATE_SKIP_PROCESS_LISTENER;
	public static String CREATE_SKIP_WRITE_LISTENER;
	public static String CREATE_RETRY_READ_LISTENER;
	public static String CREATE_RETRY_PROCESS_LISTENER;
	public static String CREATE_RETRY_WRITE_LISTENER;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, BatchQuickFixMessages.class);
	}
}
