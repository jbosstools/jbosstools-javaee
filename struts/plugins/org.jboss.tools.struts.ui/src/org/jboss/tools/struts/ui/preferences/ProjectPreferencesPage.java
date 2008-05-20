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
package org.jboss.tools.struts.ui.preferences;

import org.jboss.tools.struts.StrutsPreference;
import org.jboss.tools.struts.StrutsUtils;
import org.jboss.tools.jst.web.ui.internal.preferences.WebProjectPreferencesPage;
import org.jboss.tools.jst.web.project.helpers.IWebProjectTemplate;

public class ProjectPreferencesPage extends WebProjectPreferencesPage {
	
	public ProjectPreferencesPage() {}
	
	protected String getNewProjectOptionPath() {
		return StrutsPreference.OPTIONS_NEW_PROJECT_PATH;
	}

	protected String getImportProjectOptionPath() {
		return StrutsPreference.OPTIONS_IMPORT_PROJECT_PATH;
	}

	protected IWebProjectTemplate createTemplate() {
		return new StrutsUtils();
	}
	protected String getVersionAttribute() {
		return StrutsPreference.DEFAULT_STRUTS_VERSION.getName();
	}
	
}
