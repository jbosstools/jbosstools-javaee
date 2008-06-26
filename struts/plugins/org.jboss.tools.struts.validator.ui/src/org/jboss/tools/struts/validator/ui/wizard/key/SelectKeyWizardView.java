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
package org.jboss.tools.struts.validator.ui.wizard.key;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Properties;

import org.jboss.tools.common.model.ui.wizards.query.AbstractQueryWizardView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.jboss.tools.common.model.ServiceDialog;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.undo.XUndoManager;
import org.jboss.tools.struts.messages.StrutsUIMessages;

public class SelectKeyWizardView extends AbstractQueryWizardView {
	protected ResourcePathView path = new ResourcePathView();
	protected KeysEditor keys = new KeysEditor();
	protected XUndoManager undo = null;
	
	Properties p;
	Listener listener = new Listener();

	public SelectKeyWizardView() {
		path.addPathListener(keys);
	}
	
	public Control createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 10;
		layout.marginHeight = 10;
		layout.verticalSpacing = 10;
		layout.marginWidth = 10;
		composite.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gd);

		Control pc = path.createControl(composite);
		pc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Control kc = keys.createControl(composite);
		kc.setLayoutData(new GridData(GridData.FILL_BOTH));
		path.updateSelection();
		return composite;
	}
	
	public void setObject(Object data) {
		super.setObject(data);
		p = (Properties)data;
		model = (XModel)p.get("model"); //$NON-NLS-1$
		undo = model.getUndoManager();
		undo.beginTransaction();
		keys.setListener(listener);
		keys.setInitialSelection(p.getProperty("key")); //$NON-NLS-1$
		path.setObject(data);
	}

	public void dispose() {
		 if(undo == null) return;
		 if(code() == 0) {
		 	undo.commitTransaction();
			keys.save();
		 } else if(undo.getTransactionStatus() > 0) {
			 ServiceDialog d = model.getService();
			 int q = d.showDialog(StrutsUIMessages.QUESTION, StrutsUIMessages.SAVE_CHANGES, new String[]{StrutsUIMessages.OK, StrutsUIMessages.CANCEL}, null, ServiceDialog.QUESTION);
			 if(q == 0) {
			 	undo.commitTransaction();
				keys.save();
			 } else undo.rollbackTransaction();
		 }
		 undo = null;
		 super.dispose();
	 }
	
	class Listener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			if(p == null) return;
			String s = (String)evt.getNewValue();
			if(s == null) p.remove("key"); else p.setProperty("key", s); //$NON-NLS-1$ //$NON-NLS-2$
			updateBar();
		}
	}
	
	public void updateBar() {
		if(p == null) return;
		String s = p.getProperty("key"); //$NON-NLS-1$
		getCommandBar().setEnabled(OK, s != null && s.length() > 0);
	}

}
