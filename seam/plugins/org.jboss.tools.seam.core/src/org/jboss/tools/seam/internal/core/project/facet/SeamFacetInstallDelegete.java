/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.internal.core.project.facet;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jst.common.project.facet.core.ClasspathHelper;
import org.eclipse.jst.j2ee.web.componentcore.util.WebArtifactEdit;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;

public class SeamFacetInstallDelegete extends Object implements IDelegate {

	public static String DEV_WAR_PROFILE = "dev-war";
	public static String DEV_EAR_PROFILE = "dev";	
	
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
		.include("stringtemplate.*\\.jar")
		.include("testng-.*\\.jar");
	
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
		.include("stringtemplate.*\\.jar")
		.include("testng-.*\\.jar");

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
		.include("stylesheet/.*")
		.include("img/.*")
		.include("img")
		.exclude(".*/.*\\.ftl");
	
	public static AntCopyUtils.FileSet JBOOS_WAR_WEBINF_SET = new AntCopyUtils.FileSet()
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
	
	public static AntCopyUtils.FileSet JBOSS_EAR_META_INF_SET = new AntCopyUtils.FileSet()
		.include("META-INF/jboss-app\\.xml");
	
	public static String DROOLS_LIB_SEAM_RELATED_PATH = "drools/lib";
	
	public static String SEAM_LIB_RELATED_PATH = "lib";
	
	public static String WEB_LIBRARIES_RELATED_PATH = "WEB-INF/lib";
	
