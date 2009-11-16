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
package org.jboss.tools.seam.ui.wizard;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jst.j2ee.internal.common.classpath.J2EEComponentClasspathUpdater;
import org.eclipse.jst.servlet.ui.project.facet.WebProjectFirstPage;
import org.eclipse.jst.servlet.ui.project.facet.WebProjectWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelSynchHelper;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.ui.ServerUIUtil;
import org.jboss.ide.eclipse.as.core.server.internal.JBossServer;
import org.jboss.tools.jst.web.server.RegistrationHelper;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamProjectPreferences;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.internal.core.project.facet.AntCopyUtils;
import org.jboss.tools.seam.internal.core.project.facet.DataSourceXmlDeployer;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.internal.core.project.facet.SeamFacetProjectCreationDataModelProvider;
import org.jboss.tools.seam.ui.ISeamHelpContextIds;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.internal.project.facet.SeamInstallWizardPage;

/**
 * 
 * @author eskimo
 *
 */
@SuppressWarnings("restriction")
public class SeamProjectWizard extends WebProjectWizard {

	private SeamWebProjectFirstPage firstPage;
	private String seamConfigTemplate;

	// We need these controls there to listen to them to set seam action models.
	private Combo matchedServerTargetCombo;
	private Control[] dependentServerControls;
	private Combo serverRuntimeTargetCombo;

	public SeamProjectWizard() {
		super();
		setWindowTitle(SeamUIMessages.SEAM_PROJECT_WIZARD_NEW_SEAM_PROJECT);
	}

	public SeamProjectWizard(IDataModel model) {
		super(model);
		setWindowTitle(SeamUIMessages.SEAM_PROJECT_WIZARD_NEW_SEAM_PROJECT);
	}

	protected IDataModel createDataModel() {
		return DataModelFactory.createDataModel(new SeamFacetProjectCreationDataModelProvider());
	}

	@Override
	protected IWizardPage createFirstPage() {
		firstPage = new SeamWebProjectFirstPage(model, "first.page"); //$NON-NLS-1$

		firstPage.setImageDescriptor(ImageDescriptor.createFromFile(SeamFormWizard.class, "SeamWebProjectWizBan.png"));  //$NON-NLS-1$
		firstPage.setTitle(SeamUIMessages.SEAM_PROJECT_WIZARD_SEAM_WEB_PROJECT);
		firstPage.setDescription(SeamUIMessages.SEAM_PROJECT_WIZARD_CREATE_STANDALONE_SEAM_WEB_PROJECT);
		return firstPage;
	}

	private static final String templateJstSeam1 = "template.jst.seam"; //$NON-NLS-1$
	private static final String templateJstSeam2 = "template.jst.seam2"; //$NON-NLS-1$
	private static final String templateJstSeam21 = "template.jst.seam21"; //$NON-NLS-1$
	private static final String templateJstSeam22 = "template.jst.seam22"; //$NON-NLS-1$

	private static final Map<String, String> templates = new HashMap<String, String>();
	static {
		templates.put("jst.seam.preset", templateJstSeam1); //$NON-NLS-1$
		templates.put("jst.seam2.preset", templateJstSeam2); //$NON-NLS-1$
		templates.put("jst.seam21.preset", templateJstSeam21); //$NON-NLS-1$
		templates.put("jst.seam22.preset", templateJstSeam22); //$NON-NLS-1$
	}

	private void setSeamConfigTemplate(String seamConfigTemplate) {
		this.seamConfigTemplate = seamConfigTemplate;
	}

	@Override
	public void createPageControls(Composite container) {
		super.createPageControls(container);
		synchSeamActionModels();
		getFacetedProjectWorkingCopy().addListener(new IFacetedProjectListener() {
			public void handleEvent(IFacetedProjectEvent event) {
				synchSeamActionModels();
			}
		}, IFacetedProjectEvent.Type.PROJECT_FACETS_CHANGED);
		getFacetedProjectWorkingCopy().addListener(new IFacetedProjectListener() {
			public void handleEvent(IFacetedProjectEvent event) {
				IPreset preset = getFacetedProjectWorkingCopy().getSelectedPreset();
				if(preset!=null) {
					setSeamConfigTemplate(templates.get(preset.getId()));
				}
			}
		}, IFacetedProjectEvent.Type.SELECTED_PRESET_CHANGED);
		getFacetedProjectWorkingCopy().addListener(new IFacetedProjectListener() {
			public void handleEvent(IFacetedProjectEvent event) {
				Set<Action> actions = getFacetedProjectWorkingCopy().getProjectFacetActions();
				for (Action action : actions) {
					if(ISeamFacetDataModelProperties.SEAM_FACET_ID.equals(action.getProjectFacetVersion().getProjectFacet().getId())) {
						IDataModel seamFacetModel = (IDataModel)action.getConfig();
						seamFacetModel.setProperty(IFacetDataModelProperties.FACET_PROJECT_NAME, model.getProperty(IFacetDataModelProperties.FACET_PROJECT_NAME));
					}
				}
			}
		}, IFacetedProjectEvent.Type.PROJECT_NAME_CHANGED);
		Control control = findGroupByText(getShell(), SeamUIMessages.SEAM_PROJECT_WIZARD_EAR_MEMBERSHIP);
		if (control != null)
			control.setVisible(false);
		firstPage.isPageComplete();
	}

