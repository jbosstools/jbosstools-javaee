/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
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
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Properties;

import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jst.common.project.facet.core.ClasspathHelper;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.util.ResourcesUtils;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Install delegate for seam faset version 1.2
 * 
 * @author eskimo 
 */
public class SeamFacetInstallDelegate extends SeamFacetAbstractInstallDelegate {

	/**
	 *
	 **/
	public static final String DEV_WAR_PROFILE = "dev-war"; //$NON-NLS-1$

	/**
	 * 
	 */
	public static final String DEV_EAR_PROFILE = "dev";	 //$NON-NLS-1$

	/**
	 * 
	 */
	public static final String TEST_WAR_PROFILE = "test-war"; //$NON-NLS-1$

	/**
	 * 
	 */
	public static final String TEST_EAR_PROFILE = "test"; //$NON-NLS-1$

	/**
	 * 
	 */
	public static final AntCopyUtils.FileSet JBOSS_WAR_LIB_FILESET_WAR_CONFIG = new AntCopyUtils.FileSet()	
		.include("ajax4jsf.*\\.jar") //$NON-NLS-1$
		.include("richfaces.*\\.jar") //$NON-NLS-1$
		.include("antlr.*\\.jar") //$NON-NLS-1$
		.include("commons-beanutils.*\\.jar") //$NON-NLS-1$
		.include("commons-collections.*\\.jar") //$NON-NLS-1$
		.include("commons-digester.*\\.jar") //$NON-NLS-1$
		.include("commons-jci-core.*\\.jar") //$NON-NLS-1$
		.include("commons-jci-janino.*\\.jar") //$NON-NLS-1$
		.include("drools-compiler.*\\.jar") //$NON-NLS-1$
		.include("drools-core.*\\.jar") //$NON-NLS-1$
		.include("janino.*\\.jar") //$NON-NLS-1$		
		.include("jboss-seam-debug\\.jar") //$NON-NLS-1$
		.include("jboss-seam-ioc\\.jar") //$NON-NLS-1$
		.include("jboss-seam-mail\\.jar") //$NON-NLS-1$
		.include("jboss-seam-pdf\\.jar") //$NON-NLS-1$
		.include("jboss-seam-remoting\\.jar") //$NON-NLS-1$
		.include("jboss-seam-ui\\.jar") //$NON-NLS-1$
		.include("jboss-seam\\.jar") //$NON-NLS-1$
		.include("jbpm.*\\.jar") //$NON-NLS-1$
		.include("jsf-facelets\\.jar") //$NON-NLS-1$
		.include("oscache.*\\.jar") //$NON-NLS-1$
		.include("stringtemplate.*\\.jar") //$NON-NLS-1$
	    // el-ri needed for JBIDE-939
	    .include("el-ri.*\\.jar"); //$NON-NLS-1$ 

	/**
	 * 
	 */
	public static AntCopyUtils.FileSet JBOSS_TEST_LIB_FILESET = new AntCopyUtils.FileSet() 
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
	 * 
	 */
	public static AntCopyUtils.FileSet JBOSS_WAR_LIB_FILESET_EAR_CONFIG = new AntCopyUtils.FileSet() 
		.include("ajax4jsf.*\\.jar") //$NON-NLS-1$
		.include("richfaces.*\\.jar") //$NON-NLS-1$
		.include("commons-beanutils.*\\.jar") //$NON-NLS-1$
		.include("commons-digester.*\\.jar") //$NON-NLS-1$
		.include("commons-collections.*\\.jar") //$NON-NLS-1$
		.include("jboss-seam-debug\\.jar") //$NON-NLS-1$
		.include("jboss-seam-ioc\\.jar") //$NON-NLS-1$
		.include("jboss-seam-mail\\.jar") //$NON-NLS-1$
		.include("jboss-seam-pdf\\.jar") //$NON-NLS-1$
		.include("jboss-seam-remoting\\.jar") //$NON-NLS-1$
		.include("jboss-seam-ui\\.jar") //$NON-NLS-1$
		.include("jsf-facelets\\.jar") //$NON-NLS-1$
		.include("oscache.*\\.jar"); //$NON-NLS-1$

