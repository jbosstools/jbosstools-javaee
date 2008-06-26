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
package org.jboss.tools.struts.ui.wizard.addstruts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.jboss.tools.common.model.ui.attribute.XAttributeSupport;
import org.jboss.tools.common.model.ui.attribute.adapter.*;
import org.jboss.tools.common.model.ui.util.ModelUtilities;
import org.eclipse.jface.wizard.*;
import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.struts.StrutsUtils;
import org.jboss.tools.jst.web.context.ImportWebProjectContext;

public class AddStrutsSupportPage extends WizardPage {
	ImportWebProjectContext context;
	private XAttributeSupport support;
	boolean activated = false;
	StrutsUtils template = new StrutsUtils();
	
	public AddStrutsSupportPage(ImportWebProjectContext context) {
		super("Add Struts Studio Nature");
		this.context = context;
		XEntityData entityData = XEntityDataImpl.create(
			new String[][] {
				{"WebPrjAdoptAddStruts", ""},
				{"version", "yes"},
				{"servlet class", "yes"},
				{"url pattern", "yes"},
				{"tld files", "yes"}
			}
		);
		support = new XAS();
		support.init(ModelUtilities.getPreferenceModel().getRoot(), entityData);
		loadDefaultValues();
		support.setLayout(getLayoutForSupport());
		support.load();
	}
	
	public void dispose() {
		super.dispose();
		if (support!=null) support.dispose();
		support = null;
	}

	private void loadDefaultValues() {
		XAttributeData[] ad = support.getEntityData().getAttributeData();
		String defaultVersion = ad[0].getAttribute().getDefaultValue();
		ad[0].setValue(defaultVersion);
		onVersionChange(defaultVersion);
	}
	
	public boolean isActivated() {
		return activated;
	}

	public void createControl(Composite parent)	{		
		initializeDialogUnits(parent);
		Control control = support.createControl(parent);
		setControl(control);
		String focusAttr = "tld files";
		if(focusAttr != null && support.getFieldEditorByName(focusAttr) != null) {
			support.getFieldEditorByName(focusAttr).setFocus();
		}
		IModelPropertyEditorAdapter adapter = (IModelPropertyEditorAdapter)support.getPropertyEditorAdapterByName("version");
		adapter.addValueChangeListener(new IPL());
		setErrorMessage(null);
		setTitle("Struts Support");
		setMessage("Please select struts support options");
		setPageComplete(true);
	}
	
	private Layout getLayoutForSupport() {
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginHeight = 4;
		gridLayout.marginWidth = 4;
		gridLayout.horizontalSpacing = 10;
		gridLayout.verticalSpacing = 10;
		return gridLayout;
	}
	
	public Properties getData() {
		support.save();
		Properties p = new Properties();
		XAttributeData[] ad = support.getEntityData().getAttributeData();
		for (int i = 0; i < ad.length; i++)
		  p.setProperty(ad[i].getAttribute().getName(), ad[i].getValue());
		return p;		
	}
	
	public void setVisible(boolean visible)	{
		if (visible) {
			activated = true;
		} else {
		}
		super.setVisible(visible);
	}
	
	private void onVersionChange(String version) {
		String[] q = template.getTldTemplates(version);
		XAttributeData[] ad = support.getEntityData().getAttributeData();
		Properties defaultValues = template.getTldTemplateDefaultProperties(version);
		for (int i = 1; i < ad.length; i++) {
			String v = defaultValues.getProperty(ad[i].getAttribute().getXMLName());
			if(v != null) {
				DefaultValueAdapter ai = (DefaultValueAdapter)support.getPropertyEditorAdapterByName(ad[i].getAttribute().getName());
				if(ai != null) ai.setValue(v);
			}
			if(v == null) v = ad[i].getAttribute().getDefaultValue();
			ad[i].setValue(v);
		}
		CheckListAdapter adapter = (CheckListAdapter)support.getPropertyEditorAdapterByName("tld files");
		adapter.setTags(q);
	}

	class XAS extends XAttributeSupport {
		protected boolean keepGreedy(String name, int index, int greedyCount) {
			return index == 3;
		}
	}
	
	class IPL implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			onVersionChange((String)evt.getNewValue());
		}		 
	}
	
}
