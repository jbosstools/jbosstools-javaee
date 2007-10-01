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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jst.common.project.facet.core.ClasspathHelper;
import org.eclipse.jst.j2ee.web.componentcore.util.WebArtifactEdit;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.uriresolver.internal.URI;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.util.ResourcesUtils;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.osgi.service.prefs.BackingStoreException;

public class SeamFacetInstallDelegete extends Object implements IDelegate,ISeamFacetDataModelProperties {

	public static String DEV_WAR_PROFILE = "dev-war";
	public static String DEV_EAR_PROFILE = "dev";	
	public static String TEST_WAR_PROFILE = "test-war";
	public static String TEST_EAR_PROFILE = "test";
	
	public static AntCopyUtils.FileSet TOMCAT_WAR_LIB_FILESET = new AntCopyUtils.FileSet()
		.include("activation\\.jar")
		.include("ajax4jsf*.\\.jar")
		.include("commons-beanutils.*\\.jar")
		.include("commons-codec.*\\.jar")
		.include("commons-collections.*\\.jar")
		.include("commons-digester.*\\.jar")
		.include("commons-el.*\\.jar")
		.include("commons-lang.*\\.jar")
		.include("hibernate-all\\.jar")
		.include("itext.*\\.jar")
		.include("jboss-aop-jdk50\\.jar")
		.include("jboss-cache-jdk50\\.jar")
		.include("jboss-ejb3-all\\.jar")
		.include("jboss-seam-debug\\.jar")
		.include("jboss-seam-ui\\.jar")
		.include("jboss-seam\\.jar")
		.include("jcaptcha-all.*\\.jar")
		.include("jgroups\\.jar")
		.include("jsf-facelets\\.jar")
		.include("jstl.*\\.jar")
		.include("mail-ra\\.jar")
		.include("mail\\.jar")
		.include("mc-conf\\.jar")
		.include("myfaces-api.*\\.jar")
		.include("myfaces-impl.*\\.jar")
		.include("oscache.*\\.jar")
		.include("portlet-api-lib\\.jar")
		.include("richfaces.*\\.jar")
		.include("spring\\.jar")
		.include("thirdparty-all\\.jar");
	
	public static AntCopyUtils.FileSet JBOSS_WAR_LIB_FILESET_WAR_CONFIG = new AntCopyUtils.FileSet() 
		.include("ajax4jsf.*\\.jar")
		.include("antlr.*\\.jar")
		.include("commons-beanutils.*\\.jar")
		.include("commons-collections.*\\.jar")
		.include("commons-digester.*\\.jar")
		.include("commons-jci-core.*\\.jar")
		.include("commons-jci-janino.*\\.jar")
		.include("drools-compiler.*\\.jar")
		.include("drools-core.*\\.jar")
		.include("janino.*\\.jar")
		.include("jboss-seam-debug\\.jar")
		.include("jboss-seam-ioc\\.jar")
		.include("jboss-seam-mail\\.jar")
		.include("jboss-seam-pdf\\.jar")
		.include("jboss-seam-remoting\\.jar")
		.include("jboss-seam-ui\\.jar")
		.include("jboss-seam\\.jar")
		.include("jbpm.*\\.jar")
		.include("jsf-facelets\\.jar")
		.include("oscache.*\\.jar")
		.include("stringtemplate.*\\.jar");
	
	public static AntCopyUtils.FileSet JBOSS_TEST_LIB_FILESET = new AntCopyUtils.FileSet() 
		.include("testng-.*-jdk15\\.jar")
		.include("myfaces-api-.*\\.jar")
		.include("myfaces-impl-.*\\.jar")
		.include("servlet-api\\.jar")
		.include("hibernate-all\\.jar")
		.include("jboss-ejb3-all\\.jar")
		.include("thirdparty-all\\.jar")
		.include("el-api\\.jar")
		.include("el-ri\\.jar")
		.exclude(".*/CVS")
		.exclude(".*/\\.svn");
	
