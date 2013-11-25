/*******************************************************************************
 * Copyright (c) 2007-2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.ui.test.wizard;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.datatools.connectivity.ConnectionProfileConstants;
import org.eclipse.datatools.connectivity.ConnectionProfileException;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.drivers.DriverInstance;
import org.eclipse.datatools.connectivity.drivers.DriverManager;
import org.eclipse.datatools.connectivity.drivers.IDriverMgmtConstants;
import org.eclipse.datatools.connectivity.drivers.IPropertySet;
import org.eclipse.datatools.connectivity.drivers.PropertySetImpl;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCConnectionProfileConstants;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCDriverDefinitionConstants;
import org.eclipse.datatools.connectivity.drivers.models.TemplateDescriptor;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.internal.RuntimeWorkingCopy;
import org.eclipse.wst.server.core.internal.ServerWorkingCopy;

/**
 * @author eskimo
 * 
 */
public class JBossASAdapterInitializer {

	public static final String JBOSS_AS_HOME = "../../../../jboss-eap/jboss-as"; // JBoss //$NON-NLS-1$
	public static final String SERVERS_FILE = "../../../../studio/application_platforms.properties"; //$NON-NLS-1$

	// This constants are made to avoid dependency with
	// org.jboss.ide.eclipse.as.core plugin
	@SuppressWarnings("nls")
	public static final String JBOSS_AS_RUNTIME_TYPE_ID[] = {
			"org.jboss.ide.eclipse.as.runtime.32",
			"org.jboss.ide.eclipse.as.runtime.40",
			"org.jboss.ide.eclipse.as.runtime.42",
			"org.jboss.ide.eclipse.as.runtime.50" };

	@SuppressWarnings("nls")
	public static final String JBOSS_AS_TYPE_ID[] = {
			"org.jboss.ide.eclipse.as.32", "org.jboss.ide.eclipse.as.40",
			"org.jboss.ide.eclipse.as.42", "org.jboss.ide.eclipse.as.50" };

	public static final String JBOSS_AS_NAME[] = {
			Messages.JBossASAdapterInitializer_AppServer32, Messages.JBossASAdapterInitializer_AppServer40,
			Messages.JBossASAdapterInitializer_AppServer42, Messages.JBossASAdapterInitializer_AppServer50 };

	private static final int installedASIndex = 2;

	public static final String JBOSS_AS_HOST = "localhost"; //$NON-NLS-1$

	public static final String JBOSS_AS_DEFAULT_CONFIGURATION_NAME = "default"; //$NON-NLS-1$

	public static final String FIRST_START_PREFERENCE_NAME = "FIRST_START"; //$NON-NLS-1$

	public static final String HSQL_DRIVER_DEFINITION_ID = "DriverDefn.Hypersonic DB"; //$NON-NLS-1$

	public static final String HSQL_DRIVER_NAME = "Hypersonic DB"; //$NON-NLS-1$

	public static final String HSQL_DRIVER_TEMPLATE_ID = "org.eclipse.datatools.enablement.hsqldb.1_8.driver"; //$NON-NLS-1$

	public static final String DTP_DB_URL_PROPERTY_ID = "org.eclipse.datatools.connectivity.db.URL"; //$NON-NLS-1$

	/**
	 * Creates new JBoss AS Runtime, Server and hsqldb driver
	 * 
	 * @param jbossASLocation
	 *            location of JBoss Server
	 * @param progressMonitor
	 *            to report progress
	 * @return server working copy
	 * @throws CoreException
	 * @throws ConnectionProfileException
	 * @throws IOException 
	 */
	public static IServerWorkingCopy initJBossAS(String jbossASLocation,
			IProgressMonitor progressMonitor) throws CoreException,
			ConnectionProfileException, IOException {
		IRuntime runtime = createRuntime(null, jbossASLocation,
				progressMonitor, 2);
		IServerWorkingCopy server = null;
		if (runtime != null) {
			server = createServer(progressMonitor, runtime, 2, null);
		}
		createDriver(jbossASLocation);
		return server;
	}