	public void execute(final IProject project, IProjectFacetVersion fv,
			Object config, IProgressMonitor monitor) throws CoreException {
		final IDataModel model = (IDataModel)config;

		// get WebContents folder path from DWP model 
		WebArtifactEdit edit = WebArtifactEdit.getWebArtifactEditForRead(project);
		IVirtualComponent com = ComponentCore.createComponent(project);
		IVirtualFolder webRootFolder = com.getRootFolder().getFolder(new Path("/"));
		IVirtualFolder srcRootFolder = com.getRootFolder().getFolder(new Path("/WEB-INF/classes"));
		IContainer folder = webRootFolder.getUnderlyingFolder();
		edit.dispose();
		model.setProperty(ISeamFacetDataModelProperties.SEAM_PROJECT_NAME, project.getName());
		
		final File webContentFolder = folder.getLocation().toFile();
		final File webInfFolder = new File(webContentFolder,"WEB-INF");
		final File webInfClasses = new File(webInfFolder,"classes");
		final File webInfClassesMetaInf = new File(webInfClasses, "META-INF");
		webInfClassesMetaInf.mkdirs();
		final File webLibFolder = new File(webContentFolder,WEB_LIBRARIES_RELATED_PATH);
		final File srcFolder = srcRootFolder.getUnderlyingFolder().getLocation().toFile();
		final File webMetaInf = new File(webContentFolder, "META-INF");
		
		final String seamHomePath = model.getProperty(ISeamFacetDataModelProperties.JBOSS_SEAM_HOME).toString();
		
		final File seamHomeFolder = new File(seamHomePath);
		final File seamLibFolder = new File(seamHomePath,SEAM_LIB_RELATED_PATH);
		final File seamGenResFolder = new File(seamHomePath,"seam-gen/resources");
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
		
		AntCopyUtils.FileSet webInfClassesSet = new AntCopyUtils.FileSet(JBOOS_WAR_WEB_INF_CLASSES_SET).dir(seamGenResFolder);
		AntCopyUtils.copyFilesAndFolders(
				seamGenResFolder,srcFolder,new AntCopyUtils.FileSetFileFilter(webInfClassesSet), viewFilterSetCollection, true);

		FilterSetCollection hibernateDialectFilterSet = new FilterSetCollection();
		hibernateDialectFilterSet.addFilterSet(jdbcFilterSet);
		hibernateDialectFilterSet.addFilterSet(projectFilterSet);
		hibernateDialectFilterSet.addFilterSet(SeamFacetFilterSetFactory.createHibernateDialectFilterSet(model));
		
		AntCopyUtils.copyFileToFolder(
				hibernateConsolePropsFile, 
				project.getLocation().toFile(),
				hibernateDialectFilterSet, true);

		// ********************************************************************************************
		// Handle WAR/EAR configurations
		// ********************************************************************************************
		if(isWarConfiguration(model)) {
			
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
			
			// Copy sources to src
			AntCopyUtils.copyFileToFile(
					new File(seamGenHomeFolder,"src/Authenticator.java"),
					new File(project.getLocation().toFile(),"src/" + model.getProperty(ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_NAME).toString().replace('.', '/')+"/"+"Authenticator.java"),
					new FilterSetCollection(filtersFilterSet), true);

			AntCopyUtils.copyFileToFile(
					persistenceFile,
					new File(project.getLocation().toFile(),"src/META-INF/persistence.xml"),
					viewFilterSetCollection, true);

			AntCopyUtils.copyFileToFile(
					dataSourceDsFile, 
					new File(srcFolder,project.getName()+"-ds.xml"), 
					viewFilterSetCollection, true);
			
			AntCopyUtils.copyFileToFile(
					hibernateConsoleLaunchFile, 
					new File(project.getLocation().toFile(),project.getName()+".launch"), 
					new FilterSetCollection(projectFilterSet), true);
			
			// Copy JDBC driver if there is any
			if(model.getProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH)!=null)
				AntCopyUtils.copyFiles((String[])model.getProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH), webLibFolder);

			
		} else {
			
			// In case of EAR configuration
			AntCopyUtils.copyFiles(seamHomeFolder,webLibFolder,new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_EAR_CONFIG).dir(seamHomeFolder)));
			AntCopyUtils.copyFiles(seamLibFolder,webLibFolder,new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_EAR_CONFIG).dir(seamLibFolder)));
			AntCopyUtils.copyFiles(droolsLibFolder,webLibFolder,new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_EAR_CONFIG).dir(droolsLibFolder)));
			try {
				AntCopyUtils.copyFileToFolder(new File(SeamFacetInstallDataModelProvider.getTemplatesFolder(),"war/META-INF/MANIFEST.MF"), webMetaInf, new FilterSetCollection(projectFilterSet), true);
			} catch (IOException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
			Job create = new Job("Creating EAR and EJB modules") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					
					IProject ear = WtpUtils.createEclipseProject(model.getProperty(ISeamFacetDataModelProperties.SEAM_PROJECT_NAME)+"-ear", monitor);
					IProject ejb = WtpUtils.createEclipseProject(model.getProperty(ISeamFacetDataModelProperties.SEAM_PROJECT_NAME)+"-ejb", monitor);					try {
						FilterSet filterSet = new FilterSet();
						filterSet.addFilter("projectName", project.getName());
						filterSet.addFilter("runtimeName", WtpUtils.getServerRuntimeName(project));
						
											
						
						AntCopyUtils.copyFilesAndFolders(
								new File(SeamFacetInstallDataModelProvider.getTemplatesFolder(),"ejb"), 
								ejb.getLocation().toFile(), 
								new FilterSetCollection(filterSet), true);
						
						// *******************************************************************************************
						// Copy sources to ejb project in case of EAR configuration
						// *******************************************************************************************
						AntCopyUtils.copyFileToFile(
								new File(seamGenHomeFolder,"src/Authenticator.java"),
								new File(ejb.getLocation().toFile(),"ejbModule/" + model.getProperty(ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_NAME).toString().replace('.', '/')+"/"+"Authenticator.java"),
								new FilterSetCollection(filtersFilterSet), true);
						AntCopyUtils.copyFileToFile(
								persistenceFile,
								new File(ejb.getLocation().toFile(),"ejbModule/META-INF/persistence.xml"),
								viewFilterSetCollection, true);
						// ********************************************************************************************
						// Copy seam project indicator
						// ********************************************************************************************
						AntCopyUtils.copyFileToFolder(new File(seamGenResFolder,"seam.properties"), new File(ejb.getLocation().toFile(),"ejbModule/"), true);
						
						AntCopyUtils.copyFileToFile(
								dataSourceDsFile, 
								new File(ejb.getLocation().toFile(),"ejbModule/"+project.getName()+"-ds.xml"), 
								viewFilterSetCollection, true);
						
						AntCopyUtils.copyFileToFolder(
								new File(seamGenResFolder,"META-INF/ejb-jar.xml"), 
								new File(ejb.getLocation().toFile(),"ejbModule/META-INF/"), 
								viewFilterSetCollection, true);
						
						AntCopyUtils.copyFileToFolder(
								hibernateConsolePref,
								new File(ejb.getLocation().toFile(),".settings"),
								new FilterSetCollection(projectFilterSet), true);
						
						AntCopyUtils.copyFileToFile(
								hibernateConsoleLaunchFile, 
								new File(ejb.getLocation().toFile(),ejb.getName()+".launch"), 
								new FilterSetCollection(projectFilterSet), true);
						
						
						File earContentsFolder = new File(ear.getLocation().toFile(),"EarContent");
						File earContentsMetaInfFolder = new File(earContentsFolder,"META-INF");
			
						AntCopyUtils.copyFilesAndFolders(
								seamGenResFolder,
								earContentsFolder,
								new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_EAR_META_INF_SET).dir(seamGenResFolder)),
								viewFilterSetCollection,true);

						// Copy configuration files from template
						AntCopyUtils.copyFilesAndFolders(
								new File(SeamFacetInstallDataModelProvider.getTemplatesFolder(),"ear"), 
								ear.getLocation().toFile(), 
								new FilterSetCollection(filterSet), true);
						
						// Fill ear contents
						AntCopyUtils.copyFiles(seamHomeFolder,earContentsFolder,new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_EAR_CONTENT).dir(seamHomeFolder)));
						AntCopyUtils.copyFiles(seamLibFolder,earContentsFolder,new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_EAR_CONTENT).dir(seamLibFolder)));
						AntCopyUtils.copyFiles(droolsLibFolder,earContentsFolder,new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_EAR_CONTENT).dir(droolsLibFolder)));
						AntCopyUtils.copyFiles(seamLibFolder,earContentsFolder,new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_EAR_CONTENT).dir(seamLibFolder)));
						AntCopyUtils.copyFiles(seamGenResFolder,earContentsFolder,new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_EAR_CONTENT).dir(seamGenResFolder)));						
			
						// Copy JDBC driver if there is any
						if(model.getProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH)!=null)
							AntCopyUtils.copyFiles((String[])model.getProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH), earContentsFolder);
					} catch (IOException e) {
						SeamCorePlugin.getPluginLog().logError(e);
					} finally {
						try {
							ejb.refreshLocal(IResource.DEPTH_INFINITE, monitor);
							ear.refreshLocal(IResource.DEPTH_INFINITE, monitor);
						} catch(CoreException e) {
							SeamCorePlugin.getPluginLog().logError(e);
						}
					}
					return Status.OK_STATUS;
				}
			};
			create.setRule(ResourcesPlugin.getWorkspace().getRoot());
			create.schedule();
		}
		ClasspathHelper.addClasspathEntries(project, fv);
		project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		
		EclipseResourceUtil.addNatureToProject(project, ISeamProject.NATURE_ID);

	}

	public static boolean isWarConfiguration(IDataModel model) {
		return "war".equals(model.getProperty(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS));
	}
}
