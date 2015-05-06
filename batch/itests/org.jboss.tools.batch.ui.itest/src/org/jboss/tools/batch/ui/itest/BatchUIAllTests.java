/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 * Tomas Milata - Added Batch diagram editor (JBIDE-19717).
 ******************************************************************************/
package org.jboss.tools.batch.ui.itest;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.batch.ui.itest.ca.ClassAttributeContentAssist;
import org.jboss.tools.batch.ui.itest.ca.JobArtifactRefContentAssist;
import org.jboss.tools.batch.ui.itest.ca.JobPropertyNameContentAssist;
import org.jboss.tools.batch.ui.itest.ca.JobTransitionsContentAssist;
import org.jboss.tools.batch.ui.itest.ca.sapphire.ContentProposalTest;
import org.jboss.tools.batch.ui.itest.diagram.BatchDiagramConnectionsTest;
import org.jboss.tools.batch.ui.itest.diagram.BatchDiagramNavigationTest;
import org.jboss.tools.test.util.ProjectImportTestSetup;
import org.jboss.tools.test.util.ResourcesUtils;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Viacheslav Kabanovich
 */
public class BatchUIAllTests {

	public static Test suite() {
		// We need the index manager enabled for <* class=""> content assist
		// JavaModelManager.getIndexManager().shutdown();
		try {
			ResourcesUtils.setBuildAutomatically(false);
			ValidationFramework.getDefault().suspendAllValidation(true);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		TestSuite suiteAll = new TestSuite("Batch UI Tests");

		TestSuite suite = new TestSuite("Editor");
		suite.addTestSuite(BatchEditorTest.class);
		suite.addTestSuite(BatchHyperlinkDetectorTest.class);
		suite.addTestSuite(BatchELHyperlinkTest.class);
		suite.addTestSuite(BatchQueryParticipantTest.class);
		suite.addTestSuite(BatchRenameParticipantTest.class);

		suiteAll.addTest(suite);

		TestSuite suite1 = new TestSuite("Content assist");
		suite1.addTestSuite(JobTransitionsContentAssist.class);
		suite1.addTestSuite(JobArtifactRefContentAssist.class);
		suite1.addTestSuite(JobPropertyNameContentAssist.class);
		suite1.addTestSuite(ClassAttributeContentAssist.class);
		suite1.addTestSuite(ContentProposalTest.class);
		suiteAll.addTest(suite1);

		TestSuite suite2 = new TestSuite("Wizards");
		suite2.addTestSuite(NewBatchWizardTest.class);

		suiteAll.addTest(suite2);

		TestSuite diagramSuite = new TestSuite("Diagram editor");
		diagramSuite.addTestSuite(BatchDiagramNavigationTest.class);
		diagramSuite.addTestSuite(BatchDiagramConnectionsTest.class);
		suiteAll.addTest(diagramSuite);

		ProjectImportTestSetup testSetup = new ProjectImportTestSetup(suiteAll, "org.jboss.tools.batch.core.itest",
				new String[] { "projects/BatchTestProject" }, new String[] { "BatchTestProject" });

		// suiteAll.addTest(testSetup);

		// testSetup = new ProjectImportTestSetup(suite,
		// "org.jboss.tools.batch.core.itest",
		// new String[]{"projects/BatchTestProject"},
		// new String[]{"TestProject"});
		// suiteAll.addTest(testSetup);

		return testSetup;
	}
}