	/**
	 * Creates new JBoss AS Runtime
	 * 
	 * @param jbossASLocation
	 *            location of JBoss AS
	 * @param progressMonitor
	 * @return runtime working copy
	 * @throws CoreException
	 */
	private static IRuntime createRuntime(String runtimeName,
			String jbossASLocation, IProgressMonitor progressMonitor, int index)
			throws CoreException {
		IRuntimeWorkingCopy runtime = null;
		String type = null;
		String version = null;
		String runtimeId = null;
		IPath jbossAsLocationPath = new Path(jbossASLocation);
		IRuntimeType[] runtimeTypes = ServerUtil.getRuntimeTypes(type, version,
				JBOSS_AS_RUNTIME_TYPE_ID[index]);
		if (runtimeTypes.length > 0) {
			runtime = runtimeTypes[0].createRuntime(runtimeId, progressMonitor);
			runtime.setLocation(jbossAsLocationPath);
			if (runtimeName != null) {
				runtime.setName(runtimeName);
			}
			IVMInstall defaultVM = JavaRuntime.getDefaultVMInstall();
			// IJBossServerRuntime.PROPERTY_VM_ID
			((RuntimeWorkingCopy) runtime).setAttribute("PROPERTY_VM_ID", //$NON-NLS-1$
					defaultVM.getId());
			// IJBossServerRuntime.PROPERTY_VM_TYPE_ID
			((RuntimeWorkingCopy) runtime).setAttribute("PROPERTY_VM_TYPE_ID", //$NON-NLS-1$
					defaultVM.getVMInstallType().getId());
			// IJBossServerRuntime.PROPERTY_CONFIGURATION_NAME
			((RuntimeWorkingCopy) runtime).setAttribute(
					"org.jboss.ide.eclipse.as.core.runtime.configurationName", //$NON-NLS-1$
					JBOSS_AS_DEFAULT_CONFIGURATION_NAME);

			return runtime.save(false, progressMonitor);
		}
		return runtime;
	}

	/**
	 * Creates new JBoss Server
	 * 
	 * @param progressMonitor
	 * @param runtime
	 *            parent JBoss AS Runtime
	 * @return server working copy
	 * @throws CoreException
	 */
	private static IServerWorkingCopy createServer(
			IProgressMonitor progressMonitor, IRuntime runtime, int index,
			String name) throws CoreException {
		IServerType serverType = ServerCore
				.findServerType(JBOSS_AS_TYPE_ID[index]);
		IServerWorkingCopy server = serverType.createServer(null, null,
				runtime, progressMonitor);

		server.setHost(JBOSS_AS_HOST);
		if (name != null) {
			server.setName(name);
		} else {
			server.setName(JBOSS_AS_NAME[installedASIndex]);
		}

		// JBossServer.DEPLOY_DIRECTORY
		String deployVal = runtime.getLocation().append("server").append( //$NON-NLS-1$
				JBOSS_AS_DEFAULT_CONFIGURATION_NAME).append("deploy") //$NON-NLS-1$
				.toOSString();
		((ServerWorkingCopy) server).setAttribute(
				"org.jboss.ide.eclipse.as.core.server.deployDirectory", //$NON-NLS-1$
				deployVal);

		// IDeployableServer.TEMP_DEPLOY_DIRECTORY
		String deployTmpFolderVal = runtime.getLocation().append("server") //$NON-NLS-1$
				.append(JBOSS_AS_DEFAULT_CONFIGURATION_NAME).append("tmp") //$NON-NLS-1$
				.append("jbosstoolsTemp").toOSString(); //$NON-NLS-1$
		((ServerWorkingCopy) server).setAttribute(
				"org.jboss.ide.eclipse.as.core.server.tempDeployDirectory", //$NON-NLS-1$
				deployTmpFolderVal);

		// If we'd need to set up a username / pw for JMX, do it here.
		// ((ServerWorkingCopy)serverWC).setAttribute(JBossServer.SERVER_USERNAME,
		// authUser);
		// ((ServerWorkingCopy)serverWC).setAttribute(JBossServer.SERVER_PASSWORD,
		// authPass);

		server.save(false, progressMonitor);
		return server;
	}

	private static boolean driverIsCreated = false;

