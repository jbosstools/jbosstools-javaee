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
package org.jboss.tools.jsf.ui.wizard.project;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.jboss.tools.common.model.ui.attribute.XAttributeSupport;
import org.jboss.tools.common.model.ui.attribute.adapter.IModelPropertyEditorAdapter;
import org.jboss.tools.common.model.ui.attribute.editor.DirectoryFieldEditorEx;

import org.jboss.tools.jsf.JSFPreference;
import org.jboss.tools.jsf.web.JSFTemplate;
import org.jboss.tools.jsf.web.helpers.context.ImportProjectWizardContext;
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
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.meta.action.impl.handlers.HUtil;

public class ImportProjectFoldersPage extends WizardPage {
	static String[] ATTRIBUTES = {
		ImportProjectWizardContext.ATTR_CLASSES, 
		ImportProjectWizardContext.ATTR_LIB,
		ImportProjectWizardContext.ATTR_ADD_LIB,
		ImportProjectWizardContext.ATTR_VERSION,
		ImportProjectWizardContext.ATTR_SERVLET_VERSION
	};
	private ImportWebProjectContext context;
	private XAttributeSupport support;
	private IModelPropertyEditorAdapter webrootLocationAdapter;
	private IModelPropertyEditorAdapter srcLocationAdapter;
	private IModelPropertyEditorAdapter classesLocationAdapter;
	private IModelPropertyEditorAdapter libLocationAdapter;
	private IModelPropertyEditorAdapter addLibAdapter;
	private IModelPropertyEditorAdapter versionAdapter;
	private IModelPropertyEditorAdapter servletVersionAdapter;
	private PropertyChangeListener updateDataListener;
	AppRegisterComponent appRegister = new AppRegisterComponent();
	Composite supportControl;

	protected ImportProjectFoldersPage(ImportWebProjectContext context) {
		super("Import Project Folders");
		this.context = context;
		appRegister.setContext(context.getRegisterServerContext());
		appRegister.setLayoutForSupport(getLayoutForSupport());

		appRegister.setEnabling(false);
		XEntityData entityData = XEntityDataImpl.create(
			new String[][] {
				{ImportProjectWizardContext.PAGE_FOLDERS, ""}, //$NON-NLS-1$
				{"web root", "yes"}, //$NON-NLS-1$ //$NON-NLS-2$
				{"src", ""}, //$NON-NLS-1$ //$NON-NLS-2$
				{ATTRIBUTES[0], ""}, //$NON-NLS-1$
				{ATTRIBUTES[1], ""}, //$NON-NLS-1$
				{ATTRIBUTES[2], ""}, //$NON-NLS-1$
				{ATTRIBUTES[3], ""}, //$NON-NLS-1$
				{ATTRIBUTES[4], ""} //$NON-NLS-1$
			}
		);
		XAttributeData[] ad = entityData.getAttributeData();
		for (int i = 0; i < ad.length; i++) ad[i].setValue(""); //$NON-NLS-1$

		context.setServletVersion(JSFPreference.DEFAULT_JSF_IMPORT_SERVLET_VERSION.getValue());
		
		JSFTemplate t = new JSFTemplate();
		String[] versions = t.getVersionList();
		HUtil.hackAttributeConstraintList(new XEntityData[]{entityData}, 0, ATTRIBUTES[3], versions);
		if(versions.length > 0) {
			context.setTemplateVersion(versions[0]);
			entityData.setValue(ATTRIBUTES[3], versions[0]);
		}
		
		support = new XAttributeSupport(ModelUtilities.getPreferenceModel().getRoot(), entityData);
		support.setLayout(getLayoutForSupport());
		
		webrootLocationAdapter = support.getPropertyEditorAdapterByName("web root"); //$NON-NLS-1$
		srcLocationAdapter = support.getPropertyEditorAdapterByName("src"); //$NON-NLS-1$
		classesLocationAdapter = support.getPropertyEditorAdapterByName(ImportProjectWizardContext.ATTR_CLASSES);
		libLocationAdapter = support.getPropertyEditorAdapterByName(ImportProjectWizardContext.ATTR_LIB);
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
		if (supportControl!=null && !supportControl.isDisposed()) supportControl.dispose();
		supportControl = null;
		if (appRegister!=null) appRegister.dispose();
		appRegister = null;
		webrootLocationAdapter = null;
		srcLocationAdapter = null;
		classesLocationAdapter = null;
		libLocationAdapter = null;
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

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout());
		supportControl = support.createControl(c);
		supportControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label label = new Label(c, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Control ch = appRegister.createControl(c);
		ch.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		setControl(c);
		validate();

	}

