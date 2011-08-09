/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.seam.config.core.test;

import junit.extensions.TestSetup;
import junit.framework.Test;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamConfigTestSetup extends TestSetup {

	protected IProject project;

	public SeamConfigTestSetup(Test test) {
		super(test);
	}

	@Override
	protected void setUp() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(SeamConfigTest.PROJECT_NAME);
		if(project == null || !project.exists()) {
			project = ResourcesUtils.importProject(SeamConfigTest.PLUGIN_ID, SeamConfigTest.PROJECT_PATH);
			project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		}
	}

	@Override
	protected void tearDown() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		project.delete(true, true, null);
		JobUtils.waitForIdle();
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}
}