/*
 * Created on 04.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.jboss.tools.jsf;

import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.common.log.BaseUIPlugin;
import org.jboss.tools.common.log.IPluginLog;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelConstants;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;
import org.jboss.tools.common.model.plugin.ModelPlugin;
import org.jboss.tools.common.model.util.ClassLoaderUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

/**
 * @author Eskimo
 *
 */
public class JSFModelPlugin extends BaseUIPlugin {
	public static final String PLUGIN_ID = "org.jboss.tools.jsf"; //$NON-NLS-1$

	public static final String CA_JSF_EL_IMAGE_PATH = "images/ca/icons_JSF_EL.gif"; //$NON-NLS-1$
	public static final String CA_JSF_MESSAGES_IMAGE_PATH = "images/ca/icons_Message_Bundles.gif"; //$NON-NLS-1$

	public JSFModelPlugin() {
		super();
		INSTANCE = this;
	}

	protected void initializeDefaultPluginPreferences()
	{
		super.initializeDefaultPluginPreferences();

		Properties p = new Properties();
		p.setProperty(XModelConstants.WORKSPACE, EclipseResourceUtil.getInstallPath(this));
		p.setProperty("initialModel", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		ClassLoaderUtil.init();
		XModel initialModel = PreferenceModelUtilities.createPreferenceModel(p);
		if (initialModel != null)
		{
			try {
			PreferenceModelUtilities.initPreferenceValue(initialModel, JSFPreference.USE_DEFAULT_JSF_PROJECT_ROOT);
			PreferenceModelUtilities.initPreferenceValue(initialModel, JSFPreference.DEFAULT_JSF_VERSION);
			PreferenceModelUtilities.initPreferenceValue(initialModel, JSFPreference.DEFAULT_JSF_PROJECT_TEMPLATE);
			PreferenceModelUtilities.initPreferenceValue(initialModel, JSFPreference.DEFAULT_JSF_PROJECT_ROOT_DIR);
			PreferenceModelUtilities.initPreferenceValue(initialModel, JSFPreference.DEFAULT_JSF_SERVLET_VERSION);
			PreferenceModelUtilities.initPreferenceValue(initialModel, JSFPreference.REGISTER_NEW_JSF_PROJECT_IN_SERVER);
			PreferenceModelUtilities.initPreferenceValue(initialModel, JSFPreference.REGISTER_IMPORTED_JSF_PROJECT_IN_SERVER);
			} catch (XModelException e) {
				ModelPlugin.getPluginLog().logError(e);
			}

			PreferenceModelUtilities.getPreferenceModel().save();
		}
	}

	public static void log(String msg) {
		if(isDebugEnabled()) INSTANCE.getLog().log(new Status(Status.INFO, PLUGIN_ID, Status.OK, msg, null));		
	}

	public static void log(IStatus status) {
		if(isDebugEnabled() || !status.isOK()) INSTANCE.getLog().log(status);
	}

	public static void log(String message, Throwable exception) {
		INSTANCE.getLog().log(new Status(Status.ERROR, PLUGIN_ID, Status.OK, message, exception));		
	}

	public static boolean isDebugEnabled() {
		return INSTANCE.isDebugging();
	}

	public static JSFModelPlugin getDefault() {
		return INSTANCE;
	}

	static JSFModelPlugin INSTANCE = null;

	/**
	 * @return IPluginLog object
	 */
	public static IPluginLog getPluginLog() {
		return getDefault();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.log.BaseUIPlugin#getId()
	 */
	@Override
	public String getId() {
		return PLUGIN_ID;
	}
}