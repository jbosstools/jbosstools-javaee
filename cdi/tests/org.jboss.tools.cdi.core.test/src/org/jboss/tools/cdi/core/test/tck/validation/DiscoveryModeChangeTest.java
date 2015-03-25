/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.core.test.tck.validation;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.tests.AbstractResourceMarkerTest;
import org.osgi.framework.Bundle;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class DiscoveryModeChangeTest extends TestCase {
	protected final static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";

	public final static String JAVA_SOURCE_SUFFIX = "/src";

	protected IProject testProject;
	protected IProject rootProject;
	protected ICDIProject cdiTestProject;
	protected ICDIProject cdiRootProject;

	public DiscoveryModeChangeTest() {
		testProject = getTestProject();
		rootProject = ResourcesPlugin.getWorkspace().getRoot().getProject(getRootProjectName());
		cdiTestProject = CDICorePlugin.getCDIProject(testProject, false);
		cdiRootProject = CDICorePlugin.getCDIProject(rootProject, false);
	}

	protected String getRootProjectName() {
		return "DiscoveryModeChangeChild";
	}

	protected String getTestProjectName() {
		return "DiscoveryModeChangeTest";
	}

	protected String[] getProjectNames() {
		return new String[]{getTestProjectName(), getRootProjectName()};
	}

	protected String[] getProjectPaths() {
		String prefix = "/projects/";
		return new String[]{prefix + getTestProjectName(), prefix + getRootProjectName()};
	}

	protected void setUp() throws Exception {
		cdiTestProject = CDICorePlugin.getCDIProject(testProject, false);
		cdiRootProject = CDICorePlugin.getCDIProject(rootProject, false);
	}

	protected int getVersionIndex() {
		return cdiTestProject.getVersion().getIndex();
	}

	protected void deleteTestProject() throws Exception {
		rootProject.delete(true, true, null);
		testProject.delete(true, true, null);
	}

	public IProject getTestProject() {
		if(testProject == null) {
			try {
				testProject = findTestProject();
				if(!testProject.exists()) {
					testProject = importPreparedProject();
				}
			} catch (Exception e) {
				e.printStackTrace();
				fail("Can't import CDI test project: " + e.getMessage());
			}
		}
		return testProject;
	}

	protected IParametedType getType(String name) throws JavaModelException {
		IType type = EclipseJavaUtil.findType(EclipseUtil.getJavaProject(cdiRootProject.getNature().getProject()), name);
		return type == null ? null : cdiRootProject.getNature().getTypeFactory().newParametedType(type);
	}

	public IProject findTestProject() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(getTestProjectName());
	}

	public IProject[] importPreparedProjects() throws Exception {
		List<IProject> projects = new ArrayList<IProject>();
		importPreparedProject();
		for (String name : getProjectNames()) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
			assertTrue(project.exists());
			projects.add(project);
		}
		return projects.toArray(new IProject[projects.size()]);		
	}

	public IProject importPreparedProject() throws Exception {
		Bundle b = Platform.getBundle(PLUGIN_ID);
		IProject tckP = ResourcesPlugin.getWorkspace().getRoot().getProject(getTestProjectName());
		if(!tckP.exists()) {
			for (String name : getProjectNames()) {
				IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
				assertFalse("Error during importing TCK Project. Project " + p.getName() + " already exists.", p.exists());
			}
			for (String path : getProjectPaths()) {
				IProject project = ResourcesUtils.importProject(b, path);
				project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
			}
		}
		tckP.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		TestUtil._waitForValidation(tckP);
		return tckP;
	}

	public void testModeChange() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);

		try {
			IFile beansxml = testProject.getFile("src/META-INF/beans.xml");
			assertTrue(beansxml.exists());
		
			IFile zoo = rootProject.getFile("src/beans/Zoo.java");
			assertTrue(zoo.exists());

			IFile wilderness = testProject.getFile("src/beans/Wilderness.java");
			assertTrue(wilderness.exists());

			AbstractResourceMarkerTest.assertMarkerIsNotCreated(zoo, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 9);
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(zoo, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[getVersionIndex()], 9);
		
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(wilderness, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 9);
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(wilderness, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[getVersionIndex()], 9);
		
			IFile beansxml_all = testProject.getFile("src/META-INF/beans.xml.all");
			assertTrue(beansxml_all.exists());
			beansxml.setContents(beansxml_all.getContents(), IFile.FORCE, new NullProgressMonitor());
			JobUtils.waitForIdle();
			TestUtil.validate(beansxml);
			AbstractResourceMarkerTest.assertMarkerIsCreated(zoo, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 9);
			AbstractResourceMarkerTest.assertMarkerIsCreated(wilderness, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 9);
		
			IFile beansxml_none = testProject.getFile("src/META-INF/beans.xml.none");
			assertTrue(beansxml_none.exists());
			beansxml.setContents(beansxml_none.getContents(), IFile.FORCE, new NullProgressMonitor());
			JobUtils.waitForIdle();
			TestUtil.validate(beansxml);
			AbstractResourceMarkerTest.assertMarkerIsCreated(zoo, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[getVersionIndex()], 9);
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(wilderness, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 9);
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(wilderness, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[getVersionIndex()], 9);
		
			IFile beansxml_original = testProject.getFile("src/META-INF/beans.xml.original");
			assertTrue(beansxml_original.exists());
			beansxml.setContents(beansxml_original.getContents(), IFile.FORCE, new NullProgressMonitor());
			JobUtils.waitForIdle();
			TestUtil.validate(beansxml);
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(zoo, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 9);
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(zoo, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[getVersionIndex()], 9);
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(wilderness, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 9);
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(wilderness, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[getVersionIndex()], 9);
		} finally {
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		}
	}
}