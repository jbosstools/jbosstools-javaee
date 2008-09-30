/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jst.j2ee.model.IModelProvider;
import org.eclipse.jst.j2ee.model.ModelProviderManager;
import org.eclipse.jst.javaee.core.DisplayName;
import org.eclipse.jst.javaee.core.JavaeeFactory;
import org.eclipse.jst.javaee.core.Listener;
import org.eclipse.jst.javaee.core.ParamValue;
import org.eclipse.jst.javaee.core.UrlPatternType;
import org.eclipse.jst.javaee.web.AuthConstraint;
import org.eclipse.jst.javaee.web.Filter;
import org.eclipse.jst.javaee.web.FilterMapping;
import org.eclipse.jst.javaee.web.SecurityConstraint;
import org.eclipse.jst.javaee.web.Servlet;
import org.eclipse.jst.javaee.web.ServletMapping;
import org.eclipse.jst.javaee.web.WebApp;
import org.eclipse.jst.javaee.web.WebFactory;
import org.eclipse.jst.javaee.web.WebResourceCollection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * 
 * @author eskimo
 *
 */
public abstract class SeamFacetAbstractInstallDelegate implements ILogListener, 
										IDelegate,ISeamFacetDataModelProperties {

	public static String ORG_RICHFACES_SKIN = "org.richfaces.SKIN";
	public static String ORG_RICHFACES_SKIN_VALUE = "blueSky";
	public static String ORG_JBOSS_SEAM_SERVLET_SEAMLISTENER = "org.jboss.seam.servlet.SeamListener";
	public static String ORG_JBOSS_SEAM_SERVLET_SEAMFILTER = "org.jboss.seam.servlet.SeamFilter";
	public static String ORG_JBOSS_SEAM_SERVLET_SEAMFILTER_NAME = "Seam Filter";
	public static String ORG_JBOSS_SEAM_SERVLET_SEAMFILTER_MAPPING_VALUE = "/*";
	public static String ORG_JBOSS_SEAM_SERVLET_SEAMRESOURCESERVLET = "org.jboss.seam.servlet.SeamResourceServlet";
	public static String ORG_JBOSS_SEAM_SERVLET_SEAMRESOURCESERVLET_NAME = "Seam Resource Servlet";
	public static String ORG_JBOSS_SEAM_SERVLET_SEAMRESOURCESERVLET_VALUE = "/seam/resource/*";
	public static String FACELETS_DEVELOPMENT = "facelets.DEVELOPMENT";
	public static String JAVAX_FACES_DEFAULT_SUFFIX = "javax.faces.DEFAULT_SUFFIX";
	public static String JAVAX_FACES_DEFAULT_SUFFIX_VALUE = ".xhtml";
	public static String RESTRICT_RAW_XHTML = "Restrict raw XHTML Documents";
	public static String XHTML = "XHTML";
	public static String WEB_RESOURCE_COLLECTION_PATTERN = "*.xhtml";
	public static String SEAM_LIB_RELATED_PATH = "lib"; //$NON-NLS-1$
	public static final String DEV_WAR_PROFILE = "dev-war"; //$NON-NLS-1$
	public static final String DEV_EAR_PROFILE = "dev";	 //$NON-NLS-1$

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

	public static AntCopyUtils.FileSet JBOOS_WAR_WEBINF_SET = new AntCopyUtils.FileSet()
		.include("WEB-INF") //$NON-NLS-1$
		//.include("WEB-INF/web\\.xml") //$NON-NLS-1$
		.include("WEB-INF/pages\\.xml") //$NON-NLS-1$
		.include("WEB-INF/jboss-web\\.xml") //$NON-NLS-1$
		.include("WEB-INF/faces-config\\.xml") //$NON-NLS-1$
		.include("WEB-INF/componets\\.xml"); //$NON-NLS-1$

	public static AntCopyUtils.FileSet JBOOS_WAR_WEB_INF_CLASSES_SET = new AntCopyUtils.FileSet()
		.include("import\\.sql") //$NON-NLS-1$
		.include("security\\.drl") //$NON-NLS-1$
		.include("seam\\.properties") //$NON-NLS-1$
		.include("messages_en\\.properties"); //$NON-NLS-1$

	public static String WEB_LIBRARIES_RELATED_PATH = "WEB-INF/lib"; //$NON-NLS-1$

	static AntCopyUtils.FileSet CVS_SVN = new AntCopyUtils.FileSet()
		.include(".*") //$NON-NLS-1$
		.exclude(".*/CVS") //$NON-NLS-1$
		.exclude("CVS") //$NON-NLS-1$
		.exclude(".*\\.svn") //$NON-NLS-1$
		.exclude(".*/\\.svn");	 //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.project.facet.core.IDelegate#execute(org.eclipse.core.resources.IProject, org.eclipse.wst.common.project.facet.core.IProjectFacetVersion, java.lang.Object, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void execute(IProject project, IProjectFacetVersion fv,
			Object config, IProgressMonitor monitor) throws CoreException {
		try {
			// TODO find a better way to handle exceptions for creating seam projects
			// now here is the simple way that allows keep most of seam classes
			// untouched, this abstract class just listen to eclipse log and show an
			// error dialog if there were records logged from seam.core plugin
			startListening();
			doExecute(project,fv,config,monitor);		
		} finally {
			stopListening();
		}
		if(errorOccurs) {
			Display.getDefault().syncExec(
				new Runnable() {
					public void run() {
						ErrorDialog.openError(Display.getCurrent().getActiveShell(), 
								SeamCoreMessages.SEAM_FACET_INSTALL_ABSTRACT_DELEGATE_ERROR,
								SeamCoreMessages.SEAM_FACET_INSTALL_ABSTRACT_DELEGATE_CHECK_ERROR_LOG_VIEW,
								new Status(IStatus.ERROR,SeamCorePlugin.PLUGIN_ID,
										SeamCoreMessages.SEAM_FACET_INSTALL_ABSTRACT_DELEGATE_ERRORS_OCCURED));
					}
				});
		}
	}

	public static boolean toggleHibernateOnProject(IProject project, String defaultConsoleName) {
		IScopeContext scope = new ProjectScope(project);

		Preferences node = scope.getNode("org.hibernate.eclipse.console");

		if(node!=null) {
			node.putBoolean("hibernate3.enabled", true );
			node.put("default.configuration", defaultConsoleName );
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
			addProjectNature(project, "org.hibernate.eclipse.console.hibernateNature", new NullProgressMonitor() );
			return true;
		} catch(CoreException ce) {
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

	/**
	 * 
	 * @param model
	 * @return
	 */
	static boolean isWarConfiguration(IDataModel model) {
		return "war".equals(model.getProperty(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS)); //$NON-NLS-1$
	}

	static void createComponentsProperties(final File seamGenResFolder, String projectName, Boolean embedded) {
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
	 * @param fv
	 * @param config
	 * @param monitor
	 * @throws CoreException
	 */
	protected abstract void doExecute(IProject project, IProjectFacetVersion fv,
			Object config, IProgressMonitor monitor) throws CoreException;

	
	/**
	 * 
	 */
	private void stopListening() {
		SeamCorePlugin.getDefault().getLog().removeLogListener(this);
	}

	/**
	 * 
	 */
	private void startListening() {
		SeamCorePlugin.getDefault().getLog().addLogListener(this);
	}

	private boolean errorOccurs = false;
	
	private boolean hasErrors() {
		return errorOccurs;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.project.facet.core.IActionConfigFactory#create()
	 */
	public Object create() throws CoreException {
		return null;
	}

	public void logging(IStatus status, String plugin) {
		if(status.getPlugin().equals(SeamCorePlugin.PLUGIN_ID)) {
			errorOccurs = true; 
		}
	}

	/**
	 * @param project
	 * @param model
	 */
	protected void createSeamProjectPreferenes(final IProject project,
			final IDataModel model) {
		IScopeContext projectScope = new ProjectScope(project);
		IEclipsePreferences prefs = projectScope.getNode(SeamCorePlugin.PLUGIN_ID);

		prefs.put(JBOSS_AS_DEPLOY_AS, model.getProperty(JBOSS_AS_DEPLOY_AS).toString());
		prefs.put(SEAM_SETTINGS_VERSION, SEAM_SETTINGS_VERSION_1_1);
		prefs.put(SEAM_RUNTIME_NAME, model.getProperty(SEAM_RUNTIME_NAME).toString());
		prefs.put(SEAM_CONNECTION_PROFILE, model.getProperty(SEAM_CONNECTION_PROFILE).toString());
		prefs.put(SESSION_BEAN_PACKAGE_NAME, model.getProperty(SESSION_BEAN_PACKAGE_NAME).toString());
		prefs.put(ENTITY_BEAN_PACKAGE_NAME, model.getProperty(ENTITY_BEAN_PACKAGE_NAME).toString());
		prefs.put(TEST_CASES_PACKAGE_NAME, model.getProperty(TEST_CASES_PACKAGE_NAME).toString());
		prefs.put(TEST_CREATING, "true");

		String testSrcPath = project.getFullPath().removeLastSegments(1).append(project.getName() + "-test").append("test-src").toString();
		prefs.put(TEST_SOURCE_FOLDER, testSrcPath);

		prefs.put(SEAM_TEST_PROJECT, 
				model.getProperty(SEAM_TEST_PROJECT)==null?
						"":model.getProperty(SEAM_TEST_PROJECT).toString()); //$NON-NLS-1$

		IVirtualComponent component = ComponentCore.createComponent(project);
		IVirtualFolder rootFolder = component.getRootFolder();
		IContainer webRootFolder = rootFolder.getFolder(new Path("/")).getUnderlyingFolder(); //$NON-NLS-1$
		String webRootFolderPath = webRootFolder.getFullPath().toString();
		prefs.put(WEB_CONTENTS_FOLDER, webRootFolderPath);

		if(DEPLOY_AS_EAR.equals(model.getProperty(JBOSS_AS_DEPLOY_AS))) {
			prefs.put(SEAM_EJB_PROJECT, 
					model.getProperty(SEAM_EJB_PROJECT)==null? 
						"":model.getProperty(SEAM_EJB_PROJECT).toString()); //$NON-NLS-1$

			prefs.put(SEAM_EAR_PROJECT, 
					model.getProperty(SEAM_EAR_PROJECT)==null? 
						"":model.getProperty(SEAM_EAR_PROJECT).toString()); //$NON-NLS-1$

			String srcPath = project.getFullPath().removeLastSegments(1).append(project.getName() + "-ejb").append("ejbModule").toString();
			prefs.put(ISeamFacetDataModelProperties.ENTITY_BEAN_SOURCE_FOLDER, srcPath);
			prefs.put(ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER, srcPath);
		} else {
			IPath srcRootFolder = rootFolder.getFolder(new Path("/WEB-INF/classes")).getUnderlyingFolder().getParent().getFullPath(); //$NON-NLS-1$

			prefs.put(ISeamFacetDataModelProperties.ENTITY_BEAN_SOURCE_FOLDER, srcRootFolder.append("model").toString());
			prefs.put(ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER, srcRootFolder.append("action").toString());
		}

		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
	}

	protected void addSecurityConstraint(WebApp webApp) {
		SecurityConstraint securityConstraint = WebFactory.eINSTANCE
				.createSecurityConstraint();
		DisplayName displayName = JavaeeFactory.eINSTANCE.createDisplayName();
		displayName.setValue(RESTRICT_RAW_XHTML);
		securityConstraint.getDisplayNames().add(displayName);

		WebResourceCollection webResourceCollection = WebFactory.eINSTANCE
				.createWebResourceCollection();
		webResourceCollection.setWebResourceName(XHTML);
		UrlPatternType urlPattern = JavaeeFactory.eINSTANCE
				.createUrlPatternType();
		urlPattern.setValue(WEB_RESOURCE_COLLECTION_PATTERN);
		webResourceCollection.getUrlPatterns().add(urlPattern);

		AuthConstraint authConstraint = WebFactory.eINSTANCE
				.createAuthConstraint();
		securityConstraint.setAuthConstraint(authConstraint);

		securityConstraint.getWebResourceCollections().add(
				webResourceCollection);
		webApp.getSecurityConstraints().add(securityConstraint);
	}

	protected void createOrUpdateServletMapping(WebApp webApp, String name,
			String value) {
		if (name == null || value == null)
			return;

		List servletMappings = webApp.getServletMappings();
		boolean added = false;
		for (Iterator iterator = servletMappings.iterator(); iterator.hasNext();) {
			ServletMapping servletMapping = (ServletMapping) iterator.next();
			if (servletMapping != null
					&& name.equals(servletMapping.getServletName())) {
				added = true;
				// FIXME
			}
		}
		if (!added) {
			ServletMapping mapping = WebFactory.eINSTANCE
					.createServletMapping();
			Servlet servlet = findServletByName(webApp, name);
			if (servlet != null) {
				mapping.setServletName(servlet.getServletName());
				UrlPatternType urlPattern = JavaeeFactory.eINSTANCE
						.createUrlPatternType();
				urlPattern.setValue(value);
				mapping.getUrlPatterns().add(urlPattern);
				webApp.getServletMappings().add(mapping);
			}
		}
	}

	private Servlet findServletByName(WebApp webApp, String name) {
		Iterator it = webApp.getServlets().iterator();
		while (it.hasNext()) {
			Servlet servlet = (Servlet) it.next();
			if (servlet.getServletName() != null
					&& servlet.getServletName().trim().equals(name)) {
				return servlet;
			}
		}
		return null;
	}

	protected void createOrUpdateServlet(WebApp webApp, String servletClass,
			String servletName) {
		if (servletClass == null || servletName == null)
			return;

		List servlets = webApp.getServlets();
		boolean added = false;
		for (Iterator iterator = servlets.iterator(); iterator.hasNext();) {
			Servlet servlet = (Servlet) iterator.next();
			if (servletName.equals(servlet.getServletName())) {
				servlet.setServletName(servletName);
				added = true;
				break;
			}
		}
		if (!added) {
			Servlet servlet = WebFactory.eINSTANCE.createServlet();
			servlet.setServletName(servletName);
			servlet.setServletClass(servletClass);
			webApp.getServlets().add(servlet);
		}
	}

	protected void createOrUpdateFilterMapping(WebApp webApp, String mapping,
			String value) {
		if (mapping == null || value == null)
			return;
		List filterMappings = webApp.getFilterMappings();
		boolean added = false;
		/*for (Iterator iterator = filterMappings.iterator(); iterator.hasNext();) {
			FilterMapping filterMapping = (FilterMapping) iterator.next();
			String filterName = filterMapping.getFilterName();
			List filters = webApp.getFilters();
			for (Iterator iterator2 = filters.iterator(); iterator2.hasNext();) {
				Filter filter = (Filter) iterator2.next();
				if (filter != null && filterName != null
						&& mapping.equals(filter.getFilterName())) {
					// FIXME
					added = true;
					break;
				}
			}
			if (added)
				break;
		}*/
		if (!added) {
			FilterMapping filterMapping = WebFactory.eINSTANCE
					.createFilterMapping();
			Filter filter = (Filter) getFilterByName(webApp, mapping);
			if (filter != null) {
				filterMapping.setFilterName(filter.getFilterName());
				UrlPatternType urlPattern = JavaeeFactory.eINSTANCE
						.createUrlPatternType();
				urlPattern.setValue(value);
				filterMapping.getUrlPatterns().add(urlPattern);

				webApp.getFilterMappings().add(filterMapping);
			}
		}
	}

	protected Object getFilterByName(WebApp webApp, String name) {
		if (webApp == null || name == null)
			return null;
		List filters = webApp.getFilters();
		for (Iterator iterator = filters.iterator(); iterator.hasNext();) {
			Filter filter = (Filter) iterator.next();
			if (filter != null && name.equals(filter.getFilterName()))
				return filter;
		}

		return null;
	}

	protected void createOrUpdateFilter(WebApp webApp, String name, String clazz) {
		if (name == null || clazz == null)
			return;
		List filters = webApp.getFilters();
		boolean added = false;
		for (Iterator iterator = filters.iterator(); iterator.hasNext();) {
			Filter filter = (Filter) iterator.next();
			if (filter != null && name.endsWith(filter.getFilterName())) {
				filter.setFilterName(name);
				filter.setFilterClass(clazz);
				added = true;
				break;
			}
		}
		if (!added) {
			Filter filter = WebFactory.eINSTANCE.createFilter();
			filter.setFilterName(name);
			filter.setFilterClass(clazz);
			webApp.getFilters().add(filter);
		}
	}

	protected void createOrUpdateListener(WebApp webApp, String name) {
		if (name == null)
			return;
		List listeners = webApp.getListeners();
		boolean added = false;
		for (Iterator iterator = listeners.iterator(); iterator.hasNext();) {
			Listener listener = (Listener) iterator.next();
			if (listener != null && name.equals(listener.getListenerClass())) {
				listener.setListenerClass(name);
				added = true;
			}
		}
		if (!added) {
			Listener listener = JavaeeFactory.eINSTANCE.createListener();
			listener.setListenerClass(name);
			webApp.getListeners().add(listener);
		}
	}

	protected void createOrUpdateContextParam(WebApp webApp, String name,
			String value) {
		if (name == null || value == null)
			return;
		List paramValues = webApp.getContextParams();
		boolean added = false;
		for (Iterator iterator = paramValues.iterator(); iterator.hasNext();) {
			ParamValue paramValue = (ParamValue) iterator.next();
			if (paramValue != null && name.equals(paramValue.getParamName())) {
				paramValue.setParamValue(value);
				added = true;
				break;
			}
		}
		if (!added) {
			ParamValue paramValue = JavaeeFactory.eINSTANCE.createParamValue();
			paramValue.setParamName(name);
			paramValue.setParamValue(value);
			webApp.getContextParams().add(paramValue);
		}
	}
	
	protected abstract void configure(WebApp webApp);

	protected void configureWebXml(final IProject project) {
		IModelProvider modelProvider = ModelProviderManager
				.getModelProvider(project);
		Object modelObject = modelProvider.getModelObject();
		if (!(modelObject instanceof WebApp)) {
			// TODO log
			return;
		}
		IPath modelPath = new Path("WEB-INF").append("web.xml"); //$NON-NLS-1$ //$NON-NLS-2$
		boolean exists = project.getProjectRelativePath().append(modelPath)
				.toFile().exists();
		if (!exists) {
			modelPath = IModelProvider.FORCESAVE;
		}
		modelProvider.modify(new Runnable() {

			public void run() {
				IModelProvider modelProvider = ModelProviderManager
						.getModelProvider(project);
				Object modelObject = modelProvider.getModelObject();
				if (!(modelObject instanceof WebApp)) {
					// TODO log
					return;
				}
				WebApp webApp = (WebApp) modelObject;
				configure(webApp);
			}

		}, modelPath);

	}
}