package org.jboss.ide.seam.gen.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.ant.internal.ui.IAntUIConstants;
import org.eclipse.ant.ui.launching.IAntLaunchConfigurationConstants;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.internal.utils.FileUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.datatools.connectivity.ConnectionProfileConstants;
import org.eclipse.datatools.connectivity.ConnectionProfileException;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.db.generic.IDBConnectionProfileConstants;
import org.eclipse.datatools.connectivity.db.generic.IDBDriverDefinitionConstants;
import org.eclipse.datatools.connectivity.drivers.DriverInstance;
import org.eclipse.datatools.connectivity.drivers.DriverManager;
import org.eclipse.datatools.connectivity.drivers.DriverMgmtMessages;
import org.eclipse.datatools.connectivity.drivers.IDriverMgmtConstants;
import org.eclipse.datatools.connectivity.drivers.IPropertySet;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.externaltools.internal.launchConfigurations.ExternalToolsUtil;
import org.eclipse.ui.externaltools.internal.model.IExternalToolConstants;
import org.jboss.ide.seam.gen.Messages;
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
				final ILaunch launch2 = launches[i];
				if("seamgen".equals( launch2.getLaunchConfiguration().getName() )) { //$NON-NLS-1$
					try {
						String target = launch2.getLaunchConfiguration().getAttribute(IAntLaunchConfigurationConstants.ATTR_ANT_TARGETS,(String)null);
						if("setup".equals(target) && launch2.getAttribute( "terminated-done" )==null) { //$NON-NLS-1$ //$NON-NLS-2$
							launch2.setAttribute( "terminated-done", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
							SeamGenPlugin.getDefault().getWorkbench().getDisplay().syncExec(
									  new Runnable() {
									    public void run(){
									    	if(MessageDialog.openQuestion( getShell(), Messages.CreateProjectTitle, Messages.CreateProjectQuestion )) {
												new NewProjectAction().run( null );												
											}
									    	
									    	if(MessageDialog.openQuestion( getShell(), Messages.CreateConnectionTitle, Messages.CreateConnectionQuestion  )) {
									    		createDatabaseConnection(launch2);
									    	}
									    }

									    public DriverInstance createNewDriverInstance(String templateID,
												String name, String jarList, String driverClass) {
											if (templateID == null) return null;
											if (name == null) return null;
											if (jarList == null) return null;
											
											DriverInstance existing = DriverManager.getInstance().getDriverInstanceByName(name);
											int number = 0;
											String origName = name;
											while(existing!=null ) {
												number++;
												name = origName+number;
												existing = DriverManager.getInstance().getDriverInstanceByName("DriverDefn."+name);												 //$NON-NLS-1$
											}
											
											IPropertySet pset = DriverManager.getInstance().createDefaultInstance(templateID);
											pset.setName(name);
											String prefix = DriverMgmtMessages
													.getString("NewDriverDialog.text.id_prefix"); //$NON-NLS-1$
											String id = prefix + name;
											pset.setID(id);
											Properties props = pset.getBaseProperties();
											props.setProperty(IDriverMgmtConstants.PROP_DEFN_JARLIST, jarList);
											props.setProperty(IDBDriverDefinitionConstants.DRIVER_CLASS_PROP_ID, driverClass);
											DriverManager.getInstance().addDriverInstance(pset);
											return DriverManager.getInstance().getDriverInstanceByID(pset.getID());
										}

									    
										private void createDatabaseConnection(
												final ILaunch launch2) {
											
											Properties seamGenProperties = getSeamGenProperties( launch2.getLaunchConfiguration() );
											String projectName = seamGenProperties.getProperty( "project.name" ); //$NON-NLS-1$
									
											Properties dbProperties = new Properties();
											if(seamGenProperties!=null) {
												DriverInstance driverInstance = createNewDriverInstance("org.eclipse.datatools.connectivity.db.generic.genericDriverTemplate",  //$NON-NLS-1$
														                                                projectName + " seamgen-driver",  //$NON-NLS-1$
														                                                seamGenProperties.getProperty("driver.jar", ""), //$NON-NLS-1$ //$NON-NLS-2$
														                                                seamGenProperties.getProperty( "hibernate.connection.driver_class", "" )); //$NON-NLS-1$ //$NON-NLS-2$
												
												dbProperties.setProperty(ConnectionProfileConstants.PROP_DRIVER_DEFINITION_ID, driverInstance.getId());
												dbProperties.setProperty(IDBConnectionProfileConstants.DRIVER_CLASS_PROP_ID, seamGenProperties.getProperty( "hibernate.connection.driver_class", "" ));									 //$NON-NLS-1$ //$NON-NLS-2$
												dbProperties.setProperty(IDBConnectionProfileConstants.DATABASE_VENDOR_PROP_ID, "Generic JDBC"); //$NON-NLS-1$
												dbProperties.setProperty(IDBConnectionProfileConstants.DATABASE_VERSION_PROP_ID, "1.0"); //$NON-NLS-1$
												dbProperties.setProperty(IDBConnectionProfileConstants.DATABASE_NAME_PROP_ID, "SeamGen database"); //$NON-NLS-1$
												dbProperties.setProperty(IDBConnectionProfileConstants.PASSWORD_PROP_ID, seamGenProperties.getProperty( "hibernate.connection.password", "" )); //$NON-NLS-1$ //$NON-NLS-2$
												dbProperties.setProperty(IDBConnectionProfileConstants.USERNAME_PROP_ID, seamGenProperties.getProperty( "hibernate.connection.username", "" )); //$NON-NLS-1$ //$NON-NLS-2$
												dbProperties.setProperty(IDBConnectionProfileConstants.URL_PROP_ID, seamGenProperties.getProperty( "hibernate.connection.url", "" )); //$NON-NLS-1$ //$NON-NLS-2$
												
												
												//connection.setLoadingPath(seamGenProperties.getProperty( "driver.jar", "" ));
												
												//connection.setCustomProperty( "JDBC_DRIVER","Other");
												
												try {
													String name = projectName + " seamgen-connection"; //$NON-NLS-1$
													IConnectionProfile existing = ProfileManager.getInstance().getProfileByName(name);
													int number = 0;
													String origName = name;
													while(existing!=null) {
														number++;
														name=origName+number;
														existing = ProfileManager.getInstance().getProfileByName(name);												
													}
													
													ProfileManager.getInstance().createProfile(name, 
															Messages.SeamGenAction_ProfileDescription, 
															IDBConnectionProfileConstants.CONNECTION_PROFILE_ID, 
															dbProperties
															);
													// TODO unique name ? NewCWJDBCPage.createUniqueConnectionName( NewCWJDBCPage.getExistingConnectionNamesList(), "seamgen-connection"));
												} catch (ConnectionProfileException e) {
													SeamGenPlugin.logError("Could not create database connection", e); //$NON-NLS-1$
													MessageDialog.openError( getShell(), Messages.CouldNotCreateDatabaseConnectionTitle, Messages.CouldNotCreateDatabaseConnectionDetails );
												}
															
											} else {
												MessageDialog.openError( getShell(), Messages.CouldNotReadDatabaseSettingsTitle, Messages.CouldNotReadDatabaseSettingsDetails );
											}
										}
									  });
							return;
						}
						
						
						//							org.eclipse.ui.externaltools.internal.launchConfigurations
						Properties p = getSeamGenProperties( launch2.getLaunchConfiguration() );
						if(p!=null) {
							String seamWorkspace = p.getProperty( "workspace.home" ); //$NON-NLS-1$
							String projectName = p.getProperty( "project.name" ); //$NON-NLS-1$
							
							IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
						
							if(!"new-project".equals(target)) { //$NON-NLS-1$
								if(project.exists()) {
									project.refreshLocal( IResource.DEPTH_INFINITE, null );
								}
								return; // only autodetect for new-project stuff.
							}
							
							if(!project.exists()) {
								SeamGenPlugin.logInfo( "project " + projectName + " does not exist"); //$NON-NLS-1$ //$NON-NLS-2$
								IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);
								URI uri = new File(seamWorkspace, projectName).toURI();
								
								IPath locationPath = URIUtil.toPath(uri);
								IPath defaultDefaultLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation();
								IPath parentPath = locationPath.removeLastSegments(1);
								if (FileUtil.isPrefixOf(parentPath, defaultDefaultLocation) && FileUtil.isPrefixOf(defaultDefaultLocation, parentPath)) {
									SeamGenPlugin.logInfo( "seam workspace overlaps with eclipse. Opening project directly." ); //$NON-NLS-1$
								} else {
									description.setLocationURI(uri);
									SeamGenPlugin.logInfo( "project location should be " + uri); //$NON-NLS-1$
									
								}
								project.create(description, null);								
								project.open( null );
								SeamGenPlugin.logInfo( "project " + projectName + " created "); //$NON-NLS-1$ //$NON-NLS-2$
							} else {
								SeamGenPlugin.logInfo( "project " + projectName + " already exists"); //$NON-NLS-1$ //$NON-NLS-2$
							}
							//project.refreshLocal( IResource.DEPTH_INFINITE, null );

						} else {
							SeamGenPlugin.logInfo( "build.properties not found"); //$NON-NLS-1$
						}
					}
					catch (CoreException e) {
						SeamGenPlugin.logError( "Error when seam-gen terminated", e ); //$NON-NLS-1$
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

	public static ILaunchConfigurationWorkingCopy createSeamgenLaunchConfig(String pathToSeamgenBuildXml) throws CoreException {
		SeamGenPlugin.logInfo( "User selected: " + pathToSeamgenBuildXml + " as build.xml" ); //$NON-NLS-1$ //$NON-NLS-2$
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType launchConfigurationType = launchManager.getLaunchConfigurationType( "org.eclipse.ant.AntLaunchConfigurationType" ); //$NON-NLS-1$
		ILaunchConfigurationWorkingCopy wc = launchConfigurationType.newInstance( null, "seamgen" ); //$NON-NLS-1$
		wc.setAttribute( "process_factory_id", "org.eclipse.ant.ui.remoteAntProcessFactory" ); //$NON-NLS-1$ //$NON-NLS-2$
		wc.setAttribute(IAntLaunchConfigurationConstants.ATTR_DEFAULT_VM_INSTALL, true);
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "org.eclipse.ant.internal.ui.antsupport.InternalAntRunner"); //$NON-NLS-1$
		
		wc.setAttribute("org.eclipse.debug.core.appendEnvironmentVariables", true); //$NON-NLS-1$
		
		wc.setAttribute( "org.eclipse.jdt.launching.CLASSPATH_PROVIDER", "org.eclipse.ant.ui.AntClasspathProvider" ); //$NON-NLS-1$ //$NON-NLS-2$
		wc.setAttribute( "org.eclipse.jdt.launching.SOURCEPATH_PROVIDER", "org.eclipse.ant.ui.AntClasspathProvider" ); //$NON-NLS-1$ //$NON-NLS-2$
		
		wc.setAttribute( "org.eclipse.jdt.launching.VM_INSTALL_TYPE_ID", "org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType"); //$NON-NLS-1$ //$NON-NLS-2$
		
		wc.setAttribute( IExternalToolConstants.ATTR_LOCATION, pathToSeamgenBuildXml );
		
		wc.doSave();
		SeamGenPlugin.logInfo( "seamgen launch config saved" ); //$NON-NLS-1$
		return wc;
	}

	public void run(IAction action) {
		
		try {
			
			ILaunchConfiguration launchConfiguration = findLaunchConfig( "seamgen" );		 //$NON-NLS-1$
			
			ILaunchConfigurationWorkingCopy wc = null;
			if(launchConfiguration==null) {
				SeamGenPlugin.logInfo( "seamgen launch config not found. Creating one automatically." ); //$NON-NLS-1$
				FileDialog fileDialog = new FileDialog(window.getShell(), SWT.NONE);
				fileDialog.setText( Messages.SelectBuildXML );
				fileDialog.setFileName("build.xml"); //$NON-NLS-1$
				String text=fileDialog.open();
				if (text != null) {
					wc = createSeamgenLaunchConfig(text);
				} else {
					MessageDialog.openError( window.getShell(), Messages.NoBuildXMLSelectedTitle, Messages.NoBuildXMLSelectedDetails );
					return;
				}
			} else {
				wc = launchConfiguration.getWorkingCopy();				
			}			
			
			wc.setAttribute(IAntUIConstants.SET_INPUTHANDLER, true);
			Map userProperties = Collections.EMPTY_MAP;
			Map questions = getQuestions();
			if(!questions.isEmpty()) {
				QuestionDialog questionDialog = new QuestionDialog(window.getShell(), getTitle(), getDescription(), questions,getGroups());
				if(questionDialog.open()!= QuestionDialog.OK) {
					SeamGenPlugin.logInfo( "User cancelled dialog" ); //$NON-NLS-1$
					return;
				} else {
					userProperties = questionDialog.getPropertiesResult();
				}
			}
			
			Properties empties = new Properties();
			Iterator iterator = userProperties.entrySet().iterator();
			while(iterator.hasNext()) {
				Map.Entry element = (Entry) iterator.next();
				String value = (String) element.getValue();
				if(value==null || value.trim().length()==0) {
					iterator.remove();
					empties.setProperty( (String) element.getKey(), value );
				}
			}
			
			if(!empties.isEmpty()) {
				File createTempFile = null;
				FileOutputStream fos = null;
				try {				
					createTempFile = File.createTempFile( "seamgenempty", "properties" ); //$NON-NLS-1$ //$NON-NLS-2$
					fos = new FileOutputStream(createTempFile);
					empties.store( fos, "File used to send intentionally empty valued properties" ); //$NON-NLS-1$
				}
				catch (FileNotFoundException e) {
					SeamGenPlugin.logError( "Error while running " + getTarget(), e ); //$NON-NLS-1$
				}
				catch (IOException e) {
					SeamGenPlugin.logError( "Error while running " + getTarget(), e ); //$NON-NLS-1$
				}
				finally {
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException e) {
							// ignore
						}
					}
				}

				if(createTempFile!=null) {
					wc.setAttribute( IAntLaunchConfigurationConstants.ATTR_ANT_PROPERTY_FILES, createTempFile.toString() );
				}
			}
			
			wc.setAttribute( IAntLaunchConfigurationConstants.ATTR_ANT_PROPERTIES, userProperties);

			wc.setAttribute(IAntLaunchConfigurationConstants.ATTR_ANT_TARGETS, getTarget());

			

			ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

			launchManager.addLaunchListener( launchListener );

//			launchConfiguration = wc.doSave();
			SeamGenPlugin.logInfo( "launching seamgen " + getTarget() ); //$NON-NLS-1$
			ILaunch launch = wc.launch( ILaunchManager.RUN_MODE, null );
			
		} catch (CoreException e) {			
			SeamGenPlugin.logError( "Exception when trying to launch seamgen", e ); //$NON-NLS-1$
			MessageDialog.openError(getShell(), Messages.LaunchErrorTitle, e.getMessage());
		}
	
	}

	public Set getGroups() {
		return Collections.EMPTY_SET;
	}

	public String getDescription() {
		return MessageFormat.format(Messages.SeamGenAction_SeamGenActionName, getTarget());
	}

	public String getTitle() {
		return getDescription();
	}

	protected Map getQuestions() {		
		return Collections.EMPTY_MAP;
	}

	protected abstract String getTarget();
	
	static public ILaunchConfiguration findLaunchConfig(String name) throws CoreException {
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType launchConfigurationType = launchManager.getLaunchConfigurationType( "org.eclipse.ant.AntLaunchConfigurationType" ); //$NON-NLS-1$
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

	public static Properties getSeamGenProperties(ILaunchConfiguration lc) {
		SeamGenPlugin.logInfo( "launch completed...auto detecting project" ); //$NON-NLS-1$
		IPath location;
		try {
			location = ExternalToolsUtil.getLocation( lc );
		}
		catch (CoreException e2) {
			SeamGenPlugin.logError( "Error while loading seamgen properties", e2 ); //$NON-NLS-1$
			return null;
		}
		
		SeamGenPlugin.logInfo( "location: " + location ); //$NON-NLS-1$
		File file = new File(location.toFile().getParentFile(), "build.properties"); //$NON-NLS-1$
		SeamGenPlugin.logInfo( "build.properties: " + location ); //$NON-NLS-1$
		
		if(file.exists()) {
			Properties p = new Properties();
			FileInputStream fileInputStream = null;
			try {
				fileInputStream = new FileInputStream(file);
				p.load( fileInputStream );
			}
			catch (Exception e) {
				SeamGenPlugin.logError( "Error while loading seamgen properties", e ); //$NON-NLS-1$
			}
			finally {
				if(fileInputStream!=null)
					try {
						fileInputStream.close();
					}
					catch (IOException e1) {
						SeamGenPlugin.logError( "Error while closing seamgen properties", e1 ); //$NON-NLS-1$
					}
			}
			return p;
		} else {
			return new Properties(); // no exsting settings.
		}
		
	}

	private Shell getShell() {
		return SeamGenPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
	}
}