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
package org.jboss.tools.struts.validator.ui;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.jboss.tools.common.editor.AbstractSelectionProvider;
import org.jboss.tools.common.editor.ObjectMultiPageEditor;
import org.jboss.tools.common.editor.ObjectTextEditor;
import org.jboss.tools.common.editor.TreeGuiEditor;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.ui.texteditors.XMLTextEditorComponent;
import org.jboss.tools.struts.validator.ui.constants.ConstantsEditor;
import org.jboss.tools.struts.validator.ui.formset.FormsetsEditor;
import org.jboss.tools.struts.validator.ui.global.ValidatorsEditor;
import org.jboss.tools.struts.validator.ui.internal.ValidatorManager;

public class ValidationCompoundEditor extends ObjectMultiPageEditor {
	public static String ENTITY = "FileValidationRules";
    protected FormsetsEditor formsetsEditor;
    protected ValidatorsEditor validatorsEditor;
    protected ConstantsEditor constantsEditor;

	public void dispose() {
		try {
			getSite().setSelectionProvider(null);
		} catch (Exception e) {
			XStudioValidatorPlugin.getPluginLog().logError(e);			
		}
		selectionProvider.setHost(null);
		if (formsetsEditor != null)
			formsetsEditor.dispose();
		formsetsEditor = null;
		if (validatorsEditor != null)
			validatorsEditor.dispose();
		validatorsEditor = null;
		if (constantsEditor != null)
			constantsEditor.dispose();
		constantsEditor = null;
		super.dispose();
	}

	protected boolean isWrongEntity(String entity) {
		return entity == null || !entity.startsWith(ENTITY);
	}
	
	protected void doCreatePages() {
		if(isAppropriateNature()) {
			if(true) {
				createFormsetsPage();
				createValidatorsPage();
				createConstantsPage();
				createTreePage();
			} else {
				treeFormPage = createTreeFormPage();
				treeFormPage.setTitle("Validation Editor");
				treeFormPage.initialize(object);
				addFormPage(treeFormPage);
			}
		}
		createTextPage();
		if(textEditor != null) selectionProvider.addHost("textEditor", getTextSelectionProvider());
		initEditors();
		if(treeEditor != null) selectionProvider.addHost("treeEditor", treeEditor.getSelectionProvider());
		if(treeFormPage != null) selectionProvider.addHost("treeEditor", treeFormPage.getSelectionProvider());
		if(formsetsEditor != null) {
			selectionProvider.setHost(formsetsEditor.getSelectionProvider());
		}
	}
	
	protected void createFormsetsPage() {
		formsetsEditor = new FormsetsEditor();
		if(getModelObject().getModelEntity().getName().endsWith("11")) {
			formsetsEditor.set11();
		}
		try {
			formsetsEditor.init(getEditorSite(), getEditorInput());
			int index = addPage(formsetsEditor, getEditorInput());
			setPageText(index, "Formsets");
		} catch (Exception e) {
			XStudioValidatorPlugin.getPluginLog().logError(e);			
		}
		formsetsEditor.addErrorSelectionListener(createErrorSelectionListener());
		selectionProvider.addHost("formsetsEditor", formsetsEditor.getSelectionProvider());
	}
	
	protected void createValidatorsPage() {
		validatorsEditor = new ValidatorsEditor();
		try {
			validatorsEditor.init(getEditorSite(), getEditorInput());
			int index = addPage(validatorsEditor, getEditorInput());
			setPageText(index, "Validators");
		} catch (Exception e) {
			XStudioValidatorPlugin.getPluginLog().logError(e);			
		}
		validatorsEditor.addErrorSelectionListener(createErrorSelectionListener());
		selectionProvider.addHost("validatorsEditor", validatorsEditor.getSelectionProvider());
	}
	
	protected void createConstantsPage() {
		constantsEditor = new ConstantsEditor();
		try {
			constantsEditor.init(getEditorSite(), getEditorInput());
			int index = addPage(constantsEditor, getEditorInput());
			setPageText(index, "Constants");
		} catch (Exception e) {
			XStudioValidatorPlugin.getPluginLog().logError(e);			
		}
		constantsEditor.addErrorSelectionListener(createErrorSelectionListener());
		selectionProvider.addHost("constantsEditor", constantsEditor.getSelectionProvider());
	}

	protected void createTreePage() {
		treeEditor = new TreeGuiEditor();
		try {
			treeEditor.init(getEditorSite(), getEditorInput());
			int index = addPage(treeEditor, getEditorInput());
			setPageText(index, "Tree");
		} catch (Exception e) {
			XStudioValidatorPlugin.getPluginLog().logError(e);			
		}
		treeEditor.addErrorSelectionListener(createErrorSelectionListener());
		selectionProvider.addHost("treeEditor", treeEditor.getSelectionProvider());
	}
	
	protected ObjectTextEditor createTextEditor() {
		return new XMLTextEditorComponent();	
	}

	protected void setNormalMode() {
		if(!isAppropriateNature()) return;
		if(formsetsEditor != null) {
			formsetsEditor.setObject(object, isErrorMode());
			constantsEditor.setObject(object, isErrorMode());
			validatorsEditor.setObject(object, isErrorMode());
			treeEditor.setObject(object, isErrorMode());
			formsetsEditor.update();
			constantsEditor.update();
			validatorsEditor.update();
		}
		if (treeFormPage != null) {
			treeFormPage.initialize(getModelObject());
			treeFormPage.setErrorMode(isErrorMode());
		}
	}

	protected void setErrorMode() {
		setNormalMode();
	}

	protected void updateSelectionProvider() {
		int i = getActivePage();
		ISelectionProvider p = emptyProvider;
		if(formsetsEditor != null) {
			p = (isErrorMode) ? emptyProvider :  
					 (i == 0) ? formsetsEditor.getSelectionProvider() :
					 (i == 1) ? validatorsEditor.getSelectionProvider() :
					 (i == 2) ? constantsEditor.getSelectionProvider() :
					 (i == 3) ? (ISelectionProvider)treeEditor.getSelectionProvider() :
					 emptyProvider;
		} else if(treeEditor != null) {
			p = treeEditor.getSelectionProvider();
		} else if (treeFormPage != null) {
			p = treeFormPage.getSelectionProvider();
		}
		selectionProvider.setHost(p);
		if(p instanceof AbstractSelectionProvider) {
			((AbstractSelectionProvider)p).fireSelectionChanged();
		} else if(i == 3) {
			treeEditor.fireEditorSelected();
		}
	}
	
	AbstractSelectionProvider emptyProvider = new SP(); 

	class SP extends AbstractSelectionProvider {
		protected XModelObject getSelectedModelObject() {
			return null;
		}
		
		protected void setSelectedModelObject(XModelObject object) {}
	}
	
	protected void updateEditableMode() {
		if(isAppropriateNature() && formsetsEditor != null) {
			formsetsEditor.updateEditableMode();
			validatorsEditor.updateEditableMode();
			constantsEditor.updateEditableMode();
		}
	}

	public void registerValidator() {
		ValidatorManager.getDefault().registerValidators(this);
	}

	public void unregisterValidator() {
		ValidatorManager.getDefault().unregisterValidators(this);
	}
	
	public Object getAdapter(Class adapter) {
		return super.getAdapter(adapter);
	}

}