	/**
	 * 
	 */
	public static AntCopyUtils.FileSet JBOSS_EAR_CONTENT  = new AntCopyUtils.FileSet()
		.include("antlr.*\\.jar") //$NON-NLS-1$
		.include("commons-jci-core.*\\.jar") //$NON-NLS-1$
		.include("commons-jci-janino.*\\.jar") //$NON-NLS-1$
		.include("drools-compiler.*\\.jar") //$NON-NLS-1$
		.include("drools-core.*\\.jar") //$NON-NLS-1$
		.include("janino.*\\.jar") //$NON-NLS-1$
		.include("jboss-seam.jar") //$NON-NLS-1$
		.include("jbpm.*\\.jar") //$NON-NLS-1$
		.include("security\\.drl") //$NON-NLS-1$
		.include("stringtemplate.*\\.jar") //$NON-NLS-1$
        // el-ri needed for JBIDE-939
        .include("el-ri.*\\.jar"); //$NON-NLS-1$ 

	/**
	 * 
	 */
	public static AntCopyUtils.FileSet JBOSS_EAR_CONTENT_META_INF = new AntCopyUtils.FileSet()
		.include("META-INF/application\\.xml") //$NON-NLS-1$
		.include("META-INF/jboss-app\\.xml"); //$NON-NLS-1$

	/**
	 * 
	 */
	public static AntCopyUtils.FileSet VIEW_FILESET = new AntCopyUtils.FileSet()
		.include("home\\.xhtml") //$NON-NLS-1$
		.include("error\\.xhtml") //$NON-NLS-1$
		.include("login\\.xhtml") //$NON-NLS-1$
		.include("login\\.page.xml") //$NON-NLS-1$
		.include("index\\.html") //$NON-NLS-1$
		.include("layout") //$NON-NLS-1$
		.include("layout/.*") //$NON-NLS-1$
		.include("stylesheet") //$NON-NLS-1$
		.include("stylesheet/.*") //$NON-NLS-1$
		.include("img/.*") //$NON-NLS-1$
		.include("img") //$NON-NLS-1$
		.exclude(".*/.*\\.ftl") //$NON-NLS-1$
		.exclude(".*/CVS") //$NON-NLS-1$
		.exclude(".*/\\.svn"); //$NON-NLS-1$

	/**
	 * 
	 */
	public static AntCopyUtils.FileSet CVS_SVN = new AntCopyUtils.FileSet()
		.include(".*") //$NON-NLS-1$
		.exclude(".*/CVS") //$NON-NLS-1$
		.exclude("CVS") //$NON-NLS-1$
		.exclude(".*\\.svn") //$NON-NLS-1$
		.exclude(".*/\\.svn");	 //$NON-NLS-1$

	/**
	 * 
	 */
	public static AntCopyUtils.FileSet JBOOS_WAR_WEBINF_SET = new AntCopyUtils.FileSet()
		.include("WEB-INF") //$NON-NLS-1$
		.include("WEB-INF/web\\.xml") //$NON-NLS-1$
		.include("WEB-INF/pages\\.xml") //$NON-NLS-1$
		.include("WEB-INF/jboss-web\\.xml") //$NON-NLS-1$
		.include("WEB-INF/faces-config\\.xml") //$NON-NLS-1$
		.include("WEB-INF/componets\\.xml"); //$NON-NLS-1$

	/**
	 * 
	 */
	public static AntCopyUtils.FileSet JBOOS_WAR_WEB_INF_CLASSES_SET = new AntCopyUtils.FileSet()
		.include("import\\.sql") //$NON-NLS-1$
		.include("security\\.drl") //$NON-NLS-1$
		.include("seam\\.properties") //$NON-NLS-1$
		.include("messages_en\\.properties"); //$NON-NLS-1$

	/**
	 * 
	 */
	public static AntCopyUtils.FileSet JBOOS_EJB_WEB_INF_CLASSES_SET = new AntCopyUtils.FileSet()
		.include("import\\.sql") //$NON-NLS-1$
		.include("seam\\.properties")
		.exclude(".*/WEB-INF"); //$NON-NLS-1$

	/**
	 * 
	 */
	public static AntCopyUtils.FileSet JBOSS_EAR_META_INF_SET = new AntCopyUtils.FileSet()
		.include("META-INF/jboss-app\\.xml"); //$NON-NLS-1$

	/**
	 * 
	 */
	public static String DROOLS_LIB_SEAM_RELATED_PATH = "drools/lib"; //$NON-NLS-1$

	/**
	 * 
	 */
	public static String SEAM_LIB_RELATED_PATH = "lib"; //$NON-NLS-1$

