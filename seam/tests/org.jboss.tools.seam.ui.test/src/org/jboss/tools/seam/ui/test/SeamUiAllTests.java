/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.ui.test.ca.SeamELContentAssistJbide1645Test;
import org.jboss.tools.seam.ui.test.ca.SeamELContentAssistJbide1676Test;
import org.jboss.tools.seam.ui.test.ca.SeamELContentAssistTest;
import org.jboss.tools.seam.ui.test.ca.SeamJbide3845Test;
import org.jboss.tools.seam.ui.test.el.ELExprPartitionerTest;
import org.jboss.tools.seam.ui.test.hyperlink.SeamViewHyperlinkPartitionerTest;
import org.jboss.tools.seam.ui.test.jbide.JBide3989Test;
import org.jboss.tools.seam.ui.test.marker.SeamMarkerResolutionTest;
import org.jboss.tools.seam.ui.test.preferences.SeamPreferencesPageTest;
import org.jboss.tools.seam.ui.test.preferences.SeamSettingsPreferencesPageTest;
import org.jboss.tools.seam.ui.test.view.SeamComponentsViewAllTests;
import org.jboss.tools.seam.ui.test.wizard.OpenSeamComponentDialogTest;
import org.jboss.tools.seam.ui.test.wizard.PackageNamesTest;
import org.jboss.tools.seam.ui.test.wizard.Seam20XCreateTestProjectTest;
import org.jboss.tools.seam.ui.test.wizard.Seam20XProjectNamesTest;
import org.jboss.tools.seam.ui.test.wizard.SeamActionNewWizardTest;
import org.jboss.tools.seam.ui.test.wizard.SeamCreateTestProjectTest;
import org.jboss.tools.seam.ui.test.wizard.SeamFormNewWizardTest;
import org.jboss.tools.seam.ui.test.wizard.SeamProjectNamesTest;
import org.jboss.tools.seam.ui.test.wizard.SeamProjectNewWizardTest;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;

/**
 * @author eskimo
 *
 */
public class SeamUiAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Seam UI tests");
		
		suite.addTestSuite(ELExprPartitionerTest.class);
		suite.addTestSuite(OpenSeamComponentDialogTest.class);
		suite.addTest(SeamComponentsViewAllTests.suite());
//		suite.addTest(SeamProjectNewWizardTest.suite());

		suite.addTest(new ProjectImportTestSetup(new TestSuite(SeamActionNewWizardTest.class), "org.jboss.tools.seam.base.test", new String[]{"projects/Test1-ear", "projects/Test1-ejb", "projects/Test1"}, new String[]{"Test1-ear", "Test1-ejb", "Test1"}));

		suite.addTestSuite(SeamFormNewWizardTest.class);
		suite.addTestSuite(SeamPreferencesPageTest.class);		
		suite.addTestSuite(SeamViewHyperlinkPartitionerTest.class);
		suite.addTest(SeamELContentAssistTest.suite());
		suite.addTestSuite(SeamELContentAssistJbide1676Test.class);
		suite.addTestSuite(SeamELContentAssistJbide1645Test.class);
		suite.addTestSuite(SeamJbide3845Test.class);
//		suite.addTest(new ProjectImportTestSetup(new TestSuite(SeamSettingsPreferencesPageTest.class), "org.jboss.tools.seam.ui.test", "projects/TestSeamSettingsPreferencesPage", "TestSeamSettingsPreferencesPage"));
		suite.addTestSuite(PackageNamesTest.class);
		suite.addTestSuite(JBide3989Test.class);
		suite.addTestSuite(SeamCreateTestProjectTest.class);
		suite.addTestSuite(Seam20XCreateTestProjectTest.class);		
		suite.addTestSuite(SeamProjectNamesTest.class);
		suite.addTestSuite(Seam20XProjectNamesTest.class);
		suite.addTest(new ProjectImportTestSetup(new TestSuite(SeamMarkerResolutionTest.class),"org.jboss.tools.seam.base.test","projects/SeamWebWarTestProject","SeamWebWarTestProject") {
			@Override
			protected void setUp() throws Exception {
				super.setUp();
				IProject project = (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember("SeamWebWarTestProject");
				if (project == null)
					return;
				project.refreshLocal(IResource.DEPTH_INFINITE, null);
				JobUtils.waitForIdle();
				ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
				seamProject.setRuntimeName("UNKNOWN");
				ValidatorManager.addProjectBuildValidationSupport(project);
				project.build(IncrementalProjectBuilder.FULL_BUILD,
						new NullProgressMonitor());
				
				JobUtils.waitForIdle();
			}
		} );

		return suite;
	}
}