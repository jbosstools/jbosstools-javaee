/*
 * Created on Sep 1, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.jboss.tools.struts.ui.preferences;

import org.jboss.tools.common.model.ui.preferences.TabbedPreferencesPage;

/**
 * @author eskimo
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PluginInsetsPreferencesPage extends TabbedPreferencesPage{
	public static final String PREFERENCES[] = {
		"%Options%/Struts Studio/Automation/Plug-ins Insets/Tiles",
		"%Options%/Struts Studio/Automation/Plug-ins Insets/Validators" };
		
	public PluginInsetsPreferencesPage() {
		super(PREFERENCES);	
	}
}
