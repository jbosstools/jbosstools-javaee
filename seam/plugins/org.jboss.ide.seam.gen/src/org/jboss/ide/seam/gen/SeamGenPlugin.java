package org.jboss.ide.seam.gen;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jboss.ide.seam.gen.actions.SeamGenAction;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamFacetPreference;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class SeamGenPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.jboss.ide.seam.gen";

	// The shared instance
	private static SeamGenPlugin plugin;
	
	/**
	 * The constructor
	 */
	public SeamGenPlugin() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		initSeamGen();
	}

	private void initSeamGen() {
		ILaunchConfiguration config=null;
		try {
			config = findLaunchConfig("seamgen");
		} catch (CoreException e1) {
			logError("Exception occured during search in Launch Configuration list.", e1);
		}
		File buildXmlPath = null;
		if(config==null) {
			try {
				String seamHome = SeamFacetPreference.getStringPreference(SeamFacetPreference.SEAM_HOME_FOLDER);
				buildXmlPath = new File(seamHome+File.separator+"seam-gen"+File.separator+"build.xml");
				if(buildXmlPath.exists())
					SeamGenAction.createSeamgenLaunchConfig(buildXmlPath.getAbsolutePath());
			} catch (CoreException e) {
				logError("Cannot create configuration for Seam-Gen tool. Seamgen build.xml file: " + buildXmlPath, e);
				return;
			}
		}
	}
	
	public static final String JBOSS_AS_HOME = "../../../../jboss-eap/jboss-as";
	
	static public String assumeJBossASHome() {
		String pluginLocation=null;
		try {
			pluginLocation = FileLocator.resolve(SeamGenPlugin.getDefault().getBundle().getEntry("/")).getFile();
		} catch (IOException e) {
			SeamGenPlugin.log(new Status(IStatus.ERROR,SeamGenPlugin.PLUGIN_ID,e.getMessage(),e));
		};
		File seamGenDir = new File(pluginLocation, JBOSS_AS_HOME);
		Path  p = new Path(seamGenDir.getPath());
		p.makeAbsolute();
		if(p.toFile().exists()) {
			return p.toOSString();
		} else {
			return "";
		}
	}
	
	public static String assumeWorkspacePath() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
	}
	
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
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static SeamGenPlugin getDefault() {
		return plugin;
	}

	
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}
	
	public static void logError(String message) {
		logError(message, null);
	}

	public static void logInfo(String message) {
		log(new Status(IStatus.INFO, PLUGIN_ID, 12345, message, null));
	}

	public static void logError(String message, Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, 12345, message, e));
		
	}
	

}
