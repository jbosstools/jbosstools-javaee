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
import org.jboss.tools.jsf.ca.test.WebContentAssistProviderTest;
import org.jboss.tools.jsf.kb.test.FaceletsKbModelTest;
import org.jboss.tools.jsf.model.pv.test.JSFPromptingProviderTest;
import org.jboss.tools.jsf.test.refactoring.ELVariableRefactoringTest;
import org.jboss.tools.jsf.test.refactoring.JSF2RefactoringTest;
import org.jboss.tools.jsf.test.refactoring.MessagePropertyRefactoringTest;
import org.jboss.tools.jsf.test.validation.JSF2ComponentsValidatorTest;
import org.jboss.tools.test.util.ProjectImportTestSetup;

public class JsfAllTests {
	// public static final String PLUGIN_ID = "org.jboss.tools.jsf";

	public static Test suite() {
		TestSuite suite = new TestSuite("Test model loading for JSF projects"); //$NON-NLS-1$
		TestSuite old = new TestSuite("Tests are using JSFKickStartOldFormat"); //$NON-NLS-1$
		old.addTestSuite(JSFModelTest.class);
		old.addTestSuite(ModelFormat_2_0_0_Test.class);
		old.addTestSuite(JSFBeansTest.class);
		old.addTest(
				new ProjectImportTestSetup(WebContentAssistProviderTest.suite(), 
						"org.jboss.tools.jst.web.test", "projects/TestsWebArtefacts","TestsWebArtefacts"));
		suite.addTest(new ProjectImportTestSetup(old,
				"org.jboss.tools.jsf.test", "projects/JSFKickStartOldFormat", //$NON-NLS-1$ //$NON-NLS-2$
				"JSFKickStartOldFormat")); //$NON-NLS-1$
		suite.addTest(new ProjectImportTestSetup(new TestSuite(
				JSFPromptingProviderTest.class),
				"org.jboss.tools.jsf.test", //$NON-NLS-1$
				JSFPromptingProviderTest.TEST_PROJECT_PATH,
				JSFPromptingProviderTest.TEST_PROJECT_NAME));

		suite.addTest(new ProjectImportTestSetup(new TestSuite(
				ELVariableRefactoringTest.class), "org.jboss.tools.jsf.test", //$NON-NLS-1$
				new String[] { "projects/JSFKickStartOldFormat" }, //$NON-NLS-1$
				new String[] { "JSFKickStartOldFormat" })); //$NON-NLS-1$
		suite.addTest(new ProjectImportTestSetup(new TestSuite(
				MessagePropertyRefactoringTest.class), "org.jboss.tools.jsf.test", //$NON-NLS-1$
				new String[] { "projects/JSFKickStartOldFormat" }, //$NON-NLS-1$
				new String[] { "JSFKickStartOldFormat" })); //$NON-NLS-1$
		suite.addTest(new ProjectImportTestSetup(new TestSuite(
				JSF2ComponentsValidatorTest.class), "org.jboss.tools.jsf.test", //$NON-NLS-1$
				new String[] { "projects/JSF2ComponentsValidator" }, //$NON-NLS-1$
				new String[] { "JSF2ComponentsValidator" })); //$NON-NLS-1$
		suite.addTest(new ProjectImportTestSetup(new TestSuite(
				JSF2RefactoringTest.class), "org.jboss.tools.jsf.test", //$NON-NLS-1$
				new String[] { "projects/JSF2ComponentsValidator" }, //$NON-NLS-1$
				new String[] { "JSF2ComponentsValidator" })); //$NON-NLS-1$

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