	private void synchSeamActionModels() {
		Set<Action> actions = getFacetedProjectWorkingCopy().getProjectFacetActions();
		for (Action action : actions) {
			if(ISeamFacetDataModelProperties.SEAM_FACET_ID.equals(action.getProjectFacetVersion().getProjectFacet().getId())) {
				IDataModel model = (IDataModel)action.getConfig();
				Object targetServer = this.model.getProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER);
				if(targetServer!=null) {
					model.setProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER, targetServer);
				}
				Object targetRuntime = this.model.getProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME);
				if(targetRuntime!=null) {
					Object targetRuntimeName = targetRuntime;
					if(targetRuntime instanceof IRuntime) {
						targetRuntimeName = ((IRuntime)targetRuntime).getName();
					}
					model.setProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME, targetRuntimeName);
				}
				final DataModelSynchHelper synchHelper = firstPage.initializeSynchHelper(model);
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						synchHelper.synchCombo(matchedServerTargetCombo, ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER, dependentServerControls);
						synchHelper.synchCombo(serverRuntimeTargetCombo, ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME, null);
					}
				});
			}
		}
	}

	Control findControlByClass(Composite comp, Class claz) {
		for (Control child : comp.getChildren()) {
			if(child.getClass()==claz) {
				return child;
			} else if(child instanceof Composite){
				Control control = findControlByClass((Composite)child, claz);
				if(control!=null) return control;
			}
		}
		return null;
	}

	Control findGroupByText(Composite comp, String text) {
		for (Control child : comp.getChildren()) {
			if(child instanceof Group && ((Group)child).getText().equals(text)) {
				return child;
			} else if(child instanceof Composite){
				Control control = findGroupByText((Composite)child, text);
				if(control!=null) return control;
			}
		}
		return null;
	}

	@Override
	protected String getFinalPerspectiveID() {
		return "org.jboss.tools.seam.ui.SeamPerspective"; //$NON-NLS-1$
	}

	protected IFacetedProjectTemplate getTemplate() {
		seamConfigTemplate = SeamCorePlugin.getDefault().getPluginPreferences().getString(SeamProjectPreferences.SEAM_CONFIG_TEMPLATE);
		if(seamConfigTemplate==null || seamConfigTemplate.length()==0) {
			SeamRuntime runtime = SeamRuntimeManager.getInstance().getLatestSeamRuntime();
			if(runtime!=null) {
				if(runtime.getVersion()==SeamVersion.SEAM_1_2) {
					seamConfigTemplate = templateJstSeam1;
				} else if(runtime.getVersion()==SeamVersion.SEAM_2_0) {
					seamConfigTemplate = templateJstSeam2;
				} else if(runtime.getVersion()==SeamVersion.SEAM_2_1) {
					seamConfigTemplate = templateJstSeam21;
				} else {
					seamConfigTemplate = templateJstSeam22;
				}
			} else {
				seamConfigTemplate = templateJstSeam22;
			}
		}
		return ProjectFacetsManager.getTemplate(seamConfigTemplate);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.web.ui.internal.wizards.NewProjectDataModelFacetWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		SeamInstallWizardPage page = (SeamInstallWizardPage)getPage(SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_SEAM_FACET);
		page.finishPressed();
		IDataModel model = page.getConfig();
		model.setProperty(ISeamFacetDataModelProperties.CREATE_EAR_PROJECTS, Boolean.TRUE);		
		boolean isEAR = ISeamFacetDataModelProperties.DEPLOY_AS_EAR.equalsIgnoreCase(model.getStringProperty(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS));
		IFacetedProjectWorkingCopy fpwc = getFacetedProjectWorkingCopy();
		IProjectFacet jpaFacet = ProjectFacetsManager.getProjectFacet("jpt.jpa");
		IProjectFacetVersion pfv = fpwc.getProjectFacetVersion(jpaFacet);
		
		if (isEAR && pfv != null){
			//remove jpa facet from <project>
			// and add it to <project>-ejb with the same model			
			action = fpwc.getProjectFacetAction(jpaFacet);
			IDataModel dataModel = (IDataModel) action.getConfig();
			String connectionProfileName = dataModel.getStringProperty("JpaFacetDataModelProperties.CONNECTION");
			if (connectionProfileName == null) throw new NullPointerException("Jpa connection profile is null");
			page.setJpaConnectionProfile(connectionProfileName);
			fpwc.removeProjectFacet(jpaFacet);
		}		
		
		return super.performFinish();
	}
	
	Action action = null;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.web.ui.internal.wizards.NewProjectDataModelFacetWizard#performFinish(org.eclipse.core.runtime.IProgressMonitor)
	 */
    protected void performFinish(final IProgressMonitor monitor) throws CoreException {
    	super.performFinish(monitor);

		IProject warProject = this.getFacetedProject().getProject();
		SeamInstallWizardPage page = (SeamInstallWizardPage)getPage(SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_SEAM_FACET);
		IDataModel model = page.getConfig();

		boolean deployAsEar = ISeamFacetDataModelProperties.DEPLOY_AS_EAR.equals(model.getProperty(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS));
		IProject earProject = null;
		IProject ejbProject = null;
		List<IProject> projects = new ArrayList<IProject>();

		String parentProjectName = warProject.getName() + "-parent"; 
		IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject parentProject = wsRoot.getProject(parentProjectName);
		if (parentProject != null && parentProject.exists()) {
			projects.add(parentProject);
		}
		// build projects. We need to build it before publishing on server.
		projects.add(warProject);
		if(deployAsEar) {
			String ejbProjectName = model.getStringProperty(ISeamFacetDataModelProperties.SEAM_EJB_PROJECT);
			String earProjectName = model.getStringProperty(ISeamFacetDataModelProperties.SEAM_EAR_PROJECT);
			
			earProject = wsRoot.getProject(earProjectName);
			ejbProject = wsRoot.getProject(ejbProjectName);
			projects.add(ejbProject);
			projects.add(earProject);
		}
				
		if(ejbProject != null) {
			provideClassPath(projects, ejbProject);
			if (action != null) {
				IDataModel jpaModel = (IDataModel) action.getConfig();
				boolean isHibernatePlatform = "hibernate".equals(
						jpaModel.getStringProperty("JpaFacetDataModelProperties.PLATFORM_ID"));
				if (isHibernatePlatform){		
					IFile hibernateLaunchFile = ejbProject.getFile(ejbProject.getName() + ".launch");  //$NON-NLS-1$
					if (hibernateLaunchFile.exists()){//delete the launch configuration to prevent doubling
						hibernateLaunchFile.delete(1, monitor);
					}
				}
				
				IFacetedProject facetedProject = ProjectFacetsManager.create(ejbProject, true, null);
				//add facet
				facetedProject.installProjectFacet(action.getProjectFacetVersion(), action.getConfig(), null);
			}
		}
		
		buildProjects(projects, monitor);

		// copy JDBC driver to server libraries folder;
		// register project on the selected server;
		// deploy datasource xml file to the selected server;

		IServer server = (IServer) model.getProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER);
		if (server != null) {
			JBossServer jbs = (JBossServer) server.loadAdapter(JBossServer.class, new NullProgressMonitor());
			if (jbs != null) {
				String[] driverJars = (String[]) model.getProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH);
				String configFolder = jbs.getConfigDirectory();
				AntCopyUtils.copyFiles(driverJars, new File(configFolder, "lib"), false);
			} 
			
			RegistrationHelper.runRegisterInServerJob(warProject, server);

			IPath filePath = new Path("resources").append(warProject.getName() + "-ds.xml");

			if (deployAsEar) {
				new DataSourceXmlDeployer(earProject, server, filePath).schedule();
			} else {
				new DataSourceXmlDeployer(warProject, server, filePath).schedule();
			}			
		}
	}

    private void provideClassPath(List<IProject> projects, IProject ejbProject) throws CoreException {
    	if(ejbProject == null) return;
		int k = 0;
		while(k < 50) {
			k++;
			J2EEComponentClasspathUpdater.getInstance().forceUpdate(projects, false);
			try {
				boolean ok = checkClassPath(ejbProject);
//				System.out.println("-->" + k);
				if(ok) break;
			} catch(CoreException ee1) {
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
//		System.out.println("SeamProjectWizard: Class path provided in " + k + " iterations.");
    }

	public static boolean checkClassPath(IProject project) throws CoreException {
		if(project == null || !project.isAccessible() || !project.hasNature(JavaCore.NATURE_ID)) return false;
		IJavaProject javaProject = JavaCore.create(project);		
		IClasspathEntry[] es = javaProject.getRawClasspath();
		for (int i = 0; i < es.length; i++) {
			if(es[i].getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
				IPath p = es[i].getPath();
				//"org.eclipse.jst.j2ee.internal.module.container"
				if(p.toString().startsWith("org.eclipse.jst.j2ee")) {
					IClasspathContainer c = JavaCore.getClasspathContainer(p, javaProject);
					if(c == null) return false;
					IClasspathEntry[] cs = c.getClasspathEntries();
					return cs != null && cs.length > 0;
				}
			}
		}
		return true;
 	}

	private void buildProjects(List<IProject> projects, IProgressMonitor monitor) {
   		J2EEComponentClasspathUpdater.getInstance().forceUpdate(projects, false);
		try {
			for (IProject project : projects) {
				project.build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);
				project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
			}
		} catch (CoreException e) {
			SeamGuiPlugin.getPluginLog().logError(e);
		}
	}

    class SeamWebProjectFirstPage extends WebProjectFirstPage {
		@Override
		protected String getInfopopID() {
			return ISeamHelpContextIds.NEW_SEAM_PROJECT;
		}

		public SeamWebProjectFirstPage(IDataModel model, String pageName ) {
			super(model, pageName);
		}

		protected Composite createTopLevelComposite(Composite parent) {
			Composite top = new Composite(parent, SWT.NONE);
			top.setLayout(new GridLayout());
			top.setLayoutData(new GridData(GridData.FILL_BOTH));
			createProjectGroup(top);
			createServerTargetComposite(top);
			serverRuntimeTargetCombo = serverTargetCombo;
			createPrimaryFacetComposite(top);
			createSeamServerTargetComposite(top);
	        createPresetPanel(top);
	        return top;
		}

		protected void createSeamServerTargetComposite(Composite parent) {
	        Group group = new Group(parent, SWT.NONE);
	        group.setText(SeamUIMessages.SEAM_TARGET_SERVER);
	        group.setLayoutData(gdhfill());
	        group.setLayout(new GridLayout(2, false));

	        matchedServerTargetCombo = new Combo(group, SWT.BORDER | SWT.READ_ONLY);
			matchedServerTargetCombo.setLayoutData(gdhfill());
			Button newMatchedServerTargetButton = new Button(group, SWT.NONE);
			newMatchedServerTargetButton.setText(SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_NEW);
			newMatchedServerTargetButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (!SeamWebProjectFirstPage.this.internalLaunchNewServerWizard(getShell(), model)) {
						//Bugzilla 135288
						//setErrorMessage(ResourceHandler.InvalidServerTarget);
					}
				}
			});
			dependentServerControls = new Control[]{serverTargetCombo, newMatchedServerTargetButton};
			synchHelper.synchCombo(matchedServerTargetCombo, ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER, dependentServerControls);
			if (matchedServerTargetCombo.getSelectionIndex() == -1 && matchedServerTargetCombo.getVisibleItemCount() != 0)  
				matchedServerTargetCombo.select(0);
		}

		protected String[] getValidationPropertyNames() {
			String[] superProperties = super.getValidationPropertyNames();
			List list = Arrays.asList(superProperties);
			ArrayList arrayList = new ArrayList();
			arrayList.addAll( list );
			arrayList.add( ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME);
			arrayList.add( ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER);
			arrayList.add( ISeamFacetDataModelProperties.SEAM_PROJECT_NAME);
			return (String[])arrayList.toArray( new String[0] );
		}	

		public boolean launchNewServerWizard(Shell shell, IDataModel model) {
			return launchNewServerWizard(shell, model, null);
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
		 */
		@Override
	    public boolean isPageComplete() {
			boolean pageComplete = super.isPageComplete();

			IProjectFacet pFacet = ProjectFacetsManager.getProjectFacet(ISeamFacetDataModelProperties.SEAM_FACET_ID);
        	IFacetedProjectWorkingCopy fProject = getFacetedProjectWorkingCopy();
        	if(fProject!=null) {
	        	IProjectFacetVersion seamFacet = fProject.getProjectFacetVersion(pFacet);
	        	if(seamFacet==null) {
	        		if(pageComplete) {
		        		this.setErrorMessage(SeamUIMessages.SEAM_PROJECT_WIZARD_PAGE1_SEAM_FACET_MUST_BE_SPECIFIED);
		        		return false;
	        		}
	        	} else {
	        		if(pageComplete) {
	        			this.setErrorMessage(null);
	        		} else if(SeamUIMessages.SEAM_PROJECT_WIZARD_PAGE1_SEAM_FACET_MUST_BE_SPECIFIED.equals(getErrorMessage())) {
	        			this.setErrorMessage(null);
	        		}
	        	}
        	}
        	return pageComplete;
	    }

		public boolean launchNewServerWizard(Shell shell, final IDataModel model, String serverTypeID) {
			DataModelPropertyDescriptor[] preAdditionDescriptors = model.getValidPropertyDescriptors(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER);
			IRuntime rt = (IRuntime)model.getProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME);

			IServerLifecycleListener serverListener = new IServerLifecycleListener() {
				public void serverAdded(IServer server) {
					DataModelPropertyDescriptor[] descriptors = model.getValidPropertyDescriptors(IFacetProjectCreationDataModelProperties.FACET_RUNTIME);
					for (int i = 0; i < descriptors.length; i++) {
						if(server.getRuntime().getName().equals(descriptors[i].getPropertyDescription())) {
							model.setProperty(IFacetProjectCreationDataModelProperties.FACET_RUNTIME, descriptors[i].getPropertyValue());
							model.setProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME, descriptors[i].getPropertyValue());
						}
					}
					model.setProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER, server);
				}
				public void serverChanged(IServer server) {
				}
				public void serverRemoved(IServer server) {
				}
			};

			ServerCore.addServerLifecycleListener(serverListener);
			boolean isOK = false;
			try {
				isOK = ServerUIUtil.showNewServerWizard(shell, serverTypeID, null, (rt == null ? null : null));
			} finally {
				ServerCore.removeServerLifecycleListener(serverListener);
			}
			if (isOK && model != null) {
				DataModelPropertyDescriptor[] postAdditionDescriptors = model.getValidPropertyDescriptors(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER);
				Object[] preAddition = new Object[preAdditionDescriptors.length];
				for (int i = 0; i < preAddition.length; i++) {
					preAddition[i] = preAdditionDescriptors[i].getPropertyValue();
				}
				Object[] postAddition = new Object[postAdditionDescriptors.length];
				for (int i = 0; i < postAddition.length; i++) {
					postAddition[i] = postAdditionDescriptors[i].getPropertyValue();
				}
				Object newAddition = null;

				if (preAddition != null && postAddition != null && preAddition.length < postAddition.length) {
					for (int i = 0; i < postAddition.length; i++) {
						boolean found = false;
						Object object = postAddition[i];
						for (int j = 0; j < preAddition.length; j++) {
							if (preAddition[j] == object) {
								found = true;
								break;
							}
						}
						if (!found) {
							newAddition = object;
						}
					}
				}
				if (preAddition == null && postAddition != null && postAddition.length == 1)
					newAddition = postAddition[0];

				model.notifyPropertyChange(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER, IDataModel.VALID_VALUES_CHG);
				isPageComplete();
				if (newAddition != null)
					model.setProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER, newAddition);
				else
					return false;
			}
			return isOK;
		}

		public boolean internalLaunchNewServerWizard(Shell shell, IDataModel model) {
			return launchNewServerWizard(shell, model, getModuleTypeID());
		}

	    public void restoreDefaultSettings() {
	    	super.restoreDefaultSettings();

	    	String lastServerName = SeamProjectPreferences
			.getStringPreference(SeamProjectPreferences.SEAM_LAST_SERVER_NAME);

	    	if (lastServerName != null && lastServerName.length() > 0) {
		    	SeamFacetProjectCreationDataModelProvider.setServerName(model,lastServerName);
	    	}
	    }

	    public void storeDefaultSettings() {
	    	super.storeDefaultSettings();
	    	Preferences preferences = SeamCorePlugin.getDefault().getPluginPreferences();
	    	String serverName = SeamFacetProjectCreationDataModelProvider.getServerName(model);
	    	if (serverName != null && serverName.length() > 0) {
	    		preferences.setValue(
						SeamProjectPreferences.SEAM_LAST_SERVER_NAME,
						serverName);
	    	}
	    	if(seamConfigTemplate!=null) {
		    	preferences.setValue(
						SeamProjectPreferences.SEAM_CONFIG_TEMPLATE,
						seamConfigTemplate);
	    	}
	    }
	}
}