	public static AntCopyUtils.FileSet JBOSS_WAR_LIB_FILESET_EAR_CONFIG = new AntCopyUtils.FileSet() 
		.include("ajax4jsf.*\\.jar")
		.include("commons-beanutils.*\\.jar")
		.include("commons-digester.*\\.jar")
		.include("commons-collections.*\\.jar")
		.include("jboss-seam-debug\\.jar")
		.include("jboss-seam-ioc\\.jar")
		.include("jboss-seam-mail\\.jar")
		.include("jboss-seam-pdf\\.jar")
		.include("jboss-seam-remoting\\.jar")
		.include("jboss-seam-ui\\.jar")
		.include("jsf-facelets\\.jar")
		.include("oscache.*\\.jar");
	
	public static AntCopyUtils.FileSet JBOSS_EAR_CONTENT  = new AntCopyUtils.FileSet()
		.include("antlr.*\\.jar")
		.include("commons-jci-core.*\\.jar")
		.include("commons-jci-janino.*\\.jar")
		.include("drools-compiler.*\\.jar")
		.include("drools-core.*\\.jar")
		.include("janino.*\\.jar")
		.include("jboss-seam.jar")
		.include("jbpm.*\\.jar")
		.include("security\\.drl")
		.include("stringtemplate.*\\.jar");

	public static AntCopyUtils.FileSet JBOSS_EAR_CONTENT_META_INF = new AntCopyUtils.FileSet()
		.include("META-INF/application\\.xml")
		.include("META-INF/jboss-app\\.xml");
	
	public static AntCopyUtils.FileSet VIEW_FILESET = new AntCopyUtils.FileSet()
		.include("home\\.xhtml")
		.include("error\\.xhtml")
		.include("login\\.xhtml")
		.include("login\\.page.xml")
		.include("index\\.html")
		.include("layout")
		.include("layout/.*")
		.include("stylesheet")
		.include("stylesheet/.*")
		.include("img/.*")
		.include("img")
		.exclude(".*/.*\\.ftl")
		.exclude(".*/CVS")
		.exclude(".*/\\.svn");
	
	public static AntCopyUtils.FileSet CVS_SVN = new AntCopyUtils.FileSet()
		.include(".*")
		.exclude(".*/CVS")
		.exclude("CVS")
		.exclude(".*\\.svn")
		.exclude(".*/\\.svn");	
	
	public static AntCopyUtils.FileSet JBOOS_WAR_WEBINF_SET = new AntCopyUtils.FileSet()
		.include("WEB-INF")
		.include("WEB-INF/web\\.xml")
		.include("WEB-INF/pages\\.xml")
		.include("WEB-INF/jboss-web\\.xml")
		.include("WEB-INF/faces-config\\.xml")
		.include("WEB-INF/componets\\.xml");
	
	public static AntCopyUtils.FileSet JBOOS_WAR_WEB_INF_CLASSES_SET = new AntCopyUtils.FileSet()
		.include("import\\.sql")
		.include("security\\.drl")
		.include("seam\\.properties")
		.include("messages_en\\.properties");
	
	public static AntCopyUtils.FileSet JBOOS_EJB_WEB_INF_CLASSES_SET = new AntCopyUtils.FileSet()
		.include("import\\.sql")
		.include("seam\\.properties");
	
	public static AntCopyUtils.FileSet JBOSS_EAR_META_INF_SET = new AntCopyUtils.FileSet()
		.include("META-INF/jboss-app\\.xml");
	
	public static String DROOLS_LIB_SEAM_RELATED_PATH = "drools/lib";
	
	public static String SEAM_LIB_RELATED_PATH = "lib";
	
	public static String WEB_LIBRARIES_RELATED_PATH = "WEB-INF/lib";
	
