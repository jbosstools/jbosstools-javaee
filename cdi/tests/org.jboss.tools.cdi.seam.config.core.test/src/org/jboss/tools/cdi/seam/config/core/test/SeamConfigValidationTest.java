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

import org.eclipse.core.internal.preferences.EclipsePreferences;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.seam.config.core.CDISeamConfigPreferences;
import org.jboss.tools.cdi.seam.config.core.validation.SeamConfigValidationMessages;
import org.jboss.tools.common.preferences.SeverityPreferences;
import org.jboss.tools.test.util.JobUtils;
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
		cdiProject = CDICorePlugin.getCDIProject(project, true);
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

	/**
	 * Check that marker disappears when preference is set to IGNORE and appears again
	 * when preference is set back to WARNING. Check that marker for another preference 
	 * is always present.
	 * 
	 * @throws CoreException
	 */
	public void testPreference() throws CoreException {
		String pattern1 = MessageFormat.format(SeamConfigValidationMessages.UNRESOLVED_METHOD, "v:method1");
		String pattern2 = MessageFormat.format(SeamConfigValidationMessages.UNRESOLVED_MEMBER, "v:method2");
		AbstractResourceMarkerTest.assertMarkerIsCreated(f, pattern1, 47);
		AbstractResourceMarkerTest.assertMarkerIsCreated(f, pattern2, 38);

		EclipsePreferences ps = (EclipsePreferences)CDISeamConfigPreferences.getInstance().getDefaultPreferences();
		ps.put(CDISeamConfigPreferences.UNRESOLVED_MEMBER, SeverityPreferences.IGNORE);
		rebuild();
		AbstractResourceMarkerTest.assertMarkerIsCreated(f, pattern1, 47);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(f, pattern2, 38);
		
		ps.put(CDISeamConfigPreferences.UNRESOLVED_MEMBER, SeverityPreferences.WARNING);
		rebuild();
		AbstractResourceMarkerTest.assertMarkerIsCreated(f, pattern1, 47);
		AbstractResourceMarkerTest.assertMarkerIsCreated(f, pattern2, 38);
	}

	void rebuild() throws CoreException {
		project.build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
		JobUtils.waitForIdle();
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		JobUtils.waitForIdle();
	}

}