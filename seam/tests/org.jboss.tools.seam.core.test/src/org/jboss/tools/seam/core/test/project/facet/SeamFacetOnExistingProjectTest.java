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
package org.jboss.tools.seam.core.test.project.facet;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamProjectsSet;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.internal.core.project.facet.SeamFacetInstallDataModelProvider;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 * @author Alexey Kazakov
 */
public class SeamFacetOnExistingProjectTest extends TestCase {

	IProject project;
	ISeamProject seamProject;

	@Override
	protected void setUp() throws Exception {
		project = ResourcesUtils.importProject("org.jboss.tools.seam.core.test", "projects/jsf");
		project.build(IncrementalProjectBuilder.FULL_BUILD, null);

		IFacetedProject fproj = ProjectFacetsManager.create(project);

		IProjectFacet seamFacet = ProjectFacetsManager.getProjectFacet("jst.seam");
		IProjectFacetVersion seamFacetVersion = seamFacet.getVersion("2.0");

		fproj.installProjectFacet(seamFacetVersion, createSeamDataModel(), null);
		project.build(IncrementalProjectBuilder.FULL_BUILD, null);

		seamProject = SeamCorePlugin.getSeamProject(project, false);
	}

	@Override
	protected void tearDown() throws Exception {
		ResourcesUtils.deleteProject(project.getName());
	}

	// https://issues.jboss.org/browse/JBIDE-9183
	public void testSeamSettings() throws CoreException {
		SeamProjectsSet projectSet = new SeamProjectsSet(project);

		assertEquals(EclipseResourceUtil.getJavaSourceRoot(project), projectSet.getActionFolder());
		assertEquals(EclipseResourceUtil.getJavaSourceRoot(project), projectSet.getModelFolder());
	}

	protected IDataModel createSeamDataModel() {
		IDataModel config = (IDataModel) new SeamFacetInstallDataModelProvider().create();
//		config.setStringProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME, "Seam 2.0.0");
		config.setBooleanProperty(ISeamFacetDataModelProperties.DB_ALREADY_EXISTS, true);
		config.setBooleanProperty(ISeamFacetDataModelProperties.RECREATE_TABLES_AND_DATA_ON_DEPLOY, false);
		config.setStringProperty(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS, "war");
		config.setStringProperty(ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_NAME, "org.session.beans");
		config.setStringProperty(ISeamFacetDataModelProperties.ENTITY_BEAN_PACKAGE_NAME, "org.entity.beans");
		config.setStringProperty(ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_NAME, "org.test.beans");
		config.setStringProperty(ISeamFacetDataModelProperties.SEAM_CONNECTION_PROFILE, "noop-connection");
		config.setProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH, new String[] { "noop-driver.jar" });
		config.setBooleanProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_LIBRARIES_COPYING, true);
		return config;
	}
}