	public void execute(final IProject project, IProjectFacetVersion fv,
			Object config, IProgressMonitor monitor) throws CoreException {
		final IDataModel model = (IDataModel)config;

		// get WebContents folder path from DWP model 
		IVirtualComponent com = ComponentCore.createComponent(project);
		IVirtualFolder webRootFolder = com.getRootFolder().getFolder(new Path("/"));
		final IVirtualFolder srcRootFolder = com.getRootFolder().getFolder(new Path("/WEB-INF/classes"));
		IContainer folder = webRootFolder.getUnderlyingFolder();
		
		model.setProperty(ISeamFacetDataModelProperties.SEAM_PROJECT_NAME, project.getName());
		model.setProperty(ISeamFacetDataModelProperties.SEAM_TEST_PROJECT, project.getName()+"-test");
		
		Boolean dbExists = (Boolean)model.getProperty(ISeamFacetDataModelProperties.DB_ALREADY_EXISTS);
		Boolean dbRecreate = (Boolean)model.getProperty(ISeamFacetDataModelProperties.RECREATE_TABLES_AND_DATA_ON_DEPLOY);
		if(!dbExists && !dbRecreate) {
			model.setProperty(ISeamFacetDataModelProperties.HIBERNATE_HBM2DDL_AUTO,"update");
		} else if(dbExists && !dbRecreate) {
			model.setProperty(ISeamFacetDataModelProperties.HIBERNATE_HBM2DDL_AUTO,"validate");
		} else if(dbRecreate) {
			model.setProperty(ISeamFacetDataModelProperties.HIBERNATE_HBM2DDL_AUTO,"create-drop");
		}
		
		final File webContentFolder = folder.getLocation().toFile();
		final File webInfFolder = new File(webContentFolder,"WEB-INF");
		final File webInfClasses = new File(webInfFolder,"classes");
		final File webInfClassesMetaInf = new File(webInfClasses, "META-INF");
		webInfClassesMetaInf.mkdirs();
		final File webLibFolder = new File(webContentFolder,WEB_LIBRARIES_RELATED_PATH);
		final File srcFolder = isWarConfiguration(model)?new File(srcRootFolder.getUnderlyingFolder().getLocation().toFile(),"model"):srcRootFolder.getUnderlyingFolder().getLocation().toFile();
		final File webMetaInf = new File(webContentFolder, "META-INF");
		final SeamRuntime selectedRuntime = SeamRuntimeManager.getInstance().findRuntimeByName(model.getProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME).toString());

		final String seamHomePath = selectedRuntime.getHomeDir();
		
		final File seamHomeFolder = new File(seamHomePath);
		final File seamLibFolder = new File(seamHomePath,SEAM_LIB_RELATED_PATH);
		final File seamGenResFolder = new File(seamHomePath,"seam-gen/resources");
		final File seamGenResMetainfFolder = new File(seamGenResFolder,"META-INF");
		
		final File droolsLibFolder = new File(seamHomePath,DROOLS_LIB_SEAM_RELATED_PATH);
		final File seamGenHomeFolder = new File(seamHomePath,"seam-gen");
		final File seamGenViewSource = new File(seamGenHomeFolder,"view");
		final File dataSourceDsFile = new File(seamGenResFolder, "datasource-ds.xml");
		final File componentsFile = new File(seamGenResFolder,"WEB-INF/components"+(isWarConfiguration(model)?"-war":"")+".xml");
		
		final File hibernateConsoleLaunchFile = new File(seamGenHomeFolder, "hibernatetools/hibernate-console.launch");
		final File hibernateConsolePropsFile = new File(seamGenHomeFolder, "hibernatetools/hibernate-console.properties");
		final File hibernateConsolePref = new File(seamGenHomeFolder, "hibernatetools/.settings/org.hibernate.eclipse.console.prefs");
		final File persistenceFile = new File(seamGenResFolder,"META-INF/persistence-" + (isWarConfiguration(model)?DEV_WAR_PROFILE:DEV_EAR_PROFILE) + ".xml");
		
