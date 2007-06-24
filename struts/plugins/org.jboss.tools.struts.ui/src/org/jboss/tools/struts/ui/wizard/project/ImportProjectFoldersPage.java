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
package org.jboss.tools.struts.ui.wizard.project;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.jboss.tools.common.model.ui.attribute.XAttributeSupport;
import org.jboss.tools.common.model.ui.attribute.adapter.IModelPropertyEditorAdapter;
import org.jboss.tools.common.model.ui.attribute.editor.DirectoryFieldEditorEx;

import org.jboss.tools.struts.StrutsPreference;
import org.jboss.tools.struts.StrutsUtils;
import org.jboss.tools.jst.web.context.IImportWebProjectContext;
import org.jboss.tools.jst.web.context.ImportWebProjectContext;
import org.jboss.tools.jst.web.ui.wizards.appregister.AppRegisterComponent;
import org.jboss.tools.common.model.ui.util.ModelUtilities;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.meta.action.impl.XEntityDataImpl;
import org.jboss.tools.common.meta.action.impl.handlers.HUtil;
import org.jboss.tools.struts.webprj.model.helpers.context.*;

public class ImportProjectFoldersPage extends WizardPage {
	static String[] ATTRIBUTES = {
		ImportProjectWizardContext.ATTR_CLASSES, 
		ImportProjectWizardContext.ATTR_LIB, 
		ImportProjectWizardContext.ATTR_BUILD,
		ImportProjectWizardContext.ATTR_ADD_LIB,
		ImportProjectWizardContext.ATTR_VERSION,
		ImportProjectWizardContext.ATTR_SERVLET_VERSION
	};
	private ImportWebProjectContext context;
	private XAttributeSupport support;
	private IModelPropertyEditorAdapter classesLocationAdapter;
	private IModelPropertyEditorAdapter libLocationAdapter;
	private IModelPropertyEditorAdapter buildXmlLocationAdapter;
	private IModelPropertyEditorAdapter addLibAdapter;
	private IModelPropertyEditorAdapter versionAdapter;
	private IModelPropertyEditorAdapter servletVersionAdapter;
	private PropertyChangeListener updateDataListener;
	AppRegisterComponent appRegister = new AppRegisterComponent();

	protected ImportProjectFoldersPage(ImportWebProjectContext context)	{
		super("Import Project Folders");
		
		this.context = context;
		context.setServletVersion(StrutsPreference.DEFAULT_STRUTS_IMPORT_SERVLET_VERSION.getValue());
		appRegister.setContext(context.getRegisterTomcatContext());
		appRegister.setLayoutForSupport(getLayoutForSupport());
		//For new WTP
		appRegister.setEnabling(false);
		XEntityData entityData = XEntityDataImpl.create(
			new String[][] {
				{ImportProjectWizardContext.PAGE_FOLDERS, ""},
				{ATTRIBUTES[0], ""},
				{ATTRIBUTES[1], ""},
				{ATTRIBUTES[2], ""},
				{ATTRIBUTES[3], ""},
				{ATTRIBUTES[4], ""},
				{ATTRIBUTES[5], ""}
			}
		);
		XAttributeData[] ad = entityData.getAttributeData();
		for (int i = 0; i < ad.length; i++) ad[i].setValue("");

		StrutsUtils t = new StrutsUtils();
		String[] versions = t.getVersionList();
		HUtil.hackAttributeConstraintList(new XEntityData[]{entityData}, 0, ImportProjectWizardContext.ATTR_VERSION, versions);
		if(versions.length > 0) {
			context.setTemplateVersion(versions[0]);
			entityData.setValue(ImportProjectWizardContext.ATTR_VERSION, versions[0]);
		}
		
		support = new XAttributeSupport(ModelUtilities.getPreferenceModel().getRoot(), entityData);
		support.setLayout(getLayoutForSupport());
		classesLocationAdapter = support.getPropertyEditorAdapterByName(ImportProjectWizardContext.ATTR_CLASSES);
		libLocationAdapter = support.getPropertyEditorAdapterByName(ImportProjectWizardContext.ATTR_LIB);
		buildXmlLocationAdapter = support.getPropertyEditorAdapterByName(ImportProjectWizardContext.ATTR_BUILD);
		addLibAdapter = support.getPropertyEditorAdapterByName(ImportProjectWizardContext.ATTR_ADD_LIB);
		versionAdapter = support.getPropertyEditorAdapterByName(ImportProjectWizardContext.ATTR_VERSION);
		servletVersionAdapter = support.getPropertyEditorAdapterByName(ImportProjectWizardContext.ATTR_SERVLET_VERSION);
		appRegister.init();
		initListeners();
	}

