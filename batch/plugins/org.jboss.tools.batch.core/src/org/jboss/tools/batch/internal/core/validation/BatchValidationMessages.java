/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.internal.core.validation;

import org.eclipse.osgi.util.NLS;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BatchValidationMessages {
	private static final String BUNDLE_NAME = "org.jboss.tools.batch.internal.core.validation.messages"; //$NON-NLS-1$

	//
	public static String VALIDATING_PROJECT;
	
	public static String JOB_LISTENER_IS_NOT_FOUND;
	public static String JOB_LISTENER_IS_EXPECTED;

	public static String STEP_LISTENER_IS_NOT_FOUND;
	public static String STEP_LISTENER_IS_EXPECTED;

	public static String BATCHLET_IS_NOT_FOUND;
	public static String BATCHLET_IS_EXPECTED;

	public static String DECIDER_IS_NOT_FOUND;
	public static String DECIDER_IS_EXPECTED;

	public static String MAPPER_IS_NOT_FOUND;
	public static String MAPPER_IS_EXPECTED;
	
	public static String ANALYZER_IS_NOT_FOUND;
	public static String ANALYZER_IS_EXPECTED;

	public static String CHECKPOINT_ALGORITHM_IS_NOT_FOUND;
	public static String CHECKPOINT_ALGORITHM_IS_EXPECTED;
	
	public static String COLLECTOR_IS_NOT_FOUND;
	public static String COLLECTOR_IS_EXPECTED;

	public static String PROCESSOR_IS_NOT_FOUND;
	public static String PROCESSOR_IS_EXPECTED;

	public static String READER_IS_NOT_FOUND;
	public static String READER_IS_EXPECTED;

	public static String REDUCER_IS_NOT_FOUND;
	public static String REDUCER_IS_EXPECTED;

	public static String WRITER_IS_NOT_FOUND;
	public static String WRITER_IS_EXPECTED;

	public static String PROPERTY_IS_NOT_USED;
	public static String PROPERTY_IS_NOT_USED_1;
	public static String UNKNOWN_PROPERTY;

	public static String TARGET_NOT_FOUND;
	public static String TARGET_NOT_FOUND_ON_JOB_LEVEL;
	public static String TRANSITION_TO_SELF;
	public static String LOOP_IS_DETECTED;

	public static String EXCEPTION_CLASS_IS_NOT_FOUND;
	public static String EXCEPTION_CLASS_DOES_NOT_EXTEND_JAVA_LANG_EXCEPTION;
	
	public static String ID_IS_NOT_VALID;

	static {
		NLS.initializeMessages(BUNDLE_NAME, BatchValidationMessages.class);
	}
}
