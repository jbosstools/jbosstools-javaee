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
package org.jboss.tools.seam.ui.test.view;

import junit.extensions.TestSetup;
import junit.framework.Test;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.jst.web.ui.WebDevelopmentPerspectiveFactory;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.test.util.WorkbenchUtils;

/**
 * @author eskimo
 *
 */
public class SeamComponentsViewTestSetup extends TestSetup {

	private IProject project;

	/**
	 * @param test
	 */
	public SeamComponentsViewTestSetup(Test test) {
		super(test);
	}

	/* (non-Javadoc)
	 * @see junit.extensions.TestSetup#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		//ScopePresentationActionProvider.setPackageStructureFlat(false);
		WorkbenchUtils.getWorkbench().showPerspective(
				WebDevelopmentPerspectiveFactory.PERSPECTIVE_ID,
				WorkbenchUtils.getWorkbench().getActiveWorkbenchWindow());
		JobUtils.waitForIdle();
		project = ResourcesUtils.importProject(Platform.getBundle("org.jboss.tools.seam.ui.test"), "/projects/TestComponentView", new NullProgressMonitor());
		JobUtils.waitForIdle();
	}

	/* (non-Javadoc)
	 * @see junit.extensions.TestSetup#tearDown()
	 */
	protected void tearDown() throws Exception {
		JobUtils.waitForIdle();;
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		try {
			project.delete(true,true, null);
		} finally {
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		}
	}

}
