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
 * @author Alexey Kazakov
 */
public class ClassAttributeContentAssist extends ContentAssistantTestCase {

	private static final String FILE_NAME = "/src/META-INF/batch-jobs/job-ca-class.xml";

	public void testRetryableException() throws Exception {
		checkProposals(FILE_NAME, 
				"excepT", 
				"excepT".length(),
				new String[]{"java.lang.Exception"},
				new String[]{"java.lang.String"});
	}

	public void testNoRollbackException() throws Exception {
		checkProposals(FILE_NAME, 
				"runtimee", 
				"runtimee".length(), 
				new String[]{"java.lang.RuntimeException"},
				new String[]{"java.lang.Exception"});
	}

	public void testSkippableException() throws Exception {
		checkProposals(FILE_NAME, 
				"java.lang.Excep", 
				"java.lang.Excep".length(), 
				new String[]{"java.lang.Exception"},
				new String[]{"java.lang.RuntimeException"});
	}
}