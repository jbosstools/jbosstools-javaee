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
package org.jboss.tools.struts.validator.ui.constants;

import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.jboss.tools.common.model.ui.swt.util.BorderLayout;
import org.eclipse.swt.widgets.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.event.*;

import org.jboss.tools.common.editor.AbstractSectionEditor;
import org.jboss.tools.struts.validator.ui.*;
import org.jboss.tools.struts.validator.ui.global.*;
import org.eclipse.jface.viewers.ISelectionProvider;

public class ConstantsEditor extends AbstractSectionEditor {
	protected GlobalEditor globalEditor = new GlobalEditor();
	protected ConstantListEditor constantList = new ConstantListEditor();
	
	public ConstantsEditor() {
		globalEditor.addGlobalEditorListener(constantList);
		constantList.setGlobalEditor(globalEditor);
	}

	public void dispose() {
		super.dispose();
		if (constantList!=null) constantList.dispose();
		constantList = null;
		if (globalEditor!=null) globalEditor.dispose();
		globalEditor = null;
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
		Control v = constantList.createControl(q);
		bl.centerComposite = v;
		fireGuiModified();
	}

	public void setObject(XModelObject object, boolean erroneous) {
		super.setObject(object, erroneous);
		globalEditor.setObject(object);
		constantList.setObject(object);
	}

	public void update() {
		if(object == null) return;
		globalEditor.update();
		constantList.update();
	}

	public void stopEditing() {
		//stop editing constantList
	}

	public void structureChanged(XModelTreeEvent event) {
		globalEditor.structureChanged(event);
	}

	public ISelectionProvider getSelectionProvider() {
		return constantList.getSelectionProvider();
	}

	public void updateEditableMode() {
		update();
	}

}
