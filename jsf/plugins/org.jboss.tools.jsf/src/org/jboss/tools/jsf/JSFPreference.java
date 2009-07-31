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
package org.jboss.tools.jsf;

import org.jboss.tools.common.model.options.Preference;
import org.jboss.tools.jst.web.WebPreference;

public class JSFPreference extends WebPreference {
	public static final String OPTIONS_JSFSTUDIO_PATH = OPTIONS_PATH + "/JSF Studio"; //$NON-NLS-1$
	public static final String OPTIONS_JSF_PROJECT_PATH = OPTIONS_JSFSTUDIO_PATH + "/Project"; //$NON-NLS-1$
	public static final String OPTIONS_JSF_NEW_PROJECT_PATH = OPTIONS_JSF_PROJECT_PATH + "/New Project"; //$NON-NLS-1$
	public static final String OPTIONS_JSF_IMPORT_PROJECT_PATH = OPTIONS_JSF_PROJECT_PATH + "/Import Project"; //$NON-NLS-1$
	public static String JSF_EDITOR_PATH = "%Options%/Struts Studio/Editors/JSF Flow Diagram"; //$NON-NLS-1$
	public static String JSF_ADD_VIEW_PATH = JSF_EDITOR_PATH + "/Add View"; //$NON-NLS-1$
	
	public static final Preference USE_DEFAULT_JSF_PROJECT_ROOT = new JSFPreference(OPTIONS_JSF_NEW_PROJECT_PATH, "Use Default Path"); //$NON-NLS-1$
	public static final Preference DEFAULT_JSF_VERSION   = new JSFPreference(OPTIONS_JSF_NEW_PROJECT_PATH, "Version"); //$NON-NLS-1$
	public static final Preference DEFAULT_JSF_PROJECT_TEMPLATE = new JSFPreference(OPTIONS_JSF_NEW_PROJECT_PATH, "Project Template"); //$NON-NLS-1$
	public static final Preference DEFAULT_JSF_PAGE_TEMPLATE = new JSFPreference(OPTIONS_JSF_NEW_PROJECT_PATH, "Page Template"); //$NON-NLS-1$
	public static final Preference DEFAULT_JSF_PROJECT_ROOT_DIR = new JSFPreference(OPTIONS_JSF_NEW_PROJECT_PATH, "Projects Root"); //$NON-NLS-1$
	public static final Preference DEFAULT_JSF_SERVLET_VERSION  = new JSFPreference(OPTIONS_JSF_NEW_PROJECT_PATH, "Servlet Version"); //$NON-NLS-1$
	public static final Preference DEFAULT_JSF_IMPORT_SERVLET_VERSION  = new JSFPreference(OPTIONS_JSF_IMPORT_PROJECT_PATH, "Servlet Version"); //$NON-NLS-1$
	public static final Preference REGISTER_NEW_JSF_PROJECT_IN_SERVER      = new JSFPreference(OPTIONS_JSF_NEW_PROJECT_PATH, ATTR_REGISTER_IN_SERVER);
	public static final Preference REGISTER_IMPORTED_JSF_PROJECT_IN_SERVER = new JSFPreference(OPTIONS_JSF_IMPORT_PROJECT_PATH, ATTR_REGISTER_IN_SERVER);

	public static final Preference DO_NOT_CREATE_EMPTY_RULE = new JSFPreference(JSF_EDITOR_PATH, "doNotCreateEmptyRule"); //$NON-NLS-1$
	public static final Preference ENABLE_CONTROL_MODE_ON_TRANSITION_COMPLETED = new JSFPreference(JSF_EDITOR_PATH, "enableControlModeOnTransitionCompleted"); //$NON-NLS-1$
	public static final Preference SHOW_SHORTCUT_ICON = new JSFPreference(JSF_EDITOR_PATH, "showShortcutIcon"); //$NON-NLS-1$
	public static final Preference SHOW_SHORTCUT_PATH = new JSFPreference(JSF_EDITOR_PATH, "showShortcutPath"); //$NON-NLS-1$

	protected JSFPreference(String optionPath, String attributeName) {
		super(optionPath, attributeName);
	}

}
