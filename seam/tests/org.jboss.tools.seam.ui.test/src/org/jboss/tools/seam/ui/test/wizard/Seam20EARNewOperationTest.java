/*******************************************************************************
 * Copyright (c) 2008 Red Hat, Inc.
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

import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;

public class Seam20EARNewOperationTest extends Seam12EARNewOperationTest {
	
	private IProjectFacet seam2Facet;
	private IProjectFacetVersion seam2FacetVersion;

	public Seam20EARNewOperationTest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void setUp() throws Exception {
		assertSeamHomeAvailable();
		
		seam2Facet = ProjectFacetsManager.getProjectFacet("jst.seam");
		seam2FacetVersion = seam2Facet.getVersion("2.0");
		
		
		File folder = getSeamHomeFolder();
		
		SeamRuntimeManager.getInstance().addRuntime(SEAM_2_0_0, folder.getAbsolutePath(), SeamVersion.SEAM_2_0, true);
		SeamRuntimeManager.getInstance().findRuntimeByName(SEAM_2_0_0);
		
		super.setUp();
	}

	
	@Override
	void setUpSeamProjects() {
		setUpSeamProject(earProject);
	}
	
	@Override
	protected String getSeamRTName() {
		return AbstractSeamNewOperationTest.SEAM_2_0_0;
	}
	
	@Override
	protected File getSeamHomeFolder() {
		return new File(System.getProperty(SEAM_2_0_HOME)).getAbsoluteFile();
	}

	@Override
	protected IProjectFacetVersion getSeamFacetVersion() {
		return seam2FacetVersion;
	}

	@Override
	protected IDataModel createSeamDataModel(String deployType) {
		IDataModel model = super.createSeamDataModel(deployType);
		model.setStringProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME, SEAM_2_0_0);
		return model;
	}

}
