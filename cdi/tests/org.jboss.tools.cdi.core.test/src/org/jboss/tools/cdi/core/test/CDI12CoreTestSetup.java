/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
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
import org.jboss.tools.cdi.core.test.tck11.TCK11ProjectNameProvider;
import org.jboss.tools.cdi.core.test.tck12.TCK12ProjectNameProvider;
import org.jboss.tools.cdi.core.test.tck12.TCK12Test;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 * @author Viacheslav Kabanovich
 */
public class CDI12CoreTestSetup extends CDICoreTestSetup {
	static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";

	/**
	 * @param test
	 */
	public CDI12CoreTestSetup(Test test) {
		super(test);
	}

	@Override
	protected void setUp() throws Exception {
		TCK12Test test = new TCK12Test();
		projects = test.importPreparedProjects();
		tckProject = projects[1];
		
		updateTCK12Project();
	}

	void updateTCK12Project() throws IOException, CoreException, InvocationTargetException, InterruptedException {
		ImportOperation importOp = TemplateMerger.createImportOperation(
				tckProject.getFullPath(), 
				Platform.getBundle(PLUGIN_ID), 
				new TCK12ProjectNameProvider().getMainProjectPath(), 
				new TCK11ProjectNameProvider().getMainProjectPath());
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