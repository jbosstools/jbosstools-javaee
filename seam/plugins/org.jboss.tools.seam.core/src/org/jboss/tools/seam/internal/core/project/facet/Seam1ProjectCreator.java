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

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * @author Alexey Kazakov
 * This class helps New Seam Wizard Page to create EJB, EAR and test projects for seam 1.2 WAR project.
 */
public class Seam1ProjectCreator extends SeamProjectCreator {

	protected static AntCopyUtils.FileSet JBOSS_TEST_LIB_FILESET = new AntCopyUtils.FileSet() 
		.include("testng-.*-jdk15\\.jar") //$NON-NLS-1$
		.include("myfaces-api-.*\\.jar") //$NON-NLS-1$
		.include("myfaces-impl-.*\\.jar") //$NON-NLS-1$
		.include("servlet-api\\.jar") //$NON-NLS-1$
		.include("hibernate-all\\.jar") //$NON-NLS-1$
		.include("jboss-ejb3-all\\.jar") //$NON-NLS-1$
		.include("thirdparty-all\\.jar") //$NON-NLS-1$
		.exclude(".*/CVS") //$NON-NLS-1$
		.exclude(".*/\\.svn"); //$NON-NLS-1$

	/**
	 * @param model Seam facet data model
	 * @param seamWebProject Seam web project
	 */
	public Seam1ProjectCreator(IDataModel model, IProject seamWebProject) {
		super(model, seamWebProject);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.project.facet.SeamProjectCreator#createTestProject()
	 */
	@Override
	protected void createTestProject() {
		File testProjectDir = new File(seamWebProject.getLocation().removeLastSegments(1).toFile(), testProjectName); //$NON-NLS-1$
		testProjectDir.mkdir();

		IVirtualComponent component = ComponentCore.createComponent(seamWebProject);
		IVirtualFolder webRootVirtFolder = component.getRootFolder().getFolder(new Path("/")); //$NON-NLS-1$

		File testLibDir = new File(testProjectDir, "lib"); //$NON-NLS-1$
		File embededEjbDir = new File(testProjectDir, "embedded-ejb"); //$NON-NLS-1$
		File testSrcDir = new File(testProjectDir, "test-src"); //$NON-NLS-1$
		FilterSet filterSet = new FilterSet();
		filterSet.addFilter("projectName", seamWebProject.getName()); //$NON-NLS-1$
		filterSet.addFilter("runtimeName", WtpUtils.getServerRuntimeName(seamWebProject)); //$NON-NLS-1$
		filterSet.addFilter("webRootFolder", webRootVirtFolder.getUnderlyingFolder().getFullPath().removeFirstSegments(1).toString()); //$NON-NLS-1$

		AntCopyUtils.FileSet includeLibs = new AntCopyUtils.FileSet(JBOSS_TEST_LIB_FILESET).dir(new File(seamRuntime.getHomeDir(), "lib")); //$NON-NLS-1$
		File[] libs = includeLibs.getDir().listFiles(new AntCopyUtils.FileSetFileFilter(includeLibs));
		StringBuffer testLibraries = new StringBuffer();

		for (File file : libs) {
			testLibraries.append("\t<classpathentry kind=\"lib\" path=\"lib/" + file.getName() + "\"/>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		StringBuffer requiredProjects = new StringBuffer();
		requiredProjects.append("\t<classpathentry combineaccessrules=\"false\" kind=\"src\" path=\"/" + seamWebProject.getName() + "\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
		if (!SeamFacetAbstractInstallDelegate.isWarConfiguration(model)) {
			requiredProjects.append("\n\t<classpathentry combineaccessrules=\"false\" kind=\"src\" path=\"/" + ejbProjectName + "\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
		} 
		filterSet.addFilter("testLibraries", testLibraries.toString()); //$NON-NLS-1$
		filterSet.addFilter("requiredProjects", requiredProjects.toString()); //$NON-NLS-1$
		File testTemplateDir = null;
		try {
			testTemplateDir = new File(SeamFacetInstallDataModelProvider.getTemplatesFolder(), "test"); //$NON-NLS-1$
		} catch (IOException e) {
			SeamCorePlugin.getPluginLog().logError(e);
			return;
		}
		AntCopyUtils.FileSet excludeCvsSvn 
				 = new AntCopyUtils.FileSet(SeamFacetAbstractInstallDelegate.CVS_SVN).dir(testTemplateDir);

		AntCopyUtils.copyFilesAndFolders(
				testTemplateDir,
				testProjectDir,
				new AntCopyUtils.FileSetFileFilter(excludeCvsSvn),
				new FilterSetCollection(filterSet), true);

		excludeCvsSvn.dir(new File(seamRuntime.getHomeDir(), "embedded-ejb/conf")); //$NON-NLS-1$
		AntCopyUtils.copyFiles(
				new File(seamRuntime.getHomeDir(), "embedded-ejb/conf"), //$NON-NLS-1$
				embededEjbDir,
				new AntCopyUtils.FileSetFileFilter(excludeCvsSvn));

		AntCopyUtils.copyFiles(
				new File(seamRuntime.getHomeDir(), "lib"), //$NON-NLS-1$
				testLibDir,
				new AntCopyUtils.FileSetFileFilter(includeLibs));

		SeamFacetAbstractInstallDelegate.createComponentsProperties(testSrcDir, "", Boolean.TRUE); //$NON-NLS-1$
	}
}