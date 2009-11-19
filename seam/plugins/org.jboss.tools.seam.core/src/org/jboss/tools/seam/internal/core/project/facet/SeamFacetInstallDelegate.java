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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jst.javaee.core.DisplayName;
import org.eclipse.jst.javaee.core.JavaeeFactory;
import org.eclipse.jst.javaee.web.Filter;
import org.eclipse.jst.javaee.web.WebApp;
import org.eclipse.jst.jsf.facesconfig.emf.ApplicationType;
import org.eclipse.jst.jsf.facesconfig.emf.FacesConfigFactory;
import org.eclipse.jst.jsf.facesconfig.emf.FacesConfigType;
import org.eclipse.jst.jsf.facesconfig.emf.LifecycleType;
import org.eclipse.jst.jsf.facesconfig.emf.MessageBundleType;
import org.eclipse.jst.jsf.facesconfig.emf.PhaseListenerType;
import org.eclipse.jst.jsf.facesconfig.util.FacesConfigArtifactEdit;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * Install delegate for seam facet version 1.2
 * 
 * @author eskimo 
 */
public class SeamFacetInstallDelegate extends SeamFacetAbstractInstallDelegate {

	private static final String JAVAX_FACES_STATE_SAVING_METHOD = "javax.faces.STATE_SAVING_METHOD";

	private static final String CLIENT = "client";

	private static final String ORG_JBOSS_SEAM_WEB_SEAM_FILTER = "org.jboss.seam.web.SeamFilter";

	private static final String BLUE_SKY = "blueSky";

	private static final String ORG_AJAX4JSF_SKIN = "org.ajax4jsf.SKIN";

	public static final AntCopyUtils.FileSet JBOSS_EAR_CONTENT  = new AntCopyUtils.FileSet()
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
	    .include("el-ri.*\\.jar") //$NON-NLS-1$ 
		.include("itext.*\\.jar"); //$NON-NLS-1$


	public static final AntCopyUtils.FileSet JBOSS_WAR_LIB_FILESET_EAR_CONFIG = new AntCopyUtils.FileSet() 
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
		.include("oscache.*\\.jar") //$NON-NLS-1$
		.include("itext.*\\.jar"); //$NON-NLS-1$


	private static final String ORG_AJAX4JSF_FILTER_NAME = "ajax4jsf";

	private static final String ORG_AJAX4JSF_FILTER_CLASS = "org.ajax4jsf.Filter";

	private static final String ORG_AJAX4JSF_FILTER_DISPLAY_NAME = "Ajax4jsf Filter";

	private static final String ORG_AJAX4JSF_FILTER_MAPPING = "*.seam";

	private static final String ORG_JBOSS_SEAM_UI_SEAMFACELETVIEWHANDLER = "org.jboss.seam.ui.facelet.SeamFaceletViewHandler";

	private static final String ORG_AJAX4JSF_VIEW_HANDLERS = "org.ajax4jsf.VIEW_HANDLERS";

