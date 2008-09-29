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
package org.jboss.tools.seam.internal.core.project.facet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.jboss.tools.common.util.ResourcesUtils;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;

/**
 * @author Alexey Kazakov
 * This is basic class that helps New Seam Wizard Page to create EJB, EAR and test projects for seam WAR project. 
 */
abstract public class SeamProjectCreator {

	protected static final String TEST_WAR_PROFILE = "test-war"; //$NON-NLS-1$
	protected static final String TEST_EAR_PROFILE = "test"; //$NON-NLS-1$

	protected IDataModel model;
	protected IProject seamWebProject;
	protected SeamRuntime seamRuntime;
	protected String earProjectName;
	protected String ejbProjectName;
	protected String testProjectName;

	/**
	 * @param model Seam facet data model
	 * @param seamWebProject Seam web project
	 */
	public SeamProjectCreator(IDataModel model, IProject seamWebProject) {
		this.model = model;
		this.seamWebProject = seamWebProject;

		seamRuntime = SeamRuntimeManager.getInstance().findRuntimeByName(model.getProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME).toString());
		if(seamRuntime==null) {
			throw new RuntimeException("Can't get seam runtime " + model.getProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME).toString());
		}

		// Set default project names
		earProjectName = seamWebProject.getName() + "-ear";
		ejbProjectName = seamWebProject.getName() + "-ejb";
		testProjectName = seamWebProject.getName() + "-test";
	}

	public IDataModel getModel() {
		return model;
	}

	public void setModel(IDataModel model) {
		this.model = model;
	}

	/**
	 * Creates test project for seam web project in case of WAR deployment and test, EAR and EJB projects in case of EAR deployment.
	 * @param monitor
	 * @throws CoreException
	 */
	public void execute(IProgressMonitor monitor) throws CoreException {
		createTestProject();
		final String consoleName = SeamFacetAbstractInstallDelegate.isWarConfiguration(model) ? seamWebProject.getName() : ejbProjectName;

		String wsPath = seamWebProject.getLocation().removeLastSegments(1).toFile().getAbsoluteFile().getPath();
		IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject testProjectToBeImported = wsRoot.getProject(testProjectName);

		ResourcesUtils.importExistingProject(testProjectToBeImported, wsPath + "/" + testProjectName, testProjectName, monitor, true);
		SeamFacetAbstractInstallDelegate.toggleHibernateOnProject(testProjectToBeImported, consoleName);
	}

	/**
	 * Creates test project for given seam web project.
	 */
	abstract protected void createTestProject();

	/**
	 * Creates test project for given seam web project.
	 * @param testProjectName
	 */
	protected void createTestProject(String testProjectName) {
		if(testProjectName==null) {
			throw new IllegalArgumentException("Test project name must not be null"); 
		}
		this.testProjectName = testProjectName;
		createTestProject();
	}
}