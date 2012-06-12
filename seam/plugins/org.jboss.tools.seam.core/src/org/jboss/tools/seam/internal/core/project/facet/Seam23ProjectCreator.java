/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.seam.internal.core.project.facet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * @author Alexey Kazakov
 */
public class Seam23ProjectCreator extends Seam2ProjectCreator {

	public Seam23ProjectCreator(IDataModel model, IProject seamWebProject, SeamLibFileSetProvider seamLibFileSetProvider) {
		super(model, seamWebProject, seamLibFileSetProvider);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.project.facet.Seam2ProjectCreator#configureJBossAppXml()
	 */
	@Override
	protected void configureJBossAppXml() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.project.facet.SeamProjectCreator#copyJBossApp()
	 */
	@Override
	protected void copyJBossApp() {
		FilterSet earFilterSet =  new FilterSet();
		earFilterSet.addFilter("projectName", earProjectFolder.getName() + ".ear"); //$NON-NLS-1$ //$NON-NLS-2$
		earFilterSet.addFilter("earProjectName", earProjectName); //$NON-NLS-1$
		earFilterSet.addFilter("ejbProjectName", ejbProjectName); //$NON-NLS-1$
		earFilterSet.addFilter("testProjectName", testProjectName); //$NON-NLS-1$

		AntCopyUtils.copyFileToFolder(
			new File(seamGenResFolder, "META-INF/jboss-deployment-structure.xml"), //$NON-NLS-1$
			new File(earContentsFolder, "META-INF"), //$NON-NLS-1$
			new FilterSetCollection(earFilterSet), true);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.project.facet.SeamProjectCreator#execute(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void execute(IProgressMonitor monitor) throws CoreException {
		super.execute(monitor);
		if(ISeamFacetDataModelProperties.DEPLOY_AS_EAR.equals(model.getProperty(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS))) {
			IProject earProject = seamWebProject.getWorkspace().getRoot().getProject(earProjectName);
			File destFolder = new File(earProjectFolder, "resources");
			Seam23FacetInstallDelegate.copyDBDriverToProject(earProject, model, destFolder);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.project.facet.SeamProjectCreator#createComponentsProperties()
	 */
	@Override
	protected void createComponentsProperties() {
		File seamGenResFolder = new File(ejbProjectFolder, "ejbModule");
//		jndiPattern=java:app/app1-ejb/\#{ejbName}
		String jndiPattern = "java:app/" + ejbProjectName + "/#{ejbName}";
		Properties components = new Properties();
		components.put("jndiPattern", jndiPattern); //$NON-NLS-1$
		components.put("embeddedEjb", "false"); //$NON-NLS-1$ $NON-NLS-2$
		File componentsProps = new File(seamGenResFolder, "components.properties"); //$NON-NLS-1$
		try {
			componentsProps.createNewFile();
			components.store(new FileOutputStream(componentsProps), ""); //$NON-NLS-1$
		} catch (IOException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
	}
}