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
package org.jboss.tools.batch.ui.itest.ca;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class JobPropertyNameContentAssist extends ContentAssistantTestCase {
	private static final String FILE_NAME = "/src/META-INF/batch-jobs/job-ca-3.xml";

	public JobPropertyNameContentAssist() {}

	private static final String TEXT_TO_FIND_1 = "<property name=\"x";

	private static final String[] PROPOSALS_1 = {
		"p1", "p2", "worktime"
	};
	private static final String[] NO_PROPOSALS_1 = {
		"p3"
	};
	public void testPropertyName() throws Exception {
		checkProposals(FILE_NAME, TEXT_TO_FIND_1, TEXT_TO_FIND_1.length() - 1, PROPOSALS_1, NO_PROPOSALS_1);
	}

}