	public void dispose() {
		super.dispose();
		if (support!=null) support.dispose();
		support = null;
		updateDataListener = null;
		if (appRegister!=null) appRegister.dispose();
		appRegister = null;
		
		classesLocationAdapter = null;
		libLocationAdapter = null;
		buildXmlLocationAdapter = null;
		addLibAdapter = null;
		versionAdapter = null;
		servletVersionAdapter = null;
	}

	private Layout getLayoutForSupport() {
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginHeight = 4;
		gridLayout.marginWidth = 4;
		gridLayout.horizontalSpacing = 10;
		gridLayout.verticalSpacing = 10;
		return gridLayout;
	}

	public void createControl(Composite parent)
	{
		initializeDialogUnits(parent);
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout());
		Control ch = support.createControl(c);
		ch.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label label = new Label(c, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ch = appRegister.createControl(c);
		ch.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		setControl(c);
		validate();
	}

	public void setVisible(boolean visible) {
		if (visible) {
			lock = true;
			classesLocationAdapter.setValue(context.getClassesLocation());
			libLocationAdapter.setValue(context.getLibLocation());
			buildXmlLocationAdapter.setValue(context.getBuildXmlLocation());
			addLibAdapter.setValue("" + context.getAddLibraries());
			versionAdapter.setValue(context.getTemplateVersion());
			servletVersionAdapter.setValue(context.getServletVersion());
			for (int i = 0; i < ATTRIBUTES.length; i++) {
				FieldEditor f = support.getPropertyEditorByName(ATTRIBUTES[i]).getFieldEditor(getControl().getParent());
				if(f instanceof DirectoryFieldEditorEx)
					((DirectoryFieldEditorEx)f).setLastPath(context.getWebInfLocation());			
			}
			appRegister.loadApplicationName();
			lock = false;
		}
		validate();
		super.setVisible(visible);
	}
	
	boolean lock = false;

	private void initListeners() {
		updateDataListener = 
			new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					if(!lock) {
						updateContext();
						validate();
					}
				}
			};
		support.addPropertyChangeListener(updateDataListener);
		appRegister.addPropertyChangeListener(inputListener);
	}

	private void updateContext() {
		context.setClassesLocation(classesLocationAdapter.getStringValue(false));
		context.setLibLocation(libLocationAdapter.getStringValue(false));
		context.setBuildXmlLocation(buildXmlLocationAdapter.getStringValue(false));
		context.setAddLibraries("true".equals(addLibAdapter.getStringValue(true)));
		context.setTemplateVersion(versionAdapter.getStringValue(true));
		context.setServletVersion(servletVersionAdapter.getStringValue(true));
	}

	public void validate() {
		String message = appRegister.getErrorMessage();
		setPageComplete(message == null);
		setErrorMessage(message);
		if(message != null) return;
		if(!context.isServletVersionConsistentToWebXML()) {
			String warning = IImportWebProjectContext.SERVLET_VERSION_WARNING;
			setMessage(warning, WARNING);
		} else {
			setMessage(null, 0);
		}
	}
	
	InputChangeListener inputListener = new InputChangeListener();
	
	class InputChangeListener implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			validate();
			if(!lock) {
				  String an = appRegister.getApplicationName();
				  if(an.length() > 0 && !an.equals(context.getProjectName())) {
				  	context.setApplicationName(an);
				  }
			}
		}
	}
	
}
