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

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jdt.internal.core.JavaModelManager;
import org.jboss.tools.jsf.model.pv.test.JSFPromptingProviderTest;
import org.jboss.tools.jsf.test.refactoring.ELVariableRefactoringTest;
import org.jboss.tools.test.util.ProjectImportTestSetup;

public class JsfAllTests {
//	public static final String PLUGIN_ID = "org.jboss.tools.jsf";

	public static Test suite() {
		TestSuite suite = new TestSuite("Test model loading for JSF projects");
		TestSuite old = new TestSuite("Tests are using JSFKickStartOldFormat");
		old.addTestSuite(JSFModelTest.class);
		old.addTestSuite(ModelFormat_2_0_0_Test.class);
		old.addTestSuite(JSFBeansTest.class);
		suite.addTest(new ProjectImportTestSetup(old,"org.jboss.tools.jsf.test","projects/JSFKickStartOldFormat","JSFKickStartOldFormat"));
		suite.addTest(new ProjectImportTestSetup(new TestSuite(JSFPromptingProviderTest.class),"org.jboss.tools.jsf.test", JSFPromptingProviderTest.TEST_PROJECT_PATH, JSFPromptingProviderTest.TEST_PROJECT_NAME));
		
		suite.addTest(new ProjectImportTestSetup(new TestSuite(ELVariableRefactoringTest.class),
				"org.jboss.tools.jsf.test",
				new String[]{"projects/JSFKickStartOldFormat"},
				new String[]{"JSFKickStartOldFormat"}));
		return new DisableJavaIndexingSetup(suite);
	}
	
	public static class DisableJavaIndexingSetup extends TestSetup {

		public DisableJavaIndexingSetup(Test test) {
			super(test);
		}
		
		@Override
		protected void setUp() throws Exception {
			JavaModelManager.getIndexManager().disable();
		}

		@Override
		protected void tearDown() throws Exception {
			JavaModelManager.getIndexManager().disable();
		}
	}
}
