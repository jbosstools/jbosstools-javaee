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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
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
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jst.common.project.facet.core.ClasspathHelper;
import org.eclipse.jst.j2ee.application.Application;
import org.eclipse.jst.j2ee.application.ApplicationFactory;
import org.eclipse.jst.j2ee.application.EjbModule;
import org.eclipse.jst.j2ee.componentcore.util.EARArtifactEdit;
import org.eclipse.jst.j2ee.model.IModelProvider;
import org.eclipse.jst.j2ee.model.ModelProviderManager;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetConstants;
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
import org.eclipse.jst.jsf.core.internal.project.facet.JSFUtils;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.IImportStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamUtil;
import org.jboss.tools.seam.core.project.facet.SeamProjectPreferences;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * 
 * @author eskimo
 *
 */
public abstract class SeamFacetAbstractInstallDelegate implements ILogListener, 
										IDelegate,ISeamFacetDataModelProperties {

	public static String ORG_RICHFACES_SKIN = "org.richfaces.SKIN"; //$NON-NLS-1$
	public static String ORG_RICHFACES_SKIN_VALUE = "blueSky"; //$NON-NLS-1$
	public static String ORG_JBOSS_SEAM_SERVLET_SEAMLISTENER = "org.jboss.seam.servlet.SeamListener"; //$NON-NLS-1$
	public static String ORG_JBOSS_SEAM_SERVLET_SEAMFILTER = "org.jboss.seam.servlet.SeamFilter"; //$NON-NLS-1$
	public static String ORG_JBOSS_SEAM_SERVLET_SEAMFILTER_NAME = "Seam Filter"; //$NON-NLS-1$
	public static String ORG_JBOSS_SEAM_SERVLET_SEAMFILTER_MAPPING_VALUE = "/*"; //$NON-NLS-1$
	public static String ORG_JBOSS_SEAM_SERVLET_SEAMRESOURCESERVLET = "org.jboss.seam.servlet.SeamResourceServlet"; //$NON-NLS-1$
	public static String ORG_JBOSS_SEAM_SERVLET_SEAMRESOURCESERVLET_NAME = "Seam Resource Servlet"; //$NON-NLS-1$
	public static String ORG_JBOSS_SEAM_SERVLET_SEAMRESOURCESERVLET_VALUE = "/seam/resource/*"; //$NON-NLS-1$
	public static String FACELETS_DEVELOPMENT = "facelets.DEVELOPMENT"; //$NON-NLS-1$
	public static String JAVAX_FACES_DEFAULT_SUFFIX = "javax.faces.DEFAULT_SUFFIX"; //$NON-NLS-1$
	public static String JAVAX_FACES_DEFAULT_SUFFIX_VALUE = ".xhtml"; //$NON-NLS-1$
	public static String RESTRICT_RAW_XHTML = SeamCoreMessages.SeamFacetAbstractInstallDelegate_Restrict_raw_XHTML_Documents;
	public static String XHTML = "XHTML"; //$NON-NLS-1$
	public static String WEB_RESOURCE_COLLECTION_PATTERN = "*.xhtml"; //$NON-NLS-1$
	public static String SEAM_LIB_RELATED_PATH = "lib"; //$NON-NLS-1$
	public static final String DEV_WAR_PROFILE = "dev-war"; //$NON-NLS-1$
	public static final String DEV_EAR_PROFILE = "dev";	 //$NON-NLS-1$

	public static AntCopyUtils.FileSet JBOOS_EJB_WEB_INF_CLASSES_SET = new AntCopyUtils.FileSet()
		.include("import\\.sql") //$NON-NLS-1$
		.include("seam\\.properties") //$NON-NLS-1$
		.exclude(".*/WEB-INF"); //$NON-NLS-1$

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
		//.include("img/.*") //$NON-NLS-1$
		//.include("img") //$NON-NLS-1$
		.exclude(".*/.*\\.ftl") //$NON-NLS-1$
		.exclude(".*/CVS") //$NON-NLS-1$
		.exclude(".*/\\.svn"); //$NON-NLS-1$

	public static AntCopyUtils.FileSet JBOOS_WAR_WEBINF_SET = new AntCopyUtils.FileSet()
		.include("WEB-INF") //$NON-NLS-1$
		//.include("WEB-INF/web\\.xml") //$NON-NLS-1$
		.include("WEB-INF/pages\\.xml") //$NON-NLS-1$
		.include("WEB-INF/jboss-web\\.xml") //$NON-NLS-1$
//		.include("WEB-INF/faces-config\\.xml") //$NON-NLS-1$
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

	private static IOverwriteQuery OVERWRITE_ALL = new IOverwriteQuery() {
		public String queryOverwrite(String file) {
			return ALL;
		}	
	};
	
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
			IDataModel model = (IDataModel) config;
			boolean createEarProjects = model.getBooleanProperty(ISeamFacetDataModelProperties.CREATE_EAR_PROJECTS);
			if (createEarProjects) {
		    	// Create ear, ejb, test projects JBIDE-3782
				getProjectCreator(model, project).execute(monitor);
			}
		} finally {
			stopListening();
		}
		if(errorOccurs) {
			errorOccurs = false;
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

		Preferences node = scope.getNode("org.hibernate.eclipse.console"); //$NON-NLS-1$

		if(node!=null) {
			node.putBoolean("hibernate3.enabled", true ); //$NON-NLS-1$
			node.put("default.configuration", defaultConsoleName ); //$NON-NLS-1$
			try {
				node.flush();
			} catch (BackingStoreException e) {
				SeamCorePlugin.getDefault().logError(SeamCoreMessages.SeamFacetAbstractInstallDelegate_Could_not_save_changes_to_preferences, e);
				return false;
			}
		} else {
			return false;
		}

		try {
			addProjectNature(project, "org.hibernate.eclipse.console.hibernateNature", new NullProgressMonitor() ); //$NON-NLS-1$
			return true;
		} catch(CoreException ce) {
			SeamCorePlugin.getDefault().logError(SeamCoreMessages.SeamFacetAbstractInstallDelegate_Could_not_activate_Hibernate_nature_on_project + project.getName(), ce);			
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
	public static boolean isWarConfiguration(IDataModel model) {
		return ISeamFacetDataModelProperties.DEPLOY_AS_WAR.equals(model.getProperty(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS));
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

	private IVirtualFolder warDefaultSrcRootFolder;
	protected IVirtualFolder warModelSrcRootFolder;
	protected IVirtualFolder warActionSrcRootFolder;

	protected File seamHomeFolder;
	protected String seamHomePath;
	protected File seamLibFolder;
	protected File seamGenResFolder;
	protected File srcFolder;
	protected File webContentFolder;
	protected IPath webContentPath;
	protected File webLibFolder;
	protected IContainer webRootFolder;
	protected File seamGenHomeFolder;
	protected File seamGenViewSource;
	protected File dataSourceDsFile;
	protected File componentsFile;
	protected File webInfFolder;
	protected File webInfClasses;
	protected File webInfClassesMetaInf;
	protected File persistenceFile;
	protected File hibernateConsoleLaunchFile;
	protected File hibernateConsolePropsFile;

	/**
	 * 
	 * @param project
	 * @param fv
	 * @param model
	 * @param monitor
	 * @throws CoreException
	 */
	protected void doExecuteForWar(IProject project, IProjectFacetVersion fv,
			IDataModel model, IProgressMonitor monitor) throws CoreException {
		// get WebContents folder path from DWP model 
		IVirtualComponent component = ComponentCore.createComponent(project);
		IVirtualFolder webRootVirtFolder = component.getRootFolder().getFolder(new Path("/")); //$NON-NLS-1$
		warDefaultSrcRootFolder = component.getRootFolder().getFolder(new Path("/WEB-INF/classes")); //$NON-NLS-1$
		webRootFolder = webRootVirtFolder.getUnderlyingFolder();

		webContentFolder = webRootFolder.getLocation().toFile();
		webContentPath = webRootFolder.getFullPath();
		webInfFolder = new File(webContentFolder, "WEB-INF"); //$NON-NLS-1$
		webInfClasses = new File(webInfFolder, "classes"); //$NON-NLS-1$
		webInfClassesMetaInf = new File(webInfClasses, "META-INF"); //$NON-NLS-1$
		webInfClassesMetaInf.mkdirs();
		webLibFolder = new File(webContentFolder, WEB_LIBRARIES_RELATED_PATH);
		srcFolder = isWarConfiguration(model) ? new File(warDefaultSrcRootFolder.getUnderlyingFolder().getLocation().toFile(), DEFAULT_MODEL_SRC_FOLDER_NAME) : warDefaultSrcRootFolder.getUnderlyingFolder().getLocation().toFile();
		if (model.getBooleanProperty(CONFIGURE_WAR_PROJECT)) {
			Object runtimeName = model.getProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME);
			if (runtimeName != null) {
				copyFilesToWarProject(project, fv, model, monitor);
			} else {
				// If seam runtime is null then just modify web.xml and add seam nature.
				configureWebXml(project);
			}
		}
	}

	protected String getFacesConfigPath(WebApp webApp) {
		if(webApp==null) {
			return JSFUtils.JSF_DEFAULT_CONFIG_PATH;
		}
		File facesConfig = new File(webContentFolder, JSFUtils.JSF_DEFAULT_CONFIG_PATH);
		if(facesConfig.exists()) {
			return JSFUtils.JSF_DEFAULT_CONFIG_PATH;
		}
		String path = null;
		Iterator it = webApp.getContextParams().iterator();
		while (it.hasNext()) {
			ParamValue cp = (ParamValue) it.next();
			if (cp != null && cp.getParamName()!= null && cp.getParamName().trim().equals(JSFUtils.JSF_CONFIG_CONTEXT_PARAM)) {
				path = cp.getParamValue().trim();
				if(path.length()>0) {
					facesConfig = new File(webContentFolder, path);
					if(facesConfig.exists()) {
						return path;
					}
				}
			}
		}
		return JSFUtils.JSF_DEFAULT_CONFIG_PATH;
	}

	/**
	 * 
	 * @param project
	 * @param fv
	 * @param model
	 * @param monitor
	 * @throws CoreException
	 */
	protected void copyFilesToWarProject(IProject project, IProjectFacetVersion fv,
			IDataModel model, IProgressMonitor monitor) throws CoreException {

		final AntCopyUtils.FileSet viewFileSet = new AntCopyUtils.FileSet(VIEW_FILESET).dir(seamGenViewSource);
		AntCopyUtils.copyFilesAndFolders(
				seamGenViewSource, 
				webContentFolder, 
				new AntCopyUtils.FileSetFileFilter(viewFileSet), 
				viewFilterSetCollection, 
				false);

		IImportStructureProvider structureProvider = FileSystemStructureProvider.INSTANCE;
		File rootDir = new File(seamGenViewSource,"img"); //$NON-NLS-1$
		IPath imgPath = webContentPath.append("img"); //$NON-NLS-1$
		try {
			ImportOperation op= new ImportOperation(imgPath, rootDir, structureProvider, OVERWRITE_ALL);
			op.setCreateContainerStructure(false);
			op.run(monitor);
		} catch (InterruptedException e) {
			// should not happen
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();
			if (t instanceof CoreException) {	
				ErrorDialog.openError(Display.getCurrent().getActiveShell(), SeamCoreMessages.SeamFacetAbstractInstallDelegate_Error,
						null, ((CoreException) t).getStatus());
			}
		}
		
		// *******************************************************************
		// Copy manifest and configuration resources the same way as view
		// *******************************************************************
		AntCopyUtils.FileSet webInfSet = new AntCopyUtils.FileSet(JBOOS_WAR_WEBINF_SET).dir(seamGenResFolder);

		WebApp webApp = configureWebXml(project);

		AntCopyUtils.copyFileToFile(
				componentsFile,
				new File(webInfFolder, "components.xml"), //$NON-NLS-1$
				new FilterSetCollection(projectFilterSet), false);

		String facesConfigPath = getFacesConfigPath(webApp);
		configureFacesConfigXml(project, monitor, facesConfigPath);

		AntCopyUtils.copyFilesAndFolders(
				seamGenResFolder, webContentFolder, new AntCopyUtils.FileSetFileFilter(webInfSet), viewFilterSetCollection, false);

		final FilterSetCollection hibernateDialectFilterSet = new FilterSetCollection();
		hibernateDialectFilterSet.addFilterSet(encodedJdbcFilterSet);
		hibernateDialectFilterSet.addFilterSet(encodedProjectFilterSet);
		hibernateDialectFilterSet.addFilterSet(SeamFacetFilterSetFactory.createHibernateDialectFilterSet(model, true));

		final IContainer source = warDefaultSrcRootFolder.getUnderlyingFolder();

		// ********************************************************************************************
		// Handle WAR configurations
		// ********************************************************************************************
		if (isWarConfiguration(model)) {
			boolean sourcesDoesNotExist = warDefaultSrcRootFolder.members().length==0;

			// ********************************************************************************************
			// Copy seam project indicator
			// ********************************************************************************************

			IPath actionSrcPath = new Path(source.getFullPath().removeFirstSegments(1) + "/" + DEFAULT_ACTION_SRC_FOLDER_NAME); //$NON-NLS-1$
			IPath modelSrcPath = new Path(source.getFullPath().removeFirstSegments(1) + "/" + DEFAULT_MODEL_SRC_FOLDER_NAME); //$NON-NLS-1$

			if(sourcesDoesNotExist) {
				// Remove old source folder and create new ones.
				warDefaultSrcRootFolder.delete(IVirtualFolder.FORCE, monitor);

				WtpUtils.createSourceFolder(project, actionSrcPath, source.getFullPath().removeFirstSegments(1), webRootFolder.getFullPath().removeFirstSegments(1).append("WEB-INF/dev")); //$NON-NLS-1$
				WtpUtils.createSourceFolder(project, modelSrcPath, source.getFullPath().removeFirstSegments(1), null);

				IVirtualComponent component = ComponentCore.createComponent(project);
				warModelSrcRootFolder = component.getRootFolder().getFolder(new Path("/WEB-INF/classes")); //$NON-NLS-1$
				warActionSrcRootFolder = component.getRootFolder().getFolder(new Path("/WEB-INF/dev")); //$NON-NLS-1$

				warModelSrcRootFolder.createLink(modelSrcPath, 0, null);
				warActionSrcRootFolder.createLink(actionSrcPath, 0, null);
			} else {
				// In case if user alrady has some resources we should not remove it.
				actionSrcPath = source.getFullPath().removeFirstSegments(1);
				modelSrcPath = actionSrcPath;
				srcFolder = source.getLocation().toFile();
				warActionSrcRootFolder = warDefaultSrcRootFolder;
				warModelSrcRootFolder = warDefaultSrcRootFolder;
			}

			File actionsSrc = new File(project.getLocation().toFile(), actionSrcPath.toOSString());

			AntCopyUtils.FileSet webInfClassesSet = new AntCopyUtils.FileSet(JBOOS_WAR_WEB_INF_CLASSES_SET).dir(seamGenResFolder);
			AntCopyUtils.copyFilesAndFolders(
					seamGenResFolder, srcFolder, new AntCopyUtils.FileSetFileFilter(webInfClassesSet), viewFilterSetCollection, false);

			createComponentsProperties(srcFolder, "", false); //$NON-NLS-1$

			//AntCopyUtils.copyFileToFolder(new File(seamGenResFolder, "seam.properties"), actionsSrc, true); //$NON-NLS-1$

			AntCopyUtils.copyFileToFile(
					new File(seamGenHomeFolder, "src/Authenticator.java"), //$NON-NLS-1$
					new File(actionsSrc,model.getProperty(ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_NAME).toString().replace('.', '/') + "/" + "Authenticator.java"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					new FilterSetCollection(filtersFilterSet), false);

			AntCopyUtils.copyFileToFile(
					persistenceFile,
					new File(srcFolder, "META-INF/persistence.xml"), //$NON-NLS-1$
					viewFilterSetCollection, false);

			File resources = new File(project.getLocation().toFile(), "resources"); //$NON-NLS-1$
			AntCopyUtils.copyFileToFile(
					dataSourceDsFile, 
					new File(resources, project.getName() + "-ds.xml"),  //$NON-NLS-1$
					viewFilterSetCollection, false);

			if (hibernateConsoleLaunchFile != null) {
				AntCopyUtils.copyFileToFile(
						hibernateConsoleLaunchFile, 
						new File(project.getLocation().toFile(), project.getName() + ".launch"),  //$NON-NLS-1$
						viewFilterSetCollection, false);
			}			

			AntCopyUtils.copyFileToFolder(
					hibernateConsolePropsFile, 
					project.getLocation().toFile(),
					hibernateDialectFilterSet, false);

			WtpUtils.setClasspathEntryAsExported(project, new Path("org.eclipse.jst.j2ee.internal.web.container"), monitor); //$NON-NLS-1$
		} else {
			// In case of EAR configuration
			AntCopyUtils.copyFileToFolder(new File(seamGenResFolder, "messages_en.properties"), srcFolder, false); //$NON-NLS-1$
			WtpUtils.createSourceFolder(project, source.getFullPath().removeFirstSegments(1), source.getFullPath().removeFirstSegments(1), webRootFolder.getFullPath().removeFirstSegments(1).append("WEB-INF/dev")); //$NON-NLS-1$
		}
	}

	protected File earContentsFolder;
	protected File earLibFolder;

	/**
	 * 
	 * @param project
	 * @param fv
	 * @param model
	 * @param monitor
	 * @throws CoreException
	 */
	protected void doExecuteForEar(IProject project, IProjectFacetVersion fv,
			IDataModel model, IProgressMonitor monitor) throws CoreException {
		if(seamHomePath==null) {
			return;
		}
		model.setProperty(ISeamFacetDataModelProperties.SEAM_EAR_PROJECT, project.getName());
		IVirtualComponent component = ComponentCore.createComponent(project);
		IVirtualFolder rootVirtFolder = component.getRootFolder().getFolder(new Path("/")); //$NON-NLS-1$

		earContentsFolder = rootVirtFolder.getUnderlyingFolder().getLocation().toFile();
		earLibFolder = new File(earContentsFolder, "lib");
		File metaInfFolder = new File(earContentsFolder, "META-INF"); //$NON-NLS-1$
		File applicationXml = new File(metaInfFolder, "application.xml"); //$NON-NLS-1$
		File earProjectFolder = project.getLocation().toFile();

		FilterSet earFilterSet =  new FilterSet();
		earFilterSet.addFilter("projectName", project.getName() + ".ear"); //$NON-NLS-1$ //$NON-NLS-2$

		AntCopyUtils.copyFileToFolder(
			new File(seamGenResFolder, "META-INF/jboss-app.xml"), //$NON-NLS-1$
			metaInfFolder, new FilterSetCollection(earFilterSet), false);

		if(applicationXml.exists()) {
			configureApplicationXml(project, monitor);
		}
		// Copy configuration files from template
		try {
			AntCopyUtils.copyFilesAndFolders(
				new File(SeamFacetInstallDataModelProvider.getTemplatesFolder(), "ear"),  //$NON-NLS-1$
				earProjectFolder, new FilterSetCollection(ejbFilterSet), false);
		} catch (IOException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}

		fillEarContents();

		File resources = new File(earProjectFolder, "resources"); //$NON-NLS-1$
		AntCopyUtils.copyFileToFile(
			dataSourceDsFile, new File(resources, project.getName() + "-ds.xml"),  //$NON-NLS-1$
			viewFilterSetCollection, false);
	}

	protected void configureApplicationXml(IProject project, IProgressMonitor monitor) {
		EARArtifactEdit earArtifactEdit = null;
		try {
			earArtifactEdit = EARArtifactEdit.getEARArtifactEditForWrite(project);
			if(earArtifactEdit!=null) {
				Application application = earArtifactEdit.getApplication();
				EList modules = application.getModules();
				boolean moduleExists = false;
				for (Iterator iterator = modules.iterator(); iterator.hasNext();) {
					Object module = iterator.next();
					if(module instanceof EjbModule) {
						EjbModule ejbModule = (EjbModule)module;
						if("jboss-seam.jar".equals(ejbModule.getUri())) { //$NON-NLS-1$
							moduleExists = true;
							break;
						}
					}
				}
				if(!moduleExists) {
					EjbModule module = ApplicationFactory.eINSTANCE.createEjbModule();
					module.setUri("jboss-seam.jar"); //$NON-NLS-1$
					application.getModules().add(module);
				}

				earArtifactEdit.save(monitor);
			}
		} finally {
			if(earArtifactEdit!=null) {
				earArtifactEdit.dispose();
			}
		}
	}

	/**
	 * Fill ear contents
	 */
	abstract protected void fillEarContents();

	protected IResource getSrcFolder(IProject project) throws JavaModelException {
		IJavaProject javaProject = EclipseResourceUtil.getJavaProject(project);

		IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();
		IPackageFragmentRoot src = null;
		for (int i= 0; i < roots.length; i++) {
			if (roots[i].getKind() == IPackageFragmentRoot.K_SOURCE) {
				src = roots[i];
			}
		}
		if(src!=null) {
			return src.getResource();
		}
		return null;
	}

	/**
	 * 
	 * @param project
	 * @param fv
	 * @param model
	 * @param monitor
	 * @throws CoreException
	 */
	protected void doExecuteForEjb(final IProject project, IProjectFacetVersion fv,
			IDataModel model, IProgressMonitor monitor) throws CoreException {
		if(seamHomePath == null) {
			return;
		}

		File ejbProjectFolder = project.getLocation().toFile();
		FilterSet filtersFilterSet = SeamFacetFilterSetFactory.createFiltersFilterSet(model);
		FilterSet jdbcFilterSet = SeamFacetFilterSetFactory.createJdbcFilterSet(model);
		FilterSet projectFilterSet =  SeamFacetFilterSetFactory.createProjectFilterSet(model);

		FilterSetCollection hibernateDialectFilterSet = new FilterSetCollection();
		hibernateDialectFilterSet.addFilterSet(encodedJdbcFilterSet);
		hibernateDialectFilterSet.addFilterSet(encodedProjectFilterSet);
		hibernateDialectFilterSet.addFilterSet(SeamFacetFilterSetFactory.createHibernateDialectFilterSet(model, true));

		IResource src = getSrcFolder(project);
		if(src!=null) {
			viewFilterSetCollection = new FilterSetCollection();
			viewFilterSetCollection.addFilterSet(jdbcFilterSet);
			viewFilterSetCollection.addFilterSet(projectFilterSet);

			File srcFile = src.getLocation().toFile();
			// Copy sources to EJB project in case of EAR configuration
			AntCopyUtils.copyFileToFile(
				new File(seamGenHomeFolder, "src/Authenticator.java"), //$NON-NLS-1$
				new File(srcFile, model.getProperty(ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_NAME).toString().replace('.', '/') + "/" + "Authenticator.java"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				new FilterSetCollection(filtersFilterSet), false);

			File persistentXml = new File(srcFile, "META-INF/persistence.xml"); //$NON-NLS-1$
			if(!persistentXml.exists()) {
				AntCopyUtils.copyFileToFile(persistenceFile, new File(srcFile, "META-INF/persistence.xml"), //$NON-NLS-1$
						viewFilterSetCollection, false);
			} else {
				// TODO modify persistence.xml
			}

			File componentProperties = new File(srcFile, "components.properties"); //$NON-NLS-1$
			if(!componentProperties.exists()) {
				SeamFacetAbstractInstallDelegate.createComponentsProperties(srcFile, project.getName(), false);
			}

			AntCopyUtils.FileSet ejbSrcResourcesSet = new AntCopyUtils.FileSet(JBOOS_EJB_WEB_INF_CLASSES_SET).dir(seamGenResFolder);
			AntCopyUtils.copyFilesAndFolders(seamGenResFolder, srcFile, new AntCopyUtils.FileSetFileFilter(ejbSrcResourcesSet), viewFilterSetCollection, false);

			File ejbJarXml = new File(srcFile, "META-INF/ejb-jar.xml"); //$NON-NLS-1$
			if(!ejbJarXml.exists()) {
				AntCopyUtils.copyFileToFolder(new File(seamGenResFolder, "META-INF/ejb-jar.xml"), //$NON-NLS-1$
					new File(srcFile, "META-INF"), viewFilterSetCollection, false); //$NON-NLS-1$
			} else {
				// TODO modify ejb-jar.xml
			}
		}

		AntCopyUtils.copyFileToFile(hibernateConsoleLaunchFile, new File(
			ejbProjectFolder, ejbProjectFolder.getName() + ".launch"), //$NON-NLS-1$
			new FilterSetCollection(ejbFilterSet), false);

		AntCopyUtils.copyFileToFolder(hibernateConsolePropsFile,
			ejbProjectFolder, hibernateDialectFilterSet, false);
	}

	enum ProjectType {
		EAR,
		EJB,
		WAR
	}

	protected ProjectType projectType;
	protected FilterSet ejbFilterSet;
	protected FilterSetCollection viewFilterSetCollection;
	protected FilterSet jdbcFilterSet;
	protected FilterSet encodedJdbcFilterSet;
	protected FilterSet projectFilterSet;
	protected FilterSet encodedProjectFilterSet;
	protected FilterSet filtersFilterSet;

	/**
	 * 
	 * @param project
	 * @param fv
	 * @param config
	 * @param monitor
	 * @throws CoreException
	 */
	public void doExecute(final IProject project, IProjectFacetVersion fv,
			Object config, IProgressMonitor monitor) throws CoreException {
		final IDataModel model = (IDataModel)config;
		IFacetedProject facetedProject = ProjectFacetsManager.create(project);
		IProjectFacetVersion ejbVersion = facetedProject.getProjectFacetVersion(IJ2EEFacetConstants.EJB_FACET);
		IProjectFacetVersion webVersion = facetedProject.getProjectFacetVersion(IJ2EEFacetConstants.DYNAMIC_WEB_FACET);
		IProjectFacetVersion earVersion = facetedProject.getProjectFacetVersion(IJ2EEFacetConstants.ENTERPRISE_APPLICATION_FACET);
		IProjectFacetVersion jpaVersion = facetedProject.getProjectFacetVersion(ProjectFacetsManager.getProjectFacet("jpt.jpa"));
		initDefaultModelValues(model, webVersion!=null);

		model.setProperty(ISeamFacetDataModelProperties.SEAM_PROJECT_NAME, project.getName());

		Boolean dbExists = (Boolean) model.getProperty(ISeamFacetDataModelProperties.DB_ALREADY_EXISTS);
		Boolean dbRecreate = (Boolean) model.getProperty(ISeamFacetDataModelProperties.RECREATE_TABLES_AND_DATA_ON_DEPLOY);
		if (!dbExists && !dbRecreate) {
			model.setProperty(ISeamFacetDataModelProperties.HIBERNATE_HBM2DDL_AUTO, "update"); //$NON-NLS-1$
		} else if (dbExists && !dbRecreate) {
			model.setProperty(ISeamFacetDataModelProperties.HIBERNATE_HBM2DDL_AUTO, "validate"); //$NON-NLS-1$
		} else if (dbRecreate) {
			model.setProperty(ISeamFacetDataModelProperties.HIBERNATE_HBM2DDL_AUTO, "create-drop"); //$NON-NLS-1$
		}

		jdbcFilterSet = SeamFacetFilterSetFactory.createJdbcFilterSet(model);
		encodedJdbcFilterSet = SeamFacetFilterSetFactory.createJdbcFilterSet(model, true);
		projectFilterSet = SeamFacetFilterSetFactory.createProjectFilterSet(model);
		encodedProjectFilterSet = SeamFacetFilterSetFactory.createProjectFilterSet(model, true);
		filtersFilterSet =  SeamFacetFilterSetFactory.createFiltersFilterSet(model);

		// ****************************************************************
		// Copy view folder from seam-gen installation to WebContent folder
		// ****************************************************************
		final AntCopyUtils.FileSet viewFileSet = new AntCopyUtils.FileSet(VIEW_FILESET).dir(seamGenViewSource);
		viewFilterSetCollection = new FilterSetCollection();
		viewFilterSetCollection.addFilterSet(jdbcFilterSet);
		viewFilterSetCollection.addFilterSet(projectFilterSet);
		viewFilterSetCollection.addFilterSet(SeamFacetFilterSetFactory.createHibernateDialectFilterSet(model));

		Object runtimeName = model.getProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME);
		if(runtimeName!=null) {
			final SeamRuntime selectedRuntime = SeamRuntimeManager.getInstance().findRuntimeByName(runtimeName.toString());

			seamHomePath = selectedRuntime.getHomeDir();

			seamHomeFolder = new File(seamHomePath);
			seamLibFolder = new File(seamHomePath, SEAM_LIB_RELATED_PATH);
			seamGenResFolder = new File(seamHomePath, "seam-gen/resources"); //$NON-NLS-1$

			seamGenHomeFolder = new File(seamHomePath, "seam-gen"); //$NON-NLS-1$
			seamGenViewSource = new File(seamGenHomeFolder, "view"); //$NON-NLS-1$
			dataSourceDsFile = new File(seamGenResFolder, "datasource-ds.xml"); //$NON-NLS-1$
			componentsFile = new File(seamGenResFolder, "WEB-INF/components" + (isWarConfiguration(model) ? "-war" : "") + ".xml"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

			try {
				hibernateConsolePropsFile = new File(SeamFacetInstallDataModelProvider.getTemplatesFolder(), "hibernatetools/hibernate-console.properties"); //$NON-NLS-1$
			} catch (IOException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
			if (jpaVersion != null) {
				IScopeContext context = new ProjectScope(project);
				IEclipsePreferences prefs = context.getNode("org.eclipse.jpt.core");
				String platformId = prefs.get("org.eclipse.jpt.core.platform", null);
				if ("hibernate".equals(platformId)){
					/*
					 * Hibernate automatically creates console configuration
					 */
					hibernateConsoleLaunchFile = null;
				} else {
					try {
						hibernateConsoleLaunchFile = new File(SeamFacetInstallDataModelProvider.getTemplatesFolder(), "hibernatetools/hibernate-console_jpa.launch");//$NON-NLS-1$
					} catch (IOException e) {
						SeamCorePlugin.getPluginLog().logError(e);
					}
					//hibernateConsoleLaunchFile = new File(seamGenHomeFolder, "hibernatetools/hibernate-console_jpa.launch"); //$NON-NLS-1$
				}
			} else {
				try {
					hibernateConsoleLaunchFile = new File(SeamFacetInstallDataModelProvider.getTemplatesFolder(), "hibernatetools/hibernate-console.launch");
				} catch (IOException e) {
					SeamCorePlugin.getPluginLog().logError(e);
				}
				//hibernateConsoleLaunchFile = new File(seamGenHomeFolder, "hibernatetools/hibernate-console.launch"); //$NON-NLS-1$
			}
			//final File hibernateConsolePref = new File(seamGenHomeFolder, "hibernatetools/.settings/org.hibernate.eclipse.console.prefs"); //$NON-NLS-1$
			persistenceFile = new File(seamGenResFolder, "META-INF/persistence-" + (isWarConfiguration(model) ? DEV_WAR_PROFILE : DEV_EAR_PROFILE) + ".xml"); //$NON-NLS-1$ //$NON-NLS-2$

			ejbFilterSet = new FilterSet();
			ejbFilterSet.addFilter("projectName", project.getName()); //$NON-NLS-1$
			String serverRuntimeName = WtpUtils.getServerRuntimeName(project);
			if(serverRuntimeName!=null) {
				ejbFilterSet.addFilter("runtimeName", serverRuntimeName); //$NON-NLS-1$
			}
			if (model.getProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH) != null) {
				File driver = new File(((String[]) model.getProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH))[0]);
				ejbFilterSet.addFilter("driverJar", " " + driver.getName() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			} else {
				ejbFilterSet.addFilter("driverJar", ""); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} else {
			seamHomePath = null;
		}

		if(ejbVersion!=null) {
			projectType = ProjectType.EJB;
			doExecuteForEjb(project, fv, model, monitor);
		} else if(webVersion!=null) {
			projectType = ProjectType.WAR;
			doExecuteForWar(project, fv, model, monitor);
		} else if(earVersion!=null) {
			projectType = ProjectType.EAR;
			doExecuteForEar(project, fv, model, monitor);
		}

		if(projectType != ProjectType.EAR) {
			ClasspathHelper.addClasspathEntries(project, fv);
			createSeamProjectPreferenes(project, model);
			EclipseResourceUtil.addNatureToProject(project, ISeamProject.NATURE_ID);
		}

		project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
	}

	private void initDefaultModelValues(IDataModel model, boolean warProject) {
		if(model.getProperty(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS)==null) {
			model.setProperty(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS, warProject?ISeamFacetDataModelProperties.DEPLOY_AS_WAR:ISeamFacetDataModelProperties.DEPLOY_AS_EAR);
		}
		String projectNamePackage = (String)model.getProperty(ISeamFacetDataModelProperties.SEAM_PROJECT_NAME);
		
		projectNamePackage = SeamUtil.getSeamPackageName(projectNamePackage);
		
		IStatus status = JavaConventions.validatePackageName(projectNamePackage, CompilerOptions.VERSION_1_5, CompilerOptions.VERSION_1_5);
		if(!status.isOK()) {
			projectNamePackage = "project"; //$NON-NLS-1$
		}
		if(model.getProperty(ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_NAME)==null) {
			model.setProperty(ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_NAME, "org.domain." + projectNamePackage + ".session"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if(model.getProperty(ISeamFacetDataModelProperties.ENTITY_BEAN_PACKAGE_NAME)==null) {
			model.setProperty(ISeamFacetDataModelProperties.ENTITY_BEAN_PACKAGE_NAME, "org.domain." + projectNamePackage + ".entity"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if(model.getProperty(ISeamFacetDataModelProperties.TEST_PROJECT_CREATING)==null) {
			model.setProperty(ISeamFacetDataModelProperties.TEST_PROJECT_CREATING, new Boolean(true));
		}

		if(model.getProperty(ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_NAME)==null) {
			model.setProperty(ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_NAME, "org.domain." + projectNamePackage + ".test"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if(model.getProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME)==null && model.getBooleanProperty(CONFIGURE_DEFAULT_SEAM_RUNTIME)) {
			String runtimeName = SeamFacetInstallDataModelProvider.getSeamRuntimeDefaultValue(model);
			if((runtimeName!=null && runtimeName.length()>0)) {
				model.setProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME, runtimeName);
			}
		}
		if(model.getProperty(ISeamFacetDataModelProperties.SEAM_CONNECTION_PROFILE)==null) {
			String defaultDs = SeamProjectPreferences.getStringPreference(SeamProjectPreferences.SEAM_DEFAULT_CONNECTION_PROFILE);
			IConnectionProfile[] profiles = ProfileManager.getInstance().getProfilesByCategory("org.eclipse.datatools.connectivity.db.category"); //$NON-NLS-1$
			List<String> names = new ArrayList<String>();
			for (IConnectionProfile connectionProfile : profiles) {
				names.add(connectionProfile.getName());
			}
			if(names.contains(defaultDs)) {
				model.setProperty(ISeamFacetDataModelProperties.SEAM_CONNECTION_PROFILE, defaultDs);
			} else if(!names.isEmpty()) {
				model.setProperty(ISeamFacetDataModelProperties.SEAM_CONNECTION_PROFILE, names.get(0));
			}
		}
	}

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
		prefs.put(SEAM_SETTINGS_VERSION, SEAM_SETTINGS_VERSION_1_1);
		boolean standaloneProject = true;
		if(projectType==ProjectType.EJB) {
			// Try to find parent web seam project for that ejb project.
			ISeamProject parentWebProject = SeamUtil.findReferencingSeamWarProjectForProject(project);
			if(parentWebProject!=null) {
				// set parent web Seam project.
				prefs.put(SEAM_PARENT_PROJECT, parentWebProject.getProject().getName());
				standaloneProject = false;
			}
		}

		if(standaloneProject) {
			prefs.put(JBOSS_AS_DEPLOY_AS, model.getProperty(JBOSS_AS_DEPLOY_AS).toString());
			if(model.getProperty(SEAM_RUNTIME_NAME)!=null) {
				prefs.put(SEAM_RUNTIME_NAME, model.getProperty(SEAM_RUNTIME_NAME).toString());
			}
			if(model.getProperty(SEAM_CONNECTION_PROFILE)!=null) {
				prefs.put(SEAM_CONNECTION_PROFILE, model.getProperty(SEAM_CONNECTION_PROFILE).toString());
			}
			prefs.put(SESSION_BEAN_PACKAGE_NAME, model.getProperty(SESSION_BEAN_PACKAGE_NAME).toString());
			prefs.put(ENTITY_BEAN_PACKAGE_NAME, model.getProperty(ENTITY_BEAN_PACKAGE_NAME).toString());
			prefs.put(TEST_CASES_PACKAGE_NAME, model.getProperty(TEST_CASES_PACKAGE_NAME).toString());
			prefs.put(TEST_CREATING, "false"); //$NON-NLS-1$
			prefs.put(SEAM_TEST_PROJECT, project.getName());
	
			IVirtualComponent component = ComponentCore.createComponent(project);
			IVirtualFolder rootFolder = component.getRootFolder();
			IContainer webRootFolder = rootFolder.getFolder(new Path("/")).getUnderlyingFolder(); //$NON-NLS-1$
			String webRootFolderPath = webRootFolder.getFullPath().toString();
			IPath srcRootFolder = null;
			if(projectType == ProjectType.WAR) {
				srcRootFolder = rootFolder.getFolder(new Path("/WEB-INF/classes")).getUnderlyingFolder().getParent().getFullPath(); //$NON-NLS-1$
			} else if(projectType == ProjectType.EJB) {
				try {
					srcRootFolder = getSrcFolder(project).getFullPath();
				} catch (JavaModelException e) {
					SeamCorePlugin.getPluginLog().logError(e);
					srcRootFolder = new Path(""); //$NON-NLS-1$
				}
			}
			if(projectType == ProjectType.WAR) {
				prefs.put(WEB_CONTENTS_FOLDER, webRootFolderPath);
			} else {
				prefs.put(WEB_CONTENTS_FOLDER, srcRootFolder.toString());
			}
	
			if(!isWarConfiguration(model)) {
				prefs.put(SEAM_EJB_PROJECT, project.getName());
				prefs.put(SEAM_EAR_PROJECT, project.getName());
			}
			prefs.put(ENTITY_BEAN_SOURCE_FOLDER, srcRootFolder.toString());
			prefs.put(SESSION_BEAN_SOURCE_FOLDER, srcRootFolder.toString());
			prefs.put(TEST_SOURCE_FOLDER, srcRootFolder.toString());
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

	/**
	 * 
	 * @param project
	 * @param monitor
	 * @param webConfigName
	 */
	abstract protected void configureFacesConfigXml(final IProject project, IProgressMonitor monitor, String webConfigName);

	protected abstract void configure(WebApp webApp);

	protected WebApp configureWebXml(final IProject project) {
		IModelProvider modelProvider = ModelProviderManager
				.getModelProvider(project);
		Object modelObject = modelProvider.getModelObject();
		if (!(modelObject instanceof WebApp)) {
			// TODO log
			return null;
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
		return (WebApp)modelObject;
	}
	
	protected abstract SeamProjectCreator getProjectCreator(IDataModel model, IProject project);
}