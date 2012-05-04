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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
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
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.common.project.facet.JavaFacetUtils;
import org.eclipse.jst.common.project.facet.core.ClasspathHelper;
import org.eclipse.jst.common.project.facet.core.JavaFacet;
import org.eclipse.jst.common.project.facet.core.internal.JavaFacetUtil;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
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

	private String jbossSeamPath;

	private static final Map<String,String> COMPILER_LEVEL_TO_EXEC_ENV = new HashMap<String,String>();
    
    static
    {
        COMPILER_LEVEL_TO_EXEC_ENV.put( JavaCore.VERSION_1_3, "J2SE-1.3" ); //$NON-NLS-1$
        COMPILER_LEVEL_TO_EXEC_ENV.put( JavaCore.VERSION_1_4, "J2SE-1.4" ); //$NON-NLS-1$
        COMPILER_LEVEL_TO_EXEC_ENV.put( JavaCore.VERSION_1_5, "J2SE-1.5" ); //$NON-NLS-1$
        COMPILER_LEVEL_TO_EXEC_ENV.put( JavaCore.VERSION_1_6, "JavaSE-1.6" ); //$NON-NLS-1$
        COMPILER_LEVEL_TO_EXEC_ENV.put( JavaCore.VERSION_1_7, "JavaSE-1.7" ); //$NON-NLS-1$
    }

    private static final IPath CPE_PREFIX_FOR_EXEC_ENV = new Path( "org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType" ); //$NON-NLS-1$

	/**
	 * @param model Seam facet data model
	 * @param seamWebProject Seam web project
	 */
	public SeamProjectCreator(IDataModel model, IProject seamWebProject) {
		this.model = model;
		this.seamWebProject = seamWebProject;

		earProjectName = model.getStringProperty(ISeamFacetDataModelProperties.SEAM_EAR_PROJECT);
		ejbProjectName = model.getStringProperty(ISeamFacetDataModelProperties.SEAM_EJB_PROJECT);
		testProjectName = model.getStringProperty(ISeamFacetDataModelProperties.SEAM_TEST_PROJECT);
		
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
		
		try {
			hibernateConsoleLaunchFile = new File(SeamFacetInstallDataModelProvider.getTemplatesFolder(), "hibernatetools/hibernate-console.launch");
		} catch (IOException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
		hibernateConsolePropsFile = new File(seamGenHomeFolder, "hibernatetools/hibernate-console.properties"); //$NON-NLS-1$
		//hibernateConsoleLaunchFile = new File(seamGenHomeFolder, "hibernatetools/hibernate-console.launch"); //$NON-NLS-1$
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

		FilterSet driverSet = SeamFacetAbstractInstallDelegate.getDriverFilterSet(model);
		if(driverSet!=null) {
			viewFilterSetCollection.addFilterSet(driverSet);
		}

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
		ejbFilterSet.addFilter("earProjectName", earProjectName); //$NON-NLS-1$
		ejbFilterSet.addFilter("ejbProjectName", ejbProjectName); //$NON-NLS-1$
		ejbFilterSet.addFilter("testProjectName", testProjectName); //$NON-NLS-1$

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
		boolean testProjectCreated = createTestProject();
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
				StringBuffer earJarsStrWar = new StringBuffer();
				StringBuffer earJarsStrEjb = new StringBuffer();
				
				if(earJars != null){
					for (File file : earJars) {
						earJarsStrWar.append(" ").append(file.getName()).append(" \n");
						if (isJBossSeamJar(file)) {
							jbossSeamPath = file.getAbsolutePath();
						} else {
							earJarsStrEjb.append(" ").append(file.getName()).append(" \n");
						}
					}
				}

				if(earJarsStrEjb.length()>0) {
					earJarsStrEjb.insert(0, "Class-Path: "); //$NON-NLS-1$
				}
				FilterSetCollection manifestFilterColWar = new FilterSetCollection(projectFilterSet);
				FilterSet manifestFilterWar = new FilterSet();
				manifestFilterWar.addFilter("earLibs", earJarsStrWar.toString()); //$NON-NLS-1$
				manifestFilterColWar.addFilterSet(manifestFilterWar);
				
				if(shouldCopyLibrariesAndTemplates(model))
					AntCopyUtils.copyFileToFolder(new File(SeamFacetInstallDataModelProvider.getTemplatesFolder(), "war/META-INF/MANIFEST.MF"), webMetaInf, manifestFilterColWar, true); //$NON-NLS-1$

				FilterSetCollection manifestFilterColEjb = new FilterSetCollection(projectFilterSet);
				FilterSet manifestFilterEjb = new FilterSet();
				manifestFilterEjb.addFilter("earClasspath", earJarsStrEjb.toString()); //$NON-NLS-1$
				manifestFilterColEjb.addFilterSet(manifestFilterEjb);
				
				if(shouldCopyLibrariesAndTemplates(model))
					AntCopyUtils.copyFileToFolder(new File(SeamFacetInstallDataModelProvider.getTemplatesFolder(), "ejb/ejbModule/META-INF/MANIFEST.MF"), ejbMetaInf, manifestFilterColEjb, true); //$NON-NLS-1$
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
			IProjectFacet jf = JavaFacet.FACET;
			IProjectFacetVersion jfv = ProjectFacetsManager.create(ejbProjectToBeImported).getInstalledVersion(jf);
			JavaFacetUtil.resetClasspath(ejbProjectToBeImported, null, jfv);
			ClasspathHelper.addClasspathEntries(ejbProjectToBeImported, pfv);
//			WtpUtils.reconfigure(ejbProjectToBeImported,monitor);
			IProject earProjectToBeImported = wsRoot.getProject(earProjectName);
			ResourcesUtils.importExistingProject(earProjectToBeImported, wsPath + "/" + earProjectName, earProjectName, monitor, false);
			if (jbossSeamPath != null && jbossSeamPath.trim().length() > 0 && new File(jbossSeamPath).exists()) {
				IJavaProject ejbJavaProject = JavaCore.create(ejbProjectToBeImported);
				if (ejbJavaProject != null) {
					if (!ejbJavaProject.isOpen()) {
						ejbJavaProject.open(monitor);
					}
					IClasspathEntry[] cps = ejbJavaProject.getRawClasspath();
					IClasspathEntry[] entries = new IClasspathEntry[cps.length + 1];
					for (int i = 0; i < cps.length; i++) {
						entries[i] = cps[i];
					}
					IPath path = new Path(jbossSeamPath);
					IFile[] files = wsRoot.findFilesForLocation(path);
					IFile f = null;
					if (files != null && files.length > 0) {
						f = files[0];
					} else {
						f = wsRoot.getFile(path);
					}
					if (f.exists()) {
						path = f.getFullPath();
					}
					entries[cps.length] = JavaCore.newLibraryEntry(path, null,
							null);
					ejbJavaProject.setRawClasspath(entries, monitor);
				}
			}
			WtpUtils.reconfigure(ejbProjectToBeImported, monitor);
			configureJBossAppXml();

			WtpUtils.reconfigure(earProjectToBeImported, monitor);
		}

		IProject testProjectToBeImported = null;
		if(testProjectCreated){
			testProjectToBeImported = wsRoot.getProject(testProjectName);

			ResourcesUtils.importExistingProject(testProjectToBeImported, wsPath + "/" + testProjectName, testProjectName, monitor, true);
			// Set up compilation level for test project.

			String level = JavaFacetUtils.getCompilerLevel(seamWebProject);
			String testLevel = JavaFacetUtils.getCompilerLevel(testProjectToBeImported);
			if (!testLevel.equals(level)) {
				JavaFacetUtils.setCompilerLevel(testProjectToBeImported, level);
			}
			testProjectToBeImported.refreshLocal(IResource.DEPTH_INFINITE, monitor);

			final IVMInstall vm = JavaRuntime.getDefaultVMInstall();
			if (vm != null) {
				// See https://issues.jboss.org/browse/JBIDE-8076
				IClasspathEntry cpe = getJreContainer(seamWebProject);
				if (cpe == null) {
					final IPath path = CPE_PREFIX_FOR_EXEC_ENV.append(getCorrespondingExecutionEnvironment(level));
					cpe = JavaCore.newContainerEntry(path);
				}
				IJavaProject javaProject = JavaCore.create(testProjectToBeImported);
				IClasspathEntry[] entries = javaProject.getRawClasspath();
				int jreIndex = getJreContainerIndex(testProjectToBeImported);
				if (jreIndex == -1) {
					IClasspathEntry[] newEntries = new IClasspathEntry[entries.length+1];
					System.arraycopy( entries, 0, newEntries, 1, entries.length );
					newEntries[0] = cpe;
					javaProject.setRawClasspath(newEntries, null);
				} else {
					entries[jreIndex]=cpe;
					javaProject.setRawClasspath(entries, null);
				}
			}

			SeamFacetAbstractInstallDelegate.toggleHibernateOnProject(testProjectToBeImported, consoleName);
		}

		createSeamProjectPreferenes();
		WtpUtils.reconfigure(seamWebProject, monitor);
		if(testProjectToBeImported != null)
			WtpUtils.reconfigure(testProjectToBeImported, monitor);
	}

	private boolean isJBossSeamJar(File file) {
		String regex = "(jboss-seam){1}(-[0-9][0-9\\.]+){0,1}(.jar){1}";
		return Pattern.matches(regex, file.getName());
	}

	private static String getCorrespondingExecutionEnvironment( String compilerLevel ) {
        final String res = COMPILER_LEVEL_TO_EXEC_ENV.get( compilerLevel );

        if( res == null ) {
            throw new IllegalArgumentException( compilerLevel );
        }

        return res;
    }

	public static int getJreContainerIndex(final IProject proj)
			throws CoreException {
		final IJavaProject jproj = JavaCore.create(proj);
		final IClasspathEntry[] cp = jproj.getRawClasspath();
		for (int i = 0; i < cp.length; i++) {
			final IClasspathEntry cpe = cp[i];
			if (cpe.getEntryKind() == IClasspathEntry.CPE_CONTAINER && cpe.getPath().segment(0) .equals(JavaRuntime.JRE_CONTAINER)) {
				return i;
			}
		}
		return -1;
	}

	public static IClasspathEntry getJreContainer(final IProject proj)
			throws CoreException {
		final IJavaProject jproj = JavaCore.create(proj);
		final IClasspathEntry[] cp = jproj.getRawClasspath();
		for (int i = 0; i < cp.length; i++) {
			final IClasspathEntry cpe = cp[i];
			if (cpe.getEntryKind() == IClasspathEntry.CPE_CONTAINER
					&& cpe.getPath().segment(0)
							.equals(JavaRuntime.JRE_CONTAINER)) {
				return cpe;
			}
		}
		return null;
	}

	/**
	 * Creates test project for given seam web project.
	 */
	protected boolean createTestProject() {
		if(!(Boolean)model.getProperty(ISeamFacetDataModelProperties.TEST_PROJECT_CREATING) || !shouldCopyLibrariesAndTemplates(model))
			return false;

		File testProjectDir = new File(seamWebProject.getLocation().removeLastSegments(1).toFile(), testProjectName); //$NON-NLS-1$
		testProjectDir.mkdir();

		IVirtualComponent component = ComponentCore.createComponent(seamWebProject);
		IVirtualFolder webRootVirtFolder = component.getRootFolder().getFolder(new Path("/")); //$NON-NLS-1$

		File testLibDir = new File(testProjectDir, "lib"); //$NON-NLS-1$
		File embededEjbDir = new File(testProjectDir, "embedded-ejb"); //$NON-NLS-1$
		File testSrcDir = new File(testProjectDir, "test-src"); //$NON-NLS-1$
		FilterSet filterSet = new FilterSet();
		filterSet.addFilter("projectName", seamWebProject.getName()); //$NON-NLS-1$
		filterSet.addFilter("earProjectName", earProjectName); //$NON-NLS-1$
		filterSet.addFilter("ejbProjectName", ejbProjectName); //$NON-NLS-1$
		filterSet.addFilter("testProjectName", testProjectName); //$NON-NLS-1$

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
			return false;
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

		if(shouldCopyLibraries(model)){
			AntCopyUtils.copyFiles(
				new File(seamRuntime.getHomeDir(), "lib"), //$NON-NLS-1$
				testLibDir,
				new AntCopyUtils.FileSetFileFilter(includeLibs));
		}

		SeamFacetAbstractInstallDelegate.createComponentsProperties(testSrcDir, "", Boolean.TRUE); //$NON-NLS-1$
		return true;
	}

	/**
	 * Creates test project for given seam web project.
	 * @param testProjectName
	 */
	protected boolean createTestProject(String testProjectName) {
		if(testProjectName==null) {
			throw new IllegalArgumentException("Test project name must not be null"); 
		}
		this.testProjectName = testProjectName;
		return createTestProject();
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
		if(!shouldCopyLibrariesAndTemplates(model))
			return;
		
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
		ejbFilterSet.addFilter("earProjectName", earProjectName); //$NON-NLS-1$
		ejbFilterSet.addFilter("ejbProjectName", ejbProjectName); //$NON-NLS-1$
		ejbFilterSet.addFilter("testProjectName", testProjectName); //$NON-NLS-1$

		ejbFilterSet.addFilter("connectionProfile", model.getStringProperty(ISeamFacetDataModelProperties.SEAM_CONNECTION_PROFILE));//$NON-NLS-1$

		
		AntCopyUtils.copyFileToFile(
				hibernateConsoleLaunchFile,
				new File(ejbProjectFolder, getLaunchCfgName(ejbProjectFolder.getName()) + ".launch"),  //$NON-NLS-1$
				new FilterSetCollection(ejbFilterSet), true);

		AntCopyUtils.copyFileToFolder(
			hibernateConsolePropsFile,
			ejbProjectFolder,
			hibernateDialectFilterSet, true);
	}
	
	protected String getLaunchCfgName(String baseName){
		ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
		return lm.generateUniqueLaunchConfigurationNameFrom(baseName);
	}

	protected void createEarProject() {
		if(!shouldCopyLibrariesAndTemplates(model))
			return;
		
		earProjectFolder.mkdir();

		File earContentsFolder = new File(earProjectFolder, "EarContent"); //$NON-NLS-1$

		FilterSet earFilterSet =  new FilterSet();
		earFilterSet.addFilter("projectName", earProjectFolder.getName() + ".ear"); //$NON-NLS-1$ //$NON-NLS-2$
		earFilterSet.addFilter("earProjectName", earProjectName); //$NON-NLS-1$
		earFilterSet.addFilter("ejbProjectName", ejbProjectName); //$NON-NLS-1$
		earFilterSet.addFilter("testProjectName", testProjectName); //$NON-NLS-1$

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
		if (!SeamCorePlugin.getDefault().hasM2Facet(seamWebProject) && shouldCopyLibraries(model)) {
			AntCopyUtils.copyFiles(seamLibFolder, earContentsFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(getJbossEarContent()).dir(seamLibFolder)));
			AntCopyUtils.copyFiles(droolsLibFolder, earContentsFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(getJbossEarContent()).dir(droolsLibFolder)));
		}
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
		prefs.put(ISeamFacetDataModelProperties.TEST_CREATING, ((Boolean)model.getProperty(ISeamFacetDataModelProperties.TEST_PROJECT_CREATING)).toString());
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

			IContainer sourceFolder = rootFolder.getFolder(new Path("/WEB-INF/classes")).getUnderlyingFolder(); //$NON-NLS-1$
			IContainer parentFolder = sourceFolder.getParent();
			IPath srcRootFolder = parentFolder.getFullPath();
			IPath srcFolder = sourceFolder.getFullPath();

			IPath model = srcRootFolder.append(ISeamFacetDataModelProperties.DEFAULT_MODEL_SRC_FOLDER_NAME);
			IPath action = srcRootFolder.append(ISeamFacetDataModelProperties.DEFAULT_ACTION_SRC_FOLDER_NAME);
			IResource modelFolder = parentFolder.findMember(ISeamFacetDataModelProperties.DEFAULT_MODEL_SRC_FOLDER_NAME);
			IResource actionFolder = parentFolder.findMember(ISeamFacetDataModelProperties.DEFAULT_ACTION_SRC_FOLDER_NAME);
			if(modelFolder==null || !modelFolder.exists() || actionFolder==null || !actionFolder.exists()) {
				model = srcFolder;
				action = srcFolder;
			}

			prefs.put(ISeamFacetDataModelProperties.ENTITY_BEAN_SOURCE_FOLDER, model.toString());
			prefs.put(ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER, action.toString());
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
	
	protected boolean shouldCopyLibrariesAndTemplates(IDataModel model){
		return model.getBooleanProperty(ISeamFacetDataModelProperties.SEAM_TEMPLATES_AND_LIBRARIES_COPYING);
	}

	protected boolean shouldCopyLibraries(IDataModel model){
		return model.getBooleanProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_LIBRARIES_COPYING);
	}
}