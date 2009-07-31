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
package org.jboss.tools.jsf.ui.preferences;

import org.jboss.tools.jsf.JSFPreference;
import org.jboss.tools.jsf.web.JSFTemplate;
import org.jboss.tools.jst.web.ui.internal.preferences.WebProjectPreferencesPage;
import org.jboss.tools.jst.web.project.helpers.IWebProjectTemplate;

public class JSFProjectPreferencesPage extends WebProjectPreferencesPage {
	
	public static final String ID = "org.jboss.tools.jsf.ui.project"; //$NON-NLS-1$
	
	public JSFProjectPreferencesPage() {}
	
	protected String getNewProjectOptionPath() {
		return JSFPreference.OPTIONS_JSF_NEW_PROJECT_PATH;
	}

	protected String getImportProjectOptionPath() {
		return JSFPreference.OPTIONS_JSF_IMPORT_PROJECT_PATH;
	}

	protected IWebProjectTemplate createTemplate() {
		return new JSFTemplate();
	}
	protected String getVersionAttribute() {
		return JSFPreference.DEFAULT_JSF_VERSION.getName();
	}
	
}
