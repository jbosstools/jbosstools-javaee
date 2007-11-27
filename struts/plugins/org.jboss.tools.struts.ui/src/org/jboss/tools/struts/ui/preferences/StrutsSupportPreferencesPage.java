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

import org.jboss.tools.common.model.ui.attribute.XAttributeSupport;
import org.jboss.tools.common.model.ui.attribute.adapter.CheckListAdapter;
import org.jboss.tools.common.model.ui.preferences.TabbedPreferencesPage;
import org.jboss.tools.common.model.ui.preferences.XMOBasedPreferencesPage;

import org.jboss.tools.struts.StrutsPreference;
import org.jboss.tools.struts.StrutsUtils;

public class StrutsSupportPreferencesPage extends TabbedPreferencesPage {
	public static String STRUTS_SUPPORT_1_0 = "1.0";
	public static String STRUTS_SUPPORT_1_1 = "1.1";
	public static String STRUTS_SUPPORT_1_2 = "1.2";

	public StrutsSupportPreferencesPage() {
		StrutsSupport1xPreferencesPage page = new StrutsSupport1xPreferencesPage(STRUTS_SUPPORT_1_2);
		addPreferencePage(page);
		page = new StrutsSupport1xPreferencesPage(STRUTS_SUPPORT_1_1);
		addPreferencePage(page);
		page = new StrutsSupport1xPreferencesPage(STRUTS_SUPPORT_1_0);
		addPreferencePage(page);
	}
	
	class StrutsSupport1xPreferencesPage extends XMOBasedPreferencesPage {
		
		StrutsSupport1xPreferencesPage(String version) {
			super(getPreferenceModel().getByPath(StrutsPreference.OPTIONS_STRUTS_SUPPORT_BASE_PATH + version));
			setTags(version);
		}
		
		protected void setTags(String version) {
			CheckListAdapter adapter = (CheckListAdapter)getSupport().getPropertyEditorAdapterByName("tld files");
			adapter.setTags(new StrutsUtils().getTldTemplates(version));
		}
		
		protected XAttributeSupport createSupport() {
			return new XAS();
		}
	
	}
	
	class XAS extends XAttributeSupport {
		protected boolean keepGreedy(String name, int index, int greedyCount) {
			return index == 2;
		}
	}
}
