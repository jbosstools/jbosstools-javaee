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
import org.jboss.tools.common.base.test.validation.ValidationExceptionTest;
import org.jboss.tools.common.base.test.validation.ValidationProjectTestSetup;
import org.jboss.tools.jsf.model.pv.test.JSFPromptingProviderTest;
import org.jboss.tools.jsf.model.pv.test.JsfJbide7975Test;
import org.jboss.tools.jsf.test.project.facet.JSFFacetOnExistingProjectTest;
import org.jboss.tools.jsf.test.refactoring.ELVariableRefactoringTest;
import org.jboss.tools.jsf.test.refactoring.JSF2RefactoringTest;
import org.jboss.tools.jsf.test.refactoring.MessagePropertyRefactoringTest;
import org.jboss.tools.jsf.test.refactoring.MethodRefactoringTest;
import org.jboss.tools.jsf.test.validation.BuilderValidationTest;
import org.jboss.tools.jsf.test.validation.ELValidatorTest;
import org.jboss.tools.jsf.test.validation.FacesConfigValidatorTest;
import org.jboss.tools.jsf.test.validation.JSF2ComponentsInClassFolderTest;
import org.jboss.tools.jsf.test.validation.JSF2ComponentsValidatorTest;
import org.jboss.tools.jsf.test.validation.VarAttributesTest;
import org.jboss.tools.jsf.test.validation.WebContentTest;
import org.jboss.tools.jsf.test.validation.XHTMLDetectorAndValidatorTest;
import org.jboss.tools.jsf.test.validation.XHTMLValidatorTest;
import org.jboss.tools.test.util.ProjectImportTestSetup;

public class JsfAllTests {
	// public static final String PLUGIN_ID = "org.jboss.tools.jsf";

	public static Test suite() {
		ValidationExceptionTest.initLogger();

		TestSuite suite = new TestSuite("Test model loading for JSF projects"); //$NON-NLS-1$
		suite.addTestSuite(JSFTemplateTest.class);
		suite.addTestSuite(JSFFacetOnExistingProjectTest.class);

		TestSuite jsf2 = new TestSuite(JSF2ModelTest.class.getName());
		jsf2.addTestSuite(JSF2ModelTest.class);
		ProjectImportTestSetup testSetup = new ProjectImportTestSetup(jsf2,
				"org.jboss.tools.jsf.base.test",
				new String[]{"projects/JSF2Beans", "projects/JSF2Web"},
				new String[]{"JSF2Beans", "JSF2Web"});
		suite.addTest(testSetup);

		TestSuite old = new TestSuite("Tests are using JSFKickStartOldFormat"); //$NON-NLS-1$
		old.addTestSuite(JSFModelTest.class);
		old.addTestSuite(ModelFormat_2_0_0_Test.class);
		old.addTestSuite(JSFBeansTest.class);
		suite.addTestSuite(WebContentTest.class);
		suite.addTestSuite(XHTMLValidatorTest.class);
		suite.addTestSuite(XHTMLDetectorAndValidatorTest.class);
		suite.addTestSuite(XMLCatalogTest.class);
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
				JsfJbide7975Test.class),
				"org.jboss.tools.jsf.test", //$NON-NLS-1$
				JsfJbide7975Test.TEST_PROJECT_PATH,
				JsfJbide7975Test.TEST_PROJECT_NAME));

		suite.addTest(new ProjectImportTestSetup(new TestSuite(
				ELVariableRefactoringTest.class), "org.jboss.tools.jsf.test", //$NON-NLS-1$
				new String[] { "projects/JSFKickStartOldFormat" }, //$NON-NLS-1$
				new String[] { "JSFKickStartOldFormat" })); //$NON-NLS-1$
		suite.addTest(new ProjectImportTestSetup(new TestSuite(
				MethodRefactoringTest.class), "org.jboss.tools.jsf.test", //$NON-NLS-1$
				new String[] { "projects/JSFKickStartOldFormat" }, //$NON-NLS-1$
				new String[] { "JSFKickStartOldFormat" })); //$NON-NLS-1$
		suite.addTest(new ProjectImportTestSetup(new TestSuite(
				MessagePropertyRefactoringTest.class), "org.jboss.tools.jsf.test", //$NON-NLS-1$
				new String[] { "projects/JSFKickStartOldFormat" }, //$NON-NLS-1$
				new String[] { "JSFKickStartOldFormat" })); //$NON-NLS-1$
		suite.addTest(new ValidationProjectTestSetup(new TestSuite(
				JSF2ComponentsValidatorTest.class,
				JSF2ComponentsInClassFolderTest.class,
				FacesConfigValidatorTest.class), "org.jboss.tools.jsf.test", //$NON-NLS-1$
				new String[] { "projects/JSF2ComponentsValidator" }, //$NON-NLS-1$
				new String[] { "JSF2ComponentsValidator" })); //$NON-NLS-1$
		suite.addTest(new ProjectImportTestSetup(new TestSuite(
				JSF2RefactoringTest.class), "org.jboss.tools.jsf.test", //$NON-NLS-1$
				new String[] { "projects/JSF2ComponentsValidator" }, //$NON-NLS-1$
				new String[] { "JSF2ComponentsValidator" })); //$NON-NLS-1$
		suite.addTest(new ProjectImportTestSetup(new TestSuite(
				ELReferencesQueryParticipantTest.class), "org.jboss.tools.jsf.test", //$NON-NLS-1$
				new String[] { "projects/JSF2ComponentsValidator" }, //$NON-NLS-1$
				new String[] { "JSF2ComponentsValidator" })); //$NON-NLS-1$
//		suite.addTest(new ProjectImportTestSetup(new TestSuite(
//				I18nValidatorTest.class), "org.jboss.tools.jsf.test", //$NON-NLS-1$
//				new String[] { "projects/i18nTestProject" }, //$NON-NLS-1$
//				new String[] { "i18nTestProject" })); //$NON-NLS-1$
		suite.addTest(new ValidationProjectTestSetup(new TestSuite(ELValidatorTest.class),"org.jboss.tools.jsf.test","projects/JSFKickStartOldFormat","JSFKickStartOldFormat"));
		suite.addTest(new ValidationProjectTestSetup(new TestSuite(BuilderValidationTest.class),"org.jboss.tools.jsf.test","projects/jsf2pr","jsf2pr"));
		suite.addTest(new ValidationProjectTestSetup(new TestSuite(VarAttributesTest.class),"org.jboss.tools.jsf.test","projects/jsf2pr","jsf2pr"));

		suite.addTestSuite(ValidationExceptionTest.class); // This test should be added last!

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