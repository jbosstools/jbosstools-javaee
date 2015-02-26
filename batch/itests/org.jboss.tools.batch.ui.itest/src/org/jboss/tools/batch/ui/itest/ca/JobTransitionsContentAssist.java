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
public class JobTransitionsContentAssist extends ContentAssistantTestCase {
	private static final String FILE_NAME = "/src/META-INF/batch-jobs/job-ca-1.xml";

	public JobTransitionsContentAssist() {}

	private static final String TEXT_TO_FIND_1 = "<step id=\"step2\" next=\"";

	private static final String[] PROPOSALS_1 = {
		"step1", "step3", "flow1"
	};
	private static final String[] NO_PROPOSALS_1 = {
		"step11", "step12", "step13"
	};
	public void testTransitionsForStepNext() throws Exception {
		checkProposals(FILE_NAME, TEXT_TO_FIND_1, TEXT_TO_FIND_1.length(), PROPOSALS_1, NO_PROPOSALS_1);
	}

	private static final String TEXT_TO_FIND_2 = "restart=\"";

	private static final String[] PROPOSALS_2 = {
		"step1", "step2", "step3", "flow1"
	};
	private static final String[] NO_PROPOSALS_2 = {
		"step11", "step12", "step13"
	};
	
	public void testTransitionsForRestart() throws Exception {
		checkProposals(FILE_NAME, TEXT_TO_FIND_2, TEXT_TO_FIND_2.length(), PROPOSALS_2, NO_PROPOSALS_2);
	}

	private static final String TEXT_TO_FIND_3 = "to=\"";

	private static final String[] PROPOSALS_3 = {
		"step11", "step13",
	};
	private static final String[] NO_PROPOSALS_3 = {
		"step12",
	};
	public void testTransitionsForInnerLevelStepTo() throws Exception {
		checkProposals(FILE_NAME, TEXT_TO_FIND_3, TEXT_TO_FIND_3.length(), PROPOSALS_3, NO_PROPOSALS_3);
	}
}
