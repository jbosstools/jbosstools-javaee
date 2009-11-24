/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.ui.test.wizard;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.core.test.project.facet.AbstractSeamFacetTest;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;

/**
 * @author daniel
 *
 */
public class SeamCreateTestProjectTest extends AbstractSeamFacetTest {
	public static final String WAR = "war";
	public static final String EAR = "ear";
	
	public SeamCreateTestProjectTest(String name) {
		super(name);
	}

	protected void checkTestProjectCreation(String name, String seamVersion, String deployType, boolean createTestProject) throws CoreException{
		IDataModel model = createSeamDataModel(deployType);
		
		// set property to create test project
		model.setProperty(ISeamFacetDataModelProperties.TEST_PROJECT_CREATING, new Boolean(createTestProject));
		
		model.setStringProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME, seamVersion);
		

		final IFacetedProject fproj = createSeamProject(name, model);
		
		final IProject proj = fproj.getProject();

		assertNotNull(proj);
		assertTrue(proj.exists());
		if(createTestProject){
			assertTrue(proj.getWorkspace().getRoot().getProject(proj.getName() + "-test").exists());
			IProject testProject = proj.getWorkspace().getRoot().getProject(proj.getName() + "-test");
		}else{
			assertFalse(proj.getWorkspace().getRoot().getProject(proj.getName() + "-test").exists());
		}
	}
	
	public void testSeamWarProjectWithTestProject() throws CoreException{
		checkTestProjectCreation("test_seam12_war_t", SEAM_1_2_0, WAR, true);
	}

	public void testSeamWarProjectWithoutTestProject() throws CoreException{
		checkTestProjectCreation("test_seam12_war", SEAM_1_2_0, WAR, false);
	}

	public void testSeamEarProjectWithTestProject() throws CoreException{
		checkTestProjectCreation("test_seam12_ear_t", SEAM_1_2_0, EAR, true);
	}

	public void testSeamEarProjectWithoutTestProject() throws CoreException{
		checkTestProjectCreation("test_seam12_ear", SEAM_1_2_0, EAR, false);
	}
}
