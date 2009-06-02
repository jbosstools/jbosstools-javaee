/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.seam.ui.test.ca;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;

/**
 * 
 * @author Victor Rubezhny
 *
 */
public class SeamELContentAssistTest {
	private static final String PROJECT_NAME = "TestSeamELContentAssist";
	private static final String PAGE_NAME = "/WebContent/login.xhtml";
	
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(new ProjectImportTestSetup(new TestSuite(SeamELContentAssistTestCase.class),
				"org.jboss.tools.seam.ui.test", "projects/" + PROJECT_NAME, PROJECT_NAME) {
			public void setUp() throws Exception {
				super.setUp();
				IProject project = (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember(PROJECT_NAME);
				project.refreshLocal(IResource.DEPTH_INFINITE, null);
				JobUtils.waitForIdle();
				//To ensure that the project is built.
				project.build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
				JobUtils.waitForIdle();
			}	
		} );
		return suite;
	}

}
