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
package org.jboss.tools.cdi.seam.core.test;

import junit.extensions.TestSetup;
import junit.framework.Test;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 * @author Alexey Kazakov
 */
public class SeamCoreTestSetup extends TestSetup {

	public static final String PLUGIN_ID = "org.jboss.tools.cdi.seam.core.test";
	public static final String PROJECT_NAME = "SeamCoreTest";
	public static final String PROJECT_PATH = "/projects/SeamCoreTest";
	public static final String ROOT_PROJECT_NAME = "SeamCoreRootTest";
	public static final String ROOT_PROJECT_PATH = "/projects/SeamCoreRootTest";

	protected IProject project;
	protected IProject rootProject;

	public SeamCoreTestSetup(Test test) {
		super(test);
	}

	@Override
	protected void setUp() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
		if(!project.exists()) {
			project = ResourcesUtils.importProject(PLUGIN_ID, PROJECT_PATH);
			TestUtil._waitForValidation(project);
			rootProject = ResourcesUtils.importProject(PLUGIN_ID, ROOT_PROJECT_PATH);
			TestUtil._waitForValidation(rootProject);
		} else {
			assertTrue(ResourcesPlugin.getWorkspace().getRoot().getProject(ROOT_PROJECT_NAME).exists());
		}
	}

	@Override
	protected void tearDown() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		project.delete(true, true, null);
		rootProject.delete(true, true, null);
		JobUtils.waitForIdle();
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}
}