	/**
	 * Creates HSQL DB Driver
	 * 
	 * @param jbossASLocation
	 *            location of JBoss AS
	 * @throws ConnectionProfileException
	 * @return driver instance
	 * @throws IOException 
	 */
	private static void createDriver(String jbossASLocation)
			throws ConnectionProfileException, IOException {
		if (driverIsCreated) {
			// Don't create the driver a few times
			return;
		}
		String driverPath;
		driverPath = new File(jbossASLocation + "/server/default/lib/hsqldb.jar").getCanonicalPath(); //$NON-NLS-1$

		DriverInstance driver = DriverManager.getInstance()
				.getDriverInstanceByName(HSQL_DRIVER_NAME);
		if (driver == null) {
			TemplateDescriptor descr = TemplateDescriptor
					.getDriverTemplateDescriptor(HSQL_DRIVER_TEMPLATE_ID);
			IPropertySet instance = new PropertySetImpl(HSQL_DRIVER_NAME,
					HSQL_DRIVER_DEFINITION_ID);
			instance.setName(HSQL_DRIVER_NAME);
			instance.setID(HSQL_DRIVER_DEFINITION_ID);
			Properties props = new Properties();

			IConfigurationElement[] template = descr.getProperties();
			for (int i = 0; i < template.length; i++) {
				IConfigurationElement prop = template[i];
				String id = prop.getAttribute("id"); //$NON-NLS-1$

				String value = prop.getAttribute("value"); //$NON-NLS-1$
				props.setProperty(id, value == null ? "" : value); //$NON-NLS-1$
			}
			props.setProperty(DTP_DB_URL_PROPERTY_ID, "jdbc:hsqldb:."); //$NON-NLS-1$
			props.setProperty(IDriverMgmtConstants.PROP_DEFN_TYPE, descr
					.getId());
			props.setProperty(IDriverMgmtConstants.PROP_DEFN_JARLIST,
					driverPath);

			instance.setBaseProperties(props);
			DriverManager.getInstance().removeDriverInstance(instance.getID());
			System.gc();
			DriverManager.getInstance().addDriverInstance(instance);
		}

		driver = DriverManager.getInstance().getDriverInstanceByName(
				HSQL_DRIVER_NAME);
		if (driver != null
				&& ProfileManager.getInstance().getProfileByName("DefaultDS") == null) { //$NON-NLS-1$
			// create profile
			Properties props = new Properties();
			props.setProperty(
					ConnectionProfileConstants.PROP_DRIVER_DEFINITION_ID,
					HSQL_DRIVER_DEFINITION_ID);
			props
					.setProperty(
							IJDBCConnectionProfileConstants.CONNECTION_PROPERTIES_PROP_ID,
							""); //$NON-NLS-1$
			props
					.setProperty(
							IJDBCDriverDefinitionConstants.DRIVER_CLASS_PROP_ID,
							driver
									.getProperty(IJDBCDriverDefinitionConstants.DRIVER_CLASS_PROP_ID));
			props
					.setProperty(
							IJDBCDriverDefinitionConstants.DATABASE_VENDOR_PROP_ID,
							driver
									.getProperty(IJDBCDriverDefinitionConstants.DATABASE_VENDOR_PROP_ID));
			props
					.setProperty(
							IJDBCDriverDefinitionConstants.DATABASE_VERSION_PROP_ID,
							driver
									.getProperty(IJDBCDriverDefinitionConstants.DATABASE_VERSION_PROP_ID));
			props.setProperty(
					IJDBCDriverDefinitionConstants.DATABASE_NAME_PROP_ID,
					"Default"); //$NON-NLS-1$
			props.setProperty(IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID,
					""); //$NON-NLS-1$
			props.setProperty(
					IJDBCConnectionProfileConstants.SAVE_PASSWORD_PROP_ID,
					"false"); //$NON-NLS-1$
			props
					.setProperty(
							IJDBCDriverDefinitionConstants.USERNAME_PROP_ID,
							driver
									.getProperty(IJDBCDriverDefinitionConstants.USERNAME_PROP_ID));
			props
					.setProperty(
							IJDBCDriverDefinitionConstants.URL_PROP_ID,
							driver
									.getProperty(IJDBCDriverDefinitionConstants.URL_PROP_ID));

			ProfileManager.getInstance().createProfile("DefaultDS", //$NON-NLS-1$
					Messages.JBossASAdapterInitializer_JBossASHypersonicEmbeddedDB,
					IJDBCConnectionProfileConstants.CONNECTION_PROFILE_ID,
					props, "", false); //$NON-NLS-1$
		}
		if (driver != null) {
			driverIsCreated = true;
		}
	}
}