/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
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

import org.jboss.tools.jsf.jsp.ca.test.CAForCompositeComponentTest;
import org.jboss.tools.jsf.jsp.ca.test.CAForELinStyleTest;
import org.jboss.tools.jsf.jsp.ca.test.CAForIDTest;
import org.jboss.tools.jsf.jsp.ca.test.CAForInputTagSrcAttributeSuggestsFilePathsJBIDE1807Test;
import org.jboss.tools.jsf.jsp.ca.test.CAForUnclosedELTest;
import org.jboss.tools.jsf.jsp.ca.test.CANotEmptyWhenThereIsNoSpaceBetweenInvertedCommandsInAttributeJBIDE1759Test;
import org.jboss.tools.jsf.jsp.ca.test.CASuggestsNotOnlyELProposalsJBIDE2437Test;
import org.jboss.tools.jsf.jsp.ca.test.CAUnnecessaryElementsForDirAttributeInXHTMLPageJBIDE1813Test;
import org.jboss.tools.jsf.jsp.ca.test.CAVarAttributeForDataTableTagJBIDE2016;
import org.jboss.tools.jsf.jsp.ca.test.MissingKBBuilderTest;
import org.jboss.tools.jsf.ui.test.refactoring.ELReferencesRenameTest;
import org.jboss.tools.test.util.ProjectImportTestSetup;

public class JsfUiAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("JSF UI tests"); //$NON-NLS-1$
		suite.addTestSuite(CAForUnclosedELTest.class);
		suite.addTestSuite(CAForCompositeComponentTest.class);
//		suite.addTestSuite(MissingKBBuilderTest.class);
		suite.addTestSuite(CAForInputTagSrcAttributeSuggestsFilePathsJBIDE1807Test.class);
		suite.addTestSuite(CAForIDTest.class);
		suite.addTestSuite(CAForELinStyleTest.class);
		suite.addTestSuite(CANotEmptyWhenThereIsNoSpaceBetweenInvertedCommandsInAttributeJBIDE1759Test.class);
		suite.addTestSuite(CASuggestsNotOnlyELProposalsJBIDE2437Test.class);
		suite.addTestSuite(CAUnnecessaryElementsForDirAttributeInXHTMLPageJBIDE1813Test.class);
		suite.addTestSuite(CAVarAttributeForDataTableTagJBIDE2016.class);
		suite.addTestSuite(CssClassNewWizardTest.class);
		suite.addTestSuite(CSSStyleDialogTest.class);
		suite.addTestSuite(FacesConfigNewWizardTest.class);
		suite.addTestSuite(FasesConfigEditorTest.class);
		suite.addTestSuite(PropertiesNewWizardTest.class);
		suite.addTestSuite(JsfUiPreferencesPagesTest.class);
		suite.addTest(new ProjectImportTestSetup(new TestSuite(
				ELReferencesRenameTest.class), "org.jboss.tools.jsf.ui.test", //$NON-NLS-1$
				new String[] { "projects/testJSFProject", }, //$NON-NLS-1$
				new String[] { "testJSFProject" })); //$NON-NLS-1$
//		suite.addTest(new ProjectImportTestSetup(new TestSuite(
//				JSFNaturesInfoDialog_JBIDE5701.class),
//				"org.jboss.tools.jsf.ui.test", "projects/naturesCheckTest", //$NON-NLS-1$ //$NON-NLS-2$
//				"naturesCheckTest")); //$NON-NLS-1$
//		suite.addTest(new ProjectImportTestSetup(new TestSuite(
//				KbNaturesInfoDialog_JBIDE6125.class),
//				"org.jboss.tools.jsf.ui.test", "projects/naturesCheckKBTest", //$NON-NLS-1$ //$NON-NLS-2$
//				"naturesCheckKBTest")); //$NON-NLS-1$
		return new TestWizardsProject(suite);
	}
}