		final File applicationFile = new File(seamGenResFolder,"META-INF/application.xml");

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
				new File(webInfFolder,"components.xml"),
				new FilterSetCollection(projectFilterSet), true);
		
		AntCopyUtils.copyFilesAndFolders(
				seamGenResFolder,webContentFolder,new AntCopyUtils.FileSetFileFilter(webInfSet), viewFilterSetCollection, true);
		

		final FilterSetCollection hibernateDialectFilterSet = new FilterSetCollection();
		hibernateDialectFilterSet.addFilterSet(jdbcFilterSet);
		hibernateDialectFilterSet.addFilterSet(projectFilterSet);
		hibernateDialectFilterSet.addFilterSet(SeamFacetFilterSetFactory.createHibernateDialectFilterSet(model));
		
	
		createTestProject(model,project,selectedRuntime);

		// ********************************************************************************************
		// Handle WAR/EAR configurations
		// ********************************************************************************************
		if(isWarConfiguration(model)) {

			AntCopyUtils.FileSet webInfClassesSet = new AntCopyUtils.FileSet(JBOOS_WAR_WEB_INF_CLASSES_SET).dir(seamGenResFolder);
			AntCopyUtils.copyFilesAndFolders(
					seamGenResFolder,srcFolder,new AntCopyUtils.FileSetFileFilter(webInfClassesSet), viewFilterSetCollection, true);
			
			createComponentsProperties(srcFolder, isWarConfiguration(model)?"":project.getName()+"-ear", false);
			
			AntCopyUtils.copyFileToFolder(
					hibernateConsolePref,
					new File(project.getLocation().toFile(),".settings"),	
					new FilterSetCollection(projectFilterSet), true);
			
			// In case of WAR configuration
			AntCopyUtils.copyFiles(seamHomeFolder,webLibFolder,new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_WAR_CONFIG).dir(seamHomeFolder)));
			AntCopyUtils.copyFiles(seamLibFolder,webLibFolder,new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_WAR_CONFIG).dir(seamLibFolder)));
			AntCopyUtils.copyFiles(droolsLibFolder,webLibFolder,new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_WAR_CONFIG).dir(droolsLibFolder)));

			// ********************************************************************************************
			// Copy seam project indicator
			// ********************************************************************************************
			AntCopyUtils.copyFileToFolder(new File(seamGenResFolder,"seam.properties"), srcFolder, true);
			final IContainer source = srcRootFolder.getUnderlyingFolder();
			
			IPath actionSrcPath = new Path(source.getFullPath().lastSegment()+"/action");
			IPath modelSrcPath = new Path(source.getFullPath().lastSegment()+"/model");

			srcRootFolder.delete(IVirtualFolder.FORCE, monitor);
			WtpUtils.createSourceFolder(project, actionSrcPath, new Path(source.getFullPath().lastSegment()), new Path("WebContent/WEB-INF/dev"));
			WtpUtils.createSourceFolder(project, modelSrcPath, new Path(source.getFullPath().lastSegment()), null);			
		
			IVirtualComponent c = ComponentCore.createComponent(project);
			IVirtualFolder src = c.getRootFolder().getFolder("/WEB-INF/classes");
			src.createLink(actionSrcPath, 0, null);
			src.createLink(modelSrcPath, 0, null);					
			
			AntCopyUtils.copyFileToFile(
					new File(seamGenHomeFolder,"src/Authenticator.java"),
					new File(project.getLocation().toFile(),source.getFullPath().lastSegment()+"/action/" + model.getProperty(ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_NAME).toString().replace('.', '/')+"/"+"Authenticator.java"),
					new FilterSetCollection(filtersFilterSet), true);

			AntCopyUtils.copyFileToFile(
					persistenceFile,
					new File(srcFolder,"META-INF/persistence.xml"),
					viewFilterSetCollection, true);

			AntCopyUtils.copyFileToFile(
					dataSourceDsFile, 
					new File(srcFolder,project.getName()+"-ds.xml"), 
					viewFilterSetCollection, true);
			
			AntCopyUtils.copyFileToFile(
					hibernateConsoleLaunchFile, 
					new File(project.getLocation().toFile(),project.getName()+".launch"), 
					viewFilterSetCollection, true);
			
			AntCopyUtils.copyFileToFolder(
					hibernateConsolePropsFile, 
					project.getLocation().toFile(),
					hibernateDialectFilterSet, true);
			
			// Copy JDBC driver if there is any
			if(model.getProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH)!=null)
				AntCopyUtils.copyFiles((String[])model.getProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH), webLibFolder);

			WtpUtils.setClasspathEntryAsExported(project, new Path("org.eclipse.jst.j2ee.internal.web.container"), monitor);

			Job create = new DataSourceXmlDeployer(project);
			create.setUser(true);
			create.setRule(ResourcesPlugin.getWorkspace().getRoot());
			create.schedule();
			
		} else {
			model.setProperty(ISeamFacetDataModelProperties.SEAM_EJB_PROJECT, project.getName()+"-ejb");
			model.setProperty(ISeamFacetDataModelProperties.SEAM_EAR_PROJECT, project.getName()+"-ear");
			
			// In case of EAR configuration
			AntCopyUtils.copyFiles(seamHomeFolder,webLibFolder,new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_EAR_CONFIG).dir(seamHomeFolder)));
			AntCopyUtils.copyFiles(seamLibFolder,webLibFolder,new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_EAR_CONFIG).dir(seamLibFolder)));
			AntCopyUtils.copyFiles(droolsLibFolder,webLibFolder,new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_EAR_CONFIG).dir(droolsLibFolder)));
			AntCopyUtils.copyFileToFolder(new File(seamGenResFolder,"messages_en.properties"),srcFolder, true);

			File ear = new File(project.getLocation().removeLastSegments(1).toFile(),model.getProperty(ISeamFacetDataModelProperties.SEAM_PROJECT_NAME)+"-ear");
			File ejb = new File(project.getLocation().removeLastSegments(1).toFile(),model.getProperty(ISeamFacetDataModelProperties.SEAM_PROJECT_NAME)+"-ejb");
			ear.mkdir();
			ejb.mkdir();
			
			try {
				FilterSet filterSet = new FilterSet();
				filterSet.addFilter("projectName", project.getName());
				filterSet.addFilter("runtimeName", WtpUtils.getServerRuntimeName(project));
				if(model.getProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH)!=null) {
					File driver = new File(((String[])model.getProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH))[0]);
					filterSet.addFilter("driverJar"," " + driver.getName() + "\n");
				} else {
					filterSet.addFilter("driverJar","");
				}
				AntCopyUtils.FileSet excludeCvsSvn = new AntCopyUtils.FileSet(CVS_SVN).dir(seamGenResFolder);
				
				AntCopyUtils.copyFilesAndFolders(
						new File(SeamFacetInstallDataModelProvider.getTemplatesFolder(),"ejb"), 
						ejb, new AntCopyUtils.FileSetFileFilter(excludeCvsSvn),
						new FilterSetCollection(filterSet), true);
				
				// *******************************************************************************************
				// Copy sources to ejb project in case of EAR configuration
				// *******************************************************************************************
				AntCopyUtils.copyFileToFile(
						new File(seamGenHomeFolder,"src/Authenticator.java"),
						new File(ejb,"ejbModule/" + model.getProperty(ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_NAME).toString().replace('.', '/')+"/"+"Authenticator.java"),
						new FilterSetCollection(filtersFilterSet), true);
				AntCopyUtils.copyFileToFile(
						persistenceFile,
						new File(ejb,"ejbModule/META-INF/persistence.xml"),
						viewFilterSetCollection, true);
				
				createComponentsProperties(new File(ejb,"ejbModule"), isWarConfiguration(model)?"":project.getName()+"-ear", false);
				
				AntCopyUtils.FileSet ejbSrcResourcesSet = new AntCopyUtils.FileSet(JBOOS_EJB_WEB_INF_CLASSES_SET).dir(seamGenResFolder);
				AntCopyUtils.copyFilesAndFolders(
						seamGenResFolder,new File(ejb,"ejbModule"),new AntCopyUtils.FileSetFileFilter(ejbSrcResourcesSet), viewFilterSetCollection, true);
		
				
				// ********************************************************************************************
				// Copy seam project indicator
				// ********************************************************************************************
				AntCopyUtils.copyFileToFolder(new File(seamGenResFolder,"seam.properties"), new File(ejb,"ejbModule/"), true);
				
				AntCopyUtils.copyFileToFile(
						dataSourceDsFile, 
						new File(ejb,"ejbModule/"+project.getName()+"-ds.xml"), 
						viewFilterSetCollection, true);
				
				AntCopyUtils.copyFileToFolder(
						new File(seamGenResFolder,"META-INF/ejb-jar.xml"), 
						new File(ejb,"ejbModule/META-INF/"), 
						viewFilterSetCollection, true);
				
				AntCopyUtils.copyFileToFolder(
						hibernateConsolePref,
						new File(ejb,".settings"),
						new FilterSetCollection(projectFilterSet), true);
				
				FilterSet ejbFilterSet =  new FilterSet();
				ejbFilterSet.addFilter("projectName",ejb.getName());
				
				AntCopyUtils.copyFileToFile(
						hibernateConsoleLaunchFile, 
						new File(ejb,ejb.getName()+".launch"), 
						new FilterSetCollection(ejbFilterSet), true);
				
				AntCopyUtils.copyFileToFolder(
						hibernateConsolePropsFile, 
						ejb,
						hibernateDialectFilterSet, true);
				
				File earContentsFolder = new File(ear,"EarContent");

				FilterSet earFilterSet =  new FilterSet();
				earFilterSet.addFilter("projectName",ear.getName()+".ear");
				
				AntCopyUtils.copyFileToFolder(
						new File(seamGenResFolder,"META-INF/jboss-app.xml"),
						new File(earContentsFolder,"META-INF"),
						new FilterSetCollection(earFilterSet),true);

				// Copy configuration files from template
				AntCopyUtils.copyFilesAndFolders(
						new File(SeamFacetInstallDataModelProvider.getTemplatesFolder(),"ear"), 
						ear, new AntCopyUtils.FileSetFileFilter(excludeCvsSvn),
						new FilterSetCollection(filterSet), true);
				
				// Fill ear contents
				AntCopyUtils.copyFiles(seamHomeFolder,earContentsFolder,new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_EAR_CONTENT).dir(seamHomeFolder)));
				AntCopyUtils.copyFiles(seamLibFolder,earContentsFolder,new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_EAR_CONTENT).dir(seamLibFolder)));
				AntCopyUtils.copyFiles(droolsLibFolder,earContentsFolder,new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_EAR_CONTENT).dir(droolsLibFolder)));
				AntCopyUtils.copyFiles(seamLibFolder,earContentsFolder,new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_EAR_CONTENT).dir(seamLibFolder)));
				AntCopyUtils.copyFiles(seamGenResFolder,earContentsFolder,new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_EAR_CONTENT).dir(seamGenResFolder)));						
				

				
				if(model.getProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH)!=null)
					AntCopyUtils.copyFiles((String[])model.getProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH), earContentsFolder);

				try {
					
					File[] earJars = earContentsFolder.listFiles(new FilenameFilter() {
						/* (non-Javadoc)
						 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
						 */
						public boolean accept(File dir, String name) {
							if(name.lastIndexOf(".jar")>0) return true;
							return false;
						}
					});
					String earJarsStr = "";
					for (File file : earJars) {
						earJarsStr +=" " + file.getName() +" \n";
					}
					
					FilterSetCollection manifestFilterCol = new FilterSetCollection(projectFilterSet);
					FilterSet manifestFilter = new FilterSet();
					manifestFilter.addFilter("earLibs",earJarsStr);
					manifestFilterCol.addFilterSet(manifestFilter);
					AntCopyUtils.copyFileToFolder(new File(SeamFacetInstallDataModelProvider.getTemplatesFolder(),"war/META-INF/MANIFEST.MF"), webMetaInf, manifestFilterCol, true);
					File ejbMetaInf = new File(ejb,"ejbModule/META-INF");
					AntCopyUtils.copyFileToFolder(new File(SeamFacetInstallDataModelProvider.getTemplatesFolder(),"ejb/ejbModule/META-INF/MANIFEST.MF"), ejbMetaInf, manifestFilterCol, true);
				} catch (IOException e) {
					SeamCorePlugin.getPluginLog().logError(e);
				}
				
			} catch (IOException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
		}

		ClasspathHelper.addClasspathEntries(project, fv);
		
		createSeamProjectPreferenes(project, model);
		
		try {
			EclipseResourceUtil.addNatureToProject(project, ISeamProject.NATURE_ID);
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			String wsPath = project.getLocation().removeLastSegments(1)
			                             .toFile().getAbsoluteFile().getPath();
			if(!isWarConfiguration(model)) {
				ResourcesUtils.importProject(
						wsPath+"/"+project.getName()+"-ejb", monitor);
				ResourcesUtils.importProject(
						wsPath+"/"+project.getName()+"-ear", monitor);
			}
			ResourcesUtils.importProject(
					wsPath+"/"+project.getName()+"-test", monitor);

		} catch (IOException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		} catch (InvocationTargetException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		} catch (InterruptedException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}

	}



	public static boolean isWarConfiguration(IDataModel model) {
		return "war".equals(model.getProperty(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS));
	}
	
	
	/**
	 * @param project
	 * @param model
	 */
	private void createSeamProjectPreferenes(final IProject project,
			final IDataModel model) {
		IScopeContext projectScope = new ProjectScope(project);
		IEclipsePreferences prefs = projectScope.getNode(SeamCorePlugin.PLUGIN_ID);
		
		prefs.put(JBOSS_AS_DEPLOY_AS, model.getProperty(JBOSS_AS_DEPLOY_AS).toString());
		
		prefs.put(SEAM_RUNTIME_NAME, model.getProperty(SEAM_RUNTIME_NAME).toString());
		
		prefs.put(SEAM_CONNECTION_PROFILE,model.getProperty(SEAM_CONNECTION_PROFILE).toString());
		
		prefs.put(SESION_BEAN_PACKAGE_NAME, model.getProperty(SESION_BEAN_PACKAGE_NAME).toString());
		
		prefs.put(ENTITY_BEAN_PACKAGE_NAME, model.getProperty(ENTITY_BEAN_PACKAGE_NAME).toString());
		
		prefs.put(TEST_CASES_PACKAGE_NAME, model.getProperty(TEST_CASES_PACKAGE_NAME).toString());
	
		prefs.put(SEAM_TEST_PROJECT, 
				model.getProperty(SEAM_TEST_PROJECT)==null?
						"":model.getProperty(SEAM_TEST_PROJECT).toString());
		
		if(DEPLOY_AS_EAR.equals(model.getProperty(JBOSS_AS_DEPLOY_AS))) {
			prefs.put(SEAM_EJB_PROJECT, 
					model.getProperty(SEAM_EJB_PROJECT)==null?
							"":model.getProperty(SEAM_EJB_PROJECT).toString());
			
			prefs.put(SEAM_EAR_PROJECT, 
					model.getProperty(SEAM_EAR_PROJECT)==null?
							"":model.getProperty(SEAM_EAR_PROJECT).toString());
		}
		
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
	}

	private void createTestProject(IDataModel model, IProject seamWebProject, SeamRuntime seamRuntime) {
			String projectName = model.getProperty(ISeamFacetDataModelProperties.SEAM_PROJECT_NAME).toString();
			File testProjectDir = new File(seamWebProject.getLocation().removeLastSegments(1).toFile(),projectName+"-test");
			testProjectDir.mkdir();
			File testLibDir = new File(testProjectDir,"lib");
			File embededEjbDir = new File(testProjectDir,"embedded-ejb");
			File testSrcDir = new File(testProjectDir,"test-src");
			String seamGenResFolder = seamRuntime.getHomeDir()+"/seam-gen/resources";
			File persistenceFile = new File(seamGenResFolder ,"META-INF/persistence-" + (isWarConfiguration(model)?TEST_WAR_PROFILE:TEST_EAR_PROFILE) + ".xml");
			File jbossBeansFile = new File(seamGenResFolder ,"META-INF/jboss-beans.xml");
			FilterSet filterSet = new FilterSet();
			filterSet.addFilter("projectName", projectName);
			filterSet.addFilter("runtimeName", WtpUtils.getServerRuntimeName(seamWebProject));
		
	
			final SeamRuntime selectedRuntime = SeamRuntimeManager.getInstance().findRuntimeByName(model.getProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME).toString());
			final String seamHomePath = selectedRuntime.getHomeDir();
			
			AntCopyUtils.FileSet includeLibs 
				= new AntCopyUtils.FileSet(JBOSS_TEST_LIB_FILESET)
												.dir(new File(seamRuntime.getHomeDir(),"lib"));
			File[] libs = includeLibs.getDir().listFiles(new AntCopyUtils.FileSetFileFilter(includeLibs));
			StringBuffer testLibraries = new StringBuffer();
			
			for (File file : libs) {
				testLibraries.append("\t<classpathentry kind=\"lib\" path=\"lib/" + file.getName() + "\"/>\n");
			}
			
			StringBuffer requiredProjects = new StringBuffer();
			requiredProjects.append(
					"\t<classpathentry combineaccessrules=\"false\" kind=\"src\" path=\"/" + seamWebProject.getName() + "\"/>");
			if(!isWarConfiguration(model)) {
				requiredProjects.append(
						"\n\t<classpathentry combineaccessrules=\"false\" kind=\"src\" path=\"/" + seamWebProject.getName() + "-ejb\"/>");
			} 
			filterSet.addFilter("testLibraries",testLibraries.toString());
			filterSet.addFilter("requiredProjects",requiredProjects.toString());
			File testTemplateDir = null;
			try {
				testTemplateDir = new File(SeamFacetInstallDataModelProvider.getTemplatesFolder(),"test");
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
			
			excludeCvsSvn.dir(new File(seamRuntime.getHomeDir(),"embedded-ejb/conf"));
			AntCopyUtils.copyFiles(
					new File(seamRuntime.getHomeDir(),"embedded-ejb/conf"),
					embededEjbDir,
					new AntCopyUtils.FileSetFileFilter(excludeCvsSvn));
			
			AntCopyUtils.copyFileToFile(
					persistenceFile,
					new File(testProjectDir,"test-src/META-INF/persistence.xml"),
					new FilterSetCollection(filterSet), true);

			AntCopyUtils.copyFileToFolder(
					jbossBeansFile,
					new File(testProjectDir,"test-src/META-INF"),
					new FilterSetCollection(filterSet), true);
			
			AntCopyUtils.copyFiles(
					new File(seamRuntime.getHomeDir(),"lib"),
					testLibDir,
					new AntCopyUtils.FileSetFileFilter(includeLibs));
			
			createComponentsProperties(testSrcDir, "", Boolean.TRUE);
		}

	/**
	 * @param seamGenResFolder
	 */
	private void createComponentsProperties(final File seamGenResFolder, String projectName, Boolean embedded) {
		Properties components = new Properties();
		String prefix = "".equals(projectName)?"":projectName+"/";
		components.put("embeddedEjb", embedded.toString());
		components.put("jndiPattern", prefix+"#{ejbName}/local");
		File componentsProps = new File(seamGenResFolder,"components.properties");
		try {
			componentsProps.createNewFile();
			components.store(new FileOutputStream(componentsProps), "");
		} catch (IOException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.project.facet.core.IActionConfigFactory#create()
	 */
	public Object create() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}
}