	public static String DROOLS_LIB_SEAM_RELATED_PATH = "drools/lib"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.project.facet.SeamFacetAbstractInstallDelegate#copyFilesToWarProject(org.eclipse.core.resources.IProject, org.eclipse.wst.common.project.facet.core.IProjectFacetVersion, org.eclipse.wst.common.frameworks.datamodel.IDataModel, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void copyFilesToWarProject(final IProject project, IProjectFacetVersion fv,
			IDataModel model, IProgressMonitor monitor) throws CoreException {
		super.copyFilesToWarProject(project, fv, model, monitor);
		final File droolsLibFolder = new File(seamHomePath, DROOLS_LIB_SEAM_RELATED_PATH);
		if (isWarConfiguration(model)) {
			AntCopyUtils.copyFiles(seamHomeFolder, webLibFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_WAR_CONFIG).dir(seamHomeFolder)));
			AntCopyUtils.copyFiles(seamLibFolder, webLibFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_WAR_CONFIG).dir(seamLibFolder)));
			AntCopyUtils.copyFiles(droolsLibFolder, webLibFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_WAR_CONFIG).dir(droolsLibFolder)));
		} else {
			AntCopyUtils.copyFiles(seamHomeFolder, webLibFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_EAR_CONFIG).dir(seamHomeFolder)));
			AntCopyUtils.copyFiles(seamLibFolder, webLibFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_EAR_CONFIG).dir(seamLibFolder)));
			AntCopyUtils.copyFiles(droolsLibFolder, webLibFolder, new AntCopyUtils.FileSetFileFilter(new AntCopyUtils.FileSet(JBOSS_WAR_LIB_FILESET_EAR_CONFIG).dir(droolsLibFolder)));
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
			boolean messageBundleExists = false;
			for (Iterator iterator = applications.iterator(); iterator.hasNext();) {
				ApplicationType application = (ApplicationType) iterator.next();
				EList messageBundles = application.getMessageBundle();
				for (Iterator iterator2 = messageBundles.iterator(); iterator2.hasNext();) {
					MessageBundleType messageBundle = (MessageBundleType)iterator2.next();
					if ("messages".equals(messageBundle.getTextContent().trim())) {
						messageBundleExists = true;
						break;
					}
				}
			}
			if (!messageBundleExists) {
				MessageBundleType messageBundle = FacesConfigFactory.eINSTANCE.createMessageBundleType();
				messageBundle.setTextContent("messages");
				applicationType.getMessageBundle().add(messageBundle);
			}
			if (!applicationExists) {
				facesConfig.getApplication().add(applicationType);
			}
			EList lifecycles = facesConfig.getLifecycle();
			LifecycleType lifecycleType = null;
			boolean lifecycleExists = false;
			if (lifecycles.size() <= 0) {
				lifecycleType = FacesConfigFactory.eINSTANCE.createLifecycleType();
			} else {
				lifecycleType = (LifecycleType)lifecycles.get(0);
				lifecycleExists = true;
			}
			boolean phaseListenerExists = false;
			for (Iterator iterator = lifecycles.iterator(); iterator.hasNext();) {
				LifecycleType lifecycle = (LifecycleType) iterator.next();
				EList phaseListeners = lifecycle.getPhaseListener();
				for (Iterator iterator2 = phaseListeners.iterator(); iterator2.hasNext();) {
					PhaseListenerType messageBundle = (PhaseListenerType)iterator2.next();
					if ("org.jboss.seam.jsf.TransactionalSeamPhaseListener".equals(messageBundle.getTextContent().trim()) ||
							"org.jboss.seam.jsf.SeamPhaseListener".equals(messageBundle.getTextContent().trim())) {
						phaseListenerExists = true;
						break;
					}
				}
			}
			if (!phaseListenerExists) {
				PhaseListenerType phaseListener = FacesConfigFactory.eINSTANCE.createPhaseListenerType();
				phaseListener.setTextContent("org.jboss.seam.jsf.TransactionalSeamPhaseListener");
				lifecycleType.getPhaseListener().add(phaseListener);
			}
			if (!lifecycleExists) {
				facesConfig.getLifecycle().add(lifecycleType);
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
		// Ajax4jsf (must come first!)
		// FIXME supposing that the Ajax4jsf filter must come before the Seam filter
		createOrUpdateFilter(webApp,
				ORG_AJAX4JSF_FILTER_NAME,
				ORG_AJAX4JSF_FILTER_CLASS,
				ORG_AJAX4JSF_FILTER_DISPLAY_NAME);
		// FIXME not sure if this filter has to have the same mapping as Faces Servlet  
		createOrUpdateFilterMapping(webApp,
				ORG_AJAX4JSF_FILTER_NAME,
				ORG_AJAX4JSF_FILTER_MAPPING);
		
		createOrUpdateContextParam(webApp, ORG_AJAX4JSF_VIEW_HANDLERS,
				ORG_JBOSS_SEAM_UI_SEAMFACELETVIEWHANDLER);
		createOrUpdateContextParam(webApp, ORG_AJAX4JSF_SKIN,
				BLUE_SKY);
		// Seam
		createOrUpdateListener(webApp,
				ORG_JBOSS_SEAM_SERVLET_SEAMLISTENER);
		createOrUpdateFilter(webApp,
				ORG_JBOSS_SEAM_SERVLET_SEAMFILTER_NAME,
				ORG_JBOSS_SEAM_WEB_SEAM_FILTER);
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
		createOrUpdateContextParam(webApp, JAVAX_FACES_STATE_SAVING_METHOD,
				CLIENT);
		createOrUpdateContextParam(webApp, JAVAX_FACES_DEFAULT_SUFFIX,
				JAVAX_FACES_DEFAULT_SUFFIX_VALUE);
		// other JSF artifacts have been configured by the JSF facet

		// Security
		addSecurityConstraint(webApp);
	}

	private void createOrUpdateFilter(WebApp webApp, String name,
			String className, String displayName) {
		createOrUpdateFilter(webApp,name,className);
		Filter filter = (Filter) getFilterByName(webApp,name);
		DisplayName displayNameObj = JavaeeFactory.eINSTANCE.createDisplayName();
		displayNameObj.setValue(displayName);
		filter.getDisplayNames().add(displayNameObj);
	}

	@Override
	protected SeamProjectCreator getProjectCreator(IDataModel model,
			IProject project) {
		return new SeamProjectCreator(model,project);
	}
}