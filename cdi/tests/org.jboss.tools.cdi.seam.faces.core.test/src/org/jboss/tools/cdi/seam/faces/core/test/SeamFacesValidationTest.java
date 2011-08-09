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
package org.jboss.tools.cdi.seam.faces.core.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.jst.jsp.test.TestUtil;
import org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
 */
public class SeamFacesValidationTest extends TestCase {

	protected IProject project;

	public IProject getTestProject() throws IOException, CoreException, InvocationTargetException, InterruptedException {
		if(project==null) {
			ValidatorManager.setStatus("INIT");
			project = ResourcesPlugin.getWorkspace().getRoot().getProject(SeamFacesTestSetup.PROJECT_NAME);
			if(!project.exists()) {
				project = ResourcesUtils.importProject(SeamFacesTestSetup.PLUGIN_ID, SeamFacesTestSetup.PROJECT_PATH);
				TestUtil._waitForValidation(project);
			}
			TestUtil.waitForValidation();
		}
		return project;
	}

	public void testInjectionAnnotatedInputField() throws Exception {
		IFile file = getTestProject().getFile("src/org/jboss/beans/validation/test/Validation.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 18, 23);
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 20, 28);

		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 9);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 11);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 14);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 14);
	}

	public void testInjectionWInputElemnt() throws Exception {
		IFile file = getTestProject().getFile("src/org/jboss/beans/validation/test/Validation2.java");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 9);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 11);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 14);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 14);
	}
}