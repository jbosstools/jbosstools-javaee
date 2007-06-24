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
	public static final String OPTIONS_JSFSTUDIO_PATH = OPTIONS_PATH + "/JSF Studio";
	public static final String OPTIONS_JSF_10_LIB_PATH = OPTIONS_JSFSTUDIO_PATH + "/JSF 1.0 Libraries";
	public static final String OPTIONS_JSF_PROJECT_PATH = OPTIONS_JSFSTUDIO_PATH + "/Project";
	public static final String OPTIONS_JSF_NEW_PROJECT_PATH = OPTIONS_JSF_PROJECT_PATH + "/New Project";
	public static final String OPTIONS_JSF_IMPORT_PROJECT_PATH = OPTIONS_JSF_PROJECT_PATH + "/Import Project";
	public static String JSF_EDITOR_PATH = "%Options%/Struts Studio/Editors/JSF Flow Diagram";
	public static String JSF_ADD_VIEW_PATH = JSF_EDITOR_PATH + "/Add View";
	
	public static final Preference USE_DEFAULT_JSF_PROJECT_ROOT = new JSFPreference(OPTIONS_JSF_NEW_PROJECT_PATH, "Use Default Path");
	public static final Preference DEFAULT_JSF_VERSION   = new JSFPreference(OPTIONS_JSF_NEW_PROJECT_PATH, "Version");
	public static final Preference DEFAULT_JSF_PROJECT_TEMPLATE = new JSFPreference(OPTIONS_JSF_NEW_PROJECT_PATH, "Project Template");
	public static final Preference DEFAULT_JSF_PAGE_TEMPLATE = new JSFPreference(OPTIONS_JSF_NEW_PROJECT_PATH, "Page Template");
	public static final Preference DEFAULT_JSF_PROJECT_ROOT_DIR = new JSFPreference(OPTIONS_JSF_NEW_PROJECT_PATH, "Projects Root");
	public static final Preference DEFAULT_JSF_SERVLET_VERSION  = new JSFPreference(OPTIONS_JSF_NEW_PROJECT_PATH, "Servlet Version");
	public static final Preference DEFAULT_JSF_IMPORT_SERVLET_VERSION  = new JSFPreference(OPTIONS_JSF_IMPORT_PROJECT_PATH, "Servlet Version");
	public static final Preference REGISTER_NEW_JSF_PROJECT_IN_TOMCAT      = new JSFPreference(OPTIONS_JSF_NEW_PROJECT_PATH, ATTR_REGISTER_IN_TOMCAT);
	public static final Preference REGISTER_IMPORTED_JSF_PROJECT_IN_TOMCAT = new JSFPreference(OPTIONS_JSF_IMPORT_PROJECT_PATH, ATTR_REGISTER_IN_TOMCAT);
	public static final Preference JSF_10_LIBRARIES = new JSFPreference(OPTIONS_JSF_10_LIB_PATH, "Libraries");

	public static final Preference DO_NOT_CREATE_EMPTY_RULE = new JSFPreference(JSF_EDITOR_PATH, "doNotCreateEmptyRule");
	public static final Preference ENABLE_CONTROL_MODE_ON_TRANSITION_COMPLETED = new JSFPreference(JSF_EDITOR_PATH, "enableControlModeOnTransitionCompleted");
	public static final Preference SHOW_SHORTCUT_ICON = new JSFPreference(JSF_EDITOR_PATH, "showShortcutIcon");
	public static final Preference SHOW_SHORTCUT_PATH = new JSFPreference(JSF_EDITOR_PATH, "showShortcutPath");

	protected JSFPreference(String optionPath, String attributeName) {
		super(optionPath, attributeName);
	}

}
