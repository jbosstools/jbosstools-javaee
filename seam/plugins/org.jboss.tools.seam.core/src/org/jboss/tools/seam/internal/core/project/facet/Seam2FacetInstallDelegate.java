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
import java.util.Iterator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jst.javaee.web.WebApp;
import org.eclipse.jst.jsf.facesconfig.emf.ApplicationType;
import org.eclipse.jst.jsf.facesconfig.emf.DefaultLocaleType;
import org.eclipse.jst.jsf.facesconfig.emf.FacesConfigFactory;
import org.eclipse.jst.jsf.facesconfig.emf.FacesConfigType;
import org.eclipse.jst.jsf.facesconfig.emf.LocaleConfigType;
import org.eclipse.jst.jsf.facesconfig.emf.SupportedLocaleType;
import org.eclipse.jst.jsf.facesconfig.emf.ViewHandlerType;
import org.eclipse.jst.jsf.facesconfig.util.FacesConfigArtifactEdit;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.jboss.tools.seam.core.SeamCorePlugin;

// TODO: why not just *one* global filter set to avoid any missing names ? (assert for it in our unittests!
public class Seam2FacetInstallDelegate extends SeamFacetAbstractInstallDelegate{

	public static final AntCopyUtils.FileSet JBOSS_EAR_CONTENT  = new AntCopyUtils.FileSet()
		.include("jboss-seam.jar"); //$NON-NLS-1$

	public static final AntCopyUtils.FileSet JBOSS_EAR_LIB  = new AntCopyUtils.FileSet()
		.include("antlr-runtime.jar") //$NON-NLS-1$
		.include("commons-beanutils.*\\.jar") //$NON-NLS-1$
		.include("drools-compiler.*\\.jar") //$NON-NLS-1$
		.include("drools-core.*\\.jar") //$NON-NLS-1$
		.include("jboss-el.*.jar") //$NON-NLS-1$
		.include("mvel.*\\.jar") //$NON-NLS-1$
		.include("jbpm-jpdl.*\\.jar") //$NON-NLS-1$
		.include("richfaces-api.*\\.jar"); //$NON-NLS-1$

	public static final AntCopyUtils.FileSet JBOSS_WAR_LIB_FILESET_WAR_CONFIG = new AntCopyUtils.FileSet()	
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
		.include("jboss-seam-excel\\.jar") //$NON-NLS-1$
		.include("jboss-seam\\.jar") //$NON-NLS-1$
		.include("jbpm.*\\.jar") //$NON-NLS-1$
		.include("jsf-facelets\\.jar") //$NON-NLS-1$
		.include("oscache.*\\.jar") //$NON-NLS-1$
		.include("stringtemplate.*\\.jar") //$NON-NLS-1$
	    .include("mvel.*\\.jar") //$NON-NLS-1$
	    .include("jboss-el.jar") //$NON-NLS-1$
		.include("jxl\\.jar") //$NON-NLS-1$
		.include("itext.*\\.jar") //$NON-NLS-1$
		.include("jfreechart.*\\.jar") //$NON-NLS-1$
		.include("jcommon.*\\.jar"); //$NON-NLS-1$

	public static final AntCopyUtils.FileSet JBOSS_WAR_LIB_FILESET_EAR_CONFIG = new AntCopyUtils.FileSet() 
		.include("richfaces-impl\\.jar") //$NON-NLS-1$
		.include("richfaces-ui\\.jar") //$NON-NLS-1$
		.include("commons-digester\\.jar") //$NON-NLS-1$
		.include("jboss-seam-debug\\.jar") //$NON-NLS-1$
		.include("jboss-seam-ioc\\.jar") //$NON-NLS-1$
		.include("jboss-seam-mail\\.jar") //$NON-NLS-1$
		.include("jboss-seam-pdf\\.jar") //$NON-NLS-1$
		.include("jboss-seam-remoting\\.jar") //$NON-NLS-1$
		.include("jboss-seam-ui\\.jar") //$NON-NLS-1$
		.include("jboss-seam-excel\\.jar") //$NON-NLS-1$
		.include("jxl\\.jar") //$NON-NLS-1$
		.include("itext.*\\.jar") //$NON-NLS-1$
		.include("jfreechart.*\\.jar") //$NON-NLS-1$
		.include("jcommon.*\\.jar") //$NON-NLS-1$
		.include("jsf-facelets\\.jar"); //$NON-NLS-1$

