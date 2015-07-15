/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.core.itest;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.batch.core.BatchArtifactType;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.batch.core.IBatchProject;
import org.jboss.tools.batch.internal.core.impl.PreferredPackageManager;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.test.util.ProjectImportTestSetup;

import junit.framework.TestCase;

public class PreferredPackageManagerTest extends TestCase{
	public static String PROJECT_NAME = "BatchTestProject"; //$NON-NLS-1$
	private IProject project;
	
	private static final String TEST_PACKAGE_NAME="/BatchTestProject/src/batch/artifact";
	
	private static final String JAR_PACKAGE_NAME="/BatchTestProject/lib1/batchlib.jar";

	public PreferredPackageManagerTest() {}

	@Override
	protected void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
	}
	
	public void testPreferredPackageManager() throws JavaModelException{
		BatchArtifactType type = BatchArtifactType.BATCHLET;
		IBatchProject batchProject = BatchCorePlugin.getBatchProject(project, true);
		
		IJavaProject javaProject = EclipseUtil.getJavaProject(project);
		
		javaProject.getPackageFragmentRoots()[0].createPackageFragment("batch.artifact", true, new NullProgressMonitor());

		PreferredPackageManager.savePreferredPackage(batchProject, type, TEST_PACKAGE_NAME);
		
		IPackageFragment suggestedPackageName = PreferredPackageManager.getPackageSuggestion(batchProject, type);
		
		assertEquals("Unexpected suggested package name", TEST_PACKAGE_NAME, suggestedPackageName.getPath().toString());
	}

	public void testArtifactFromJar() throws JavaModelException{
		BatchArtifactType type = BatchArtifactType.PARTITION_COLLECTOR;
		IBatchProject batchProject = BatchCorePlugin.getBatchProject(project, true);
		
		PreferredPackageManager.savePreferredPackage(batchProject, type, JAR_PACKAGE_NAME);
		
		IPackageFragment suggestedPackageName = PreferredPackageManager.getPackageSuggestion(batchProject, type);
		
		assertNull("Suggested package name should be null", suggestedPackageName);
	}
}
