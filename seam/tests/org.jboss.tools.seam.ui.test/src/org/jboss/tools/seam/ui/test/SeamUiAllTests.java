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

package org.jboss.tools.seam.ui.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jboss.tools.seam.core.test.refactoring.SeamPropertyRefactoringTest;
import org.jboss.tools.seam.ui.test.ca.SeamELContentAssistJbide1645Test;
import org.jboss.tools.seam.ui.test.ca.SeamELContentAssistJbide1676Test;
import org.jboss.tools.seam.ui.test.ca.SeamELContentAssistTest;
import org.jboss.tools.seam.ui.test.hyperlink.SeamViewHyperlinkPartitionerTest;
import org.jboss.tools.seam.ui.test.preferences.SeamPreferencesPageTest;
import org.jboss.tools.seam.ui.test.preferences.SeamSettingsPreferencesPageTest;
import org.jboss.tools.seam.ui.test.view.SeamComponentsViewAllTests;
import org.jboss.tools.seam.ui.test.wizard.OpenSeamComponentDialogTest;
import org.jboss.tools.seam.ui.test.wizard.Seam12EARNewOperationTest;
import org.jboss.tools.seam.ui.test.wizard.Seam12WARNewOperationTest;
import org.jboss.tools.seam.ui.test.wizard.Seam20EARNewOperationTest;
import org.jboss.tools.seam.ui.test.wizard.Seam20WARNewOperationTest;
import org.jboss.tools.seam.ui.test.wizard.SeamFormNewWizardTest;
import org.jboss.tools.seam.ui.test.wizard.SeamProjectNewWizardTest;
import org.jboss.tools.test.util.ProjectImportTestSetup;

/**
 * @author eskimo
 *
 */
public class SeamUiAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Seam UI tests");

		suite.addTestSuite(OpenSeamComponentDialogTest.class);
		suite.addTest(SeamComponentsViewAllTests.suite());
		suite.addTestSuite(SeamProjectNewWizardTest.class);
		suite.addTestSuite(SeamFormNewWizardTest.class);
		suite.addTestSuite(SeamPreferencesPageTest.class);		
		suite.addTestSuite(SeamViewHyperlinkPartitionerTest.class);
		suite.addTestSuite(SeamELContentAssistTest.class);
		suite.addTestSuite(SeamELContentAssistJbide1676Test.class);
		suite.addTestSuite(SeamELContentAssistJbide1645Test.class);
		suite.addTest(new ProjectImportTestSetup(new TestSuite(SeamSettingsPreferencesPageTest.class), "org.jboss.tools.seam.ui.test", "projects/TestSeamSettingsPreferencesPage", "TestSeamSettingsPreferencesPage"));

		suite.addTest(new ProjectImportTestSetup(new TestSuite(Seam12EARNewOperationTest.class),
					"org.jboss.tools.seam.ui.test",
					new String[]{"projects/seam_ear", "projects/seam_ear-ejb", "projects/seam_ear-test"},
					new String[]{"seam_ear", "seam_ear-ejb", "seam_ear-test"}));
		suite.addTest(new ProjectImportTestSetup(new TestSuite(Seam12WARNewOperationTest.class),
				"org.jboss.tools.seam.ui.test",
				new String[]{"projects/seam_war", "projects/seam_war-test"},
				new String[]{"seam_war", "seam_war-test"}));
		suite.addTest(new ProjectImportTestSetup(new TestSuite(Seam20EARNewOperationTest.class),
				"org.jboss.tools.seam.ui.test",
				new String[]{"projects/seam_ear", "projects/seam_ear-ejb", "projects/seam_ear-test"},
				new String[]{"seam_ear", "seam_ear-ejb", "seam_ear-test"}));
		suite.addTest(new ProjectImportTestSetup(new TestSuite(Seam20WARNewOperationTest.class),
				"org.jboss.tools.seam.ui.test",
				new String[]{"projects/seam_war", "projects/seam_war-test"},
				new String[]{"seam_war", "seam_war-test"}));

		return suite;
	}
}