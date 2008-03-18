 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.core.test.refactoring;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.test.util.JUnitUtils;
import org.jboss.tools.test.util.WorkbenchUtils;
import org.jboss.tools.test.util.xpl.EditorTestHelper;

/**
 * @author Alexey Kazakov
 */
public class SeamPropertyRefactoringTest extends TestCase {
	IProject warProject;
	IProject ejbProject;
	IProject testProject;
	ISeamProject seamWarProject;
	ISeamProject seamEjbProject;
	ISeamProject seamTestProject;

	public SeamPropertyRefactoringTest() {
		super("Seam Property Refactoring Tests");
	}

	protected void setUp() throws Exception {
		if(warProject==null) {
			IResource project = ResourcesPlugin.getWorkspace().getRoot().findMember("RefactoringTestProject-war");
			assertNotNull("Can't load RefactoringTestProject-war", project);
			warProject = project.getProject();
			warProject.build(IncrementalProjectBuilder.FULL_BUILD, null);
			EditorTestHelper.joinBackgroundActivities();
		}

		if(ejbProject==null) {
			IResource project = ResourcesPlugin.getWorkspace().getRoot().findMember("RefactoringTestProject-ejb");
			assertNotNull("Can't load RefactoringTestProject-ejb", project);
			ejbProject = project.getProject();
			ejbProject.build(IncrementalProjectBuilder.FULL_BUILD, null);
			EditorTestHelper.joinBackgroundActivities();
		}

		if(testProject==null) {
			IResource project = ResourcesPlugin.getWorkspace().getRoot().findMember("RefactoringTestProject-test");
			assertNotNull("Can't load RefactoringTestProject-test", project);
			testProject = project.getProject();
			testProject.build(IncrementalProjectBuilder.FULL_BUILD, null);
			EditorTestHelper.joinBackgroundActivities();
		}

		if(seamWarProject==null) {
			seamWarProject = SeamCorePlugin.getSeamProject(warProject, true);
			assertNotNull("Seam WAR project is null", seamWarProject);
			EditorTestHelper.joinBackgroundActivities();
		}

		if(seamEjbProject==null) {
			seamEjbProject = SeamCorePlugin.getSeamProject(ejbProject, true);
			assertNotNull("Seam EJB project is null", seamEjbProject);
			EditorTestHelper.joinBackgroundActivities();
		}

		if(seamTestProject==null) {
			seamTestProject = SeamCorePlugin.getSeamProject(testProject, true);
			assertNotNull("Seam test project is null", seamTestProject);
			EditorTestHelper.joinBackgroundActivities();
		}
	}

	public void testWarProjectRename() throws CoreException {
		RenameSupport support = RenameSupport.create(JavaCore.create(warProject), "NewWarProjectName", RenameSupport.UPDATE_REFERENCES);

		Shell parent = WorkbenchUtils.getActiveShell();
		IWorkbenchWindow context = WorkbenchUtils.getWorkbench().getActiveWorkbenchWindow();
		try {
			support.perform(parent, context);
		} catch (InterruptedException e) {
			JUnitUtils.fail("Rename failed", e);
		} catch (InvocationTargetException e) {
			JUnitUtils.fail("Rename failed", e);
		}
		EditorTestHelper.joinBackgroundActivities();
		String newParentName = seamEjbProject.getParentProjectName();
		assertEquals("NewWarProjectName", newParentName);
	}
}