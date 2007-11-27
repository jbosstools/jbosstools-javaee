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
package org.jboss.tools.struts.validator.ui.formset;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.jboss.tools.common.editor.AbstractSectionEditor;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.ui.swt.util.BorderLayout;
import org.jboss.tools.common.model.ui.swt.util.VerticalFillLayout;
import org.jboss.tools.struts.validator.ui.ValidationCompoundEditor;
import org.jboss.tools.struts.validator.ui.XStudioValidatorPlugin;
import org.jboss.tools.struts.validator.ui.formset.model.FModel;
import org.jboss.tools.struts.validator.ui.formset.model.FModelListener;
import org.jboss.tools.struts.validator.ui.formset.model.FormsetsModel;
import org.jboss.tools.struts.validator.ui.internal.ValidatorContext;

public class FormsetsEditor extends AbstractSectionEditor implements FModelListener {
	protected FormsetsBar bar = new FormsetsBar();
	protected LanguageEditor languageEditor = new LanguageEditor();
	protected FormsEditor formsEditor = new FormsEditor();
	protected FormsetsModel formsetsModel = new FormsetsModel();
	protected FElementEditor elementEditor = new FElementEditor();
	protected MenuInvoker menu = new MenuInvoker(); 
	protected boolean contextFlag = false; 
	
	public FormsetsEditor() {
		formsetsModel.addListener(this);
		languageEditor.formsetsEditor = this;
		formsEditor.setFormsetsModel(formsetsModel);
		bar.formsetsEditor = this;
		formsEditor.addListener(elementEditor);
		formsEditor.addListener(bar);
		menu.setBar(bar);
		formsEditor.registerMenu(menu);
	} 

	public void set11() {
		elementEditor.set11();
	}

	protected boolean isWrongEntity(String entity) {
		return entity == null || !entity.startsWith(ValidationCompoundEditor.ENTITY);
	}
	
	protected void updateGui() {
		if(control != null && !control.isDisposed()) return;
		control = new SashForm(guiControl, SWT.HORIZONTAL);
		control.setLayoutData(new GridData(GridData.FILL_BOTH));
		createLeftComponent((Composite)control);
		elementEditor.createControl((Composite)control);
		((SashForm)control).setWeights(new int[]{30,70});
		fireGuiModified();
	}
	
	private Control createLeftComponent(Composite parent) {
		Group g = new Group(parent, SWT.NONE);
		g.setText("Formsets");
		BorderLayout bl = new BorderLayout();
		g.setLayout(bl);
		Composite c1 = new Composite(g, SWT.NONE);
		bl.northComposite = c1;
		bl.northHeight = SWT.DEFAULT;
		c1.setLayout(new VerticalFillLayout());		
		bar.createControl(c1);
		new Label(c1, SWT.SEPARATOR | SWT.HORIZONTAL);
		languageEditor.createControl(c1);
		new Label(c1, SWT.SEPARATOR | SWT.HORIZONTAL);
		formsEditor.createControl(g);
		bl.centerComposite = formsEditor.getControl();
		if (contextFlag) {
			ValidatorContext context = new ValidatorContext();
			context.createContext(this);
		}
		return g;
	}

	public Control getControl() {
		return wrapper;
	}
	
	public FormsetsModel getFormsetsModel() {
		return formsetsModel;
	}

	public void setObject(XModelObject object, boolean erroneous) {
		super.setObject(object, erroneous);
		if (isWrongEntity())
			return;
		try {
			bar.setObject(object);
			formsetsModel.setObject(object);
			languageEditor.update();
			formsEditor.setObject(object);
			elementEditor.update();
		} catch (Exception e) {
			XStudioValidatorPlugin.getPluginLog().logError(e);			
		}
	}
	
	public void update() {
		formsetsModel.reload();
		elementEditor.update(); //hack
	}

	public void stopEditing() {
		///TO_DO
	}

	public void modelChanged(FModel source) {
		elementEditor.update(source);
		if(source == formsetsModel) {
			languageEditor.update();
			formsEditor.reloadTree();
		} else {
			formsEditor.updateTree(source);
			elementEditor.update(source);
		}
	}

	public ISelectionProvider getSelectionProvider() {
		return formsEditor.getSelectionProvider();
	}

	public void updateEditableMode() {
		languageEditor.update();
		formsEditor.updateSelection();
		elementEditor.update();
	}
	
	public boolean getContextFlag() {
		return contextFlag;
	}
	
	public void setContextFlag(boolean flag) {
		this.contextFlag = flag;
	}
	
}
