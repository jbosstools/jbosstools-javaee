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
package org.jboss.tools.cdi.core.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.extensions.TestSetup;
import junit.framework.Test;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 * @author Viacheslav Kabanovich
 */
public class DependentProjectsTestSetup extends TestSetup {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";
	IProject project1 = null;
	IProject project2 = null;
	IProject project3 = null;

	public DependentProjectsTestSetup(Test test) {
		super(test);
	}

	@Override
	protected void setUp() throws Exception {
		project1 = getTestProject("/projects/CDITest1", "CDITest1");
		project2 = getTestProject("/projects/CDITest2", "CDITest2");
		project3 = getTestProject("/projects/CDITest3", "CDITest3");
	}

	private static IProject getTestProject(String projectPath, String projectName) throws IOException, CoreException, InvocationTargetException, InterruptedException {
		IProject project = ResourcesUtils.importProject(PLUGIN_ID, projectPath);
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		return project;
	}

	@Override
	public void tearDown() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		project1.delete(true, true, null);
		project2.delete(true, true, null);
		project3.delete(true, true, null);
		JobUtils.waitForIdle();
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}
}