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
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collections;

import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jst.common.project.facet.JavaFacetUtils;
import org.eclipse.jst.common.project.facet.core.ClasspathHelper;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.jboss.tools.common.util.ResourcesUtils;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Alexey Kazakov
 * This is basic class that helps New Seam Wizard Page to create EJB, EAR and test projects for seam WAR project. 
 */
public class SeamProjectCreator {

	protected static final String TEST_WAR_PROFILE = "test-war"; //$NON-NLS-1$
	protected static final String TEST_EAR_PROFILE = "test"; //$NON-NLS-1$
	protected static final String DEV_WAR_PROFILE = "dev-war"; //$NON-NLS-1$
	protected static final String DEV_EAR_PROFILE = "dev"; //$NON-NLS-1$

	private static AntCopyUtils.FileSet JBOSS_TEST_LIB_FILESET = new AntCopyUtils.FileSet() 
		.include("testng-.*-jdk15\\.jar") //$NON-NLS-1$
		.include("myfaces-api-.*\\.jar") //$NON-NLS-1$
		.include("myfaces-impl-.*\\.jar") //$NON-NLS-1$
		.include("servlet-api\\.jar") //$NON-NLS-1$
		.include("hibernate-all\\.jar") //$NON-NLS-1$
		.include("jboss-ejb3-all\\.jar") //$NON-NLS-1$
		.include("thirdparty-all\\.jar") //$NON-NLS-1$
		.exclude(".*/CVS") //$NON-NLS-1$
		.exclude(".*/\\.svn"); //$NON-NLS-1$

	protected IDataModel model;
	protected IProject seamWebProject;
	protected SeamRuntime seamRuntime;
	protected String seamHomePath;
	protected File seamHomeFolder;
	protected File seamLibFolder;
	protected File seamGenHomeFolder;

	protected String earProjectName;
	protected String ejbProjectName;
	protected String testProjectName;

	protected File earProjectFolder;
	protected File ejbProjectFolder;
	protected File earContentsFolder;

	protected FilterSet filtersFilterSet;
	protected File seamGenResFolder;
	protected File persistenceFile;
	protected File hibernateConsoleLaunchFile;
	protected File hibernateConsolePropsFile;

	protected FilterSet jdbcFilterSet;
	protected FilterSet encodedJdbcFilterSet;
	protected FilterSet projectFilterSet;
	protected FilterSet encodedProjectFilterSet;
	protected FilterSetCollection viewFilterSetCollection;
	protected FilterSetCollection hibernateDialectFilterSet;
	protected File dataSourceDsFile;

	protected File ejbTemplateDir;
	protected AntCopyUtils.FileSet excludeCvsSvn;
	protected FilterSet ejbFilterSet;

	protected File webMetaInf;
	protected File ejbMetaInf;

	protected File droolsLibFolder;

