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

import org.eclipse.swt.*;
import org.jboss.tools.common.model.ui.swt.util.*;
import org.eclipse.swt.widgets.*;
import org.jboss.tools.struts.validator.ui.formset.model.*;

public class FElementEditor implements FSelectionListener {
	protected Composite control;
	protected Object currentEditor = null;
	protected FConstantsEditor constants = new FConstantsEditor();
	protected DependencyEditor dependency = new DependencyEditor();
	protected FModel selectedModel = null;
	protected BorderLayout bl = new BorderLayout();

	public FElementEditor() {}
	
	public void set11() {
		dependency.set11();
	}

	public Control createControl(Composite parent) {
		control = new Composite(parent, SWT.NONE);
		control.setLayout(bl);
		constants.createControl(control);
		dependency.createControl(control);
		enableComponent(-1);
		return control;	
	}
	
	private void enableComponent(int i) {
		if(i == -1) {
			bl.centerComposite = null;
			constants.getControl().setVisible(false);
			dependency.getControl().setVisible(false);
			currentEditor = null;
		} else if(i == 0) {
			bl.centerComposite = constants.getControl();
			constants.getControl().setVisible(true);
			dependency.getControl().setVisible(false);
			constants.getControl().update();
		} else if(i == 1) {
			bl.centerComposite = dependency.getControl();
			constants.getControl().setVisible(false);
			dependency.getControl().setVisible(true);
			dependency.getControl().update();
		}
	}
	
	public Control getControl() {
		return control;
	}
	
	public void update() {
		update(selectedModel);
	}

	public void update(FModel model) {
		if(currentEditor == constants) constants.update();
		if(currentEditor == dependency) dependency.update();
	}

	public void setSelected(FModel fmodel) {
		if(selectedModel == fmodel) return;
		selectedModel = fmodel;
		if(selectedModel instanceof FConstantsModel) {
			currentEditor = constants;
			enableComponent(0);
			constants.setFModel(fmodel);
			dependency.setFModel(null);
		} else if(selectedModel instanceof FieldModel) {
			fmodel = ((FieldModel)fmodel).getDefaultDependency();
			dependency.setFModel(fmodel);
			dependency.getFieldEditor().setEnabled(true);
			constants.setFModel(null);
			if(currentEditor != dependency) {
				currentEditor = dependency;
				enableComponent(1);
			}
		} else if(selectedModel instanceof DependencyModel) {
			dependency.setFModel(fmodel);
			dependency.getFieldEditor().setEnabled(false);
			constants.setFModel(null);
			if(currentEditor != dependency) {
				currentEditor = dependency;
				enableComponent(1);
			}
		} else if(currentEditor != null) {
			currentEditor = null;
			enableComponent(-1);
			constants.setFModel(null);
			constants.setFModel(null);
		}
		selectedModel = fmodel;
		control.getParent().update();
		control.update();
		control.layout(true);
		control.redraw();
	}

}
