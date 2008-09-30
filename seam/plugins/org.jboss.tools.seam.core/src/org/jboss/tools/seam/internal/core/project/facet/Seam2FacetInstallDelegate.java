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

import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.common.project.facet.core.ClasspathHelper;
import org.eclipse.jst.javaee.web.WebApp;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;

// TODO: why not just *one* global filter set to avoid any missing names ? (assert for it in our unittests!
public class Seam2FacetInstallDelegate extends SeamFacetAbstractInstallDelegate{

	public static AntCopyUtils.FileSet JBOSS_WAR_LIB_FILESET_WAR_CONFIG = new AntCopyUtils.FileSet()	
		.include("ajax4jsf.*\\.jar") //$NON-NLS-1$
		.include("richfaces.*\\.jar")
		.include("antlr-runtime.*\\.jar") //$NON-NLS-1$		
		.include("commons-beanutils.*\\.jar") //$NON-NLS-1$
		//.include("commons-collections.*\\.jar") //$NON-NLS-1$
		.include("commons-digester.*\\.jar") //$NON-NLS-1$
		.include("commons-jci-core.*\\.jar") //$NON-NLS-1$
		.include("commons-jci-janino.*\\.jar") //$NON-NLS-1$
		.include("drools-compiler.*\\.jar") //$NON-NLS-1$
		.include("drools-core.*\\.jar") //$NON-NLS-1$
		.include("core.jar") //$NON-NLS-1$
		//.include("janino.*\\.jar") //$NON-NLS-1$		
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
	    .include("mvel14.jar") //$NON-NLS-1$
	    .include("jboss-el.jar"); //$NON-NLS-1$

	public static AntCopyUtils.FileSet JBOSS_WAR_LIB_FILESET_EAR_CONFIG = new AntCopyUtils.FileSet() 
		.include("richfaces-impl\\.jar") //$NON-NLS-1$
		.include("richfaces-ui\\.jar") //$NON-NLS-1$
		.include("commons-beanutils\\.jar") //$NON-NLS-1$
		.include("commons-digester\\.jar") //$NON-NLS-1$
		.include("jboss-seam-debug\\.jar") //$NON-NLS-1$
		.include("jboss-seam-ioc\\.jar") //$NON-NLS-1$
		.include("jboss-seam-mail\\.jar") //$NON-NLS-1$
		.include("jboss-seam-pdf\\.jar") //$NON-NLS-1$
		.include("jboss-seam-remoting\\.jar") //$NON-NLS-1$
		.include("jboss-seam-ui\\.jar") //$NON-NLS-1$
		.include("jsf-facelets\\.jar"); //$NON-NLS-1$

	public static String DROOLS_LIB_SEAM_RELATED_PATH = "lib"; //$NON-NLS-1$

	public void doExecute(final IProject project, IProjectFacetVersion fv,
			Object config, IProgressMonitor monitor) throws CoreException {
		final IDataModel model = (IDataModel)config;

		// get WebContents folder path from DWP model 
		IVirtualComponent component = ComponentCore.createComponent(project);
		IVirtualFolder webRootVirtFolder = component.getRootFolder().getFolder(new Path("/")); //$NON-NLS-1$
		final IVirtualFolder srcRootFolder = component.getRootFolder().getFolder(new Path("/WEB-INF/classes")); //$NON-NLS-1$
		IContainer webRootFolder = webRootVirtFolder.getUnderlyingFolder();

		model.setProperty(ISeamFacetDataModelProperties.SEAM_PROJECT_NAME, project.getName());

		Boolean dbExists = (Boolean)model.getProperty(ISeamFacetDataModelProperties.DB_ALREADY_EXISTS);
		Boolean dbRecreate = (Boolean)model.getProperty(ISeamFacetDataModelProperties.RECREATE_TABLES_AND_DATA_ON_DEPLOY);
		if(!dbExists && !dbRecreate) {
			model.setProperty(ISeamFacetDataModelProperties.HIBERNATE_HBM2DDL_AUTO,"update"); //$NON-NLS-1$
		} else if(dbExists && !dbRecreate) {
			model.setProperty(ISeamFacetDataModelProperties.HIBERNATE_HBM2DDL_AUTO,"validate"); //$NON-NLS-1$
		} else if(dbRecreate) {
			model.setProperty(ISeamFacetDataModelProperties.HIBERNATE_HBM2DDL_AUTO,"create-drop"); //$NON-NLS-1$
		}

		final File webContentFolder = webRootFolder.getLocation().toFile();
		final File webInfFolder = new File(webContentFolder,"WEB-INF"); //$NON-NLS-1$
		final File webInfClasses = new File(webInfFolder,"classes"); //$NON-NLS-1$
		final File webInfClassesMetaInf = new File(webInfClasses, "META-INF"); //$NON-NLS-1$
		webInfClassesMetaInf.mkdirs();
		final File webLibFolder = new File(webContentFolder,WEB_LIBRARIES_RELATED_PATH);
		final File srcFolder = isWarConfiguration(model)?new File(srcRootFolder.getUnderlyingFolder().getLocation().toFile(),"model"):srcRootFolder.getUnderlyingFolder().getLocation().toFile(); //$NON-NLS-1$
		final SeamRuntime selectedRuntime = SeamRuntimeManager.getInstance().findRuntimeByName(model.getProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME).toString());

