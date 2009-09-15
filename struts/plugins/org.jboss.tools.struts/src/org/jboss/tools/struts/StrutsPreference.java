/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.struts;

import org.jboss.tools.common.model.options.Preference;
import org.jboss.tools.jst.web.WebPreference;

public class StrutsPreference extends WebPreference {
	public static final String OPTIONS_STRUTS_SUPPORT_BASE_PATH = "%Options%/Struts Studio/Project/Struts Support/Struts Support "; //$NON-NLS-1$
	public static final String OPTIONS_STRUTS_SUPPORT_1_0_PATH = OPTIONS_STRUTS_SUPPORT_BASE_PATH + "1.0"; //$NON-NLS-1$
	public static final String OPTIONS_STRUTS_SUPPORT_1_1_PATH = OPTIONS_STRUTS_SUPPORT_BASE_PATH + "1.1"; //$NON-NLS-1$

	public static final Preference USE_DEFAULT_PROJECT_ROOT = new StrutsPreference(OPTIONS_NEW_PROJECT_PATH, "Use Default Path"); //$NON-NLS-1$
	public static final Preference DEFAULT_STRUTS_VERSION   = new StrutsPreference(OPTIONS_NEW_PROJECT_PATH, "Struts Version"); //$NON-NLS-1$
	public static final Preference DEFAULT_PROJECT_TEMPLATE = new StrutsPreference(OPTIONS_NEW_PROJECT_PATH, "Project Template"); //$NON-NLS-1$
	public static final Preference DEFAULT_PAGE_TEMPLATE = new StrutsPreference(OPTIONS_NEW_PROJECT_PATH, "Page Template"); //$NON-NLS-1$
	public static final Preference DEFAULT_PROJECT_ROOT_DIR = new StrutsPreference(OPTIONS_NEW_PROJECT_PATH, "Projects Root"); //$NON-NLS-1$
	public static final Preference DEFAULT_TLD_SET          = new StrutsPreference(OPTIONS_NEW_PROJECT_PATH, "Default TLDs"); //$NON-NLS-1$
	
	public static final Preference REGISTER_NEW_PROJECT_IN_SERVER      = new StrutsPreference(OPTIONS_NEW_PROJECT_PATH, ATTR_REGISTER_IN_SERVER);
	public static final Preference REGISTER_IMPORTED_PROJECT_IN_SERVER = new StrutsPreference(OPTIONS_IMPORT_PROJECT_PATH, ATTR_REGISTER_IN_SERVER);

	public static final Preference DEFAULT_STRUTS_IMPORT_SERVLET_VERSION  = new StrutsPreference(OPTIONS_IMPORT_PROJECT_PATH, "Servlet Version"); //$NON-NLS-1$

	public static final String WEB_FLOW_DIAGRAM_PATH   = Preference.EDITOR_PATH + "/Web Flow Diagram"; //$NON-NLS-1$
	public static final Preference REMOVE_PAGE_AND_FILE = new StrutsPreference(WEB_FLOW_DIAGRAM_PATH, "removePageWithFile"); //$NON-NLS-1$
	public static final Preference DO_NOT_SHOW_DIAGRAM = new StrutsPreference(WEB_FLOW_DIAGRAM_PATH, "doNotShowDiagram"); //$NON-NLS-1$
	public static final Preference ENABLE_CONTROL_MODE_ON_TRANSITION_COMPLETED = new StrutsPreference(WEB_FLOW_DIAGRAM_PATH, "enableControlModeOnTransitionCompleted"); //$NON-NLS-1$
	public static final Preference SHOW_SHORTCUT_ICON = new StrutsPreference(WEB_FLOW_DIAGRAM_PATH, "showShortcutIcon"); //$NON-NLS-1$
	public static final Preference SHOW_SHORTCUT_PATH = new StrutsPreference(WEB_FLOW_DIAGRAM_PATH, "showShortcutPath"); //$NON-NLS-1$

	public static String ADD_PAGE_PATH = WEB_FLOW_DIAGRAM_PATH + "/Add Page"; //$NON-NLS-1$

	protected StrutsPreference(String optionPath, String attributeName)	{
		super(optionPath, attributeName);
	}

}
