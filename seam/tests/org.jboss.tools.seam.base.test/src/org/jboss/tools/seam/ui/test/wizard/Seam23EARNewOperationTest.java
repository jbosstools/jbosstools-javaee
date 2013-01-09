/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.seam.ui.test.wizard;

import java.io.File;

import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.core.test.project.facet.AbstractSeam2FacetInstallDelegateTest;
import org.jboss.tools.seam.core.test.project.facet.AbstractSeamFacetTest;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;

/**
 * @author Alexey Kazakov
 */
public class Seam23EARNewOperationTest extends Seam20EARNewOperationTest {

	public Seam23EARNewOperationTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		assertSeamHomeAvailable();

		seam2Facet = ProjectFacetsManager.getProjectFacet("jst.seam");
		seam2FacetVersion = seam2Facet.getVersion("2.3");

		File folder = getSeamHomeFolder();

		SeamRuntimeManager.getInstance().addRuntime(AbstractSeam2FacetInstallDelegateTest.SEAM_2_3_0, folder.getAbsolutePath(), SeamVersion.SEAM_2_3, true);
		assertNotNull(SeamRuntimeManager.getInstance().findRuntimeByName(AbstractSeam2FacetInstallDelegateTest.SEAM_2_3_0));

		super.setUp();
	}

	@Override
	protected String getSeamRTName() {
		return AbstractSeam2FacetInstallDelegateTest.SEAM_2_3_0;
	}

	@Override
	protected File getSeamHomeFolder() {
		return super.getSeamHomeFolder(AbstractSeamFacetTest.SEAM23_FOLDER_NAME);
	}

	@Override
	protected String getSystemPropertyName() {
		return AbstractSeam2FacetInstallDelegateTest.SEAM_201GA_HOME_PROPERY;
	}

	@Override
	protected IDataModel createSeamDataModel(String deployType) {
		IDataModel model = super.createSeamDataModel(deployType);
		model.setStringProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME, AbstractSeam2FacetInstallDelegateTest.SEAM_2_3_0);
		model.setBooleanProperty(ISeamFacetDataModelProperties.TEST_PROJECT_CREATING, false);
		return model;
	}

	@Override
	protected boolean shouldCheckTestProject() {
		// Test project was not supposed to be created.
		return false;
	}
}