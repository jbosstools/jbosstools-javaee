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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;
import org.jboss.tools.jsf.model.pv.test.JSFPromptingProviderTest;
import org.jboss.tools.jsf.test.refactoring.ELVariableRefactoringTest;
import org.jboss.tools.jsf.test.refactoring.JSF2RefactoringTest;
import org.jboss.tools.jsf.test.refactoring.MessagePropertyRefactoringTest;
import org.jboss.tools.jsf.test.validation.ELValidatorTest;
import org.jboss.tools.jsf.test.validation.JSF2ComponentsValidatorTest;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;

public class JsfAllTests {
	// public static final String PLUGIN_ID = "org.jboss.tools.jsf";

	public static Test suite() {
		TestSuite suite = new TestSuite("Test model loading for JSF projects"); //$NON-NLS-1$
		TestSuite old = new TestSuite("Tests are using JSFKickStartOldFormat"); //$NON-NLS-1$
		old.addTestSuite(JSFModelTest.class);
		old.addTestSuite(ModelFormat_2_0_0_Test.class);
		old.addTestSuite(JSFBeansTest.class);
		suite.addTestSuite(JSFPaletteTest.class);
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
		suite.addTest(new ProjectImportTestSetup(new TestSuite(ELValidatorTest.class),"org.jboss.tools.jsf.test","projects/JSFKickStartOldFormat","JSFKickStartOldFormat") {
			@Override
			protected void setUp() throws Exception {
				super.setUp();
				IProject project = (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember("JSFKickStartOldFormat");
				project.refreshLocal(IResource.DEPTH_INFINITE, null);
				JobUtils.waitForIdle();
				
				ValidatorManager.addProjectBuildValidationSupport(project);
			}
		} );

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