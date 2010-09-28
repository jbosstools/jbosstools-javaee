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

import java.util.Map;
import java.util.TreeMap;

import org.jboss.tools.common.model.options.Preference;
import org.jboss.tools.common.model.ui.preferences.*;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.internal.templates.TemplateContextTypeIdsJSP;
import org.eclipse.ui.*;
import org.jboss.tools.common.meta.constraint.impl.XAttributeConstraintAList;
import org.jboss.tools.common.model.*;
import org.jboss.tools.jsf.web.JSFTemplate;

public class JSFFlowTabbedPreferencesPage extends TabbedPreferencesPage implements IWorkbenchPreferencePage {

	public static final String ID = "org.jboss.tools.jsf.ui.jsfflowdiagram"; //$NON-NLS-1$
	
	public static String JSF_EDITOR_PATH = Preference.EDITOR_PATH + "/JSF Flow Diagram"; //$NON-NLS-1$
	public static String JSF_ADD_VIEW_PATH = Preference.EDITOR_PATH + "/JSF Flow Diagram/Add View"; //$NON-NLS-1$
	
	public JSFFlowTabbedPreferencesPage() {
		XModel model = getPreferenceModel();
		XModelObject editor = model.getByPath(JSF_EDITOR_PATH);
		addPreferencePage(new XMOBasedPreferencesPage(editor));
		XModelObject addView = model.getByPath(JSF_ADD_VIEW_PATH);
		initTemplateList(addView);
		addPreferencePage(new XMOBasedPreferencesPage(addView));
	}

	public void init(IWorkbench workbench)  {}
	
	void initTemplateList(XModelObject addView) {
		if(addView == null) return;
		TemplateStore store = JSPUIPlugin.getInstance().getTemplateStore();
		Map<String, Template> templates = new TreeMap<String, Template>();
		Template[] ts = store.getTemplates(TemplateContextTypeIdsJSP.NEW);
		for (Template t: ts) {
			templates.put(t.getName(), t);
		}		
		XAttributeConstraintAList l = (XAttributeConstraintAList)addView.getModelEntity().getAttribute("Page Template").getConstraint(); //$NON-NLS-1$
		l.setValues(templates.keySet().toArray(new String[0]));
	}

}
