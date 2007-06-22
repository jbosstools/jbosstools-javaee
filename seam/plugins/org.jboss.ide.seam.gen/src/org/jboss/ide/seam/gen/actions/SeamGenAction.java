package org.jboss.ide.seam.gen.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.ant.internal.ui.IAntUIConstants;
import org.eclipse.ant.internal.ui.launchConfigurations.IAntLaunchConfigurationConstants;
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
				if("seamgen".equals( launch2.getLaunchConfiguration().getName() )) {
					try {
						String target = launch2.getLaunchConfiguration().getAttribute(IAntLaunchConfigurationConstants.ATTR_ANT_TARGETS,(String)null);
						if("setup".equals(target) && launch2.getAttribute( "terminated-done" )==null) {
							launch2.setAttribute( "terminated-done", "true" );
							SeamGenPlugin.getDefault().getWorkbench().getDisplay().syncExec(
									  new Runnable() {
									    public void run(){
									    	if(MessageDialog.openQuestion( getShell(), "Create new Seam project", "Create new seam project ?" )) {
												new NewProjectAction().run( null );												
											}
									    	
									    	if(MessageDialog.openQuestion( getShell(), "Create DB Connection", "Create DB Connection ?"  )) {
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
												existing = DriverManager.getInstance().getDriverInstanceByName("DriverDefn."+name);												
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
											String projectName = seamGenProperties.getProperty( "project.name" );
									
											Properties dbProperties = new Properties();
											if(seamGenProperties!=null) {
												DriverInstance driverInstance = createNewDriverInstance("org.eclipse.datatools.connectivity.db.generic.genericDriverTemplate", 
														                                                projectName + " seamgen-driver", 
														                                                seamGenProperties.getProperty("driver.jar", ""),
														                                                seamGenProperties.getProperty( "hibernate.connection.driver_class", "" ));
												
												dbProperties.setProperty(ConnectionProfileConstants.PROP_DRIVER_DEFINITION_ID, driverInstance.getId());
												dbProperties.setProperty(IDBConnectionProfileConstants.DRIVER_CLASS_PROP_ID, seamGenProperties.getProperty( "hibernate.connection.driver_class", "" ));									
												dbProperties.setProperty(IDBConnectionProfileConstants.DATABASE_VENDOR_PROP_ID, "Generic JDBC");
												dbProperties.setProperty(IDBConnectionProfileConstants.DATABASE_VERSION_PROP_ID, "1.0");
												dbProperties.setProperty(IDBConnectionProfileConstants.DATABASE_NAME_PROP_ID, "SeamGen database");
												dbProperties.setProperty(IDBConnectionProfileConstants.PASSWORD_PROP_ID, seamGenProperties.getProperty( "hibernate.connection.password", "" ));
												dbProperties.setProperty(IDBConnectionProfileConstants.USERNAME_PROP_ID, seamGenProperties.getProperty( "hibernate.connection.username", "" ));
												dbProperties.setProperty(IDBConnectionProfileConstants.URL_PROP_ID, seamGenProperties.getProperty( "hibernate.connection.url", "" ));
												
												
												//connection.setLoadingPath(seamGenProperties.getProperty( "driver.jar", "" ));
												
												//connection.setCustomProperty( "JDBC_DRIVER","Other");
												
												try {
													String name = projectName + " seamgen-connection";
													IConnectionProfile existing = ProfileManager.getInstance().getProfileByName(name);
													int number = 0;
													String origName = name;
													while(existing!=null) {
														number++;
														name=origName+number;
														existing = ProfileManager.getInstance().getProfileByName(name);												
													}
													
													ProfileManager.getInstance().createProfile(name, 
															"Database created for seam-gen project", 
															IDBConnectionProfileConstants.CONNECTION_PROFILE_ID, 
															dbProperties
															);
													// TODO unique name ? NewCWJDBCPage.createUniqueConnectionName( NewCWJDBCPage.getExistingConnectionNamesList(), "seamgen-connection"));
												} catch (ConnectionProfileException e) {
													SeamGenPlugin.logError("Could not create database connection", e);
													MessageDialog.openError( getShell(), "Could not create database connection", "Could not create database connection. See Error log for details" );
												}
															
											} else {
												MessageDialog.openError( getShell(), "Could not read database settings", "Could not read database settings. See Error log for details" );
											}
										}
									  });
							return;
						}
						
						
						//							org.eclipse.ui.externaltools.internal.launchConfigurations
						Properties p = getSeamGenProperties( launch2.getLaunchConfiguration() );
						if(p!=null) {
							String seamWorkspace = p.getProperty( "workspace.home" );
							String projectName = p.getProperty( "project.name" );
							
							IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
						
							if(!"new-project".equals(target)) {
								if(project.exists()) {
									project.refreshLocal( IResource.DEPTH_INFINITE, null );
								}
								return; // only autodetect for new-project stuff.
							}
							
							if(!project.exists()) {
								SeamGenPlugin.logInfo( "project " + projectName + " does not exist");
								IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);
								URI uri = new File(seamWorkspace, projectName).toURI();
								
								IPath locationPath = URIUtil.toPath(uri);
								IPath defaultDefaultLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation();
								IPath parentPath = locationPath.removeLastSegments(1);
								if (FileUtil.isPrefixOf(parentPath, defaultDefaultLocation) && FileUtil.isPrefixOf(defaultDefaultLocation, parentPath)) {
									SeamGenPlugin.logInfo( "seam workspace overlaps with eclipse. Opening project directly." );
								} else {
									description.setLocationURI(uri);
									SeamGenPlugin.logInfo( "project location should be " + uri);
									
								}
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
				QuestionDialog questionDialog = new QuestionDialog(window.getShell(), getTitle(), getDescription(), questions,getGroups());
				if(questionDialog.open()!= QuestionDialog.OK) {
					SeamGenPlugin.logInfo( "User cancelled dialog" );
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
				try {				
					createTempFile = File.createTempFile( "seamgenempty", "properties" );
					empties.store( new FileOutputStream(createTempFile), "File used to send intentionally empty valued properties" );
				}
				catch (FileNotFoundException e) {
					SeamGenPlugin.logError( "Error while running " + getTarget(), e );
				}
				catch (IOException e) {
					SeamGenPlugin.logError( "Error while running " + getTarget(), e );
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
			SeamGenPlugin.logInfo( "launching seamgen " + getTarget() );
			ILaunch launch = wc.launch( ILaunchManager.RUN_MODE, null );
			
		} catch (CoreException e) {			
			SeamGenPlugin.logError( "Exception when trying to launch seamgen", e );
			MessageDialog.openError(getShell(), "Seam-gen could not start", e.getMessage());
		}
	
	}

	public Set getGroups() {
		return Collections.EMPTY_SET;
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
	
	static public ILaunchConfiguration findLaunchConfig(String name) throws CoreException {
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

	public static Properties getSeamGenProperties(ILaunchConfiguration lc) {
		SeamGenPlugin.logInfo( "launch completed...auto detecting project" );
		IPath location;
		try {
			location = ExternalToolsUtil.getLocation( lc );
		}
		catch (CoreException e2) {
			SeamGenPlugin.logError( "Error while loading seamgen properties", e2 );
			return null;
		}
		
		SeamGenPlugin.logInfo( "location: " + location );
		File file = new File(location.toFile().getParentFile(), "build.properties");
		SeamGenPlugin.logInfo( "build.properties: " + location );
		
		if(file.exists()) {
			Properties p = new Properties();
			FileInputStream fileInputStream = null;
			try {
				fileInputStream = new FileInputStream(file);
				p.load( fileInputStream );
			}
			catch (Exception e) {
				SeamGenPlugin.logError( "Error while loading seamgen properties", e );
				if(fileInputStream!=null)
					try {
						fileInputStream.close();
					}
					catch (IOException e1) {
						SeamGenPlugin.logError( "Error while closing seamgen properties", e );
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