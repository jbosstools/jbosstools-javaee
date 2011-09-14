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

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 * @author Alexey Kazakov
 */
public class SeamCoreTest extends TestCase {

	protected IProject project;

	protected IProject getTestProject() throws Exception {
		if(project==null) {
			project = ResourcesPlugin.getWorkspace().getRoot().getProject(SeamCoreTestSetup.PROJECT_NAME);
			if(!project.exists()) {
				project = ResourcesUtils.importProject(SeamCoreTestSetup.PLUGIN_ID, SeamCoreTestSetup.PROJECT_PATH);
				TestUtil.waitForValidation();
			}
		}
		return project;
	}
}