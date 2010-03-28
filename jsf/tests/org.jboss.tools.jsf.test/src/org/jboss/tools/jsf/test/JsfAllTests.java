/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jboss.tools.jsf.model.pv.test.JSFPromptingProviderTest;
import org.jboss.tools.jsf.test.refactoring.ELVariableRefactoringTest;
import org.jboss.tools.test.util.ProjectImportTestSetup;

public class JsfAllTests {
//	public static final String PLUGIN_ID = "org.jboss.tools.jsf";

	public static Test suite() {
		TestSuite suite = new TestSuite("Test model loading for JSF projects");
		
		suite.addTestSuite(JSFModelTest.class);
		suite.addTestSuite(ModelFormat_2_0_0_Test.class);
		// FIXME http://jira.jboss.org/jira/browse/JBIDE-2441
		suite.addTestSuite(JSFImportTest.class);
		suite.addTestSuite(JSFBeansTest.class);
		suite.addTestSuite(JSFPromptingProviderTest.class);
		
		suite.addTest(new ProjectImportTestSetup(new TestSuite(ELVariableRefactoringTest.class),
				"org.jboss.tools.jsf.test",
				new String[]{"projects/JSFKickStartProject"},
				new String[]{"JSFKickStartProject"}));
		return suite;
	}

}