	/**
	 * 
	 */
	public static String WEB_LIBRARIES_RELATED_PATH = "WEB-INF/lib"; //$NON-NLS-1$

	/**
	 * 
	 */
	public void doExecute(final IProject project, IProjectFacetVersion fv,
			Object config, IProgressMonitor monitor) throws CoreException {
		final IDataModel model = (IDataModel)config;

		// get WebContents folder path from DWP model 
		IVirtualComponent component = ComponentCore.createComponent(project);
		IVirtualFolder webRootVirtFolder = component.getRootFolder().getFolder(new Path("/")); //$NON-NLS-1$
		final IVirtualFolder srcRootFolder = component.getRootFolder().getFolder(new Path("/WEB-INF/classes")); //$NON-NLS-1$
		IContainer webRootFolder = webRootVirtFolder.getUnderlyingFolder();

		model.setProperty(ISeamFacetDataModelProperties.SEAM_PROJECT_NAME, project.getName());
		model.setProperty(ISeamFacetDataModelProperties.SEAM_TEST_PROJECT, project.getName() + "-test"); //$NON-NLS-1$

		Boolean dbExists = (Boolean) model.getProperty(ISeamFacetDataModelProperties.DB_ALREADY_EXISTS);
		Boolean dbRecreate = (Boolean) model.getProperty(ISeamFacetDataModelProperties.RECREATE_TABLES_AND_DATA_ON_DEPLOY);
		if (!dbExists && !dbRecreate) {
			model.setProperty(ISeamFacetDataModelProperties.HIBERNATE_HBM2DDL_AUTO, "update"); //$NON-NLS-1$
		} else if (dbExists && !dbRecreate) {
			model.setProperty(ISeamFacetDataModelProperties.HIBERNATE_HBM2DDL_AUTO, "validate"); //$NON-NLS-1$
		} else if (dbRecreate) {
			model.setProperty(ISeamFacetDataModelProperties.HIBERNATE_HBM2DDL_AUTO, "create-drop"); //$NON-NLS-1$
		}

		final String consoleName = isWarConfiguration(model) ? project.getName() : project.getName() + "-ejb";

		final File webContentFolder = webRootFolder.getLocation().toFile();
		final File webInfFolder = new File(webContentFolder, "WEB-INF"); //$NON-NLS-1$
		final File webInfClasses = new File(webInfFolder, "classes"); //$NON-NLS-1$
		final File webInfClassesMetaInf = new File(webInfClasses, "META-INF"); //$NON-NLS-1$
		webInfClassesMetaInf.mkdirs();
		final File webLibFolder = new File(webContentFolder, WEB_LIBRARIES_RELATED_PATH);
		final File srcFolder = isWarConfiguration(model) ? new File(srcRootFolder.getUnderlyingFolder().getLocation().toFile(), "model") : srcRootFolder.getUnderlyingFolder().getLocation().toFile(); //$NON-NLS-1$
		final File webMetaInf = new File(webContentFolder, "META-INF"); //$NON-NLS-1$
		final SeamRuntime selectedRuntime = SeamRuntimeManager.getInstance().findRuntimeByName(model.getProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME).toString());

		final String seamHomePath = selectedRuntime.getHomeDir();

		final File seamHomeFolder = new File(seamHomePath);
		final File seamLibFolder = new File(seamHomePath, SEAM_LIB_RELATED_PATH);
		final File seamGenResFolder = new File(seamHomePath, "seam-gen/resources"); //$NON-NLS-1$
		final File seamGenResMetainfFolder = new File(seamGenResFolder, "META-INF"); //$NON-NLS-1$

