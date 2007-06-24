/*
 * Created on 04.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.jboss.tools.struts;

import java.util.Properties;

import org.jboss.tools.common.log.BaseUIPlugin;
import org.jboss.tools.common.log.IPluginLog;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelConstants;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;
import org.jboss.tools.common.model.util.ClassLoaderUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.osgi.framework.BundleContext;

/**
 * @author Eskimo
 *
 */
public class StrutsModelPlugin extends BaseUIPlugin {
	public static final String PLUGIN_ID = "org.jboss.tools.struts";
	static StrutsModelPlugin INSTANCE = null;
	
	public StrutsModelPlugin() {
		INSTANCE = this;
	}

	public void stop(BundleContext context) throws Exception {
//		StrutsStudioVariableInitializer.save(getPreferenceStore());
		super.stop(context);
	}

	protected void initializeDefaultPluginPreferences() {
		super.initializeDefaultPluginPreferences();
		
		Properties p = new Properties();
		p.setProperty(XModelConstants.WORKSPACE, EclipseResourceUtil.getInstallPath(this));
		p.setProperty("initialModel", "true");
		
		ClassLoaderUtil.init();
		XModel initialModel = PreferenceModelUtilities.createPreferenceModel(p);
		if (initialModel != null)
		{
			PreferenceModelUtilities.initPreferenceValue(initialModel, StrutsPreference.USE_DEFAULT_PROJECT_ROOT);
			PreferenceModelUtilities.initPreferenceValue(initialModel, StrutsPreference.DEFAULT_STRUTS_VERSION);
			PreferenceModelUtilities.initPreferenceValue(initialModel, StrutsPreference.DEFAULT_PROJECT_TEMPLATE);
			PreferenceModelUtilities.initPreferenceValue(initialModel, StrutsPreference.DEFAULT_PROJECT_ROOT_DIR);
			PreferenceModelUtilities.initPreferenceValue(initialModel, StrutsPreference.DEFAULT_TLD_SET);
			PreferenceModelUtilities.initPreferenceValue(initialModel, StrutsPreference.REMOVE_PAGE_AND_FILE);
			
			PreferenceModelUtilities.getPreferenceModel().save();
		}
	}

	public static StrutsModelPlugin getDefault() {
		return INSTANCE;
	}

	public static boolean isDebugEnabled() {
		return INSTANCE.isDebugging();
	}
	
	public static IPluginLog getPluginLog() {
		return getDefault();
	}
}