	public void setVisible(boolean visible) {
		if (visible) {
			lock = true;

			webrootLocationAdapter.setValue(context.getWebRootPath());
			srcLocationAdapter.setValue(context.getModules()[0].getAttributeValue("java src")); //$NON-NLS-1$

			classesLocationAdapter.setValue(context.getClassesLocation());
			libLocationAdapter.setValue(context.getLibLocation());
			addLibAdapter.setValue("" + context.getAddLibraries()); //$NON-NLS-1$
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
	
	public void setDialogSize() {
	    Shell shell = getShell();
		getShell().setSize(shell.getSize().x,shell.getSize().y);
	}	

	boolean lock = false;

	private void initListeners() {
		updateDataListener = new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					if(!lock) {
						updateContext();
						setDependencies();
						validateSrcLastPath();
						validate();
					} 
				}
			};
		support.addPropertyChangeListener(updateDataListener);
		appRegister.addPropertyChangeListener(inputListener);
	}

	private void updateContext() {
		context.getModules()[0].setAttributeValue("root", webrootLocationAdapter.getStringValue(false)); //$NON-NLS-1$
		context.getModules()[0].setAttributeValue("java src", srcLocationAdapter.getStringValue(false)); //$NON-NLS-1$
		
		context.setClassesLocation(classesLocationAdapter.getStringValue(false));
		context.setLibLocation(libLocationAdapter.getStringValue(false));
		context.setAddLibraries("true".equals(addLibAdapter.getStringValue(true))); //$NON-NLS-1$
		context.setTemplateVersion(versionAdapter.getStringValue(true));
		context.setServletVersion(servletVersionAdapter.getStringValue(true));
		context.setBuildXmlLocation(""/*buildXmlLocationAdapter.getStringValue(false)*/); //$NON-NLS-1$
	}
	
	public void validate() {
		setDependencies();
		String message = appRegister.getErrorMessage();
		if(message == null) {
			if(context.getWebRootPath() == null || context.getWebRootPath().length() == 0) {
				
				message = DefaultCreateHandler.getReguiredMessage(webrootLocationAdapter.getAttribute().getName());
			}
		}
		setPageComplete(message == null);
		setErrorMessage(message);
		if(message == null && !context.isServletVersionConsistentToWebXML()) {
			String warning = IImportWebProjectContext.SERVLET_VERSION_WARNING;
			setMessage(warning, WARNING);
		} else {
			setMessage(null, 0);
		}
		validateSrcLastPath();
	}
	
	void validateSrcLastPath() {
		String srcLocation = srcLocationAdapter.getStringValue(true);
		if(srcLocation.length() == 0) {
			String webRootLocation = webrootLocationAdapter.getStringValue(true);
			DirectoryFieldEditorEx srcField = (DirectoryFieldEditorEx)support.getFieldEditorByName("src"); //$NON-NLS-1$
			if(webRootLocation.length() > 0 && srcField != null) {
				srcField.setLastPath(webRootLocation);
			}
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
	
	protected void setDependencies() {
		boolean b = "true".equals(addLibAdapter.getStringValue(true)); //$NON-NLS-1$
		FieldEditor f = support.getFieldEditorByName("version"); //$NON-NLS-1$
		f.setEnabled(b, supportControl);		
	}
	
}
