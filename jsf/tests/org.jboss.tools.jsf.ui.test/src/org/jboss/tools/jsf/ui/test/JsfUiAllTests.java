/*******************************************************************************
 * Copyright (c) 2007-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.ui.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.common.base.test.validation.ValidationProjectTestSetup;
import org.jboss.tools.jsf.jsp.ca.test.CADefaultELStartingCharTest;
import org.jboss.tools.jsf.jsp.ca.test.CAELInsideTagBodyInJspFileTest;
import org.jboss.tools.jsf.jsp.ca.test.CAForCompositeComponentTest;
import org.jboss.tools.jsf.jsp.ca.test.CAForELJavaAndJSTCompareTest;
import org.jboss.tools.jsf.jsp.ca.test.CAForELinStyleTest;
import org.jboss.tools.jsf.jsp.ca.test.CAForIDTest;
import org.jboss.tools.jsf.jsp.ca.test.CAForInputTagSrcAttributeSuggestsFilePathsJBIDE1807Test;
import org.jboss.tools.jsf.jsp.ca.test.CAForJSF2BeansInJavaTest;
import org.jboss.tools.jsf.jsp.ca.test.CAForJSF2BeansTest;
import org.jboss.tools.jsf.jsp.ca.test.CAForUnclosedELTest;
import org.jboss.tools.jsf.jsp.ca.test.CAInEventAttributesTest;
import org.jboss.tools.jsf.jsp.ca.test.CAJsfAddInfoInELMessagesTest;
import org.jboss.tools.jsf.jsp.ca.test.CAJsfMessagesProposalsFilteringTest;
import org.jboss.tools.jsf.jsp.ca.test.CAJsfMessagesProposalsTest;
import org.jboss.tools.jsf.jsp.ca.test.CAJsfResourceBundlePropertyApplyTest;
import org.jboss.tools.jsf.jsp.ca.test.CANotEmptyWhenThereIsNoSpaceBetweenInvertedCommandsInAttributeJBIDE1759Test;
import org.jboss.tools.jsf.jsp.ca.test.CASuggestsNotOnlyELProposalsJBIDE2437Test;
import org.jboss.tools.jsf.jsp.ca.test.CAUnnecessaryElementsForDirAttributeInXHTMLPageJBIDE1813Test;
import org.jboss.tools.jsf.jsp.ca.test.CAVarAttributeForDataTableTagJBIDE2016;
import org.jboss.tools.jsf.jsp.ca.test.JavaClassContentAssistProviderTest;
import org.jboss.tools.jsf.jsp.ca.test.JsfJBide3845Test;
import org.jboss.tools.jsf.jsp.ca.test.JsfJspJbide1704Test;
import org.jboss.tools.jsf.jsp.ca.test.JsfJspJbide1717Test;
import org.jboss.tools.jsf.jsp.ca.test.JsfJspJbide6259Test;
import org.jboss.tools.jsf.jsp.ca.test.JsfJspLongResourceBundlePropertyNamesTest;
import org.jboss.tools.jsf.jsp.ca.test.JspElFunctionsTest;
import org.jboss.tools.jsf.jsp.ca.test.WebContentAssistProviderTest;
import org.jboss.tools.jsf.jsp.hover.ELTooltipTest;
import org.jboss.tools.jsf.ui.test.refactoring.ELReferencesRenameTest;
import org.jboss.tools.test.util.ProjectImportTestSetup;

public class JsfUiAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("JSF UI tests"); //$NON-NLS-1$

		ValidationFramework.getDefault().suspendAllValidation(true);

		suite.addTestSuite(JBossPerspectiveTest.class);
		suite.addTestSuite(NewJSFProjectTest.class);
		suite.addTestSuite(CAForUnclosedELTest.class);
		suite.addTestSuite(CAForCompositeComponentTest.class);
		suite.addTestSuite(CAForJSF2BeansTest.class);
		suite.addTestSuite(CAForJSF2BeansInJavaTest.class);
//		suite.addTestSuite(MissingKBBuilderTest.class);
		suite.addTestSuite(CAForInputTagSrcAttributeSuggestsFilePathsJBIDE1807Test.class);
		suite.addTestSuite(CAForIDTest.class);
		suite.addTestSuite(CAForELinStyleTest.class);
		suite.addTestSuite(CANotEmptyWhenThereIsNoSpaceBetweenInvertedCommandsInAttributeJBIDE1759Test.class);
		suite.addTestSuite(CASuggestsNotOnlyELProposalsJBIDE2437Test.class);
		suite.addTestSuite(CAUnnecessaryElementsForDirAttributeInXHTMLPageJBIDE1813Test.class);
		suite.addTestSuite(CAVarAttributeForDataTableTagJBIDE2016.class);
		suite.addTestSuite(FacesConfigNewWizardTest.class);
		suite.addTestSuite(FasesConfigEditorTest.class);
		suite.addTestSuite(PropertiesNewWizardTest.class);
		suite.addTestSuite(JsfJspJbide6259Test.class);
		suite.addTestSuite(JsfJspJbide1704Test.class);
 		suite.addTestSuite(JsfJspJbide1717Test.class);
		suite.addTestSuite(JsfJBide3845Test.class);
		suite.addTestSuite(CAInEventAttributesTest.class);
		
		suite.addTest(
				new ProjectImportTestSetup(JsfJspLongResourceBundlePropertyNamesTest.suite(),
				"org.jboss.tools.jsf.ui.test",
				new String[] { "projects/CAForCompositeComponentTest", }, //$NON-NLS-1$
				new String[] { "CAForCompositeComponentTest" })); //$NON-NLS-1$
		suite.addTest(
				new ProjectImportTestSetup(WebContentAssistProviderTest.suite(),
				"org.jboss.tools.jsf.ui.test",
				new String[] { "projects/TestsWebArtefacts", }, //$NON-NLS-1$
				new String[] { "TestsWebArtefacts" })); //$NON-NLS-1$
 		suite.addTest(new ProjectImportTestSetup(new TestSuite(
				ELReferencesRenameTest.class), "org.jboss.tools.jsf.ui.test", //$NON-NLS-1$
				new String[] { "projects/testJSFProject", }, //$NON-NLS-1$
				new String[] { "testJSFProject" })); //$NON-NLS-1$
		suite.addTest(new ProjectImportTestSetup(new TestSuite(
				JSFNaturesInfoDialog_JBIDE5701.class),
				"org.jboss.tools.jsf.ui.test", "projects/naturesCheckTest", //$NON-NLS-1$ //$NON-NLS-2$
				"naturesCheckTest")); //$NON-NLS-1$
		suite.addTest(new ProjectImportTestSetup(new TestSuite(
				KbNaturesInfoDialog_JBIDE6125.class),
				"org.jboss.tools.jsf.ui.test", "projects/naturesCheckKBTest", //$NON-NLS-1$ //$NON-NLS-2$
				"naturesCheckKBTest")); //$NON-NLS-1$
		suite.addTest(new ProjectImportTestSetup(new TestSuite(AddJSFCapabilitiesTest.class),
				"org.jboss.tools.jsf.ui.test",
				new String[]{"projects/test_add_jsf_capabilities"},
				new String[]{"test_add_jsf_capabilities"}));
		suite.addTest(new ValidationProjectTestSetup(new TestSuite(JSPProblemMarkerResolutionTest.class),
				"org.jboss.tools.jsf.ui.test",
				new String[]{"projects/test_jsf_project"},
				new String[]{"test_jsf_project"}));
 		suite.addTest(
				new ProjectImportTestSetup(new TestSuite(TestPalette.class),
				"org.jboss.tools.jsf.ui.test",
				new String[] { "projects/testJSFProject", }, //$NON-NLS-1$
				new String[] { "testJSFProject" })); //$NON-NLS-1$
 		suite.addTest(
				new ProjectImportTestSetup(new TestSuite(CAJsfMessagesProposalsTest.class),
				"org.jboss.tools.jsf.ui.test",
				new String[] { "projects/testJSFProject", }, //$NON-NLS-1$
				new String[] { "testJSFProject" })); //$NON-NLS-1$
 		suite.addTest(
				new ProjectImportTestSetup(new TestSuite(JavaClassContentAssistProviderTest.class),
				"org.jboss.tools.jsf.ui.test",
				new String[] { "projects/testJSFProject", }, //$NON-NLS-1$
				new String[] { "testJSFProject" })); //$NON-NLS-1$
		suite.addTestSuite(JsfUiPreferencesPagesTest.class);
		suite.addTestSuite(TaglibXMLUnformatedDTD_JBIDE5642.class);

		suite.addTest(new ProjectImportTestSetup(new TestSuite(
				JspElFunctionsTest.class), "org.jboss.tools.jsf.ui.test", //$NON-NLS-1$
				new String[] { "projects/testJSFProject", }, //$NON-NLS-1$
				new String[] { "testJSFProject" })); //$NON-NLS-1$

		suite.addTest(new ProjectImportTestSetup(new TestSuite(
				CADefaultELStartingCharTest.class), "org.jboss.tools.jsf.ui.test", //$NON-NLS-1$
				new String[] { "projects/JsfJbide1704Test", }, //$NON-NLS-1$
				new String[] { "JsfJbide1704Test" })); //$NON-NLS-1$

		suite.addTest(new ProjectImportTestSetup(new TestSuite(
				CAJsfMessagesProposalsFilteringTest.class), "org.jboss.tools.jsf.base.test", //$NON-NLS-1$
				new String[] { "projects/JSF2KickStartWithoutLibs", }, //$NON-NLS-1$
				new String[] { "JSF2KickStartWithoutLibs" })); //$NON-NLS-1$

		suite.addTest(new ProjectImportTestSetup(new TestSuite(
				ELTooltipTest.class), "org.jboss.tools.jsf.base.test", //$NON-NLS-1$
				new String[] { "projects/JSF2KickStartWithoutLibs", }, //$NON-NLS-1$
				new String[] { "JSF2KickStartWithoutLibs" })); //$NON-NLS-1$

		suite.addTest(new ProjectImportTestSetup(new TestSuite(
				CAForELJavaAndJSTCompareTest.class), "org.jboss.tools.jsf.base.test", //$NON-NLS-1$
				new String[] { "projects/JSF2KickStartWithoutLibs", }, //$NON-NLS-1$
				new String[] { "JSF2KickStartWithoutLibs" })); //$NON-NLS-1$

		suite.addTest(new ProjectImportTestSetup(new TestSuite(
				CAELInsideTagBodyInJspFileTest.class), "org.jboss.tools.jsf.ui.test", //$NON-NLS-1$
				new String[] { "projects/Jbide3845Test", }, //$NON-NLS-1$
				new String[] { "Jbide3845Test" })); //$NON-NLS-1$

		suite.addTest(new ProjectImportTestSetup(new TestSuite(
				CAJsfAddInfoInELMessagesTest.class), "org.jboss.tools.jsf.base.test", //$NON-NLS-1$
				new String[] { "projects/JSF2KickStartWithoutLibs", }, //$NON-NLS-1$
				new String[] { "JSF2KickStartWithoutLibs" })); //$NON-NLS-1$

		suite.addTest(new ProjectImportTestSetup(new TestSuite(
				CAJsfResourceBundlePropertyApplyTest.class), "org.jboss.tools.jsf.base.test", //$NON-NLS-1$
				new String[] { "projects/JSF2KickStartWithoutLibs", }, //$NON-NLS-1$
				new String[] { "JSF2KickStartWithoutLibs" })); //$NON-NLS-1$

		return suite;
	}
}