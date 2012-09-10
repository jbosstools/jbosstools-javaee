/******************************************************************************* 
 * Copyright (c) 2009-2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.ui.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.cdi.core.test.CDICoreTestSetup;
import org.jboss.tools.cdi.ui.test.marker.CDIMarkerResolutionTest;
import org.jboss.tools.cdi.ui.test.marker.CDIQuickFixTest;
import org.jboss.tools.cdi.ui.test.perspective.CDIPerspectiveTest;
import org.jboss.tools.cdi.ui.test.preferences.CDIPreferencePageTest;
import org.jboss.tools.cdi.ui.test.search.CDISearchParticipantTest;
import org.jboss.tools.cdi.ui.test.search.ELReferencesQueryParticipantTest;
import org.jboss.tools.cdi.ui.test.search.FiveDependentProjectsSearchParticipantTest;
import org.jboss.tools.cdi.ui.test.search.FiveDependentProjectsTestSetup;
import org.jboss.tools.cdi.ui.test.validation.java.CDIAsYouTypeCDIAndELValidatorsMassagesProcessingTest;
import org.jboss.tools.cdi.ui.test.validation.java.CDIAsYouTypeInJavaSupressWarningsTest;
import org.jboss.tools.cdi.ui.test.validation.java.CDIAsYouTypeInJavaValidationTest;
import org.jboss.tools.cdi.ui.test.wizard.AddQualifiersToBeanWizardTest;
import org.jboss.tools.cdi.ui.test.wizard.NewCDIClassWizardFactoryTest;
import org.jboss.tools.cdi.ui.test.wizard.NewCDIWebProjectWizardTest;
import org.jboss.tools.cdi.ui.test.wizard.NewCDIWizardTest;
import org.jboss.tools.cdi.ui.test.wizard.OpenCDINamedBeanDialogTest;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 * @author Alexey Kazakov
 */
public class CDIUIAllTests {

	public static Test suite() {
		// it could be done here because it is not needed to be enabled back
		JavaModelManager.getIndexManager().shutdown();
		try {
			ResourcesUtils.setBuildAutomatically(false);
			ValidationFramework.getDefault().suspendAllValidation(true);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		TestSuite suiteAll = new TestSuite("CDI UI Tests");
		TestSuite suite = new TestSuite("TCK Tests");

		suiteAll.addTestSuite(CDIMarkerResolutionTest.class);
		
		suiteAll.addTestSuite(CDIQuickFixTest.class);

		suiteAll.addTestSuite(CDIPerspectiveTest.class);
		suiteAll.addTestSuite(NewCDIClassWizardFactoryTest.class);
		suiteAll.addTestSuite(CDIPreferencePageTest.class);
		suiteAll.addTestSuite(NewCDIWizardTest.class);
		suiteAll.addTestSuite(NewCDIWebProjectWizardTest.class);

		suite.addTestSuite(CAELProposalFilteringTest.class);
		suite.addTestSuite(CDISearchParticipantTest.class);
		suite.addTestSuite(ELReferencesQueryParticipantTest.class);
		suite.addTestSuite(CATest.class);
		suite.addTestSuite(OpenCDINamedBeanDialogTest.class);
		suite.addTestSuite(CDIAsYouTypeInJavaValidationTest.class); 
		suite.addTestSuite(CDIAsYouTypeInJavaSupressWarningsTest.class);
		suite.addTestSuite(CDIAsYouTypeCDIAndELValidatorsMassagesProcessingTest.class);

		suiteAll.addTest(new CDICoreTestSetup(suite));

		suiteAll.addTestSuite(AddQualifiersToBeanWizardTest.class);
		suiteAll.addTestSuite(CDIRefactoringTest.class);

		TestSuite dependentSuite = new TestSuite("Dependent Projects Tests");
		dependentSuite.addTestSuite(FiveDependentProjectsSearchParticipantTest.class);
		FiveDependentProjectsTestSetup dependent = new FiveDependentProjectsTestSetup(dependentSuite);
		suiteAll.addTest(dependent);

		return suiteAll;
	}
}