	/**
	 * @param model Seam facet data model
	 * @param seamWebProject Seam web project
	 */
	public SeamProjectCreator(IDataModel model, IProject seamWebProject) {
		this.model = model;
		this.seamWebProject = seamWebProject;

		// Set default project names
		earProjectName = seamWebProject.getName() + "-ear";
		ejbProjectName = seamWebProject.getName() + "-ejb";
		testProjectName = seamWebProject.getName() + "-test";

		seamRuntime = SeamRuntimeManager.getInstance().findRuntimeByName(model.getProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME).toString());
		if(seamRuntime==null) {
			throw new RuntimeException("Can't get seam runtime " + model.getProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME).toString());
		}
		seamHomePath = seamRuntime.getHomeDir();
		seamHomeFolder = new File(seamHomePath);
		seamLibFolder = new File(seamHomePath, SeamFacetAbstractInstallDelegate.SEAM_LIB_RELATED_PATH);
		seamGenHomeFolder = new File(seamHomePath, "seam-gen"); //$NON-NLS-1$

		filtersFilterSet =  SeamFacetFilterSetFactory.createFiltersFilterSet(model);
		seamGenResFolder = new File(seamGenHomeFolder, "resources"); //$NON-NLS-1$
		persistenceFile = new File(seamGenResFolder, "META-INF/persistence-" + (SeamFacetAbstractInstallDelegate.isWarConfiguration(model) ? DEV_WAR_PROFILE : DEV_EAR_PROFILE) + ".xml"); //$NON-NLS-1$ //$NON-NLS-2$
		hibernateConsoleLaunchFile = new File(seamGenHomeFolder, "hibernatetools/hibernate-console.launch"); //$NON-NLS-1$
		hibernateConsolePropsFile = new File(seamGenHomeFolder, "hibernatetools/hibernate-console.properties"); //$NON-NLS-1$
		dataSourceDsFile = new File(seamGenResFolder, "datasource-ds.xml"); //$NON-NLS-1$

		IVirtualComponent component = ComponentCore.createComponent(seamWebProject);
		IVirtualFolder webRootVirtFolder = component.getRootFolder().getFolder(new Path("/")); //$NON-NLS-1$
		IContainer webRootFolder = webRootVirtFolder.getUnderlyingFolder();
		File webContentFolder = webRootFolder.getLocation().toFile();
		webMetaInf = new File(webContentFolder, "META-INF"); //$NON-NLS-1$
		earProjectFolder = new File(seamWebProject.getLocation().removeLastSegments(1).toFile(), earProjectName);
		earContentsFolder = new File(earProjectFolder, "EarContent"); //$NON-NLS-1$
		ejbProjectFolder = new File(seamWebProject.getLocation().removeLastSegments(1).toFile(), ejbProjectName);
		ejbMetaInf = new File(ejbProjectFolder, "ejbModule/META-INF"); //$NON-NLS-1$		

		jdbcFilterSet = SeamFacetFilterSetFactory.createJdbcFilterSet(model);
		encodedJdbcFilterSet = SeamFacetFilterSetFactory.createJdbcFilterSet(model, true);
		projectFilterSet =  SeamFacetFilterSetFactory.createProjectFilterSet(model);
		encodedProjectFilterSet = SeamFacetFilterSetFactory.createProjectFilterSet(model, true);

		viewFilterSetCollection = new FilterSetCollection();
		viewFilterSetCollection.addFilterSet(jdbcFilterSet);
		viewFilterSetCollection.addFilterSet(projectFilterSet);

		hibernateDialectFilterSet = new FilterSetCollection();
		hibernateDialectFilterSet.addFilterSet(encodedJdbcFilterSet);
		hibernateDialectFilterSet.addFilterSet(encodedProjectFilterSet);
		hibernateDialectFilterSet.addFilterSet(SeamFacetFilterSetFactory.createHibernateDialectFilterSet(model, true));

		try {
			ejbTemplateDir = new File(SeamFacetInstallDataModelProvider.getTemplatesFolder(), "ejb");
		} catch (IOException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
		excludeCvsSvn = new AntCopyUtils.FileSet(SeamFacetAbstractInstallDelegate.CVS_SVN).dir(ejbTemplateDir);

		ejbFilterSet = new FilterSet();
		ejbFilterSet.addFilter("projectName", seamWebProject.getName()); //$NON-NLS-1$
		ejbFilterSet.addFilter("runtimeName", WtpUtils.getServerRuntimeName(seamWebProject)); //$NON-NLS-1$
		if (model.getProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH) != null) {
			File driver = new File(((String[]) model.getProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH))[0]);
			ejbFilterSet.addFilter("driverJar", " " + driver.getName() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else {
			ejbFilterSet.addFilter("driverJar", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		droolsLibFolder = new File(seamHomePath, SeamFacetInstallDelegate.DROOLS_LIB_SEAM_RELATED_PATH);
	}

	public SeamVersion getVersion() {
		String seamVersionString = getModel().getProperty(IFacetDataModelProperties.FACET_VERSION_STR).toString();
		return SeamVersion.parseFromString(seamVersionString);
	}

	public IDataModel getModel() {
		return model;
	}

	public void setModel(IDataModel model) {
		this.model = model;
	}

	protected AntCopyUtils.FileSet getJBossTestLibFileset() {
		return JBOSS_TEST_LIB_FILESET;
	}

	protected AntCopyUtils.FileSet getJbossEarContent() {
		return SeamFacetInstallDelegate.JBOSS_EAR_CONTENT;
	}

	/**
	 * Creates test project for seam web project in case of WAR deployment and test, EAR and EJB projects in case of EAR deployment.
	 * @param monitor
	 * @throws CoreException
	 */
	public void execute(IProgressMonitor monitor) throws CoreException {
		createTestProject();
		final String consoleName = SeamFacetAbstractInstallDelegate.isWarConfiguration(model) ? seamWebProject.getName() : ejbProjectName;

		if(!SeamFacetAbstractInstallDelegate.isWarConfiguration(model)) {
			createEjbProject();
			createEarProject();

			try {
				File[] earJars = earContentsFolder.listFiles(new FilenameFilter() {
					/* (non-Javadoc)
					 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
					 */
					public boolean accept(File dir, String name) {
						if (SeamCorePlugin.getDefault().hasM2Facet(seamWebProject)) {
							return false;
						}
						return name.lastIndexOf(".jar") > 0; //$NON-NLS-1$
					}
				});
				String earJarsStr = ""; //$NON-NLS-1$
				for (File file : earJars) {
					earJarsStr += " " + file.getName() + " \n"; //$NON-NLS-1$ //$NON-NLS-2$
				}

				FilterSetCollection manifestFilterCol = new FilterSetCollection(projectFilterSet);
				FilterSet manifestFilter = new FilterSet();
				manifestFilter.addFilter("earLibs", earJarsStr); //$NON-NLS-1$
				manifestFilterCol.addFilterSet(manifestFilter);
				AntCopyUtils.copyFileToFolder(new File(SeamFacetInstallDataModelProvider.getTemplatesFolder(), "war/META-INF/MANIFEST.MF"), webMetaInf, manifestFilterCol, true); //$NON-NLS-1$
				AntCopyUtils.copyFileToFolder(new File(SeamFacetInstallDataModelProvider.getTemplatesFolder(), "ejb/ejbModule/META-INF/MANIFEST.MF"), ejbMetaInf, manifestFilterCol, true); //$NON-NLS-1$
			} catch (IOException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
		}

		SeamFacetAbstractInstallDelegate.toggleHibernateOnProject(seamWebProject, consoleName);

		String wsPath = seamWebProject.getLocation().removeLastSegments(1).toFile().getAbsoluteFile().getPath();
		IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();

		if (!SeamFacetAbstractInstallDelegate.isWarConfiguration(model)) {
			IProject ejbProjectToBeImported = wsRoot.getProject(ejbProjectName);

			ResourcesUtils.importExistingProject(ejbProjectToBeImported, wsPath + "/" + ejbProjectName, ejbProjectName, monitor, false);
			// Set up compilation level and java facet for ejb project. 
			String level = JavaFacetUtils.getCompilerLevel(seamWebProject);
			String ejbLevel = JavaFacetUtils.getCompilerLevel(ejbProjectToBeImported);
			if (!ejbLevel.equals(level)) {
				JavaFacetUtils.setCompilerLevel(ejbProjectToBeImported, level);
			}
			Action action = new Action(Action.Type.VERSION_CHANGE, JavaFacetUtils.compilerLevelToFacet(level), null);
			IFacetedProject facetedProject = ProjectFacetsManager.create(ejbProjectToBeImported);
			facetedProject.modify(Collections.singleton(action), null);

			SeamFacetAbstractInstallDelegate.toggleHibernateOnProject(ejbProjectToBeImported, consoleName);
			IProjectFacet sf = ProjectFacetsManager.getProjectFacet("jst.ejb");  
			IProjectFacetVersion pfv = ProjectFacetsManager.create(ejbProjectToBeImported).getInstalledVersion(sf);
			ClasspathHelper.addClasspathEntries(ejbProjectToBeImported, pfv);
			WtpUtils.reconfigure(ejbProjectToBeImported,monitor);
			IProject earProjectToBeImported = wsRoot.getProject(earProjectName);
			ResourcesUtils.importExistingProject(earProjectToBeImported, wsPath + "/" + earProjectName, earProjectName, monitor, false);
			
			configureJBossAppXml();
			
			WtpUtils.reconfigure(earProjectToBeImported, monitor);
		}

		IProject testProjectToBeImported = wsRoot.getProject(testProjectName);

		ResourcesUtils.importExistingProject(testProjectToBeImported, wsPath + "/" + testProjectName, testProjectName, monitor, true);
		// Set up compilation level for test project.
		String level = JavaFacetUtils.getCompilerLevel(seamWebProject);
		String testLevel = JavaFacetUtils.getCompilerLevel(testProjectToBeImported);
		if (!testLevel.equals(level)) {
			JavaFacetUtils.setCompilerLevel(testProjectToBeImported, level);
		}
		testProjectToBeImported.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		SeamFacetAbstractInstallDelegate.toggleHibernateOnProject(testProjectToBeImported, consoleName);

		createSeamProjectPreferenes();
		WtpUtils.reconfigure(seamWebProject, monitor);
		WtpUtils.reconfigure(testProjectToBeImported, monitor);
	}

	/**
	 * Creates test project for given seam web project.
	 */
	protected void createTestProject() {
		model.setProperty(ISeamFacetDataModelProperties.SEAM_TEST_PROJECT, testProjectName);

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

		AntCopyUtils.FileSet includeLibs = new AntCopyUtils.FileSet(getJBossTestLibFileset()).dir(new File(seamRuntime.getHomeDir(), "lib")); //$NON-NLS-1$
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

	/**
	 * Creates EJB project for given seam web project.
	 * @param ejbProjectName
	 */
	protected void createEjbProject(String ejbProjectName) {
		if(ejbProjectName==null) {
			throw new IllegalArgumentException("EJB project name must not be null"); 
		}
		this.ejbProjectName = ejbProjectName;
		createEjbProject();
	}

	protected void createEjbProject() {
		model.setProperty(ISeamFacetDataModelProperties.SEAM_EJB_PROJECT, ejbProjectName);

		ejbProjectFolder.mkdir();

		AntCopyUtils.copyFilesAndFolders(
			ejbTemplateDir,
			ejbProjectFolder, new AntCopyUtils.FileSetFileFilter(excludeCvsSvn),
			new FilterSetCollection(ejbFilterSet), true);

		// *******************************************************************************************
		// Copy sources to EJB project in case of EAR configuration
		// *******************************************************************************************
		AntCopyUtils.copyFileToFile(
			new File(seamGenHomeFolder, "src/Authenticator.java"), //$NON-NLS-1$
			new File(ejbProjectFolder, "ejbModule/" + model.getProperty(ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_NAME).toString().replace('.', '/') + "/" + "Authenticator.java"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			new FilterSetCollection(filtersFilterSet), true);
		AntCopyUtils.copyFileToFile(
			persistenceFile,
			new File(ejbProjectFolder, "ejbModule/META-INF/persistence.xml"), //$NON-NLS-1$
			viewFilterSetCollection, true);

		SeamFacetAbstractInstallDelegate.createComponentsProperties(new File(ejbProjectFolder, "ejbModule"), earProjectName, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		AntCopyUtils.FileSet ejbSrcResourcesSet = new AntCopyUtils.FileSet(SeamFacetAbstractInstallDelegate.JBOOS_EJB_WEB_INF_CLASSES_SET).dir(seamGenResFolder);
		AntCopyUtils.copyFilesAndFolders(
			seamGenResFolder, new File(ejbProjectFolder, "ejbModule"), new AntCopyUtils.FileSetFileFilter(ejbSrcResourcesSet), viewFilterSetCollection, true); //$NON-NLS-1$

		AntCopyUtils.copyFileToFolder(
			new File(seamGenResFolder, "META-INF/ejb-jar.xml"),  //$NON-NLS-1$
			new File(ejbProjectFolder, "ejbModule/META-INF/"),  //$NON-NLS-1$
			viewFilterSetCollection, true);

		/*AntCopyUtils.copyFileToFolder(
			hibernateConsolePref,
			new File(ejbProjectFolder,".settings"), //$NON-NLS-1$
			new FilterSetCollection(projectFilterSet), true);*/

		FilterSet ejbFilterSet =  new FilterSet();
		ejbFilterSet.addFilter("projectName", ejbProjectFolder.getName()); //$NON-NLS-1$

		AntCopyUtils.copyFileToFile(
			hibernateConsoleLaunchFile,
			new File(ejbProjectFolder, ejbProjectFolder.getName() + ".launch"),  //$NON-NLS-1$
			new FilterSetCollection(ejbFilterSet), true);

		AntCopyUtils.copyFileToFolder(
			hibernateConsolePropsFile,
			ejbProjectFolder,
			hibernateDialectFilterSet, true);
	}

	protected void createEarProject() {
		model.setProperty(ISeamFacetDataModelProperties.SEAM_EAR_PROJECT, earProjectName);

		earProjectFolder.mkdir();

		File earContentsFolder = new File(earProjectFolder, "EarContent"); //$NON-NLS-1$

		FilterSet earFilterSet =  new FilterSet();
		earFilterSet.addFilter("projectName", earProjectFolder.getName() + ".ear"); //$NON-NLS-1$ //$NON-NLS-2$

		AntCopyUtils.copyFileToFolder(
			new File(seamGenResFolder, "META-INF/jboss-app.xml"), //$NON-NLS-1$
			new File(earContentsFolder, "META-INF"), //$NON-NLS-1$
			new FilterSetCollection(earFilterSet), true);

		// Copy configuration files from template
		try {
			AntCopyUtils.copyFilesAndFolders(
				new File(SeamFacetInstallDataModelProvider.getTemplatesFolder(), "ear"),  //$NON-NLS-1$
				earProjectFolder, new AntCopyUtils.FileSetFileFilter(excludeCvsSvn),
				new FilterSetCollection(ejbFilterSet), true);
		} catch (IOException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}

		// Fill ear contents
		AntCopyUtils.copyFiles(seamHomeFolder, earContentsFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(getJbossEarContent()).dir(seamHomeFolder)));
		AntCopyUtils.copyFiles(seamLibFolder, earContentsFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(getJbossEarContent()).dir(seamLibFolder)));
		AntCopyUtils.copyFiles(droolsLibFolder, earContentsFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(getJbossEarContent()).dir(droolsLibFolder)));
		AntCopyUtils.copyFiles(seamGenResFolder, earContentsFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(getJbossEarContent()).dir(seamGenResFolder)));						

		File resources = new File(earProjectFolder, "resources");
		AntCopyUtils.copyFileToFile(
			dataSourceDsFile,
			new File(resources, seamWebProject.getName() + "-ds.xml"),  //$NON-NLS-1$ //$NON-NLS-2$
			viewFilterSetCollection, true);
	}

	protected void createSeamProjectPreferenes() {
		IScopeContext projectScope = new ProjectScope(seamWebProject);
		IEclipsePreferences prefs = projectScope.getNode(SeamCorePlugin.PLUGIN_ID);

		String testSrcPath = seamWebProject.getFullPath().removeLastSegments(1).append(testProjectName).append("test-src").toString();
		prefs.put(ISeamFacetDataModelProperties.TEST_CREATING, "true");
		prefs.put(ISeamFacetDataModelProperties.SEAM_TEST_PROJECT, testProjectName);
		prefs.put(ISeamFacetDataModelProperties.TEST_SOURCE_FOLDER, testSrcPath);

		if(!SeamFacetAbstractInstallDelegate.isWarConfiguration(model)) {
			prefs.put(ISeamFacetDataModelProperties.SEAM_EJB_PROJECT, ejbProjectName);
			prefs.put(SeamFacetAbstractInstallDelegate.SEAM_EAR_PROJECT, earProjectName);

			String srcPath = seamWebProject.getFullPath().removeLastSegments(1).append(ejbProjectName).append("ejbModule").toString();
			prefs.put(ISeamFacetDataModelProperties.ENTITY_BEAN_SOURCE_FOLDER, srcPath);
			prefs.put(ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER, srcPath);
		} else {
			IVirtualComponent component = ComponentCore.createComponent(seamWebProject);
			IVirtualFolder rootFolder = component.getRootFolder();
			IPath srcRootFolder = rootFolder.getFolder(new Path("/WEB-INF/classes")).getUnderlyingFolder().getParent().getFullPath(); //$NON-NLS-1$

			prefs.put(ISeamFacetDataModelProperties.ENTITY_BEAN_SOURCE_FOLDER, srcRootFolder.append(ISeamFacetDataModelProperties.DEFAULT_MODEL_SRC_FOLDER_NAME).toString());
			prefs.put(ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER, srcRootFolder.append(ISeamFacetDataModelProperties.DEFAULT_ACTION_SRC_FOLDER_NAME).toString());
		}

		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
	}
	
	protected void configureJBossAppXml() {
		// Do nothing special for Seam 1.2
	}
}