		final File droolsLibFolder = new File(seamHomePath, DROOLS_LIB_SEAM_RELATED_PATH);
		final File seamGenHomeFolder = new File(seamHomePath, "seam-gen"); //$NON-NLS-1$
		final File seamGenViewSource = new File(seamGenHomeFolder, "view"); //$NON-NLS-1$
		final File dataSourceDsFile = new File(seamGenResFolder, "datasource-ds.xml"); //$NON-NLS-1$
		final File componentsFile = new File(seamGenResFolder, "WEB-INF/components" + (isWarConfiguration(model) ? "-war" : "") + ".xml"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		final File hibernateConsoleLaunchFile = new File(seamGenHomeFolder, "hibernatetools/hibernate-console.launch"); //$NON-NLS-1$
		final File hibernateConsolePropsFile = new File(seamGenHomeFolder, "hibernatetools/hibernate-console.properties"); //$NON-NLS-1$
		//final File hibernateConsolePref = new File(seamGenHomeFolder, "hibernatetools/.settings/org.hibernate.eclipse.console.prefs"); //$NON-NLS-1$
		final File persistenceFile = new File(seamGenResFolder, "META-INF/persistence-" + (isWarConfiguration(model) ? DEV_WAR_PROFILE : DEV_EAR_PROFILE) + ".xml"); //$NON-NLS-1$ //$NON-NLS-2$

		final File applicationFile = new File(seamGenResFolder, "META-INF/application.xml"); //$NON-NLS-1$

		final FilterSet jdbcFilterSet = SeamFacetFilterSetFactory.createJdbcFilterSet(model);
		final FilterSet projectFilterSet =  SeamFacetFilterSetFactory.createProjectFilterSet(model);
		final FilterSet filtersFilterSet =  SeamFacetFilterSetFactory.createFiltersFilterSet(model);

		// ****************************************************************
		// Copy view folder from seam-gen installation to WebContent folder
		// ****************************************************************
		final AntCopyUtils.FileSet viewFileSet = new AntCopyUtils.FileSet(VIEW_FILESET).dir(seamGenViewSource);
		final FilterSetCollection viewFilterSetCollection = new FilterSetCollection();
		viewFilterSetCollection.addFilterSet(jdbcFilterSet);
		viewFilterSetCollection.addFilterSet(projectFilterSet);

		AntCopyUtils.copyFilesAndFolders(
				seamGenViewSource, 
				webContentFolder, 
				new AntCopyUtils.FileSetFileFilter(viewFileSet), 
				viewFilterSetCollection, 
				true);

		// *******************************************************************
		// Copy manifest and configuration resources the same way as view
		// *******************************************************************
		AntCopyUtils.FileSet webInfSet = new AntCopyUtils.FileSet(JBOOS_WAR_WEBINF_SET).dir(seamGenResFolder);

		AntCopyUtils.copyFileToFile(
				componentsFile,
				new File(webInfFolder, "components.xml"), //$NON-NLS-1$
				new FilterSetCollection(projectFilterSet), true);

		AntCopyUtils.copyFilesAndFolders(
				seamGenResFolder, webContentFolder, new AntCopyUtils.FileSetFileFilter(webInfSet), viewFilterSetCollection, true);

		final FilterSetCollection hibernateDialectFilterSet = new FilterSetCollection();
		hibernateDialectFilterSet.addFilterSet(jdbcFilterSet);
		hibernateDialectFilterSet.addFilterSet(projectFilterSet);
		hibernateDialectFilterSet.addFilterSet(SeamFacetFilterSetFactory.createHibernateDialectFilterSet(model));

		createTestProject(model, project, selectedRuntime);

		// ********************************************************************************************
		// Handle WAR/EAR configurations
		// ********************************************************************************************
		if (isWarConfiguration(model)) {
			AntCopyUtils.FileSet webInfClassesSet = new AntCopyUtils.FileSet(JBOOS_WAR_WEB_INF_CLASSES_SET).dir(seamGenResFolder);
			AntCopyUtils.copyFilesAndFolders(
					seamGenResFolder, srcFolder, new AntCopyUtils.FileSetFileFilter(webInfClassesSet), viewFilterSetCollection, true);

			createComponentsProperties(srcFolder, isWarConfiguration(model) ? "" : project.getName() + "-ear", false); //$NON-NLS-1$ //$NON-NLS-2$

			/*AntCopyUtils.copyFileToFolder(
					hibernateConsolePref,
					new File(project.getLocation().toFile(),".settings"),	 //$NON-NLS-1$
					new FilterSetCollection(projectFilterSet), true);*/

			// In case of WAR configuration
			AntCopyUtils.copyFiles(seamHomeFolder, webLibFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_WAR_CONFIG).dir(seamHomeFolder)));
			AntCopyUtils.copyFiles(seamLibFolder, webLibFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_WAR_CONFIG).dir(seamLibFolder)));
			AntCopyUtils.copyFiles(droolsLibFolder, webLibFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_WAR_CONFIG).dir(droolsLibFolder)));

			// ********************************************************************************************
			// Copy seam project indicator
			// ********************************************************************************************
			AntCopyUtils.copyFileToFolder(new File(seamGenResFolder, "seam.properties"), srcFolder, true); //$NON-NLS-1$
			final IContainer source = srcRootFolder.getUnderlyingFolder();

			IPath actionSrcPath = new Path(source.getFullPath().removeFirstSegments(1) + "/action"); //$NON-NLS-1$
			IPath modelSrcPath = new Path(source.getFullPath().removeFirstSegments(1) + "/model"); //$NON-NLS-1$

			srcRootFolder.delete(IVirtualFolder.FORCE, monitor);
			WtpUtils.createSourceFolder(project, actionSrcPath, source.getFullPath().removeFirstSegments(1), webRootFolder.getFullPath().removeFirstSegments(1).append("WEB-INF/dev")); //$NON-NLS-1$
			WtpUtils.createSourceFolder(project, modelSrcPath, source.getFullPath().removeFirstSegments(1), null);			

			srcRootFolder.createLink(actionSrcPath, 0, null);
			srcRootFolder.createLink(modelSrcPath, 0, null);					

			AntCopyUtils.copyFileToFile(
					new File(seamGenHomeFolder, "src/Authenticator.java"), //$NON-NLS-1$
					new File(project.getLocation().toFile(), source.getFullPath().removeFirstSegments(1) + "/action/" + model.getProperty(ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_NAME).toString().replace('.', '/') + "/" + "Authenticator.java"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					new FilterSetCollection(filtersFilterSet), true);

			AntCopyUtils.copyFileToFile(
					persistenceFile,
					new File(srcFolder, "META-INF/persistence.xml"), //$NON-NLS-1$
					viewFilterSetCollection, true);

			File resources = new File(project.getLocation().toFile(), "resources");
			AntCopyUtils.copyFileToFile(
					dataSourceDsFile, 
					new File(resources, project.getName() + "-ds.xml"),  //$NON-NLS-1$
					viewFilterSetCollection, true);

			AntCopyUtils.copyFileToFile(
					hibernateConsoleLaunchFile, 
					new File(project.getLocation().toFile(), project.getName() + ".launch"),  //$NON-NLS-1$
					viewFilterSetCollection, true);

			AntCopyUtils.copyFileToFolder(
					hibernateConsolePropsFile, 
					project.getLocation().toFile(),
					hibernateDialectFilterSet, true);

			WtpUtils.setClasspathEntryAsExported(project, new Path("org.eclipse.jst.j2ee.internal.web.container"), monitor); //$NON-NLS-1$
		} else {
			model.setProperty(ISeamFacetDataModelProperties.SEAM_EJB_PROJECT, project.getName() + "-ejb"); //$NON-NLS-1$
			model.setProperty(ISeamFacetDataModelProperties.SEAM_EAR_PROJECT, project.getName() + "-ear"); //$NON-NLS-1$

			// In case of EAR configuration
			AntCopyUtils.copyFiles(seamHomeFolder, webLibFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_EAR_CONFIG).dir(seamHomeFolder)));
			AntCopyUtils.copyFiles(seamLibFolder, webLibFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_EAR_CONFIG).dir(seamLibFolder)));
			AntCopyUtils.copyFiles(droolsLibFolder, webLibFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_EAR_CONFIG).dir(droolsLibFolder)));
			AntCopyUtils.copyFileToFolder(new File(seamGenResFolder, "messages_en.properties"), srcFolder, true); //$NON-NLS-1$

			File ear = new File(project.getLocation().removeLastSegments(1).toFile(), model.getProperty(ISeamFacetDataModelProperties.SEAM_PROJECT_NAME) + "-ear"); //$NON-NLS-1$
			File ejb = new File(project.getLocation().removeLastSegments(1).toFile(), model.getProperty(ISeamFacetDataModelProperties.SEAM_PROJECT_NAME) + "-ejb"); //$NON-NLS-1$
			ear.mkdir();
			ejb.mkdir();

			try {
				FilterSet filterSet = new FilterSet();
				filterSet.addFilter("projectName", project.getName()); //$NON-NLS-1$
				filterSet.addFilter("runtimeName", WtpUtils.getServerRuntimeName(project)); //$NON-NLS-1$
				if (model.getProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH) != null) {
					File driver = new File(((String[]) model.getProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH))[0]);
					filterSet.addFilter("driverJar", " " + driver.getName() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				} else {
					filterSet.addFilter("driverJar", ""); //$NON-NLS-1$ //$NON-NLS-2$
				}
				AntCopyUtils.FileSet excludeCvsSvn = new AntCopyUtils.FileSet(CVS_SVN).dir(seamGenResFolder);

				AntCopyUtils.copyFilesAndFolders(
						new File(SeamFacetInstallDataModelProvider.getTemplatesFolder(), "ejb"),  //$NON-NLS-1$
						ejb, new AntCopyUtils.FileSetFileFilter(excludeCvsSvn),
						new FilterSetCollection(filterSet), true);

				// *******************************************************************************************
				// Copy sources to ejb project in case of EAR configuration
				// *******************************************************************************************
				AntCopyUtils.copyFileToFile(
						new File(seamGenHomeFolder, "src/Authenticator.java"), //$NON-NLS-1$
						new File(ejb, "ejbModule/" + model.getProperty(ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_NAME).toString().replace('.', '/') + "/" + "Authenticator.java"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						new FilterSetCollection(filtersFilterSet), true);
				AntCopyUtils.copyFileToFile(
						persistenceFile,
						new File(ejb, "ejbModule/META-INF/persistence.xml"), //$NON-NLS-1$
						viewFilterSetCollection, true);

				createComponentsProperties(new File(ejb, "ejbModule"), isWarConfiguration(model) ? "" : project.getName() + "-ear", false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

				AntCopyUtils.FileSet ejbSrcResourcesSet = new AntCopyUtils.FileSet(JBOOS_EJB_WEB_INF_CLASSES_SET).dir(seamGenResFolder);
				AntCopyUtils.copyFilesAndFolders(
						seamGenResFolder, new File(ejb, "ejbModule"), new AntCopyUtils.FileSetFileFilter(ejbSrcResourcesSet), viewFilterSetCollection, true); //$NON-NLS-1$

				// ********************************************************************************************
				// Copy seam project indicator
				// ********************************************************************************************
				AntCopyUtils.copyFileToFolder(new File(seamGenResFolder, "seam.properties"), new File(ejb, "ejbModule/"), true); //$NON-NLS-1$ //$NON-NLS-2$

				AntCopyUtils.copyFileToFolder(
						new File(seamGenResFolder, "META-INF/ejb-jar.xml"),  //$NON-NLS-1$
						new File(ejb, "ejbModule/META-INF/"),  //$NON-NLS-1$
						viewFilterSetCollection, true);

				/*AntCopyUtils.copyFileToFolder(
						hibernateConsolePref,
						new File(ejb,".settings"), //$NON-NLS-1$
						new FilterSetCollection(projectFilterSet), true);*/

				FilterSet ejbFilterSet =  new FilterSet();
				ejbFilterSet.addFilter("projectName", ejb.getName()); //$NON-NLS-1$

				AntCopyUtils.copyFileToFile(
						hibernateConsoleLaunchFile, 
						new File(ejb, ejb.getName() + ".launch"),  //$NON-NLS-1$
						new FilterSetCollection(ejbFilterSet), true);

				AntCopyUtils.copyFileToFolder(
						hibernateConsolePropsFile, 
						ejb,
						hibernateDialectFilterSet, true);

				File earContentsFolder = new File(ear, "EarContent"); //$NON-NLS-1$

				FilterSet earFilterSet =  new FilterSet();
				earFilterSet.addFilter("projectName", ear.getName() + ".ear"); //$NON-NLS-1$ //$NON-NLS-2$

				AntCopyUtils.copyFileToFolder(
						new File(seamGenResFolder, "META-INF/jboss-app.xml"), //$NON-NLS-1$
						new File(earContentsFolder, "META-INF"), //$NON-NLS-1$
						new FilterSetCollection(earFilterSet), true);

				// Copy configuration files from template
				AntCopyUtils.copyFilesAndFolders(
						new File(SeamFacetInstallDataModelProvider.getTemplatesFolder(), "ear"),  //$NON-NLS-1$
						ear, new AntCopyUtils.FileSetFileFilter(excludeCvsSvn),
						new FilterSetCollection(filterSet), true);

				// Fill ear contents
				AntCopyUtils.copyFiles(seamHomeFolder, earContentsFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_EAR_CONTENT).dir(seamHomeFolder)));
				AntCopyUtils.copyFiles(seamLibFolder, earContentsFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_EAR_CONTENT).dir(seamLibFolder)));
				AntCopyUtils.copyFiles(droolsLibFolder, earContentsFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_EAR_CONTENT).dir(droolsLibFolder)));
				AntCopyUtils.copyFiles(seamLibFolder, earContentsFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_EAR_CONTENT).dir(seamLibFolder)));
				AntCopyUtils.copyFiles(seamGenResFolder, earContentsFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_EAR_CONTENT).dir(seamGenResFolder)));						

				File resources = new File(ear, "resources");
				AntCopyUtils.copyFileToFile(
						dataSourceDsFile, 
						new File(resources, project.getName() + "-ds.xml"),  //$NON-NLS-1$ //$NON-NLS-2$
						viewFilterSetCollection, true);

				try {
					File[] earJars = earContentsFolder.listFiles(new FilenameFilter() {
						/* (non-Javadoc)
						 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
						 */
						public boolean accept(File dir, String name) {
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
					File ejbMetaInf = new File(ejb, "ejbModule/META-INF"); //$NON-NLS-1$
					AntCopyUtils.copyFileToFolder(new File(SeamFacetInstallDataModelProvider.getTemplatesFolder(), "ejb/ejbModule/META-INF/MANIFEST.MF"), ejbMetaInf, manifestFilterCol, true); //$NON-NLS-1$
				} catch (IOException e) {
					SeamCorePlugin.getPluginLog().logError(e);
				}
			} catch (IOException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
		}

		ClasspathHelper.addClasspathEntries(project, fv);

		createSeamProjectPreferenes(project, model);

		EclipseResourceUtil.addNatureToProject(project, ISeamProject.NATURE_ID);

		toggleHibernateOnProject(project, consoleName);

		project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		String wsPath = project.getLocation().removeLastSegments(1)
		                             .toFile().getAbsoluteFile().getPath();

		IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();

		if (!isWarConfiguration(model)) {
			IProject ejbProjectToBeImported = wsRoot.getProject(project.getName() + "-ejb");
			ResourcesUtils.importExistingProject(ejbProjectToBeImported, wsPath + "/" + project.getName() + "-ejb", project.getName() + "-ejb");
			toggleHibernateOnProject(ejbProjectToBeImported, consoleName);
			IProjectFacet sf = ProjectFacetsManager.getProjectFacet("jst.ejb");  
			IProjectFacetVersion pfv = ProjectFacetsManager.create(ejbProjectToBeImported).getInstalledVersion(sf);
			ClasspathHelper.addClasspathEntries(ejbProjectToBeImported, pfv);

			IProject earProjectToBeImported = wsRoot.getProject(project.getName() + "-ear");
			ResourcesUtils.importExistingProject(earProjectToBeImported, wsPath + "/" + project.getName() + "-ear", project.getName() + "-ear");
		}

		IProject testProjectToBeImported = wsRoot.getProject(project.getName() + "-test");
		ResourcesUtils.importExistingProject(testProjectToBeImported, wsPath + "/" + project.getName() + "-test", project.getName() + "-test");
		toggleHibernateOnProject(testProjectToBeImported, consoleName);
	}

	/**
	 * 
	 * @param model
	 * @return
	 */
	public static boolean isWarConfiguration(IDataModel model) {
		return "war".equals(model.getProperty(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS)); //$NON-NLS-1$
	}

	private void createTestProject(IDataModel model, IProject seamWebProject, SeamRuntime seamRuntime) {
		String projectName = model.getProperty(ISeamFacetDataModelProperties.SEAM_PROJECT_NAME).toString();
		File testProjectDir = new File(seamWebProject.getLocation().removeLastSegments(1).toFile(), projectName + "-test"); //$NON-NLS-1$
		testProjectDir.mkdir();

		IVirtualComponent component = ComponentCore.createComponent(seamWebProject);
		IVirtualFolder webRootVirtFolder = component.getRootFolder().getFolder(new Path("/")); //$NON-NLS-1$

		File testLibDir = new File(testProjectDir, "lib"); //$NON-NLS-1$
		File embededEjbDir = new File(testProjectDir, "embedded-ejb"); //$NON-NLS-1$
		File testSrcDir = new File(testProjectDir, "test-src"); //$NON-NLS-1$
		String seamGenResFolder = seamRuntime.getResourceTemplatesDir();
		File persistenceFile = new File(seamGenResFolder , "META-INF/persistence-" + (isWarConfiguration(model) ? TEST_WAR_PROFILE:TEST_EAR_PROFILE) + ".xml"); //$NON-NLS-1$ //$NON-NLS-2$
		File jbossBeansFile = new File(seamGenResFolder , "META-INF/jboss-beans.xml"); //$NON-NLS-1$
		FilterSet filterSet = new FilterSet();
		filterSet.addFilter("projectName", projectName); //$NON-NLS-1$
		filterSet.addFilter("runtimeName", WtpUtils.getServerRuntimeName(seamWebProject)); //$NON-NLS-1$
		filterSet.addFilter("webRootFolder", webRootVirtFolder.getUnderlyingFolder().getFullPath().removeFirstSegments(1).toString()); //$NON-NLS-1$

		final SeamRuntime selectedRuntime = SeamRuntimeManager.getInstance().findRuntimeByName(model.getProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME).toString());
		final String seamHomePath = selectedRuntime.getHomeDir();

		AntCopyUtils.FileSet includeLibs 
			= new AntCopyUtils.FileSet(JBOSS_TEST_LIB_FILESET)
											.dir(new File(seamRuntime.getHomeDir(), "lib")); //$NON-NLS-1$
		File[] libs = includeLibs.getDir().listFiles(new AntCopyUtils.FileSetFileFilter(includeLibs));
		StringBuffer testLibraries = new StringBuffer();

		for (File file : libs) {
			testLibraries.append("\t<classpathentry kind=\"lib\" path=\"lib/" + file.getName() + "\"/>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		StringBuffer requiredProjects = new StringBuffer();
		requiredProjects.append(
				"\t<classpathentry combineaccessrules=\"false\" kind=\"src\" path=\"/" + seamWebProject.getName() + "\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
		if (!isWarConfiguration(model)) {
			requiredProjects.append(
					"\n\t<classpathentry combineaccessrules=\"false\" kind=\"src\" path=\"/" + seamWebProject.getName() + "-ejb\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
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
				 = new AntCopyUtils.FileSet(CVS_SVN).dir(testTemplateDir);

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

		createComponentsProperties(testSrcDir, "", Boolean.TRUE); //$NON-NLS-1$
	}

	/**
	 * 
	 * @param seamGenResFolder
	 * @param projectName
	 * @param embedded
	 */
	private void createComponentsProperties(final File seamGenResFolder, String projectName, Boolean embedded) {
		Properties components = new Properties();
		String prefix = "".equals(projectName) ? "" : projectName + "/"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		components.put("embeddedEjb", embedded.toString()); //$NON-NLS-1$
		components.put("jndiPattern", prefix + "#{ejbName}/local"); //$NON-NLS-1$ //$NON-NLS-2$
		File componentsProps = new File(seamGenResFolder, "components.properties"); //$NON-NLS-1$
		try {
			componentsProps.createNewFile();
			components.store(new FileOutputStream(componentsProps), ""); //$NON-NLS-1$
		} catch (IOException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
	}

	/**
	 * 
	 * @param project
	 * @param defaultConsoleName
	 * @return
	 */
	public static boolean toggleHibernateOnProject(IProject project, String defaultConsoleName) {
		IScopeContext scope = new ProjectScope(project);

		Preferences node = scope.getNode("org.hibernate.eclipse.console");

		if (node != null) {
			node.putBoolean("hibernate3.enabled", true);
			node.put("default.configuration", defaultConsoleName);
			try {
				node.flush();
			} catch (BackingStoreException e) {
				SeamCorePlugin.getDefault().logError("Could not save changes to preferences", e);
				return false;
			}
		} else {
			return false;
		}

		try {
			addProjectNature(project, "org.hibernate.eclipse.console.hibernateNature", new NullProgressMonitor());
			return true;
		} catch (CoreException ce) {
			SeamCorePlugin.getDefault().logError("Could not activate Hibernate nature on project " + project.getName(), ce);			
			return false;
		}		
	}

	/**
	 * Add the given project nature to the given project (if it isn't already added).
	 * 
	 * @param project
	 * @param nature
	 * @param monitor
	 * @return true if nature where added, false if not
	 * @throws OperationCanceledException if job were canceled or CoreException if something went wrong. 
	 */
	public static boolean addProjectNature(IProject project, String nature, IProgressMonitor monitor) throws CoreException {
		if (monitor != null && monitor.isCanceled()) {
			throw new OperationCanceledException();
		}

		if (!project.hasNature(nature)) {
			IProjectDescription description = project.getDescription();
			String[] prevNatures = description.getNatureIds();
			String[] newNatures = new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			newNatures[prevNatures.length] = nature;
			description.setNatureIds(newNatures);
			project.setDescription(description, monitor);
			return true;
		} else {
			monitor.worked(1);
			return false;
		}
	}
}