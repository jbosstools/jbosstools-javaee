/*************************************************************************************
 * Copyright (c) 2013 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.tools.cdi.ui.test.marker;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationErrorManager;
import org.jboss.tools.cdi.ui.marker.CreateBeansXMLMarkerResolution;
import org.jboss.tools.common.base.test.MarkerResolutionTestUtil;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.test.util.ResourcesUtils;


public class CreateBeansXMLMarkerResolutionTest extends TestCase {
	IProject missingBeansXmlChildProject;
	boolean saveAutoBuild;
	
	public void setUp() throws Exception {
		missingBeansXmlChildProject = ResourcesPlugin.getWorkspace().getRoot().getProject("missingBeansXmlChildProject");
		assertEquals("Can't load missingBeansXmlChildProject", true, missingBeansXmlChildProject.exists());
		saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		TestUtil._waitForValidation(missingBeansXmlChildProject);
	}

	public void tearDown() throws CoreException {
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		IFile file = missingBeansXmlChildProject.getFile("src/META-INF/beans.xml");
		if(file.exists()){
			file.delete(true, new NullProgressMonitor());
		}
	}
	
	public void testCreateBeansXMLMarkerResolutionTest2() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(missingBeansXmlChildProject, 
				CDIMarkerResolutionTest.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_BEANS_XML_ID,
				CreateBeansXMLMarkerResolution.class);
	}
}
