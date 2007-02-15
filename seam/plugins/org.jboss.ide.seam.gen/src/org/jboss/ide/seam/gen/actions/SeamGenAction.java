package org.jboss.ide.seam.gen.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.ant.internal.ui.IAntUIConstants;
import org.eclipse.ant.internal.ui.launchConfigurations.IAntLaunchConfigurationConstants;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.externaltools.internal.launchConfigurations.ExternalToolsUtil;
import org.eclipse.ui.externaltools.internal.model.IExternalToolConstants;
import org.jboss.ide.seam.gen.QuestionDialog;
import org.jboss.ide.seam.gen.SeamGenPlugin;

public abstract class SeamGenAction implements IWorkbenchWindowActionDelegate {

	private final class LaunchListener implements ILaunchesListener2 {
		public void launchesRemoved(ILaunch[] launches) {
			
		}

		public void launchesChanged(ILaunch[] launches) {
			
		}

		public void launchesAdded(ILaunch[] launches) {
			
		
		}

		public void launchesTerminated(ILaunch[] launches) {
			for (int i = 0; i < launches.length; i++) {
				ILaunch launch2 = launches[i];
				if("seamgen".equals( launch2.getLaunchConfiguration().getName() )) {
					try {
						//							org.eclipse.ui.externaltools.internal.launchConfigurations
						SeamGenPlugin.logInfo( "launch completed...auto detecting project" );
						IPath location = ExternalToolsUtil.getLocation( launch2.getLaunchConfiguration() );
						SeamGenPlugin.logInfo( "location: " + location );
						File file = new File(location.toFile().getParentFile(), "build.properties");
						SeamGenPlugin.logInfo( "build.properties: " + location );
						if(file.exists()) {
							Properties p = new Properties();
							p.load( new FileInputStream(file) );
							String workspace = p.getProperty( "workspace.home" );
							String projectName = p.getProperty( "project.name" );
							
							IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
							
							if(!project.exists()) {
								SeamGenPlugin.logInfo( "project " + projectName + " does not exist");
								IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);
								URI uri = new File(workspace, projectName).toURI();
								description.setLocationURI(uri);
								SeamGenPlugin.logInfo( "project location should be " + uri);
								project.create(description, null);
								project.open( null );
								SeamGenPlugin.logInfo( "project " + projectName + " created ");
							} else {
								SeamGenPlugin.logInfo( "project " + projectName + " already exists");
							}
							//project.refreshLocal( IResource.DEPTH_INFINITE, null );

						} else {
							SeamGenPlugin.logInfo( "build.properties not found");
						}
					}
					catch (CoreException e) {
						SeamGenPlugin.logError( "Error when seam-gen terminated", e );
					}
					catch (FileNotFoundException e) {
						SeamGenPlugin.logError( "Error when seam-gen terminated", e );
					}
					catch (IOException e) {
						SeamGenPlugin.logError( "Error when seam-gen terminated", e );
					} finally {

					}
				}

			}

		}
	}

	protected IWorkbenchWindow window;
	private LaunchListener launchListener = new LaunchListener();

	public SeamGenAction() {
		super();
	}

	public void run(IAction action) {
		
		try {
			
			ILaunchConfiguration launchConfiguration = findLaunchConfig( "seamgen" );		
			
			ILaunchConfigurationWorkingCopy wc = null;
			if(launchConfiguration==null) {
				SeamGenPlugin.logInfo( "seamgen launch config not found. Creating one automatically." );
				FileDialog fileDialog = new FileDialog(window.getShell(), SWT.NONE);
				fileDialog.setText( "Select Seam Gen build.xml..." );
				fileDialog.setFileName("build.xml");
				String text=fileDialog.open();
				if (text != null) {
					SeamGenPlugin.logInfo( "User selected: " + text + " as build.xml" );
					ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
					ILaunchConfigurationType launchConfigurationType = launchManager.getLaunchConfigurationType( "org.eclipse.ant.AntLaunchConfigurationType" );
					wc = launchConfigurationType.newInstance( null, "seamgen" );
					wc.setAttribute( "process_factory_id", "org.eclipse.ant.ui.remoteAntProcessFactory" );
					wc.setAttribute(IAntUIConstants.ATTR_DEFAULT_VM_INSTALL, true);
					wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "org.eclipse.ant.internal.ui.antsupport.InternalAntRunner");
					
					wc.setAttribute("org.eclipse.debug.core.appendEnvironmentVariables", true);
					
					wc.setAttribute( "org.eclipse.jdt.launching.CLASSPATH_PROVIDER", "org.eclipse.ant.ui.AntClasspathProvider" );
					wc.setAttribute( "org.eclipse.jdt.launching.SOURCEPATH_PROVIDER", "org.eclipse.ant.ui.AntClasspathProvider" );
					
					wc.setAttribute( "org.eclipse.jdt.launching.VM_INSTALL_TYPE_ID", "org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType");
					
					wc.setAttribute( IExternalToolConstants.ATTR_LOCATION, text );
					
					wc.doSave();
					SeamGenPlugin.logInfo( "seamgen launch config saved" );
				} else {
					MessageDialog.openError( window.getShell(), "No build.xml selected", "You have to select the build.xml to be used by Seam Gen." );
					return;
				}
			} else {
				wc = launchConfiguration.getWorkingCopy();				
			}			
			
			wc.setAttribute(IAntUIConstants.SET_INPUTHANDLER, true);
			Map userProperties = Collections.EMPTY_MAP;
			Map questions = getQuestions();
			if(!questions.isEmpty()) {
				QuestionDialog questionDialog = new QuestionDialog(window.getShell(), getTitle(), getDescription(), questions);
				if(questionDialog.open()!= QuestionDialog.OK) {
					SeamGenPlugin.logInfo( "User cancelled dialog" );
					return;
				} else {
					userProperties = questionDialog.getPropertiesResult();
				}
			}
			wc.setAttribute( IAntLaunchConfigurationConstants.ATTR_ANT_PROPERTIES, userProperties);

			wc.setAttribute(IAntLaunchConfigurationConstants.ATTR_ANT_TARGETS, getTarget());


			ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

			launchManager.addLaunchListener( launchListener );

//			launchConfiguration = wc.doSave();
			SeamGenPlugin.logInfo( "launching seamgen " + getTarget() );
			ILaunch launch = wc.launch( ILaunchManager.RUN_MODE, null );
			
		} catch (CoreException e) {			
			SeamGenPlugin.logError( "Exception when trying to launch seamgen", e );
		}
	
	}

	public String getDescription() {
		return "Seam Gen " + getTarget();
	}

	public String getTitle() {
		return getDescription();
	}

	protected Map getQuestions() {		
		return Collections.EMPTY_MAP;
	}

	protected abstract String getTarget();
	
	private ILaunchConfiguration findLaunchConfig(String name) throws CoreException {
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType launchConfigurationType = launchManager.getLaunchConfigurationType( "org.eclipse.ant.AntLaunchConfigurationType" );
		ILaunchConfiguration[] launchConfigurations = launchManager.getLaunchConfigurations( launchConfigurationType );
	
		for (int i = 0; i < launchConfigurations.length; i++) { // can't believe there is no look up by name API
			ILaunchConfiguration launchConfiguration = launchConfigurations[i];
			if(launchConfiguration.getName().equals(name)) {
				return launchConfiguration;
			}
		} 
		return null;
	}

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}