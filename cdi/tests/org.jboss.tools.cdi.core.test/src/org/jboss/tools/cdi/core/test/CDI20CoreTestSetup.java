/******************************************************************************* 
 * Copyright (c) 2017 Red Hat, Inc. 
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

import junit.framework.Test;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.jboss.tools.cdi.core.test.tck20.TCK20ProjectNameProvider;
import org.jboss.tools.cdi.core.test.tck20.TCK20Test;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;


public class CDI20CoreTestSetup extends CDICoreTestSetup {
	static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";

	/**
	 * @param test
	 */
	public CDI20CoreTestSetup(Test test) {
		super(test);
	}

	@Override
	protected void setUp() throws Exception {
		TCK20Test test = new TCK20Test();
		projects = test.importPreparedProjects();
		tckProject = projects[1];
		
		updateTCK20Project();
	}

	void updateTCK20Project() throws IOException, CoreException, InvocationTargetException, InterruptedException {
		ImportOperation importOp = TemplateMerger.createImportOperation(
				tckProject.getFullPath(), 
				Platform.getBundle(PLUGIN_ID), 
				new TCK20ProjectNameProvider().getMainProjectPath(), 
				new TCK20ProjectNameProvider().getMainProjectPath());
		updateProject(tckProject, importOp);
	}

	public static void updateProject(IProject project, ImportOperation importOp) 
			throws CoreException, InvocationTargetException, InterruptedException {
		boolean state = ResourcesUtils.setBuildAutomatically(false);

		importOp.run(null);

		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		JobUtils.waitForIdle(200);
		TestUtil._waitForValidation(project);

		ResourcesUtils.setBuildAutomatically(state);
	}

}