		final String seamHomePath = selectedRuntime.getHomeDir();

		final File seamHomeFolder = new File(seamHomePath);
		final File seamLibFolder = new File(seamHomePath,SEAM_LIB_RELATED_PATH);
		final File seamGenResFolder = new File(seamHomePath,"seam-gen/resources"); //$NON-NLS-1$

		final File droolsLibFolder = new File(seamHomePath,DROOLS_LIB_SEAM_RELATED_PATH);
		final File seamGenHomeFolder = new File(seamHomePath,"seam-gen"); //$NON-NLS-1$
		final File seamGenViewSource = new File(seamGenHomeFolder,"view"); //$NON-NLS-1$
		final File dataSourceDsFile = new File(seamGenResFolder, "datasource-ds.xml"); //$NON-NLS-1$
		final File componentsFile = new File(seamGenResFolder,"WEB-INF/components"+(isWarConfiguration(model)?"-war":"")+".xml"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		final File hibernateConsoleLaunchFile = new File(seamGenHomeFolder, "hibernatetools/hibernate-console.launch"); //$NON-NLS-1$
		final File hibernateConsolePropsFile = new File(seamGenHomeFolder, "hibernatetools/hibernate-console.properties"); //$NON-NLS-1$
		//final File hibernateConsolePref = new File(seamGenHomeFolder, "hibernatetools/.settings/org.hibernate.eclipse.console.prefs"); //$NON-NLS-1$
		final File persistenceFile = new File(seamGenResFolder,"META-INF/persistence-" + (isWarConfiguration(model)?DEV_WAR_PROFILE:DEV_EAR_PROFILE) + ".xml"); //$NON-NLS-1$ //$NON-NLS-2$

		//final File applicationFile = new File(seamGenResFolder,"META-INF/application.xml"); //$NON-NLS-1$

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
		viewFilterSetCollection.addFilterSet(SeamFacetFilterSetFactory.createHibernateDialectFilterSet(model));

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

		configureWebXml(project);
		
		AntCopyUtils.copyFileToFile(
				componentsFile,
				new File(webInfFolder,"components.xml"), //$NON-NLS-1$
				new FilterSetCollection(projectFilterSet), true);

		AntCopyUtils.copyFilesAndFolders(
				seamGenResFolder,webContentFolder,new AntCopyUtils.FileSetFileFilter(webInfSet), viewFilterSetCollection, true);

		final FilterSetCollection hibernateDialectFilterSet = new FilterSetCollection();
		hibernateDialectFilterSet.addFilterSet(jdbcFilterSet);
		hibernateDialectFilterSet.addFilterSet(projectFilterSet);
		hibernateDialectFilterSet.addFilterSet(SeamFacetFilterSetFactory.createHibernateDialectFilterSet(model));

		// ********************************************************************************************
		// Handle WAR configurations
		// ********************************************************************************************
		if(isWarConfiguration(model)) {
			AntCopyUtils.FileSet webInfClassesSet = new AntCopyUtils.FileSet(JBOOS_WAR_WEB_INF_CLASSES_SET).dir(seamGenResFolder);
			AntCopyUtils.copyFilesAndFolders(
					seamGenResFolder,srcFolder,new AntCopyUtils.FileSetFileFilter(webInfClassesSet), viewFilterSetCollection, true);

			createComponentsProperties(srcFolder, isWarConfiguration(model)?"":project.getName()+"-ear", false); //$NON-NLS-1$ //$NON-NLS-2$

			/*AntCopyUtils.copyFileToFolder(
					hibernateConsolePref,
					new File(project.getLocation().toFile(),".settings"),	 //$NON-NLS-1$
					new FilterSetCollection(projectFilterSet), true);*/

			// In case of WAR configuration
			AntCopyUtils.copyFiles(seamHomeFolder,webLibFolder,new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_WAR_CONFIG).dir(seamHomeFolder)));
			AntCopyUtils.copyFiles(seamLibFolder,webLibFolder,new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_WAR_CONFIG).dir(seamLibFolder)));
			AntCopyUtils.copyFiles(droolsLibFolder,webLibFolder,new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_WAR_CONFIG).dir(droolsLibFolder)));

			// ********************************************************************************************
			// Copy seam project indicator
			// ********************************************************************************************
			final IContainer source = srcRootFolder.getUnderlyingFolder();

			IPath actionSrcPath = new Path(source.getFullPath().removeFirstSegments(1)+"/action"); //$NON-NLS-1$
			IPath modelSrcPath = new Path(source.getFullPath().removeFirstSegments(1)+"/model"); //$NON-NLS-1$

			srcRootFolder.delete(IVirtualFolder.FORCE, monitor);
			WtpUtils.createSourceFolder(project, actionSrcPath, source.getFullPath().removeFirstSegments(1), webRootFolder.getFullPath().removeFirstSegments(1).append("WEB-INF/dev")); //$NON-NLS-1$
			WtpUtils.createSourceFolder(project, modelSrcPath, source.getFullPath().removeFirstSegments(1), null);			

			srcRootFolder.createLink(actionSrcPath, 0, null);
			srcRootFolder.createLink(modelSrcPath, 0, null);					

			File actionsSrc = new File(project.getLocation().toFile(), source.getFullPath().removeFirstSegments(1) + "/action/");
			
			AntCopyUtils.copyFileToFolder(new File(seamGenResFolder, "seam.properties"), actionsSrc, true); //$NON-NLS-1$
			
			AntCopyUtils.copyFileToFile(
					new File(seamGenHomeFolder,"src/Authenticator.java"), //$NON-NLS-1$
					new File(actionsSrc, model.getProperty(ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_NAME).toString().replace('.', '/')+"/"+"Authenticator.java"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					new FilterSetCollection(filtersFilterSet), true);

			AntCopyUtils.copyFileToFile(
					persistenceFile,
					new File(srcFolder,"META-INF/persistence.xml"), //$NON-NLS-1$
					viewFilterSetCollection, true);

			File resources = new File(project.getLocation().toFile(),"resources");
			AntCopyUtils.copyFileToFile(
					dataSourceDsFile, 
					new File(resources,project.getName()+"-ds.xml"),  //$NON-NLS-1$
					viewFilterSetCollection, true);

			// to fix seam2 tests for war deployment 
//			File srcModelMetaInf = new File(project.getLocation().append(modelSrcPath).toFile(),"META-INF");
//			AntCopyUtils.copyFileToFile(
//					dataSourceDsFile, 
//					new File(srcModelMetaInf,project.getName()+"-ds.xml"),  //$NON-NLS-1$
//					viewFilterSetCollection, true);

			AntCopyUtils.copyFileToFile(
					hibernateConsoleLaunchFile, 
					new File(project.getLocation().toFile(),project.getName()+".launch"),  //$NON-NLS-1$
					viewFilterSetCollection, true);

			AntCopyUtils.copyFileToFolder(
					hibernateConsolePropsFile, 
					project.getLocation().toFile(),
					hibernateDialectFilterSet, true);

			WtpUtils.setClasspathEntryAsExported(project, new Path("org.eclipse.jst.j2ee.internal.web.container"), monitor); //$NON-NLS-1$
		} else {
			// In case of EAR configuration
			AntCopyUtils.copyFiles(seamHomeFolder, webLibFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_EAR_CONFIG).dir(seamHomeFolder)));
			AntCopyUtils.copyFiles(seamLibFolder, webLibFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_EAR_CONFIG).dir(seamLibFolder)));
			AntCopyUtils.copyFiles(droolsLibFolder, webLibFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_EAR_CONFIG).dir(droolsLibFolder)));
			AntCopyUtils.copyFileToFolder(new File(seamGenResFolder, "messages_en.properties"), srcFolder, true); //$NON-NLS-1$
		}

		ClasspathHelper.addClasspathEntries(project, fv);

		// TODO
		createSeamProjectPreferenes(project, model);

		EclipseResourceUtil.addNatureToProject(project, ISeamProject.NATURE_ID);

		project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.project.facet.SeamFacetAbstractInstallDelegate#configure(org.eclipse.jst.javaee.web.WebApp)
	 */
	@Override
	protected void configure(WebApp webApp) {
		// Ajax4jsf
		createOrUpdateContextParam(webApp, ORG_RICHFACES_SKIN,
				ORG_RICHFACES_SKIN_VALUE);
		// Seam
		createOrUpdateListener(webApp,
				ORG_JBOSS_SEAM_SERVLET_SEAMLISTENER);
		createOrUpdateFilter(webApp,
				ORG_JBOSS_SEAM_SERVLET_SEAMFILTER_NAME,
				ORG_JBOSS_SEAM_SERVLET_SEAMFILTER);
		createOrUpdateFilterMapping(webApp,
				ORG_JBOSS_SEAM_SERVLET_SEAMFILTER_NAME,
				ORG_JBOSS_SEAM_SERVLET_SEAMFILTER_MAPPING_VALUE);
		createOrUpdateServlet(webApp,
				ORG_JBOSS_SEAM_SERVLET_SEAMRESOURCESERVLET,
				ORG_JBOSS_SEAM_SERVLET_SEAMRESOURCESERVLET_NAME);
		createOrUpdateServletMapping(webApp,
				ORG_JBOSS_SEAM_SERVLET_SEAMRESOURCESERVLET_NAME,
				ORG_JBOSS_SEAM_SERVLET_SEAMRESOURCESERVLET_VALUE);
		// Facelets development mode (disable in production)
		createOrUpdateContextParam(webApp, FACELETS_DEVELOPMENT, "true");
		// JSF
		createOrUpdateContextParam(webApp, JAVAX_FACES_DEFAULT_SUFFIX,
				JAVAX_FACES_DEFAULT_SUFFIX_VALUE);
		// other JSF artifacts have been configured by the JSF facet

		// Security
		addSecurityConstraint(webApp);
	}
}