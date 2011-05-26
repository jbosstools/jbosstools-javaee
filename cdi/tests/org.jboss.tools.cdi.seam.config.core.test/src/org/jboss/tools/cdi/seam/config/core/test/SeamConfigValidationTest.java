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

import java.text.MessageFormat;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.seam.config.core.validation.SeamConfigValidationMessages;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class SeamConfigValidationTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.seam.config.core.test";
	protected static String PROJECT_NAME = "CDIConfigValidationTest";
	protected static String PROJECT_PATH = "/projects/CDIConfigValidationTest";

	protected IProject project;
	protected ICDIProject cdiProject;
	IFile f;

	public SeamConfigValidationTest() {
		project = getTestProject();
		cdiProject = CDICorePlugin.getCDIProject(project, false);
		f = project.getFile("src/META-INF/beans.xml");
		assertTrue(f.exists());
	}

	public IProject getTestProject() {
		if(project==null) {
			try {
				project = findTestProject();
				if(project==null || !project.exists()) {
					project = ResourcesUtils.importProject(PLUGIN_ID, PROJECT_PATH);
				}
			} catch (Exception e) {
				e.printStackTrace();
				fail("Can't import CDI test project: " + e.getMessage());
			}
		}
		return project;
	}

	public static IProject findTestProject() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
	}

	public void testBeanResolution() throws CoreException {
		AbstractResourceMarkerTest.assertMarkerIsCreated(f, MessageFormat.format(SeamConfigValidationMessages.UNRESOLVED_TYPE, "v:MyBean2"), 8);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(f, MessageFormat.format(SeamConfigValidationMessages.UNRESOLVED_TYPE, "v:MyBean1"));
	}

	public void testFieldResolution() throws CoreException {
		AbstractResourceMarkerTest.assertMarkerIsCreated(f, MessageFormat.format(SeamConfigValidationMessages.UNRESOLVED_MEMBER, "param"), 21);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(f, MessageFormat.format(SeamConfigValidationMessages.UNRESOLVED_MEMBER, "value"), 28);
	}

	public void testMethodResolution() throws CoreException {
		//It is unresolved member because no member with that name is found. 
		AbstractResourceMarkerTest.assertMarkerIsCreated(f, MessageFormat.format(SeamConfigValidationMessages.UNRESOLVED_MEMBER, "v:method2"), 38);
		
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(f, MessageFormat.format(SeamConfigValidationMessages.UNRESOLVED_MEMBER, "v:method1"), 34);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(f, MessageFormat.format(SeamConfigValidationMessages.UNRESOLVED_METHOD, "v:method1"), 34);

		AbstractResourceMarkerTest.assertMarkerIsNotCreated(f, MessageFormat.format(SeamConfigValidationMessages.UNRESOLVED_MEMBER, "v:method1"), 42);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(f, MessageFormat.format(SeamConfigValidationMessages.UNRESOLVED_METHOD, "v:method1"), 42);

		AbstractResourceMarkerTest.assertMarkerIsCreated(f, MessageFormat.format(SeamConfigValidationMessages.UNRESOLVED_METHOD, "v:method1"), 47);
	}

	public void testAnnotationMemberResolution() throws CoreException {
		AbstractResourceMarkerTest.assertMarkerIsCreated(f, MessageFormat.format(SeamConfigValidationMessages.UNRESOLVED_MEMBER, "v:field3"), 15);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(f, MessageFormat.format(SeamConfigValidationMessages.UNRESOLVED_MEMBER, "v:field1"));
	}
}