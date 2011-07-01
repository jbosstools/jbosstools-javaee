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
package org.jboss.tools.jsf.test.validation;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.jst.web.kb.internal.validation.ELValidationMessages;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
  */
public class WebContentTest extends AbstractResourceMarkerTest {

	protected static String PLUGIN_ID = "org.jboss.tools.jsf.test";
	protected static String PROJECT_NAME = "jsf2pr";
	protected static String PROJECT_PATH = "/projects/jsf2pr";
	IProject project;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
		if(!project.exists()) {
			project = ResourcesUtils.importProject(PLUGIN_ID, PROJECT_PATH);
		}
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		JobUtils.waitForIdle();
		project.delete(true, true, null);
		JobUtils.waitForIdle();
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}

	public void testWebContentValidation() throws CoreException {
		IFile file = project.getFile("WebContent/inputname.xhtml");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, MessageFormat.format(ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME, "broken"), 6);
		file = project.getFile("TestWebContent/inputname.xhtml");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, MessageFormat.format(ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME, "broken"), 6);
	}
}