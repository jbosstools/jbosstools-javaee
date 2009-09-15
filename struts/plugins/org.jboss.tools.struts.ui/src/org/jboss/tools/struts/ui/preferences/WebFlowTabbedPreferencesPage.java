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

import org.jboss.tools.common.model.options.Preference;
import org.jboss.tools.common.model.ui.preferences.*;
import org.eclipse.ui.*;
import org.jboss.tools.common.meta.constraint.impl.XAttributeConstraintAList;
import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.*;
import org.jboss.tools.struts.ui.StrutsUIPlugin;

public class WebFlowTabbedPreferencesPage extends TabbedPreferencesPage implements IWorkbenchPreferencePage {
	public static String STRUTS_EDITOR_PATH = Preference.EDITOR_PATH + "/Web Flow Diagram";
	public static String ADD_PAGE_PATH = StrutsPreference.ADD_PAGE_PATH;
	
	public WebFlowTabbedPreferencesPage() {
		XModel model = getPreferenceModel();		
		XModelObject strutsEditor = model.getByPath(STRUTS_EDITOR_PATH);
		addPreferencePage(new Tab(strutsEditor));
		XModelObject addPage = model.getByPath(ADD_PAGE_PATH);
		initTemplateList(addPage);
		addPreferencePage(new XMOBasedPreferencesPage(addPage));
	}

	public void init(IWorkbench workbench)  {
	}
	
	class Tab extends XMOBasedPreferencesPage {
		public Tab(XModelObject xmo) {
			super(xmo);
		}
		public String getTitle() {
			return WebFlowTabbedPreferencesPage.this.getTitle();
		}		
	}

	void initTemplateList(XModelObject addView) {
		try {
			StrutsUtils templates = new StrutsUtils();
			XAttributeConstraintAList l = (XAttributeConstraintAList)addView.getModelEntity().getAttribute("Page Template").getConstraint();
			l.setValues(templates.getPageTemplateList());
		} catch (Exception e) {
			StrutsUIPlugin.getPluginLog().logError(e);
		}
	}

}