	public static String DROOLS_LIB_SEAM_RELATED_PATH = "lib"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.project.facet.SeamFacetAbstractInstallDelegate#doExecuteForEjb(org.eclipse.core.resources.IProject, org.eclipse.wst.common.project.facet.core.IProjectFacetVersion, org.eclipse.wst.common.frameworks.datamodel.IDataModel, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void doExecuteForEjb(final IProject project, IProjectFacetVersion fv,
			IDataModel model, IProgressMonitor monitor) throws CoreException {
		super.doExecuteForEjb(project, fv, model, monitor);
		IResource src = getSrcFolder(project);
		if(src!=null && seamHomeFolder!=null) {
			File srcFile = src.getLocation().toFile();
			AntCopyUtils.copyFileToFolder(new File(seamGenResFolder, "security.drl"), srcFile, false); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.project.facet.SeamFacetAbstractInstallDelegate#copyFilesToWarProject(org.eclipse.core.resources.IProject, org.eclipse.wst.common.project.facet.core.IProjectFacetVersion, org.eclipse.wst.common.frameworks.datamodel.IDataModel, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void copyFilesToWarProject(final IProject project, IProjectFacetVersion fv,
			IDataModel model, IProgressMonitor monitor) throws CoreException {
		super.copyFilesToWarProject(project, fv, model, monitor);
		final File droolsLibFolder = new File(seamHomePath, DROOLS_LIB_SEAM_RELATED_PATH);
		if(isWarConfiguration(model)) {
			if (!SeamCorePlugin.getDefault().hasM2Facet(project)) {
				AntCopyUtils.copyFiles(seamHomeFolder, webLibFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_WAR_CONFIG).dir(seamHomeFolder)));
				AntCopyUtils.copyFiles(seamLibFolder, webLibFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_WAR_CONFIG).dir(seamLibFolder)));
			}
			final IContainer source = warActionSrcRootFolder.getUnderlyingFolder();
			File actionsSrc = new File(project.getLocation().toFile(), source.getFullPath().removeFirstSegments(1).toString());
			AntCopyUtils.copyFileToFolder(new File(seamGenResFolder, "seam.properties"), actionsSrc, true); //$NON-NLS-1$
			if (!SeamCorePlugin.getDefault().hasM2Facet(project)) {
				AntCopyUtils.copyFiles(droolsLibFolder, webLibFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_WAR_CONFIG).dir(droolsLibFolder)));
			}
		} else {
			if (!SeamCorePlugin.getDefault().hasM2Facet(project)) {
				AntCopyUtils.copyFiles(seamHomeFolder, webLibFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_EAR_CONFIG).dir(seamHomeFolder)));
				AntCopyUtils.copyFiles(seamLibFolder, webLibFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_EAR_CONFIG).dir(seamLibFolder)));
				AntCopyUtils.copyFiles(droolsLibFolder, webLibFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_EAR_CONFIG).dir(droolsLibFolder)));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.project.facet.SeamFacetAbstractInstallDelegate#fillEarContents()
	 */
	@Override
	protected void fillEarContents() {
		final File droolsLibFolder = new File(seamHomePath, DROOLS_LIB_SEAM_RELATED_PATH);
		AntCopyUtils.copyFiles(seamHomeFolder, earContentsFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_EAR_CONTENT).dir(seamHomeFolder)), false);
		AntCopyUtils.copyFiles(seamLibFolder, earContentsFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_EAR_CONTENT).dir(seamLibFolder)), false);
		AntCopyUtils.copyFiles(seamLibFolder, earLibFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_EAR_LIB).dir(seamLibFolder)), false);
		AntCopyUtils.copyFiles(droolsLibFolder, earContentsFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_EAR_CONTENT).dir(droolsLibFolder)), false);
		AntCopyUtils.copyFiles(seamGenResFolder, earContentsFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_EAR_CONTENT).dir(seamGenResFolder)), false);						
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.project.facet.SeamFacetAbstractInstallDelegate#configureFacesConfigXml(org.eclipse.core.resources.IProject, org.eclipse.core.runtime.IProgressMonitor, java.lang.String)
	 */
	@Override
	protected void configureFacesConfigXml(final IProject project, IProgressMonitor monitor, String webConfigName) {
		FacesConfigArtifactEdit facesConfigEdit = null;
		try {
			facesConfigEdit = FacesConfigArtifactEdit.getFacesConfigArtifactEditForWrite(project, webConfigName);
			FacesConfigType facesConfig = facesConfigEdit.getFacesConfig();
			EList applications = facesConfig.getApplication();
			ApplicationType applicationType = null;
			boolean applicationExists = false;
			if (applications.size() <= 0) {
				applicationType = FacesConfigFactory.eINSTANCE.createApplicationType();
			} else {
				applicationType = (ApplicationType) applications.get(0);
				applicationExists = true;
			}
			boolean localeConfigExists = false;
			for (Iterator iterator = applications.iterator(); iterator.hasNext();) {
				ApplicationType application = (ApplicationType) iterator.next();
				EList localeConfigs = application.getLocaleConfig();
				if(!localeConfigs.isEmpty()) {
					localeConfigExists = true;
					break;
				}
			}
			if (!localeConfigExists) {
				LocaleConfigType locale = FacesConfigFactory.eINSTANCE.createLocaleConfigType();
				DefaultLocaleType defaultLocale = FacesConfigFactory.eINSTANCE.createDefaultLocaleType();
				defaultLocale.setTextContent("en");
				locale.setDefaultLocale(defaultLocale);
				SupportedLocaleType supportedLocale = FacesConfigFactory.eINSTANCE.createSupportedLocaleType();
				supportedLocale.setTextContent("bg");
				locale.getSupportedLocale().add(supportedLocale);
				supportedLocale = FacesConfigFactory.eINSTANCE.createSupportedLocaleType();
				supportedLocale.setTextContent("de");
				locale.getSupportedLocale().add(supportedLocale);
				supportedLocale = FacesConfigFactory.eINSTANCE.createSupportedLocaleType();
				supportedLocale.setTextContent("en");
				locale.getSupportedLocale().add(supportedLocale);
				supportedLocale = FacesConfigFactory.eINSTANCE.createSupportedLocaleType();
				supportedLocale.setTextContent("fr");
				locale.getSupportedLocale().add(supportedLocale);
				supportedLocale = FacesConfigFactory.eINSTANCE.createSupportedLocaleType();
				supportedLocale.setTextContent("tr");
				locale.getSupportedLocale().add(supportedLocale);
				applicationType.getLocaleConfig().add(locale);
			}
			boolean viewHandlerExists = false;
			for (Iterator iterator = applications.iterator(); iterator.hasNext();) {
				ApplicationType application = (ApplicationType) iterator.next();
				EList viewHandlers = application.getViewHandler();
				for (Iterator iterator2 = viewHandlers.iterator(); iterator2.hasNext();) {
					ViewHandlerType viewHandlerType = (ViewHandlerType)iterator2.next();
					if ("com.sun.facelets.FaceletViewHandler".equals(viewHandlerType.getTextContent().trim())) {
						viewHandlerExists = true;
						break;
					}
				}
			}
			if (!viewHandlerExists) {
				ViewHandlerType viewHandler = FacesConfigFactory.eINSTANCE.createViewHandlerType();
				viewHandler.setTextContent("com.sun.facelets.FaceletViewHandler");
				applicationType.getViewHandler().add(viewHandler);
			}
			if (!applicationExists) {
				facesConfig.getApplication().add(applicationType);
			}
			facesConfigEdit.save(monitor);
		} finally {
			if (facesConfigEdit != null) {
				facesConfigEdit.dispose();
			}
		}
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
	
	@Override
	protected SeamProjectCreator getProjectCreator(IDataModel model,
			IProject project) {
		return new Seam2ProjectCreator(model,project);
	}
}