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
package org.jboss.tools.struts.validator.ui.global;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.event.*;
import org.jboss.tools.common.model.ui.swt.util.BorderLayout;
import org.jboss.tools.common.editor.*;
import org.jboss.tools.struts.validator.ui.*;
import org.eclipse.jface.viewers.ISelectionProvider;

public class ValidatorsEditor extends AbstractSectionEditor {
	protected GlobalEditor globalEditor = new GlobalEditor();
	protected ValidatorListEditor validatorList = new ValidatorListEditor();
	protected ValidatorEditor validatorEditor = new ValidatorEditor();

	public ValidatorsEditor() {
		globalEditor.addGlobalEditorListener(validatorList);
		validatorList.setGlobalEditor(globalEditor);
		validatorList.addListener(validatorEditor);
	}

	public void dispose() {
		super.dispose();
		if (globalEditor!=null) globalEditor.dispose();
		globalEditor = null;
		if (validatorList!=null) validatorList.dispose();
		validatorList = null;
		if (validatorEditor!=null) validatorEditor.dispose();
		validatorEditor = null;
	}
	
	protected boolean isWrongEntity(String entity) {
		return entity == null || !entity.startsWith(ValidationCompoundEditor.ENTITY);
	}
	
	protected void updateGui() {
		if(control != null && !control.isDisposed()) return;
		Composite q = new Composite(guiControl, SWT.NONE);
		control = q;
		control.setLayoutData(new GridData(GridData.FILL_BOTH));
		BorderLayout bl = new BorderLayout();
		bl.northHeight = SWT.DEFAULT;
		q.setLayout(bl);
		Control g = globalEditor.createControl(q);
		bl.northComposite = g;    
		SashForm c1 = new SashForm(q, SWT.HORIZONTAL);
		validatorList.createControl(c1);
		validatorEditor.createControl(c1);
		c1.setWeights(new int[]{30,70});
		c1.pack();
		c1.layout();
		bl.centerComposite = c1;
		fireGuiModified();
	}
	
	public void setObject(XModelObject object, boolean erroneous) {
		super.setObject(object, erroneous);
		globalEditor.setObject(object);
		validatorList.setObject(object);
	}

	public void update() {
		if(object == null || control == null || control.isDisposed()) return;
		globalEditor.update();
		validatorList.update();
	}

	public void stopEditing() {}

	public void nodeChanged(XModelTreeEvent event) {
		// fire to validatorEditor
	}

	public void structureChanged(XModelTreeEvent event) {
		if(object == null || control == null || control.isDisposed()) return;
		globalEditor.structureChanged(event);
		validatorList.structureChanged(event);
	}

	public ISelectionProvider getSelectionProvider() {
		return validatorList.getSelectionProvider();
	}

	public void updateEditableMode() {
		globalEditor.updateCommandsEnabled();
		validatorList.update();
		validatorEditor.update();
	}
	
}
